package sigmatechnology.se.realtime_file_synchronisation;
import java.nio.file.Path;
import java.nio.file.Paths;

import sigmatechnology.se.realtime_file_synchronisation.diff_match_patch.SynchronizeRoot;

public class Main {	
	public static void main(String[] args) throws InterruptedException {		
		//Server server = new Server();
		//Client client = new Client();
		
		String sRepo1 = "src/sigmatechnology/se/realtime_file_synchronisation/TestRepo1/",
			sRepo2 = "src/sigmatechnology/se/realtime_file_synchronisation/TestRepo2/";
		Path repo1 = Paths.get(sRepo1);
		Path repo2 = Paths.get(sRepo2);
		
		SynchronizeRoot sync1 = new SynchronizeRoot(repo1, null);
		SynchronizeRoot sync2 = new SynchronizeRoot(repo2, null);
		
		while(true){
			Thread.sleep(500);
			System.out.println("New diff cycle:");
			sync2.applyDiffs(sync1.getDiffs());
		}
	}
}
