import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;

import diff_match_patch.Sync;
import diff_match_patch.fraser_neil.diff_match_patch.Diff;


public class Main {
	public static void main(String[] args) throws InterruptedException {
		Path doc = Paths.get("send.txt");
		Path doc2 = Paths.get("recieve.txt");
		Sync sync;
		sync = new Sync(doc);
		
		while(true){
			Thread.sleep(2000);
			LinkedList<Diff> list = sync.getDiff();
			try{
				System.out.println("Difference in files: ");
				
				Iterator<Diff> it = list.iterator();
				while(it.hasNext()){
					Diff d = it.next();
					System.out.println(d.toString());
				}
				System.out.println();
				
				sync.applyDiff(list, doc2);
				
			}
			catch(IndexOutOfBoundsException e2){}
			catch(NullPointerException e2){
				System.out.println("Null");
			}
		}
	}
}
