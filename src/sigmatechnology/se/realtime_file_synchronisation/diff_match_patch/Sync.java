package sigmatechnology.se.realtime_file_synchronisation.diff_match_patch;
/**
 * Version one of sync, can keep a single document in sync. The documents can be on 
 * different computers without any problems.
 * 
 * @author David Strömner
 * @date 2015-04-21
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;

import sigmatechnology.se.realtime_file_synchronisation.diff_match_patch.fraser_neil.diff_match_patch;
import sigmatechnology.se.realtime_file_synchronisation.diff_match_patch.fraser_neil.diff_match_patch.Diff;
import sigmatechnology.se.realtime_file_synchronisation.diff_match_patch.fraser_neil.diff_match_patch.Operation;
import sigmatechnology.se.realtime_file_synchronisation.diff_match_patch.fraser_neil.diff_match_patch.Patch;

public class Sync {
	public static final Charset ENCODING = Charset.forName("ISO-8859-1");
	private diff_match_patch dmp;
	private Path modDoc;
	private String baseString;
	
	/**
	 * Initialize a newly created Sync object by reading the given file so it got a 
	 * base to work against when calculating the diffs. 
	 * 
	 * @param doc File to be open
	 * @throws IOException When the file couldn't be open for whatever reason.
	 */
	public Sync(Path doc){
		modDoc = doc;
		dmp = new diff_match_patch();
		try {
			baseString = openReadFile(modDoc);
		} catch (IOException e) {
			// TODO Print to the console
			// "Could not open the file: '" + modDoc + "." 
			e.printStackTrace();
		}
	}
	
	/**
	 * Calculates a diff between the base document and the current document.
	 * If a modifications is spotted it will reflect so in the return and the
	 * base document will be updated to match the current version.
	 * 
	 * @return A list containing the calculated diff between the base document 
	 * and the current document
	 */
	public LinkedList<Diff> getDiff(){	
		String modifiedString = "";
		try {
			modifiedString = openReadFile(modDoc);
		} catch (IOException e) {
			// TODO Print to the console
			// "Could not open the file: '" + modDoc + "." 
			e.printStackTrace();
		}
		LinkedList<Diff> list = dmp.diff_main(baseString, modifiedString, true);
		baseString = modifiedString;
		
		return list;
	}
	
	/**
	 * Given a diff and a file to apply the diff too the method does its best to
	 * apply the diff in the correct place. Ignores diffs that states the
	 * documents are identical and diffs that are null.
	 * 
	 * @param diffList List containing the diffs that should be applied.
	 * @param doc File to apply the diffs to.
	 * @throws IOException Throws IO exception when the file can't be accessed.
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
			// "Could not apply the patch to the file: '" + doc + "." 
			e.printStackTrace();
		}
		String s = (String)o[0];
		try {
			openWriteFile(doc, s);
		} catch (IOException e) {
			// TODO Print to the console
			// "Could not open the file: '" + doc + ". File is busy." 
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