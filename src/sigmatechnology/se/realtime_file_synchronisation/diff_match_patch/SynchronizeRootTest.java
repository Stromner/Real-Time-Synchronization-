package sigmatechnology.se.realtime_file_synchronisation.diff_match_patch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.ui.PartInitException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sigmatechnology.se.realtime_file_synchronisation.Util;

public class SynchronizeRootTest{
	private static final String sRepo1 = "src/sigmatechnology/se/realtime_file_synchronisation/TestRepo1/",
				sRepo2 = "src/sigmatechnology/se/realtime_file_synchronisation/TestRepo2/",
				sIgnoreFolder1 = "src/sigmatechnology/se/realtime_file_synchronisation/TestRepo1/Ignore",
				sIgnoreFolder2 = "src/sigmatechnology/se/realtime_file_synchronisation/TestRepo2/Ignore",
				sIgnoreFile1 = "src/sigmatechnology/se/realtime_file_synchronisation/TestRepo1/Ignore/test.txt",
				sIgnoreFile2 = "src/sigmatechnology/se/realtime_file_synchronisation/TestRepo2/Ignore/test.txt",
				sSend1 = "src/sigmatechnology/se/realtime_file_synchronisation/TestRepo1/send.txt",
				sSend2 = "src/sigmatechnology/se/realtime_file_synchronisation/TestRepo2/send.txt",
				sRecieve1 = "src/sigmatechnology/se/realtime_file_synchronisation/TestRepo1/recieve.txt",
				sRecieve2 = "src/sigmatechnology/se/realtime_file_synchronisation/TestRepo2/recieve.txt";
	private static Path docSend1, docSend2, repo1, repo2;
	private SynchronizeRoot syncRepo1, syncRepo2;
	
	@BeforeClass
	public static void oneTimeSetUp(){
		repo1 = prepareFolder(sRepo1);
		repo2 = prepareFolder(sRepo2);
		prepareFolder(sIgnoreFolder1);
		prepareFolder(sIgnoreFolder2);
		prepareFile(sIgnoreFile1);
		prepareFile(sIgnoreFile2);
		prepareFile(sRecieve1);
		prepareFile(sRecieve2);
		docSend1 = prepareFile(sSend1);
		docSend2 = prepareFile(sSend2);
	}
	
	@Before
	public void setUp(){
		syncRepo1 = new SynchronizeRoot(repo1, null);
		syncRepo2 = new SynchronizeRoot(repo2, null);
		
		emptyFile(sSend1);
		emptyFile(sSend2);
	}
	
	//
	// Single File Tests without Eclipse
	//
	@Test
	public void testEmptyToOneChar() throws InterruptedException{
		System.out.println("\t\t--- testEmptyToOneChar ---");
		Util.openWriteFile(docSend1, "a");
		syncRepo2.applyDiffs(syncRepo1.getDiffs());
		assertTrue(Util.openReadFile(docSend2).equals("a"));
	}
	
	@Test
	public void testOneCharToEmpty(){
		System.out.println("\t\t--- testOneCharToEmpty ---");
		Util.openWriteFile(docSend1, "a");
		syncRepo1.getDiffs(); // Update the base string
		
		Util.openWriteFile(docSend1, "");
		syncRepo2.applyDiffs(syncRepo1.getDiffs());
		assertEquals(true, Util.openReadFile(docSend2).equals(""));
	}
	
	@Test
	public void testOneRowToMany(){
		System.out.println("\t\t--- testOneRowToMany ---");
		Util.openWriteFile(docSend1, "a");
		Util.openWriteFile(docSend2, "a");
		syncRepo1.getDiffs(); // Update the base string
		syncRepo2.getDiffs(); // Update the base string
		
		Util.openWriteFile(docSend1, "a\na");
		syncRepo2.applyDiffs(syncRepo1.getDiffs());
		assertEquals(true, Util.openReadFile(docSend2).equals("a\na"));
	}
	
	@Test
	public void testManyRowToOne(){
		System.out.println("\t\t--- testManyRowToOne ---");
		Util.openWriteFile(docSend1, "a\na");
		Util.openWriteFile(docSend2, "a\na");
		syncRepo1.getDiffs(); // Update the base string
		syncRepo2.getDiffs(); // Update the base string
		
		Util.openWriteFile(docSend1, "a");
		syncRepo2.applyDiffs(syncRepo1.getDiffs());
		assertEquals(true, Util.openReadFile(docSend2).equals("a"));
	}
	
	@Test
	public void testManyRowsToLess(){
		System.out.println("\t\t--- testManyRowsToLess ---");
		Util.openWriteFile(docSend1, "a\nb\nc\nd");
		Util.openWriteFile(docSend2, "a\nb\nc\nd");
		syncRepo1.getDiffs(); // Update the base string
		syncRepo2.getDiffs(); // Update the base string
		
		Util.openWriteFile(docSend1, "a\nb\nd");
		syncRepo2.applyDiffs(syncRepo1.getDiffs());
		assertEquals(true, Util.openReadFile(docSend2).equals("a\nb\nd"));
	}
	
	@Test
	public void testManyRowsToMore(){
		System.out.println("\t\t--- testManyRowsToMore ---");
		Util.openWriteFile(docSend1, "a\nb\nc\nd");
		Util.openWriteFile(docSend2, "a\nb\nc\nd");
		syncRepo1.getDiffs(); // Update the base string
		syncRepo2.getDiffs(); // Update the base string
		
		Util.openWriteFile(docSend1, "a\nb\nbb\nc\ncc\nd");
		syncRepo2.applyDiffs(syncRepo1.getDiffs());
		assertEquals(true, Util.openReadFile(docSend2).equals("a\nb\nbb\nc\ncc\nd"));
	}
	
