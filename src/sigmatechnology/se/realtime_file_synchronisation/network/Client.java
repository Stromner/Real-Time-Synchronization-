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
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.Platform;

import sigmatechnology.se.realtime_file_synchronisation.Util;
import sigmatechnology.se.realtime_file_synchronisation.diff_match_patch.SynchronizeDocument;
import sigmatechnology.se.realtime_file_synchronisation.plugin.Controller;


// TODO Implement all the receive methods
// TODO Look over the safety measures

public class Client {
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private Thread receiveThread;
	private String path = Platform.getInstallLocation().getURL().toString().substring(6) + "plugins/sigmatechnology.se.realtime_file_synchronisation/config.txt";
	private String friend;
	private List<Object> lastPackage;
	
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
		send(Packets.CONNECTUSER, me, user);
		friend = user;
	}
	
	public Socket getSocket(){
		return socket;
	}
	
	public String getFriend(){
		return friend;
	}
	
	/**
	 * Send a message to the server, takes a dotted object array of extra argument.
	 * @param p type of message.
	 * @param args additional arguments that needs to be sent with the type.
	 */
	public Boolean send(Packets p, Object... args) {
		if(!socket.isClosed()){
			List<Object> argsList = new ArrayList<Object>();
			argsList.add(p);
			for (Object o : args) {
				argsList.add(o);
			}
			
			try {
				out.writeObject(argsList);
				out.flush();
			} catch (Exception e) {
				disconnect();
			}
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * @return last package that the server received in. Meant for testing only.
	 */
	public List<Object> getLastPackage(){
		return lastPackage;
	}
	
	/**
	 * Initiate a new client by reading the config.txt for IP and port.
	 * Opens a new I/O stream for the created socket. Starts a new 
	 * thread that listen for messages from the server.
	 */
	private void init(){
		String text = Util.openReadFile(Paths.get(path));
		String[] s = text.split(":");
		
		try {
			// Create a new socket with its I/O
			socket = new Socket(s[0], Integer.parseInt(s[1]));
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			
			// Listen is blocking so runs in own thread
			receiveThread = new Thread(){
				public void run(){
					receive();
				}
			};
			receiveThread.start();
		} catch (NumberFormatException | IOException e) {
			disconnect();
		}
	}
	
	/**
	 * Blocking call that waits for a message from the client to appear.
	 */
	private void receive(){
		while(!receiveThread.isInterrupted()){
			try {
				List<Object> argsList = (ArrayList<Object>) in.readObject();
				
				// Testing purpose
				lastPackage = argsList;
						
				switch((Packets)argsList.get(0)){
					case CONNECTSERVER:
						System.out.println("Client: Connect");
						Controller.getInstance().updateUserList(Packets.CONNECTSERVER, (String)argsList.get(1));
						break;
					case DISCONNECTSERVER:
						System.out.println("Client: Disconnect");
						Controller.getInstance().updateUserList(Packets.DISCONNECTSERVER, (String)argsList.get(1));
						break;
					case CONNECTUSER:
						System.out.println("Client: Connect to user");
						Controller.getInstance().userConnected((String)argsList.get(1), true);
						friend = (String) argsList.get(1);
						break;
					case DISCONNECTUSER:
						System.out.println("Client: Disconnect from user");
						Controller.getInstance().userConnected((String)argsList.get(1), false);
						friend = null;
						break;
					case SYNCFILE:
						System.out.println("Client: SyncFile");
						Controller.getInstance().getSynchronizeRoot().applyDiffs((LinkedList<SynchronizeDocument>) argsList.get(1));
						Controller.getInstance().getLauncher().updatePane("New sync");
						break;
					case CHAT:
						System.out.println("Client: Chat");
						Controller.getInstance().msgToGUI((String)argsList.get(1), (String)argsList.get(2));
						break;
					case ERROR:
						System.out.println("Client: Error: " + argsList.get(1));
						break;
					case OK:
						System.out.println("Client: Ok");
						break;
					default:
						break;
				}
			} catch (ClassNotFoundException | IOException e) {
				disconnect();
			}
		}
	}
	
	/**
	 * Close down the socket and its streams.
	 */
	public void disconnect(){
		try {
			receiveThread.interrupt();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}