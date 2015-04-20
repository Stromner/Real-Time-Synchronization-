import java.util.Iterator;
import java.util.LinkedList;

import GoogleDiffMatchPatch.diff_match_patch.Diff;


public class Main {
	public static void main(String[] args) {
		String doc = "test.txt";
		String doc2 = "testRecieve.txt";
		Sync sync = new Sync(doc);
		
		while(true){
			LinkedList<Diff> list = sync.getDiff();
			try{
				list.get(1); // If we can fetch the second element we know that the two documents aren't identical.
				System.out.println("Difference in files: ");
				
				Iterator<Diff> it = list.iterator();
				while(it.hasNext()){
					Diff d = it.next();
					System.out.println(d.toString());
				}
				System.out.println();
				
				sync.applyDiff(list, doc2);
			}
			catch(IndexOutOfBoundsException e){
			}
		}
	}
}
