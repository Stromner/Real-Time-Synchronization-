package sigmatechnology.se.diff_match_patch;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class SynchronizeRootTest extends TestCase{
	private String sRepo1 = "src/sigmatechnology/se/diff_match_patch/TestRepo1/",
				sRepo2 = "src/sigmatechnology/se/diff_match_patch/TestRepo2/",
				sIgnoreFolder1 = "src/sigmatechnology/se/diff_match_patch/TestRepo1/Ignore",
				sIgnoreFolder2 = "src/sigmatechnology/se/diff_match_patch/TestRepo2/Ignore",
				sIgnoreFile1 = "src/sigmatechnology/se/diff_match_patch/TestRepo1/Ignore/test.txt",
				sIgnoreFile2 = "src/sigmatechnology/se/diff_match_patch/TestRepo2/Ignore/test.txt",
				sSend1 = "src/sigmatechnology/se/diff_match_patch/TestRepo1/send.txt",
				sSend2 = "src/sigmatechnology/se/diff_match_patch/TestRepo2/send.txt",
				sRecieve1 = "src/sigmatechnology/se/diff_match_patch/TestRepo1/recieve.txt",
				sRecieve2 = "src/sigmatechnology/se/diff_match_patch/TestRepo2/recieve.txt",
				sRoot = "";
	private Path docSend, docRecieve, root;
	private SynchronizeRoot sync;
	private static Boolean oneTimeSetUpDone = false;
	
	@Before
	public void setUp(){
		if(!oneTimeSetUpDone){
			root = Paths.get(sRoot);
			prepareFolder(sRepo1);
			prepareFolder(sRepo2);
			prepareFolder(sIgnoreFolder1);
			prepareFolder(sIgnoreFolder2);
			prepareFile(sIgnoreFile1);
			prepareFile(sIgnoreFile2);
			prepareFile(sSend1);
			prepareFile(sSend2);
			prepareFile(sRecieve1);
			prepareFile(sRecieve2);
			docSend = prepareFile(sSend1);
			docRecieve = prepareFile(sRecieve2);
			sync = new SynchronizeRoot(root, null);
		}
		
		sync.update();
		sync.setIgnoreList(null);
		emptyFile(sSend1);
		emptyFile(sRecieve1);
	}
	
	//
	// Single File Tests
	//
	@Test
	public void testEmptyToOneChar(){
		sync.openWriteFile(docSend, "a");
		sync.applyDiff(sync.getDiff(docSend), docRecieve);
		assertEquals(true, sync.openReadFile(docRecieve).equals("a"));
	}
	
	@Test
	public void testOneCharToEmpty(){
		sync.openWriteFile(docSend, "a");
		sync.getDiff(docSend); // Update the base string
		
		sync.openWriteFile(docSend, "");
		sync.applyDiff(sync.getDiff(docSend), docRecieve);
		assertEquals(true, sync.openReadFile(docRecieve).equals(""));
	}
	
	@Test
	public void testOneRowToMany(){
		sync.openWriteFile(docSend, "a");
		sync.openWriteFile(docRecieve, "a");
		sync.getDiff(docSend); // Update the base string
		
		sync.openWriteFile(docSend, "a\na");
		sync.applyDiff(sync.getDiff(docSend), docRecieve);
		assertEquals(true, sync.openReadFile(docRecieve).equals("a\na"));
	}
	
	@Test
	public void testManyRowToOne(){
		sync.openWriteFile(docSend, "a\na");
		sync.openWriteFile(docRecieve, "a\na");
		sync.getDiff(docSend); // Update the base string
		
		sync.openWriteFile(docSend, "a");
		sync.applyDiff(sync.getDiff(docSend), docRecieve);
		assertEquals(true, sync.openReadFile(docRecieve).equals("a"));
	}
	
	@Test
	public void testManyRowsToLess(){
		sync.openWriteFile(docSend, "a\nb\nc\nd");
		sync.openWriteFile(docRecieve, "a\nb\nc\nd");
		sync.getDiff(docSend); // Update the base string
		
		sync.openWriteFile(docSend, "a\nb\nd");
		sync.applyDiff(sync.getDiff(docSend), docRecieve);
		assertEquals(true, sync.openReadFile(docRecieve).equals("a\nb\nd"));
	}
	
	@Test
	public void testManyRowsToMore(){
		sync.openWriteFile(docSend, "a\nb\nc\nd");
		sync.openWriteFile(docRecieve, "a\nb\nc\nd");
		sync.getDiff(docSend); // Update the base string
		
		sync.openWriteFile(docSend, "a\nb\nbb\nc\ncc\nd");
		sync.applyDiff(sync.getDiff(docSend), docRecieve);
		assertEquals(true, sync.openReadFile(docRecieve).equals("a\nb\nbb\nc\ncc\nd"));
	}
	
	@Test
	public void testDifferenceBetweenFiles(){
		sync.openWriteFile(docSend, "abba\ndabba\ncabba\ndabba");
		sync.openWriteFile(docRecieve, "abba\ndabba\nMOLA\ndabba");
		sync.getDiff(docSend); // Update the base string
		
		sync.openWriteFile(docSend, "abba\ndabba\ncabba123\ndabba");
		sync.applyDiff(sync.getDiff(docSend), docRecieve);
		assertEquals(true, sync.openReadFile(docRecieve).equals("abba\ndabba\nMOLA123\ndabba"));
	}
	
	@Test
	public void testSwedishCharacters(){
		sync.openWriteFile(docSend, "едц");
		sync.applyDiff(sync.getDiff(docSend), docRecieve);
		assertEquals(true, sync.openReadFile(docRecieve).equals("едц"));
	}
	
	@Test
	public void testReplaceDocument(){
		sync.openWriteFile(docSend, "One day I went to the forrest\nto pick some flowers.");
		sync.openWriteFile(docRecieve, "One day I went to the forrest\nto pick some flowers.");
		sync.getDiff(docSend); // Update the base string
		
		sync.openWriteFile(docSend, "I cut hair for a living.");
		sync.applyDiff(sync.getDiff(docSend), docRecieve);
		assertEquals(true, sync.openReadFile(docRecieve).equals("I cut hair for a living."));
	}
	
	//
	// Root Folder Test
	//
	
	@Test
	public void testDocumentAsRoot(){
		sync = new SynchronizeRoot(docSend, null);
		
		sync.openWriteFile(docSend, "a");
		sync.applyDiff(sync.getDiff(docSend), docRecieve);
		assertEquals(true, sync.openReadFile(docRecieve).equals("a"));
	}
	
	@Test
	public void testTwoDocumentsSync(){
		// Fetch the path for the two test documents extra text documents created
		// at the start.
		Path docSend2 = prepareFile(sSend2),
				docRecieve2 = prepareFile(sRecieve1);
		sync.update();
		
		sync.openWriteFile(docSend, "a");
		sync.applyDiff(sync.getDiff(docSend), docRecieve); 
		
		sync.openWriteFile(docSend2, "a");
		sync.applyDiff(sync.getDiff(docSend2), docRecieve2);

		assertEquals(true, sync.openReadFile(docRecieve).equals("a"));
		assertEquals(true, sync.openReadFile(docRecieve2).equals("a"));
	}
	
	@Test
	public void testIgnoreList(){
		// Test for a simple file
		List<Path> ll = new LinkedList<Path>();
		Path p = prepareFile(sIgnoreFile1);
		ll.add(p);
		sync = new SynchronizeRoot(root, ll);
		
		sync.openWriteFile(p, "abc");
		assertEquals(true, sync.getDiff(p) == null);
		
		// Test for folder, if a folder is ignored everything inside it should also be ignored
		ll.remove(0);
		Path p2 = prepareFolder(sIgnoreFolder1);
		ll.add(p2);
		sync.setIgnoreList(ll);
		
		sync.openWriteFile(p, "abc");
		assertEquals(true, sync.getDiff(p) == null);
	}
	
	@Test
	// Can not automate a test for remove folder, to remove a folder from inside Java all files inside it needs 
	// to be removed first. If so can not guarantee that the folder remove is run before the files removes which 
	// would render the test moot. 
	public void testRemoveFile() throws IOException{
		Path p = prepareFile(sIgnoreFile1);
		Files.delete(p);
		sync.update();
		assertEquals(true, sync.getDiff(p) == null);
	}
	
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
		
		emptyFile(s);
		
		return p;
	}
	
	private Path prepareFolder(String s){
		// Create the file if it doesn't exist from a previous run of the program.
		File f = new File(s);
		f.mkdir();
		Path p;
		p = Paths.get(s);
		
		return p;
	}
	
	private void emptyFile(String s){
		// Empty the file in case of old data
		try {
			PrintWriter pw = new PrintWriter(s);
			pw.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
