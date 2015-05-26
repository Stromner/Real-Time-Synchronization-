package sigmatechnology.se.realtime_file_synchronisation.diff_match_patch;
/**
 * Keep a root folder root folder in sync and provides support for ignore a 
 * subset of folders and files inside the root. Meant to be run over network 
 * however works exactly the same way if it were to be run locally. The only 
 * exception is that two instances of the program needs to be active for it 
 * to work and they are set up for different repos.
 * 
 * To refresh the root folder the updateRoot() method needs to be called 
 * explicitly. The only time it's called implicitly is when SynchronizeRoot is 
 * created.
 * 
 * @author David Strömner
 * @date 2015-05-08
 */

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import sigmatechnology.se.realtime_file_synchronisation.Util;
import sigmatechnology.se.realtime_file_synchronisation.diff_match_patch.fraser_neil.diff_match_patch;
import sigmatechnology.se.realtime_file_synchronisation.diff_match_patch.fraser_neil.diff_match_patch.Diff;
import sigmatechnology.se.realtime_file_synchronisation.diff_match_patch.fraser_neil.diff_match_patch.Operation;
import sigmatechnology.se.realtime_file_synchronisation.diff_match_patch.fraser_neil.diff_match_patch.Patch;
import sigmatechnology.se.realtime_file_synchronisation.network.Packets;
import sigmatechnology.se.realtime_file_synchronisation.plugin.Controller;

public class SynchronizeRoot extends Thread{
	public static final Charset ENCODING = Charset.forName("ISO-8859-1");
	private diff_match_patch dmp;
	private Path root;
	private List<Path> ignore;
	private Map<String, SynchronizeDocument> rootMap;
	private ReentrantLock lock;
	
	/**
	 * Initialize the synchronization object by searching through the provided root folder.
	 * Each file inside the root folder and not on the ignore list will be synchronized.
	 * 
	 * @param doc root to synchronize.
	 * @param ignoreDocs files and folders inside the root to ignore. Null if everything
	 * should be included.
	 */
	public SynchronizeRoot(Path doc, List<Path> ignoreDocs){
		if(doc == null){
			throw new IllegalArgumentException("Root must be valid, can't be null.");
		}
		
		lock = new ReentrantLock();
		root = doc;
		ignore = ignoreDocs;
		dmp = new diff_match_patch();
		rootMap = new HashMap<String,SynchronizeDocument>();
		update();
	}
	
