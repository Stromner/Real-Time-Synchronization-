package sigmatechnology.se.realtime_file_synchronisation.plugin;

import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import sigmatechnology.se.realtime_file_synchronisation.diff_match_patch.SynchronizeRoot;
import sigmatechnology.se.realtime_file_synchronisation.gui.Launcher;
import sigmatechnology.se.realtime_file_synchronisation.network.Client;
import sigmatechnology.se.realtime_file_synchronisation.network.Packets;

public class Controller extends Thread{
	private static Controller instance;
	private Client client;
	private Launcher launcher;
	private SynchronizeRoot root;
	private List<String> userList;
	
	private Controller(){
		userList = new LinkedList<String>();
		launcher = new Launcher();
	}
	
	public static Controller getInstance(){
		if(instance == null){
			instance = new Controller();
		}
		
		return instance;
	}
	
	
	/**
	 * Updates the list that contains all the users connected to the server. Valid packet types are NEWUSER and DELETEUSER.
	 * After update the list is sent to the GUI for it to update too.
	 * 
	 * @param packet type of packet. Either NEWUSER or DELETEUSER.
	 * @param s name of the user that connected/disconnected from the server
	 */
	public void updateUserList(Packets packetType, String s){
		if(Packets.NEWUSER == packetType){
			userList.add(s);
			launcher.setServerNickList((String[]) userList.toArray(new String[userList.size()]));
		}
		else if(Packets.DISCONNECTUSER == packetType){
			userList.remove(s);
			launcher.setServerNickList((String[]) userList.toArray(new String[userList.size()]));
		}
	}
	
	/**
	 * A user connected/disconnected to us, update the GUI with that information and necessary GUI changes.
	 * 
	 * @param friend name of the user that connected/disconnected us.
	 * @param isConnecting true if connected. False if disconnected.
	 */
	public void userConnected(String friend, Boolean isConnecting){
		if(isConnecting){
			launcher.clientConnectedToUs(friend);
		}
		else{
			launcher.clientDisconnectedFromUs();
		}
	}
	
	/**
	 * Displays the chat message in the GUI.
	 * @param userName name of the user that sent the message.
	 * @param msg from user.
	 */
	public void msgToGUI(String userName, String msg){
		launcher.addToChat(msg, userName);
	}
	
	
	/**
	 * Create the SynchronizedRoot class in its own thread.
	 */
	public void initSynchronizeRoot(){
		root = new SynchronizeRoot(Paths.get(launcher.getFilePath()), launcher.getIgnoreList());
		root.start();
	}
	
	/**
	 * Initiate the client class for connecting to the server. If an error occurs during the initiations the client will close itself.
	 */
	public void initClient(){
		client = new Client();
	}
	
	/**
	 * Closes the client.
	 */
	public void killClient(){
		// Disconnect from user if we're connected to one
		if(client.getFriend() != null){
			client.send(Packets.STOPCOLLABORATION, client.getFriend());
		}
		
		// Disconnect from server if we're connected to one
		if(!client.getSocket().isClosed()){
			client.send(Packets.DISCONNECTSERVER);
		}
		
		client.disconnect();
		client = null;
		
		// Remove all users
		while(userList.size() >= 1){
			userList.remove(0);
			System.out.println("removing");
		}
	}
	
	/**
	 * Close SynchronizeRoot
	 */
	public void killSynchronizeRoot(){
		root.interrupt();
	}
	
	/**
	 * @return the instance of the SynchronizedRoot class.
	 */
	public SynchronizeRoot getSynchronizeRoot(){
		return root;
	}
	
	/**
	 * 
	 */
	public Launcher getLauncher(){
		return launcher;
	}
	
	/**
	 * @return instance of client or null if not initiated or initiated incorrectly.
	 */
	public Client getClient(){
		return client;
	}
}