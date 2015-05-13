package sigmatechnology.se.realtime_file_synchronisation.plugin;

import java.util.LinkedList;
import java.util.List;

import sigmatechnology.se.realtime_file_synchronisation.diff_match_patch.SynchronizeRoot;
import sigmatechnology.se.realtime_file_synchronisation.network.Client;
import sigmatechnology.se.realtime_file_synchronisation.network.Packets;

public class Controller implements Runnable{
	private static Controller instance;
	private Client client;
	private SynchronizeRoot root;
	private List<String> userList;
	
	private Controller(){
		userList = new LinkedList<String>();
	}
	
	public static Controller getInstance(){
		if(instance == null){
			instance = new Controller();
		}
		
		return instance;
	}
	
	/**
	 * @return the instance of the SynchronizedRoot class.
	 */
	public SynchronizeRoot getRoot(){
		return root;
	}
	
	@Override
	public void run() {
		// Controller pseudo workflow
		
		// Start GUI(controller) thread
		// Loop Wait for input from the GUI
			// Create Client(username)
				// Error handle
					// Close client
						// Bad connection
						// Bad user name
				// Ok
					// If user disconnects from server
						// Break out of loop
					// Wait for user list from server that the GUI can fetch through a get method
					// Loop Wait for input from the GUI
						// Connect to the selected user
							// Error handle
								// User not on the list
							// Ok
								// If our users disconnects the user || the user disconnect our user
									// Break out of loop
								// If user disconnects from server
									// Break out of inner loop
									// Break out of outer loop
								// Start Sync thread(root, ignoreList)
								// Wait for input from GUI
									// Send chat message to the user
					// End loop
		// End loop
		
		// Methods
		// getClient
		// getSynchronizedRoot
		// updateUserList
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
		}
		else{
			userList.remove(s);
		}
	}
	
	/**
	 * A user connected/disconnected to us, update the GUI with that information and necessary GUI changes.
	 * 
	 * @param nickName name of the user that connected/disconnected us.
	 * @param status true if connected. False if disconnected.
	 */
	public void userConnected(String nickName, Boolean status){
		// Update GUI update with the user information
		// Update GUI so the specified fields are disabled
	}
	
	/**
	 * Displays the chat message in the GUI.
	 * @param userName name of the user that sent the message.
	 * @param msg from user.
	 */
	public void msgToGUI(String userName, String msg){
		// addToChat(String msg, String member)
	}
}