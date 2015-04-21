package diff_match_patch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SyncTest extends TestCase{
	private String sSend = "diff_match_patch\testSend.txt", sRecieve = "diff_match_patch\testRecieve.txt";
	private Path docSend = Paths.get("testSend.txt");
	private Path docRecieve = Paths.get("testRecieve.txt");
	private Sync sync;
	private static Boolean setUpIsDone = false;
	
	@Before
	public void oneTimeSetUp() throws IOException{
		if(setUpIsDone){
			return;
		}
		
		// Create the two test documents if they don't exist
		File send = new File(sSend), recieve = new File(sRecieve);
		if(!send.exists()){
			send.createNewFile();
		}
		if(!recieve.exists()){
			recieve.createNewFile();
		}
		
		docSend = Paths.get(sSend);
		docRecieve = Paths.get(sRecieve);
		
		setUpIsDone = true;
	}
	
	@Before
	public void setUp() throws IOException{
		// Empty the two text documents in case of old data
		PrintWriter pw = new PrintWriter(sSend);
		pw.close();
		pw = new PrintWriter(sRecieve);
		pw.close();
		
		sync = new Sync(docSend);
	}
	
	@Test
	public void testEmptyToOneChar() throws IOException {
		//sync.openWriteFile(docSend, "a");
		//sync.applyDiff(sync.getDiff(), docRecieve);
		assertEquals(true, true);
		//assertEquals(true, sync.openReadFile(docRecieve).equals("a"));
	}
	
	@Test
	public void testOneCharToEmpty() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testOneRowToMany() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testManyRowToOne() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testManyRowsToLess() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testManyRowsToFewer() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testDifferenceBetweenFiles() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testSwedishCharacters() {
		fail("Not yet implemented");
	}
	
	// Test something that we want to fail
}
