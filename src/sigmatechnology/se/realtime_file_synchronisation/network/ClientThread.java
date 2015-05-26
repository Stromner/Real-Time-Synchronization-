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
	private int status;
	
	/**
	 * 
	 * @param socket
	 * @param server
	 */
	public ClientThread(Socket socket, Server server){
		this.socket = socket;
		this.server = server;
		lock = new ReentrantLock();
		status = 0;
		
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
		
		// Inform all the other users about us disconnecting
		removeUser();
		
		// Remove us from the set and map(If we're in them)
		for (Map.Entry<String, ClientThread> entry : server.userMap.entrySet()){
			if(entry.getKey() == userName){
				server.userMap.remove(userName);
				break;
			}
		}
		
		for (ClientThread t : server.threadSet) {
			if(t.getId() == getId()){
				server.threadSet.remove(t);
				break;
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
	
	public void setUserStatus(int status){
		this.status = status;
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
							System.out.println("ClientThread[" + userName +"]: Connect");
							newUser(argsList);
							break;
						case DISCONNECTSERVER:
							System.out.println("ClientThread[" + userName +"]: Disconnect");
							disconnect();
							break;
						case STARTCOLLABORATION:
							System.out.println("ClientThread[" + userName +"]: Connect to user");
							connectUsers(argsList);
							break;
						case ACKUSERCONNECT:
							ClientThread ct1 = server.userMap.get(argsList.get(1));
							ct1.setUserStatus(1);
							break;
						case DENYUSERCONNECT:
							ClientThread ct2 = server.userMap.get(argsList.get(1));
							ct2.setUserStatus(2);
							break;
						case STOPCOLLABORATION:
							System.out.println("ClientThread[" + userName +"]: Disconnect from user");
							disconnectUsers(argsList);
							break;
						case SYNCFILE:
							System.out.println("ClientThread[" + userName +"]: SyncFile");
							synchronizeFileWithUser(argsList);
							break;
						case CHAT:
							System.out.println("ClientThread[" + userName +"]: Chat");
							chatMessageToUser(argsList);
							break;
						case ERROR:
							System.out.println("ClientThread[" + userName +"]: Error");
							break;
						default:
							break;
						
					}
				}
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
				disconnect();
				System.out.println("ClientThread[" + userName +"]: receive failed");
			}
		}
	}
	
	private void newUser(List<Object> argsList){
		if(server.userMap.get((String)argsList.get(1)) == null){
			userName = (String)argsList.get(1);
			send(Packets.GRANTACCESS);
			
			server.userMap.put(userName, this);
			
			// Inform all other users that are not us
			Iterator<?> it = server.userMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<?,?> pair = (Map.Entry<?,?>)it.next();
				ClientThread ct = (ClientThread)pair.getValue();
				// Inform us about all the other users too!
				if(ct.userName != userName){
					ct.send(Packets.NEWUSER, userName);
					send(Packets.NEWUSER, ct.userName);
				}
			}
		}
		else{
			System.out.println("ClientThread[" + userName +"]: user already on the server.");
			send(Packets.DENYACCESS, "User " + userName + " already exists on the server.");
		}
	}
	
	private void removeUser(){
		if(userName != null && server.userMap.get(userName) != null){
			// Inform all other users that are not us
			Iterator<?> it = server.userMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<?,?> pair = (Map.Entry<?,?>)it.next();
				ClientThread ct = (ClientThread)pair.getValue();
				if(ct.userName != userName){
					ct.send(Packets.DISCONNECTUSER, userName);
				}
			}

			System.out.println("ClientThread[" + userName +"]: User " + userName + " disconnected.");
			return;
		}
		else{
			System.out.println("ClientThread[" + userName +"]: Could not delete user");
		}
	}
	
	private void connectUsers(List<Object> argsList) throws ClassNotFoundException, IOException{
		ClientThread ct = server.userMap.get(argsList.get(1));
		
		if(server.userMap.get(userName) == null){
			send(Packets.ERROR, "You are not connected to the server");
			return;
		}
		else if(threadFriend != null){
			send(Packets.ERROR, "You are already connected to " + threadFriend.userName);
			return;
		}		
		else if(ct == null){
			send(Packets.ERROR, "User " + argsList.get(1) + " can not be found on the server.");
			return;
		}
		
		// Inform the other users that we connected to it.
		ct.send(Packets.STARTCOLLABORATION, userName, argsList.get(2));
		// Wait for its answer
		while(status == 0){
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(status == 1){
			setFriend(ct);
			ct.setFriend(this);
			
			// Inform the user that it went ok
		    send(Packets.ACKUSERCONNECT, argsList.get(1));
		}
		else{
			send(Packets.DENYUSERCONNECT, "User " + argsList.get(1) + " denied your connection.");
		}
		status = 0;
		
		// TODO Add checks with belonging data for checking that all documents are equal.
	}
	
	private void disconnectUsers(List<Object> argsList){
		if(server.userMap.get(userName) == null){
			send(Packets.ERROR, "You are not connected to the server");
			return;
		}
		if(threadFriend == null){
			send(Packets.ERROR, "Not conected to any user. Can not disconnect.");
			return;
		}
		
		threadFriend.send(Packets.STOPCOLLABORATION, userName);
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
			send(Packets.ERROR, "Not connected to another user. Can not send chat");
			return;
		}
		
		threadFriend.send(Packets.CHAT, userName, argsList.get(1));
		// Inform the user that it went ok
		send(Packets.OK);
	}
}
