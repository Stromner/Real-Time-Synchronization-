import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
import java.util.LinkedList;

import GoogleDiffMatchPatch.diff_match_patch;
import GoogleDiffMatchPatch.diff_match_patch.Diff;
import GoogleDiffMatchPatch.diff_match_patch.Patch;

public class Sync {
	private diff_match_patch dmp;
	private String modDoc, baseString;
	
	public Sync(String doc){
		modDoc = doc;
		dmp = new diff_match_patch();
		FileChannel fc = openFile(modDoc);
		baseString = readFile(fc);
		closeFile(fc);
	}
	
	/**
	 * 
	 * @return
	 */
	public LinkedList<Diff> getDiff(){		
		FileChannel fc = openFile(modDoc);
		String modifiedString;
		modifiedString = readFile(fc);
		closeFile(fc);
		
		System.out.println("Mod: " + modifiedString + "\tBase: " + baseString);
		
		LinkedList<Diff> list = dmp.diff_main(baseString, modifiedString, true);
		baseString = modifiedString;
		
		return list;
	}
	
	/**
	 * 
	 * @param diff
	 * @return
	 */
	public void applyDiff(LinkedList<Diff> diffList, String doc){
		LinkedList<Patch> patchList = dmp.patch_make(diffList);
		FileChannel fc = openFile(doc);
		
		Object o[] = dmp.patch_apply(patchList, readFile(fc));
		String s = (String)o[0];
		
		writeFile(fc, s);
		closeFile(fc);
	}
	
	// http://stackoverflow.com/questions/128038/how-can-i-lock-a-file-using-java-if-possible
	private FileChannel openFile(String fileName){
		try {
			FileChannel fc = new RandomAccessFile(new File(fileName), "rw").getChannel();
			return fc;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	// TODO Change so it read entire documents not just 1024 bytes
	private String readFile(FileChannel fc){
		ByteBuffer buf = ByteBuffer.allocate(1024);
		try {
			FileLock fl = fc.lock();
			fc.read(buf);
			String s = new String(buf.array(), Charset.forName("UTF-8"));
			fl.release();
			
			return s;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void writeFile(FileChannel fc, String data){
		byte[] b = data.getBytes(Charset.forName("UTF-8"));
		ByteBuffer buf = ByteBuffer.wrap(b);
		try {
			FileLock fl = fc.lock();
			fc.write(buf);
			fl.release();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void closeFile(FileChannel fc){
		try {
			fc.lock().release();
			fc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}