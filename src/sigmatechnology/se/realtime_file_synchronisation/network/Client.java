package sigmatechnology.se.realtime_file_synchronisation.network;

/**
 * Client that connects to <code>Server<code, get the IP and port of the server 
 * through the config.txt document.
 * 
 * @author Magnus Källtén
 * @author David Strömner
 * @version 2015-05-04
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import sigmatechnology.se.realtime_file_synchronisation.Util;


// TODO Implement all the receive methods
// TODO Look over the safety measures

public class Client {
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private Thread receiveThread;
	private String path = "src/sigmatechnology/se/realtime_file_synchronisation/config.txt";
	
	/**
	 * Initiate a new client.
	 */
	public Client(){
		init();
	}
	
	/**
	 * Connect to a user
	 * @param me nickname that this client is using.
	 * @param user nickname to the client we're trying to connect to.
	 */
	public void connectToUser(String me, String user){
		send(Packets.CONNECT, me, user);
	}
	
	/**
	 * Send a message to the server, takes a dotted object array of extra argument.
	 * @param p type of message.
	 * @param args additional arguments that needs to be sent with the type.
	 */
	public void send(Packets p, Object... args) {
		List<Object> argsList = new ArrayList<Object>();
		argsList.add(p);
		for (Object o : args) {
			argsList.add(o);
		}
		
		try {
			out.writeObject(argsList);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
			disconnect();
		}
	}
	
	/**
	 * Initiate a new client by reading the config.txt for IP and port.
	 * Opens a new I/O stream for the created socket. Starts a new 
	 * thread that listen for messages from the server.
	 */
	private void init(){
		String text = Util.openReadFile(Paths.get(path));
		String[] textSplitted = text.split("\n");
		
		// The row after the client config text is interesting for us
		for(int i=0;i<textSplitted.length;i++){
			if(textSplitted[i].toLowerCase().contains("Client".toLowerCase())){
				String[] s = textSplitted[i+1].split(":");
				try {
					socket = new Socket(s[0], Integer.parseInt(s[1]));
					out = new ObjectOutputStream(socket.getOutputStream());
					in = new ObjectInputStream(socket.getInputStream());
					
					receiveThread = new Thread(){
						public void run(){
							receive();
						}
					};
					receiveThread.start();
				} catch (NumberFormatException | IOException e) {
					e.printStackTrace();
					disconnect();
				}
				break;
			}
		}
	}
	
	/**
	 * Blocking call that waits for a message from the client to appear.
	 */
	private void receive(){
		while(true){
			try {
				List<Object> argsList = (ArrayList<Object>) in.readObject();
				switch((Packets)argsList.get(0)){
					case NEWUSER:
						System.out.println("NewUser");
						break;
					case DELTEUSER:
						System.out.println("DeleteUser");
						break;
					case CONNECT:
						System.out.println("Connect");
						break;
					case SYNCFILE:
						System.out.println("SyncFile");
						break;
					case CHAT:
						System.out.println("Chat");
						break;
					case ERROR:
						System.out.println("Error");
						break;
					default:
						break;
					
				}
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
				disconnect();
			}
		}
	}
	
	/**
	 * Close down the socket and its streams.
	 */
	private void disconnect(){
		try {
			out.close();
			in.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}