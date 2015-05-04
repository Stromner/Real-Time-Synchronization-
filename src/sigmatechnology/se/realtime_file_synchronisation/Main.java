package sigmatechnology.se.realtime_file_synchronisation;


import java.util.Scanner;

import sigmatechnology.se.realtime_file_synchronisation.network.Client;
import sigmatechnology.se.realtime_file_synchronisation.network.Packets;
import sigmatechnology.se.realtime_file_synchronisation.network.Server;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String data, data2;
		
		(new Thread(new Server())).start();
		Scanner scanInput = new Scanner(System.in);
		data = scanInput.nextLine();
		Client cc3 = new Client(data);
		data2 = scanInput.nextLine();
		scanInput.close();
		cc3.send(Packets.CHAT, data, data2, "TestMessage");
		
	}
}
