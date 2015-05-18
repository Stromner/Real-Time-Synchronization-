package sigmatechnology.se.realtime_file_synchronisation.network;

/**
 * 
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class ClientThread extends Thread{
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private String userName;
	private Server server;
	private ClientThread threadFriend;
	private List<Object> lastPackage;
	private ReentrantLock lock;
	
	/**
	 * 
	 * @param socket
	 * @param server
	 */
	public ClientThread(Socket socket, Server server){
		this.socket = socket;
		this.server = server;
		lock = new ReentrantLock();
		
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			disconnect();
		}
		
	}
	
	/**
	 * 
	 */
	public void disconnect(){
		interrupt();
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Remove us from the set and map(If we're in them)
		for (Map.Entry<String, ClientThread> entry : server.userMap.entrySet()){
			if(entry.getKey() == userName){
				server.userMap.remove(userName);
			}
		}
		
		for (ClientThread t : server.threadSet) {
			if(t.getId() == getId()){
				server.threadSet.remove(t);
			}
		}
	}
	
	/**
	 * 
	 * @param p
	 * @param args
	 */
	public void send(Packets p, Object... args) {
		lock.lock();
		try{
			List<Object> argsList = new ArrayList<Object>();			
			argsList.add(p);
			for (Object o : args) {
				argsList.add(o);
			}
			
			try {
				out.writeObject(argsList);
				out.flush();
			} catch (Exception e) {
				System.out.println("ClientThread[" + userName +"]: send failed, tried to send packet: " + p);
				disconnect();
			}
		} finally{
			lock.unlock();
		}
	}
	
	/**
	 * 
	 */
	public List<Object> getLastPackage(){
		return lastPackage;
	}
	
	/**
	 * 
	 * @param s
	 */
	public void setFriend(ClientThread s){
		threadFriend = s;
	}
	
	/**
	 * 
	 */
	public void run(){
		receive();
	}
	
	/**
	 * 
	 */
	private void receive(){
		while(!isInterrupted()){
			try {
				if(in.available() != -1){
					List<Object> argsList = (ArrayList<Object>) in.readObject();
					lastPackage = argsList;
					
					// Testing purpose
					lastPackage = argsList;
					
					switch((Packets)argsList.get(0)){
						case CONNECTSERVER:
							newUser(argsList);
							break;
						case DISCONNECTSERVER:
							removeUser(argsList);
							break;
						case CONNECTUSER:
							connectUsers(argsList);
							break;
						case DISCONNECTUSER:
							disconnectUsers(argsList);
							break;
						case SYNCFILE:
							System.out.println("ClientThread: SyncFile");
							synchronizeFileWithUser(argsList);
							break;
						case CHAT:
							System.out.println("ClienThread: Chat");
							chatMessageToUser(argsList);
							break;
						case ERROR:
							System.out.println("ClientThread: Error");
							// TODO Expand? Might be errors that needed to be handled in different ways.
							break;
						default:
							break;
						
					}
				}
			} catch (ClassNotFoundException | IOException e) {
				System.out.println("ClientThread[" + userName +"]: receive failed");
				disconnect();
			}
		}
	}
	
	private void newUser(List<Object> argsList){
		System.out.println("ClientThread: Connect");
		userName = (String)argsList.get(1);
		if(server.userMap.get(userName) == null){
			send(Packets.OK);
			
			server.userMap.put(userName, this);
			
			// Inform all other users that are not us
			Iterator<?> it = server.userMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<?,?> pair = (Map.Entry<?,?>)it.next();
				ClientThread ct = (ClientThread)pair.getValue();
				if(ct.userName != userName){
					ct.send(Packets.CONNECTSERVER, userName);
				}
			}
		}
		else{
			System.out.println("ClientThread[" + userName +"]: user already on the server.");
			send(Packets.ERROR, "User " + userName + " already exists on the server.");
		}
	}
	
	private void removeUser(List<Object> argsList){
		System.out.println("ClientThread: Disconnect");
		if(userName != null && server.userMap.get(userName) != null){
			// Inform all other users that are not us
			Iterator<?> it = server.userMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<?,?> pair = (Map.Entry<?,?>)it.next();
				ClientThread ct = (ClientThread)pair.getValue();
				if(ct.userName != userName){
					ct.send(Packets.DISCONNECTSERVER, userName);
				}
			}

			System.out.println("ClientThread[" + userName +"]: User " + userName + " disconnected.");
			disconnect();
			return;
		}
		else{
			System.out.println("ClientThread[" + userName +"]: Could not delete user");
			send(Packets.ERROR, "User " + userName + " can not be found on the server.s");
		}
	}
	
	private void connectUsers(List<Object> argsList){
		System.out.println("ClientThread: Connect to user");
		if(server.userMap.get(userName) == null){
			send(Packets.ERROR, "You are not connected to the server");
			return;
		}
		if(threadFriend != null){
			send(Packets.ERROR, "You are already connected to " + threadFriend.userName);
			return;
		}		
		threadFriend = server.userMap.get(argsList.get(1));
		if(threadFriend == null){
			send(Packets.ERROR, "User " + argsList.get(1) + " can not be found. Can not connect you too it");
			return;
		}
		
		// Inform the other users that we connected to it.
		threadFriend.setFriend(this);
		threadFriend.send(Packets.CONNECTUSER, userName);
		
		// Inform the user that it went ok
	    send(Packets.OK);
	    if(isInterrupted()){
	    	return;
	    }
		// TODO Add checks with belonging data for checking that all documents are equal.
	}
	
	private void disconnectUsers(List<Object> argsList){
		System.out.println("ClientThread: Disconnect from user");
		if(server.userMap.get(userName) == null){
			send(Packets.ERROR, "You are not connected to the server");
			return;
		}
		if(threadFriend == null){
			send(Packets.ERROR, "Not conected to any user. Can not disconnect.");
			return;
		}
		
		threadFriend.send(Packets.DISCONNECTUSER, userName);
		threadFriend.setFriend(null);
		threadFriend = null; // Goodbye my friend :´(
		// Inform the user that it went ok
		send(Packets.OK);
	}
	
	private void synchronizeFileWithUser(List<Object> argsList){
		if(server.userMap.get(userName) == null){
			send(Packets.ERROR, "You are not connected to the server");
			return;
		}
		if(threadFriend == null){
			send(Packets.ERROR, "Not connected to another user. Can not send diffs");
			return;
		}
		
		threadFriend.send(Packets.SYNCFILE, argsList.get(1));
		// Inform the user that it went ok
		send(Packets.OK);
	}
	
	private void chatMessageToUser(List<Object> argsList){
		if(server.userMap.get(userName) == null){
			send(Packets.ERROR, "You are not connected to the server");
			return;
		}
		if(threadFriend == null){
			send(Packets.ERROR, "Not connected to another user. Can not send diffs");
			return;
		}
		
		threadFriend.send(Packets.CHAT, argsList.get(1), userName);
		// Inform the user that it went ok
		send(Packets.OK);
	}
}
