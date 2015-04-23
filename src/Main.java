import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;

import diff_match_patch.SynchronizeRoot;
import diff_match_patch.fraser_neil.diff_match_patch.Diff;


public class Main {
	public static void main(String[] args) throws InterruptedException {
		Path doc = Paths.get("");
		Path testFileSend = Paths.get("send.txt");
		
		Path testFileRecieve = Paths.get("recieve.txt");
		SynchronizeRoot sync = new SynchronizeRoot(doc);
		
		while(true){
			Thread.sleep(2000);
			LinkedList<Diff> list = sync.getDiff(testFileSend);
			try{
				System.out.println("Difference in files: ");
				
				Iterator<Diff> it = list.iterator();
				while(it.hasNext()){
					Diff d = it.next();
					System.out.println(d.toString());
				}
				System.out.println();
				
				sync.applyDiff(list, testFileRecieve);
				
			}
			catch(IndexOutOfBoundsException e2){}
			catch(NullPointerException e2){
				System.out.println("Null");
			}
		}
	}
}