	@Test
	public void testDifferenceBetweenFiles(){
		System.out.println("\t\t--- testDifferenceBetweenFiles ---");
		Util.openWriteFile(docSend1, "abba\ndabba\ncabba\ndabba");
		Util.openWriteFile(docSend2, "abba\ndabba\nMOLA\ndabba");
		syncRepo1.getDiffs(); // Update the base string
		syncRepo2.getDiffs(); // Update the base string
		
		Util.openWriteFile(docSend1, "abba\ndabba\ncabba123\ndabba");
		syncRepo2.applyDiffs(syncRepo1.getDiffs());
		assertEquals(true, Util.openReadFile(docSend2).equals("abba\ndabba\nMOLA123\ndabba"));
	}
	
	@Test
	public void testSwedishCharacters(){
		System.out.println("\t\t--- testSwedishCharacters ---");
		Util.openWriteFile(docSend1, "едц");
		syncRepo2.applyDiffs(syncRepo1.getDiffs());
		assertEquals(true, Util.openReadFile(docSend2).equals("едц"));
	}
	
	@Test
	public void testReplaceDocument(){
		System.out.println("\t\t--- testReplaceDocument ---");
		Util.openWriteFile(docSend1, "One day I went to the forrest\nto pick some flowers.");
		Util.openWriteFile(docSend2, "One day I went to the forrest\nto pick some flowers.");
		syncRepo1.getDiffs(); // Update the base string
		syncRepo2.getDiffs(); // Update the base string
		
		Util.openWriteFile(docSend1, "I cut hair for a living.");
		syncRepo2.applyDiffs(syncRepo1.getDiffs());
		assertEquals(true, Util.openReadFile(docSend2).equals("I cut hair for a living."));
	}
	
	//
	// Single File Tests with Eclipse
	//
	
	@Test
	public void testDocumentSync() throws PartInitException{
		System.out.println("\t\t--- testDocumentSync ---");
		// Open an existing file in the editor
		//IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		//org.eclipse.core.runtime.Path path = new org.eclipse.core.runtime.Path("C:/Users/David/Desktop/Test/src/sigmatechnology/se/realtime_file_synchronisation/TestRepo1/"+sSend1); // Conflicting import, must use full package name
		//IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		//IDE.openEditor(page, file, true);
		fail();
	}
	
	@Test
	public void testCursorPosition(){
		System.out.println("\t\t--- testCursorPosition ---");
		fail();
	}
	
	//
	// Root Folder Test
	//
	
	@Test
	public void testDocumentAsRoot(){
		System.out.println("\t\t--- testDocumentAsRoot ---");
		SynchronizeRoot sync1 = new SynchronizeRoot(docSend1, null);
		SynchronizeRoot sync2 = new SynchronizeRoot(docSend2, null);
		
		Util.openWriteFile(docSend1, "a");
		sync2.applyDiffs(sync1.getDiffs());
		assertEquals(true, Util.openReadFile(docSend2).equals("a"));
	}
	
	@Test
	public void testTwoDocumentsSync(){
		System.out.println("\t\t--- testTwoDocumentsSync ---");
		// Fetch the path for the two test documents extra text documents created
		// at the start.
		Path docRecieve1 = prepareFile(sRecieve1),
				docRecieve2 = prepareFile(sRecieve2);
		
		Util.openWriteFile(docSend1, "a");
		syncRepo2.applyDiffs(syncRepo1.getDiffs()); 
		
		Util.openWriteFile(docRecieve1, "a");
		syncRepo2.applyDiffs(syncRepo1.getDiffs());

		assertEquals(true, Util.openReadFile(docSend2).equals("a"));
		assertEquals(true, Util.openReadFile(docRecieve2).equals("a"));
	}
	
	@Test
	public void testIgnoreList(){
		System.out.println("\t\t--- testIgnoreList ---");
		// Test for a simple file
		List<Path> ll = new LinkedList<Path>();
		Path p = prepareFile(sIgnoreFile1);
		ll.add(p);
		SynchronizeRoot sync = new SynchronizeRoot(repo1, ll);
		
		LinkedList<SynchronizeDocument> l = sync.getDiffs();
		for(int i = 0;i<l.size();i++){
			assertEquals(true, repo1.resolve(l.get(i).getPath()).compareTo(Paths.get(sIgnoreFile1)) != 0);
		}
		
		// Test for folder, if a folder is ignored everything inside it should also be ignored
		ll.remove(0);
		Path p2 = prepareFolder(sIgnoreFolder1);
		ll.add(p2);
		sync.setIgnoreList(ll);
		
		l = sync.getDiffs();
		for(int i = 0;i<l.size();i++){
			assertEquals(true, repo1.resolve(l.get(i).getPath()).compareTo(Paths.get(sIgnoreFile1)) != 0);
		}
	}
	
	private static Path prepareFile(String s){
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
	
	private static Path prepareFolder(String s){
		// Create the file if it doesn't exist from a previous run of the program.
		File f = new File(s);
		f.mkdir();
		Path p;
		p = Paths.get(s);
		
		return p;
	}
	
	private static void emptyFile(String s){
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
