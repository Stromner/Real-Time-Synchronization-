package diff_match_patch;
/**
 * Version two of sync, can keep an entire root folder in sync. The documents 
 * can be synchronized over the net. To run it locally two instances of the
 * program needs to run.
 * 
 * @author David Strömner
 * @date 2015-04-22
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;

import diff_match_patch.fraser_neil.diff_match_patch;
import diff_match_patch.fraser_neil.diff_match_patch.Diff;
import diff_match_patch.fraser_neil.diff_match_patch.Operation;
import diff_match_patch.fraser_neil.diff_match_patch.Patch;

public class SynchronizeRoot {
	public static final Charset ENCODING = Charset.forName("ISO-8859-1");
	private diff_match_patch dmp;
	private Path root;
	private HashMap<String, PathWithData> rootMap;
	
	/**
	 * Initialize the synchronization object by searching through the provided root folder.
	 * Each file inside the root folder will be synchronized.
	 * 
	 * @param doc Root to search through.
	 */
	// TODO Add support for excluding support for selected files by the user.
	public SynchronizeRoot(Path doc){
		root = doc;
		dmp = new diff_match_patch();
		rootMap = new HashMap<String,PathWithData>();
		
		// Read through the root for any files. For each file found create
		// an entry in the rootMap
		try {
			Files.walk(root).forEach(filePath -> {
			    if (Files.isRegularFile(filePath)) {
			    	try {
			    		System.out.println(filePath.toString());
						rootMap.put(filePath.toString(), new PathWithData(filePath, openReadFile(filePath)));
					} catch (Exception e) {
						// TODO Print to console
						System.out.println("Could not open" + filePath.toString() + ".");
						e.printStackTrace();
					}
			    }
			});
		} catch (IOException e) {
			// TODO Print to console
			System.out.println("Could not open all the files in the root directory");
			e.printStackTrace();
		}
	}
	
	/**
	 * Calculates a diff between the old version of a document and the current version of 
	 * the document. If a modifications is spotted it will reflect so in the return.
	 * 
	 * @param doc Document to check if changed.
	 * @return A list containing the calculate diff between the old document 
	 * and the current one.
	 */
	public LinkedList<Diff> getDiff(Path doc){
		PathWithData file = rootMap.get(doc.toString());
		String modifiedString = "";
		
		try {
			modifiedString = openReadFile(doc);
		} catch (IOException e) {
			// TODO Print to the console
			System.out.println("Could not open the file: '" + doc.toString() + "."); 
			e.printStackTrace();
		}
		LinkedList<Diff> list = dmp.diff_main(file.getData(), modifiedString, true);
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
		if((diffList.get(0).operation == Operation.EQUAL && diffList.get(1) == null) || (diffList.get(0) == null)){
			System.out.println("AHHH");
			return;
		}
		
		LinkedList<Patch> patchList = dmp.patch_make(diffList);
		Object o[] = null;
		try {
			o = dmp.patch_apply(patchList, openReadFile(doc));
		} catch (IOException e) {
			// TODO Print to the console
			System.out.println("Could not apply the patch to the file: '" + doc + ".");
			e.printStackTrace();
		}
		String s = (String)o[0];
		try {
			openWriteFile(doc, s);
		} catch (IOException e) {
			// TODO Print to the console
			System.out.println("Could not open the file: '" + doc + ". File is busy.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Opens and read all data from the file.
	 * 
	 * @param filePath Path to the file to be read.
	 * @return The data from the file.
	 * @throws IOException Throws IO Exception when the document can't be opened.
	 */
	protected String openReadFile(Path filePath) throws IOException{
		String line;
		StringBuilder sb = new StringBuilder();
		// Read the document
		BufferedReader reader = Files.newBufferedReader(filePath, ENCODING);
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
	}
	
	/**
	 * Open and write all new data to a file.
	 * 
	 * @param filePath Path to the file to write too.
	 * @param s String containing the data to be written.
	 * @throws IOException Throws IO Exception when the file can't be opened.
	 */
	protected void openWriteFile(Path filePath, String s) throws IOException{
		BufferedWriter writer = Files.newBufferedWriter(filePath, ENCODING);
		writer.write(s, 0, s.length());
		writer.close();
	}
}