	@Override
	public void run(){
		while(!isInterrupted()){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				break;
			}
			Controller.getInstance().getClient().send(Packets.SYNCFILE, getDiffs());
			update(); // Any changes to the files we track?
		}
	}
	
	/**
	 * Update the root folder, adds new files and removes deleted files. Respects the
	 * given ignore list from initiation.
	 */
	public void update(){
		// Read through the root for any files. For each file found create
		// an entry in the rootMap
		try {
			Files.walk(root).forEach(filePath -> {
			    if (Files.isRegularFile(filePath)) {
			    	// If the file isn't already in our map, add it.
			    	Path p = root.relativize(filePath);
			    	if(rootMap.containsKey(p.toString()) == false){
			    		// However, make sure it's not on the ignore list.
			    		Boolean notIgnored = true;
			    		if(ignore != null){
				    		for(int i=0;i<ignore.size();i++){
				    			if(ignore.get(i).equals(filePath) || ignore.get(i).relativize(filePath).toString().equals("")){
				    				notIgnored = false;
				    				break;
				    			}
				    		}
			    		}
			    		if(notIgnored){
							rootMap.put(p.toString(), new SynchronizeDocument(p.toString(), Util.openReadFile(filePath)));
							Controller.getInstance().getLauncher().updatePane("Tracking file: " + p.toString());
			    		}
			    	}
			    }
			});
		} catch (Exception e) {
			Controller.getInstance().getLauncher().updatePane("Could not open all the files in the root directory");
			e.printStackTrace();
		}
		
		// If any files were removed update the rootMap.
		Iterator<?> it = rootMap.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<?,?> pair = (Map.Entry<?,?>)it.next();
	        SynchronizeDocument sd = (SynchronizeDocument)pair.getValue();
	        if(!root.resolve(sd.getPath()).toFile().exists()){
	        	it.remove();
	        }
	    }
	}
	
	public void setIgnoreList(List<Path> ignoreDocs){
		ignore = ignoreDocs;
	}

	/**
	 * Calculate a diff for each of the synchronized files. If no changes have been made 
	 * since last call it's still included in the list as a simple element saying everything is equal.
	 * 
	 * @return all the diffs for the different documents.
	 */
	public LinkedList<SynchronizeDocument> getDiffs(){
		lock.lock();
		LinkedList<SynchronizeDocument> l = new LinkedList<SynchronizeDocument>();
		Iterator<?> it = rootMap.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<?,?> pair = (Map.Entry<?,?>)it.next();
	        SynchronizeDocument sd = (SynchronizeDocument)pair.getValue();
	        sd.setDiffs(getDiff(Paths.get(sd.getPath())));
	        l.add(sd);
	    }
	    lock.unlock();
	    return l;
	}
	
	/**
	 * Applies each diff in the list to the corresponding document. 
	 * 
	 * @param diffs list of diffs to be applied to the document.
	 */
	public void applyDiffs(LinkedList<SynchronizeDocument> diffs){
		lock.lock();
		SynchronizeDocument sd;
		for(int i=0;i<diffs.size();i++){
			sd = diffs.get(i);
			applyDiff(sd.getDiffs(), Paths.get(sd.getPath()));
		}
		lock.unlock();
	}	
	
	/**
	 * Calculates a diff between the old version of a document and the current version of 
	 * the document. If a modifications is spotted it will reflect so in the return.
	 * 
	 * @param doc document to check if changed.
	 * @return a list containing the calculate diff between the old document 
	 * and the current one. null if the document could not be open/found.
	 */
	private LinkedList<Diff> getDiff(Path doc){
		SynchronizeDocument file;
		if((file = rootMap.get(doc.toString())) == null){
			return null;
		}
		
		// Is the file opened in Eclipse? If so read the data from there
		String eclipseString = Util.openEclipseGetContent(root.resolve(doc).toString());
		String modifiedString;
		modifiedString = eclipseString != null ? eclipseString : Util.openReadFile(root.resolve(doc));
		
		LinkedList<Diff> list = dmp.diff_main(file.getData(),modifiedString, true);
		file.setData(modifiedString);
		return list;
	}
	
	/**
	 * Given a diff and a file to apply the diff too this method does its best to
	 * apply the diff in the correct places. Ignores diffs that are empty and 
	 * diffs that are null.
	 * 
	 * @param diffList list containing the diffs that should be applied.
	 * @param doc file to apply the diffs to.
	 */
	private void applyDiff(LinkedList<Diff> diffList, Path doc){
		// If we can't fetch the first element then the document is empty, nothing to do.
		// If there is only one element and that element is equal then no modifications have
		// been made. If we can't find the document then we can't patch it.
		if(diffList == null || (diffList.size() == 0) || 
				(diffList.get(0).operation == Operation.EQUAL && diffList.size() == 1) || 
				rootMap.get(doc.toString())== null){
			return;
		}
		
		LinkedList<Patch> patchList = dmp.patch_make(diffList);
		Object o[] = null;
		
		// Is the file opened in Eclipse? If so patch the file through the IDE
		String eclipseString = Util.openEclipseGetContent(root.resolve(doc).toString());
		String s;
		if(eclipseString != null){
			o = dmp.patch_apply(patchList, eclipseString);
			s = (String)o[0];
			Util.openEclipseWriteContent(root.resolve(doc).toString(), s);
		}
		else{
			System.out.println("doc: " + doc);
			o = dmp.patch_apply(patchList, Util.openReadFile(root.resolve(doc)));
			s = (String)o[0];
			Util.openWriteFile(root.resolve(doc), s);
			System.out.println("s: " + s);
		}
		
		// Update the internal file
		o = dmp.patch_apply(patchList, rootMap.get(doc.toString()).getData());
		s = (String)o[0];
		SynchronizeDocument sd = rootMap.get(doc.toString());
		sd.setData(s);
	}
}