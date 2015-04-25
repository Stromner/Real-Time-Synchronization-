package sigmatechnology.se.diff_match_patch;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class SynchronizeRootTest extends TestCase{
	private String sSend = "src/sigmatechnology/se/diff_match_patch/testSend1.txt",
				sRecieve = "src/sigmatechnology/se/diff_match_patch/testRecieve1.txt",
				sRoot = "";
	private Path docSend, docRecieve, root;
	private SynchronizeRoot sync;
	private static Boolean oneTimeSetUpDone = false;
	
	@Before
	public void setUp(){
		if(!oneTimeSetUpDone){
			root = Paths.get(sRoot);
			sync = new SynchronizeRoot(root, null);
			docSend = prepareFile(sSend);
			docRecieve = prepareFile(sRecieve);
		}
		
		sync.updateRoot();
		emptyFile(sSend);
		emptyFile(sRecieve);
	}
	
	//
	// Single File Tests
	//
	@Test
	public void testEmptyToOneChar() throws IOException {
		sync.openWriteFile(docSend, "a");
		sync.applyDiff(sync.getDiff(docSend), docRecieve);
		assertEquals(true, sync.openReadFile(docRecieve).equals("a"));
	}
	
	@Test
	public void testOneCharToEmpty() throws IOException {
		sync.openWriteFile(docSend, "a");
		sync.getDiff(docSend); // Update the base string
		
		sync.openWriteFile(docSend, "");
		sync.applyDiff(sync.getDiff(docSend), docRecieve);
		assertEquals(true, sync.openReadFile(docRecieve).equals(""));
	}
	
	@Test
	public void testOneRowToMany() throws IOException {
		sync.openWriteFile(docSend, "a");
		sync.openWriteFile(docRecieve, "a");
		sync.getDiff(docSend); // Update the base string
		
		sync.openWriteFile(docSend, "a\na");
		sync.applyDiff(sync.getDiff(docSend), docRecieve);
		assertEquals(true, sync.openReadFile(docRecieve).equals("a\na"));
	}
	
	@Test
	public void testManyRowToOne() throws IOException {
		sync.openWriteFile(docSend, "a\na");
		sync.openWriteFile(docRecieve, "a\na");
		sync.getDiff(docSend); // Update the base string
		
		sync.openWriteFile(docSend, "a");
		sync.applyDiff(sync.getDiff(docSend), docRecieve);
		assertEquals(true, sync.openReadFile(docRecieve).equals("a"));
	}
	
	@Test
	public void testManyRowsToLess() throws IOException {
		sync.openWriteFile(docSend, "a\nb\nc\nd");
		sync.openWriteFile(docRecieve, "a\nb\nc\nd");
		sync.getDiff(docSend); // Update the base string
		
		sync.openWriteFile(docSend, "a\nb\nd");
		sync.applyDiff(sync.getDiff(docSend), docRecieve);
		assertEquals(true, sync.openReadFile(docRecieve).equals("a\nb\nd"));
	}
	
	@Test
	public void testManyRowsToMore() throws IOException {
		sync.openWriteFile(docSend, "a\nb\nc\nd");
		sync.openWriteFile(docRecieve, "a\nb\nc\nd");
		sync.getDiff(docSend); // Update the base string
		
		sync.openWriteFile(docSend, "a\nb\nbb\nc\ncc\nd");
		sync.applyDiff(sync.getDiff(docSend), docRecieve);
		assertEquals(true, sync.openReadFile(docRecieve).equals("a\nb\nbb\nc\ncc\nd"));
	}
	
	@Test
	public void testDifferenceBetweenFiles() throws IOException {
		sync.openWriteFile(docSend, "abba\ndabba\ncabba\ndabba");
		sync.openWriteFile(docRecieve, "abba\ndabba\nMOLA\ndabba");
		sync.getDiff(docSend); // Update the base string
		
		sync.openWriteFile(docSend, "abba\ndabba\ncabba123\ndabba");
		sync.applyDiff(sync.getDiff(docSend), docRecieve);
		assertEquals(true, sync.openReadFile(docRecieve).equals("abba\ndabba\nMOLA123\ndabba"));
	}
	
	@Test
	public void testSwedishCharacters() throws IOException {
		sync.openWriteFile(docSend, "едц");
		sync.applyDiff(sync.getDiff(docSend), docRecieve);
		assertEquals(true, sync.openReadFile(docRecieve).equals("едц"));
	}
	
	@Test
	public void testReplaceDocument() throws IOException {
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
	public void testDocumentAsRoot() throws IOException{
		sync = new SynchronizeRoot(docSend, null);
		
		sync.openWriteFile(docSend, "a");
		sync.applyDiff(sync.getDiff(docSend), docRecieve);
		assertEquals(true, sync.openReadFile(docRecieve).equals("a"));
	}
	
	@Test
	public void testTwoDocumentsSync() throws IOException{
		// Create an additional two test documents
		Path docSend2 = prepareFile("src/sigmatechnology/se/diff_match_patch/testRecieve2.txt"),
				docRecieve2 = prepareFile("src/sigmatechnology/se/diff_match_patch/testSend2.txt");
		sync.updateRoot();
		
		sync.openWriteFile(docSend, "a");
		sync.applyDiff(sync.getDiff(docSend), docRecieve); 
		
		sync.openWriteFile(docSend2, "a");
		sync.applyDiff(sync.getDiff(docSend2), docRecieve2);

		assertEquals(true, sync.openReadFile(docRecieve).equals("a"));
		assertEquals(true, sync.openReadFile(docRecieve2).equals("a"));
	}
	
	@Test
	public void testIgnoreList() throws IOException{
		fail();
	}
	
	@Test
	public void testRemoveFile() throws IOException{
		fail();
	}
	
	private Path prepareFile(String s){
		// Create the file if it doesn't exist from a previous run of the program.
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
		// If the files exist from a previous run the root would already added them at
		// the start. However emptying the file does not empty the modified string.
		// Calculating the diff after emptying the file however would.
		sync.updateRoot();
		sync.getDiff(p);
		
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
