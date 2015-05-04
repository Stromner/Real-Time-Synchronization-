package sigmatechnology.se.realtime_file_synchronisation.diff_match_patch;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class SyncTest extends TestCase{
	private String sSend = "./src/diff_match_patch/testSend.txt",
				sRecieve = "./src/diff_match_patch/testRecieve.txt";
	private Path docSend = Paths.get(sSend);
	private Path docRecieve = Paths.get(sRecieve);
	private Sync sync;
	private static Boolean oneTimeSetUpDone = false;
	
	@Before
	public void setUp(){
		if(!oneTimeSetUpDone){
			// Create the two test documents if they don't exist
			File send = new File(sSend), recieve = new File(sRecieve);
			try {
				if(!send.exists()){	
					send.createNewFile();
				} 
				if(!recieve.exists()){
					recieve.createNewFile();
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			
			docSend = Paths.get(sSend);
			docRecieve = Paths.get(sRecieve);
			
			oneTimeSetUpDone = true;
		}
		
		
		// Empty the two text documents in case of old data
		try {
			PrintWriter pw = new PrintWriter(sSend);
			pw.close();
			pw = new PrintWriter(sRecieve);
			pw.close();
			
			sync = new Sync(docSend);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testEmptyToOneChar() throws IOException {
		sync.openWriteFile(docSend, "a");
		sync.applyDiff(sync.getDiff(), docRecieve);
		assertEquals(true, sync.openReadFile(docRecieve).equals("a"));
	}
	
	@Test
	public void testOneCharToEmpty() throws IOException {
		sync.openWriteFile(docSend, "a");
		sync.getDiff(); // Update the base string
		
		sync.openWriteFile(docSend, "");
		sync.applyDiff(sync.getDiff(), docRecieve);
		assertEquals(true, sync.openReadFile(docRecieve).equals(""));
	}
	
	@Test
	public void testOneRowToMany() throws IOException {
		sync.openWriteFile(docSend, "a");
		sync.openWriteFile(docRecieve, "a");
		sync.getDiff(); // Update the base string
		
		sync.openWriteFile(docSend, "a\na");
		sync.applyDiff(sync.getDiff(), docRecieve);
		assertEquals(true, sync.openReadFile(docRecieve).equals("a\na"));
	}
	
	@Test
	public void testManyRowToOne() throws IOException {
		sync.openWriteFile(docSend, "a\na");
		sync.openWriteFile(docRecieve, "a\na");
		sync.getDiff(); // Update the base string
		
		sync.openWriteFile(docSend, "a");
		sync.applyDiff(sync.getDiff(), docRecieve);
		assertEquals(true, sync.openReadFile(docRecieve).equals("a"));
	}
	
	@Test
	public void testManyRowsToLess() throws IOException {
		sync.openWriteFile(docSend, "a\nb\nc\nd");
		sync.openWriteFile(docRecieve, "a\nb\nc\nd");
		sync.getDiff(); // Update the base string
		
		sync.openWriteFile(docSend, "a\nb\nd");
		sync.applyDiff(sync.getDiff(), docRecieve);
		assertEquals(true, sync.openReadFile(docRecieve).equals("a\nb\nd"));
	}
	
	@Test
	public void testManyRowsToMore() throws IOException {
		sync.openWriteFile(docSend, "a\nb\nc\nd");
		sync.openWriteFile(docRecieve, "a\nb\nc\nd");
		sync.getDiff(); // Update the base string
		
		sync.openWriteFile(docSend, "a\nb\nbb\nc\ncc\nd");
		sync.applyDiff(sync.getDiff(), docRecieve);
		assertEquals(true, sync.openReadFile(docRecieve).equals("a\nb\nbb\nc\ncc\nd"));
	}
	
	@Test
	public void testDifferenceBetweenFiles() throws IOException {
		sync.openWriteFile(docSend, "abba\ndabba\ncabba\ndabba");
		sync.openWriteFile(docRecieve, "abba\ndabba\nMOLA\ndabba");
		sync.getDiff(); // Update the base string
		
		sync.openWriteFile(docSend, "abba\ndabba\ncabba123\ndabba");
		sync.applyDiff(sync.getDiff(), docRecieve);
		assertEquals(true, sync.openReadFile(docRecieve).equals("abba\ndabba\nMOLA123\ndabba"));
	}
	
	@Test
	public void testSwedishCharacters() throws IOException {
		sync.openWriteFile(docSend, "едц");
		sync.applyDiff(sync.getDiff(), docRecieve);
		assertEquals(true, sync.openReadFile(docRecieve).equals("едц"));
	}
	
	@Test
	public void testReplaceDocument() throws IOException {
		sync.openWriteFile(docSend, "One day I went to the forrest\nto pick some flowers.");
		sync.openWriteFile(docRecieve, "One day I went to the forrest\nto pick some flowers.");
		sync.getDiff(); // Update the base string
		
		sync.openWriteFile(docSend, "I cut hair for a living.");
		sync.applyDiff(sync.getDiff(), docRecieve);
		assertEquals(true, sync.openReadFile(docRecieve).equals("I cut hair for a living."));
	}
	
	// Test something that we want to fail
}
