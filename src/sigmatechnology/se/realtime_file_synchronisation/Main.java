package sigmatechnology.se.realtime_file_synchronisation;


import sigmatechnology.se.realtime_file_synchronisation.network.Client;
import sigmatechnology.se.realtime_file_synchronisation.network.Packets;
import sigmatechnology.se.realtime_file_synchronisation.network.Server;

public class Main {

	public static void main(String[] args) {
		Server server = new Server();
		Client client = new Client();
		
		client.send(Packets.ERROR);
	}
}
