package sigmatechnology.se.realtime_file_synchronisation;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import sigmatechnology.se.realtime_file_synchronisation.diff_match_patch.SynchronizeRoot;
import sigmatechnology.se.realtime_file_synchronisation.network.ClientConnection;
import sigmatechnology.se.realtime_file_synchronisation.network.Packet;
import sigmatechnology.se.realtime_file_synchronisation.network.PacketType;
import sigmatechnology.se.realtime_file_synchronisation.network.Server;


public class Main {	
	public static void main(String[] args) throws InterruptedException {		
		// Create client
		//	Connect to the server
		//	Fetch user list
		//	Display user list
		// Wait for our user to select a user and press connect
		// Create connection with other user
		// Create SynchronizeRoot
		// Loop
		// 	Get diffs
		// 	1) From Eclipse
		// 	2) From other files
		// Send diffs
		// End Loop
		
		// Client receive thread
		// Wait for package
		// Handle package
		// 	If package of patch type
		//	Patch diffs
		// 	1) To Eclipse
		// 	2) To other files
		
		String data, data2;
		
		(new Thread(new Server())).start();
		Scanner scanInput = new Scanner(System.in);
		data = scanInput.nextLine();
		ClientConnection cc3 = new ClientConnection(data);
		data2 = scanInput.nextLine();
		ClientConnection cc32 = new ClientConnection(data2);
		scanInput.close();
		cc3.send(new Packet(PacketType.CHAT, data, data2, "TestMessage"));
		
		String sRepo1 = "src/sigmatechnology/se/realtime_file_synchronisation/TestRepo1/",
			sRepo2 = "src/sigmatechnology/se/realtime_file_synchronisation/TestRepo2/";
		Path repo1 = Paths.get(sRepo1);
		Path repo2 = Paths.get(sRepo2);
		
		SynchronizeRoot sync1 = new SynchronizeRoot(repo1, null);
		SynchronizeRoot sync2 = new SynchronizeRoot(repo2, null);
		
		while(true){
			Thread.sleep(1000);
			System.out.println("New diff cycle:");
			sync2.applyDiffs(sync1.getDiffs());
		}
	}
}
