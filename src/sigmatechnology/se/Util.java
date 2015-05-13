package sigmatechnology.se;

/**
 * Utility class for misc methods.
 * 
 * @author David Strömner
 * @version 2015-04-27
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import sigmatechnology.se.diff_match_patch.SynchronizeRoot;

public class Util {
	/**
	 * Opens and read all data from the file.
	 * 
	 * @param filePath Path to the file to be read.
	 * @return The data from the file. null if the file could not be opened.
	 */
	public static String openReadFile(Path filePath){
		String line;
		StringBuilder sb = new StringBuilder();
		// Read the document
		BufferedReader reader;
		try {
			reader = Files.newBufferedReader(filePath, SynchronizeRoot.ENCODING);
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
	public static void openWriteFile(Path filePath, String s){
		BufferedWriter writer;
		try {
			writer = Files.newBufferedWriter(filePath, SynchronizeRoot.ENCODING);
			writer.write(s, 0, s.length());
			writer.close();
		} catch (IOException e) {
			// TODO Print to console
			System.out.println("Could not write to " + filePath.toString());
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates a folder at path s if there is none.
	 * @param s
	 * @return
	 */
	private Path prepareFolder(String s){
		// Create the file if it doesn't exist from a previous run of the program.
		File f = new File(s);
		f.mkdir();
		Path p;
		p = Paths.get(s);
		
		return p;
	}
	
	/**
	 * Creates a file at path s if there is none
	 * @param s
	 * @return
	 */
	private Path prepareFile(String s){
		// Create the file if it doesn't exist from a previous run of the program
		File f = new File(s);
		try{
			if(!f.exists()){
				f.createNewFile();
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		Path p;
		p = Paths.get(s);
		
		return p;
	}
}
