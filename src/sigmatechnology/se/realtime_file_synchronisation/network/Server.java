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
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
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
	Map<String, ClientThread> userMap;
	Set<ClientThread> threadSet;
	private String path = "src/sigmatechnology/se/realtime_file_synchronisation/config.txt";
	
	/**
	 * Start a new server
	 */
	public Server(){
		userMap = new ConcurrentHashMap<String, ClientThread>();
		threadSet = new CopyOnWriteArraySet<ClientThread>();
		
		if(!init()){
			return;
		}
		// Listen is blocking so needs to run in own thread
		listenThread = new Thread(){
			public void run(){
				listen();
			}
		};
		listenThread.start();
	}
	
	/**
	 * @return the internal map between nick names and their thread. Meant for testing only.
	 */
	public Map getMap(){
		return userMap;
	}
	
	/**
	 * Configure the server by reading the config.txt document to get its port and 
	 * start its listening thread.
	 */
	private Boolean init(){
		String text = Util.openReadFile(Paths.get(path));
		String[] s = text.split(":");
		try {
			listenSocket = new ServerSocket(Integer.parseInt(s[1]));
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Blocking call that listen for new users. When a new user connects a <code>ClientThread<code> 
	 * is created for it.
	 */
	private void listen(){
		while(!listenThread.isInterrupted()){
			try {
				Socket clientSocket = listenSocket.accept();
				// Create a new anonymous thread, will add itself to the map once its user got valid nick.
				ClientThread thread = new ClientThread(clientSocket, this);
				threadSet.add(thread);
				thread.start();
			} catch (IOException e) {
				e.printStackTrace();
				listenThread.interrupt();
			}
		}
	}
}