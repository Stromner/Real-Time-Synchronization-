package sigmatechnology.se.realtime_file_synchronisation.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientConnection {
	Socket socket;
	String serverIP = "127.0.0.1";
	int port = 20001;
	String nickname;
	ClientReceiver cr;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	
	public ClientConnection(String nickname){
		try {
			socket = new Socket(serverIP, port);
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush(); //Needs to happen before the InputStream server side
			ois = new ObjectInputStream(socket.getInputStream());
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		this.nickname = nickname;
		//Start listening/waiting for packets on the socket
		cr = new ClientReceiver(socket, ois);
		cr.start();
		//TestSend
		send(new Packet(PacketType.REGISTER, nickname));
		//send(new Packet(PacketType.CHAT, "Magnus", "message"));
	}

	/**
	 * Sends packet via socket to the server
	 * @param packet
	 */
	public void send(Packet packet){
		try{
			System.out.println("Before flush: Attempt to send from CC");
			oos.writeObject(packet);
			oos.flush();
			System.out.println("After flush: Attempt to send from CC");
		}
		catch(IOException e){}
	} 
}