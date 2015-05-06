package sigmatechnology.se.realtime_file_synchronisation.network;

/**
 * Server that takes care of request from the <code>Client<code> class. Reads
 * its listening port from the config.txt document.
 * 
 * @author Magnus Källtén
 * @author David Strömner
 * @version 2015-05-05
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import sigmatechnology.se.realtime_file_synchronisation.Util;

// TODO Implement all the receive methods
// TODO Implement all the send methods
// TODO Look over the safety measures

public class Server{
	private ServerSocket listenSocket;
	private Thread listenThread;
	private Map<String, ClientThread> userMap;
	private Set<ClientThread> threadSet;
	private String path = "src/sigmatechnology/se/realtime_file_synchronisation/config.txt";
	
	/**
	 * Start a new server
	 */
	public Server(){
		userMap = new ConcurrentHashMap<String, ClientThread>();
		threadSet = new CopyOnWriteArraySet<ClientThread>();
		
		init();
		// Listen is blocking so needs to run in own thread
		listenThread = new Thread(){
			public void run(){
				listen();
			}
		};
		listenThread.start();
	}
	
	/**
	 * Configure the server by reading the config.txt document to get its port and 
	 * start its listening thread.
	 */
	private void init(){
		String text = Util.openReadFile(Paths.get(path));
		String[] textSplitted = text.split("\n");
		
		// The row after the server config text is interesting for us
		for(int i=0;i<textSplitted.length;i++){
			if(textSplitted[i].toLowerCase().contains("server".toLowerCase())){
				String[] s = textSplitted[i+1].split(":");
				try {
					listenSocket = new ServerSocket(Integer.parseInt(s[0]));
				} catch (NumberFormatException | IOException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}
	
	/**
	 * Blocking call that listen for new users. When a new user connects a <code>ClientThread<code> 
	 * is created for it.
	 */
	private void listen(){
		while(true){
			try {
				Socket clientSocket = listenSocket.accept();
				System.out.println("New Client");
				// Create a new anonymous thread, will add itself to the map once its user got valid nick.
				ClientThread thread = new ClientThread(clientSocket);
				threadSet.add(thread);
				thread.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Thread class that takes care of talking with individual clients. 
	 * 
	 * @author Magnus Källtén
	 * @author David Strömner
	 * @version 2015-05-05
	 */
	private class ClientThread extends Thread{
		private ObjectOutputStream out;
		private ObjectInputStream in;
		private Socket socket;
		private Thread receiveThread;
		
		/**
		 * Initiate the new thread by creating an I/O stream for it.
		 * Got its own listening thread for requests from the client.
		 * @param socket to create the I/O stream from.
		 */
		public ClientThread(Socket socket){
			this.socket = socket;
			try {
				out = new ObjectOutputStream(socket.getOutputStream());
				in = new ObjectInputStream(socket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
				disconnect();
			}
			
			// Receive is blocking so needs to run in its own thread.
			receiveThread = new Thread(){
				public void run(){
					receive();
				}
			};
			receiveThread.start();
		}
		
		/**
		 * Send a message to the user, takes a dotted object array of extra argument.
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
		 * Blocking call that waits for a message from the client to appear.
		 */
		private void receive(){
			while(true){
				try {
					List<Object> argsList = (ArrayList<Object>) in.readObject();
					System.out.print("Message from Client: ");
					switch((Packets)argsList.get(0)){
						case NEWUSER:
							System.out.println("NewUser");
							// TODO Add user to the hashmap, inform all other threads(that is not me!) about the new user
							break;
						case DELTEUSER:
							System.out.println("DeleteUser");
							// TODO Remove user from the hashmap, inform all other threads(that is not me!) about the deleted user
							break;
						case CONNECT:
							System.out.println("Connect");
							// TODO Connect a user to another user with all the checks needed for it
							break;
						case SYNCFILE:
							System.out.println("SyncFile");
							// TODO Send a diff list to another user with all the checks needed for it
							break;
						case CHAT:
							System.out.println("Chat");
							// TODO Forward the chat message to the user we're connected to with all the checks needed for it
							break;
						case ERROR:
							System.out.println("Error");
							// TODO Expand? Might be errors that needed to be handled in different ways.
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
		 * Kill the threads serving a client elegantly.
		 */
		private void disconnect(){
			try {
				out.close();
				in.close();
				socket.close();
				Thread.currentThread().interrupt();
				// Remove us from the set and map(If we're in them)
				for (Map.Entry<String, ClientThread> entry : userMap.entrySet()){
					if(entry.getValue().getId() == getId()){
						userMap.remove(entry.getKey(), entry.getValue());
					}
				}
				
				for (ClientThread t : threadSet) {
					if(t.getId() == getId()){
						threadSet.remove(t);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}