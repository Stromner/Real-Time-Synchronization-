package sigmatechnology.se.diff_match_patch;
/**
 * Can keep an entire root folder in sync and ignore a subset of folders and 
 * files inside the root. Meant to be run over network however works exactly 
 * the same way if it were to be run locally. The only exception is that two 
 * instances of the program needs to be active for it to work.
 * 
 * To refresh the root folder the updateRoot() method needs to be called 
 * explicitly. The only time it's called implicitly is when SynchronizeRoot is 
 * created.
 * 
 * @author David Strömner
 * @date 2015-04-26
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sigmatechnology.se.diff_match_patch.fraser_neil.diff_match_patch;
import sigmatechnology.se.diff_match_patch.fraser_neil.diff_match_patch.Diff;
import sigmatechnology.se.diff_match_patch.fraser_neil.diff_match_patch.Operation;
import sigmatechnology.se.diff_match_patch.fraser_neil.diff_match_patch.Patch;

public class SynchronizeRoot {
	public static final Charset ENCODING = Charset.forName("ISO-8859-1");
	private diff_match_patch dmp;
	private Path root;
	private List<Path> ignore;
	private Map<String, PathWithData> rootMap;
	
	/**
	 * Initialize the synchronization object by searching through the provided root folder.
	 * Each file inside the root folder will be synchronized.
	 * 
	 * @param doc Root to search through.
	 * @param ignoreDocs Files and folders inside the root to ignore. Null if everything
	 * should be included.
	 */
	public SynchronizeRoot(Path doc, List<Path> ignoreDocs){
		if(doc == null){
			throw new IllegalArgumentException("Must have a valid root");
		}
		
		root = doc;
		ignore = ignoreDocs;
		dmp = new diff_match_patch();
		rootMap = new HashMap<String,PathWithData>();
		update();
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
			    	if(rootMap.containsKey(filePath.toString()) == false){
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
							rootMap.put(filePath.toString(), new PathWithData(filePath, openReadFile(filePath)));
			    		}
			    	}
			    }
			});
		} catch (Exception e) {
			// TODO Print to console
			System.out.println("Could not open all the files in the root directory");
			e.printStackTrace();
		}
		
		// If any files were removed update the rootMap.
		Iterator it = rootMap.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        PathWithData pwd = (PathWithData)pair.getValue();
	        if(!pwd.getPath().toFile().exists()){
	        	it.remove();
	        }
	    }
	}
	
	public void setIgnoreList(List<Path> ignoreDocs){
		ignore = ignoreDocs;
	}
	
	/**
	 * Calculates a diff between the old version of a document and the current version of 
	 * the document. If a modifications is spotted it will reflect so in the return.
	 * 
	 * @param doc Document to check if changed.
	 * @return A list containing the calculate diff between the old document 
	 * and the current one. null if the document could not be open/found.
	 */
	public LinkedList<Diff> getDiff(Path doc){
		PathWithData file;
		if((file = rootMap.get(doc.toString())) == null){
			return null;
		}
		String modifiedString = "";
		
		modifiedString = openReadFile(doc);
		LinkedList<Diff> list = dmp.diff_main(file.getData(),modifiedString, true);
		file.setData(modifiedString);
		
		return list;
	}
	
	/**
	 * Given a diff and a file to apply the diff too this method does its best to
	 * apply the diff in the correct places. Ignores diffs that are empty and 
	 * diffs that are null.
	 * 
	 * @param diffList List containing the diffs that should be applied.
	 * @param doc File to apply the diffs to.
	 */
	public void applyDiff(LinkedList<Diff> diffList, Path doc){
		// If we can't fetch the first element then the document is empty, nothing to do.
		// If there is only one element and that element is equal then no modifications have
		// been made.
		if((diffList.get(0) == null) || (diffList.get(0).operation == Operation.EQUAL && diffList.size() == 1)){
			return;
		}
		
		LinkedList<Patch> patchList = dmp.patch_make(diffList);
		Object o[] = null;
		o = dmp.patch_apply(patchList, openReadFile(doc));
		String s = (String)o[0];
		openWriteFile(doc, s);
	}
	
	/**
	 * Opens and read all data from the file.
	 * 
	 * @param filePath Path to the file to be read.
	 * @return The data from the file. null if the file could not be opened.
	 */
	protected String openReadFile(Path filePath){
		String line;
		StringBuilder sb = new StringBuilder();
		// Read the document
		BufferedReader reader;
		try {
			reader = Files.newBufferedReader(filePath, ENCODING);
			// Append each line in the document to our string
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append("\n"); // readLine discards new-row characters, re-append them.
			}
			if(sb.length() != 0){
				sb.deleteCharAt(sb.length()-1); // However last line doesn't contain one, remove it.
			}
			reader.close();
			return new String(sb.toString());
		} catch (IOException e) {
			// TODO Print to console
			System.out.println("Could not write to " + filePath.toString());
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Open and write all new data to a file.
	 * 
	 * @param filePath Path to the file to write too.
	 * @param s String containing the data to be written.
	 */
	protected void openWriteFile(Path filePath, String s){
		BufferedWriter writer;
		try {
			writer = Files.newBufferedWriter(filePath, ENCODING);
			writer.write(s, 0, s.length());
			writer.close();
		} catch (IOException e) {
			// TODO Print to console
			System.out.println("Could not write to " + filePath.toString());
			e.printStackTrace();
		}
	}
}