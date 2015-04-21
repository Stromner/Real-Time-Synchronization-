import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;

import diff_match_patch.Sync;
import diff_match_patch.fraser_neil.diff_match_patch.Diff;
import diff_match_patch.fraser_neil.diff_match_patch.Operation;


public class Main {
	public static void main(String[] args) throws InterruptedException {
		Path doc = Paths.get("testSend.txt");
		Path doc2 = Paths.get("testRecieve.txt");
		Sync sync;
		try {
			sync = new Sync(doc);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		while(true){
			Thread.sleep(2000);
			LinkedList<Diff> list = sync.getDiff();
			try{
				// If we can't fetch the first element then the document is empty, nothing to do.
				// If there is only one element and that element is equal then no modifications have
				// been made.
				if((list.get(0).operation == Operation.EQUAL && list.get(1) == null) || (list.get(0) == null)){
					continue;
				}
				System.out.println("Difference in files: ");
				
				Iterator<Diff> it = list.iterator();
				while(it.hasNext()){
					Diff d = it.next();
					System.out.println(d.toString());
				}
				System.out.println();
				
				try {
					sync.applyDiff(list, doc2);
				} catch (IOException e) {
					continue;
				}
				
			}
			catch(IndexOutOfBoundsException e2){}
			catch(NullPointerException e2){
				System.out.println("Null");
			}
		}
	}
}
