package sigmatechnology.se.realtime_file_synchronisation;


import java.util.Scanner;

import sigmatechnology.se.realtime_file_synchronisation.network.ClientConnection;
import sigmatechnology.se.realtime_file_synchronisation.network.Packet;
import sigmatechnology.se.realtime_file_synchronisation.network.PacketType;
import sigmatechnology.se.realtime_file_synchronisation.network.Server;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String data, data2;
		
		(new Thread(new Server())).start();
		Scanner scanInput = new Scanner(System.in);
		data = scanInput.nextLine();
		ClientConnection cc3 = new ClientConnection(data);
		data2 = scanInput.nextLine();
		ClientConnection cc32 = new ClientConnection(data2);
		scanInput.close();
		cc3.send(new Packet(PacketType.CHAT, data, data2, "TestMessage"));
		
	}
}
