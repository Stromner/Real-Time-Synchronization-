package sigmatechnology.se.realtime_file_synchronisation.network;

/**
 * Client that connects to <code>Server<code, get the IP and port of the server 
 * through the config.txt document.
 * 
 * @author Magnus Källtén
 * @author David Strömner
 * @version 2015-05-04
 */

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.eclipse.core.runtime.Platform;

import sigmatechnology.se.realtime_file_synchronisation.Util;
import sigmatechnology.se.realtime_file_synchronisation.diff_match_patch.SynchronizeDocument;
import sigmatechnology.se.realtime_file_synchronisation.gui.ResponseDialog;
import sigmatechnology.se.realtime_file_synchronisation.plugin.Controller;

public class Client {
	public static final int TIMEOUT = 5000;
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private Thread receiveThread;
	private String path = Platform.getInstallLocation().getURL().toString().substring(6) + "plugins/sigmatechnology.se.realtime_file_synchronisation/config.txt";
	private String friend;
	private List<Object> lastPackage;
	private int status;
	
	/**
	 * Initiate a new client.
	 */
	public Client(){
		init();
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
				out.reset();
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
	 * Sets what the response is from the dialog and if the user wants to allow the connection
	 * @param status - 1 Connection granted. Anything else results in a connection denied.
	 */
	public void setResponseFromDialog(int status){
		this.status = status;
	}
	
	/**
	 * Initiate a new client by reading the config.txt for IP and port.
	 * Opens a new I/O stream for the created socket. Starts a new 
	 * thread that listen for messages from the server.
	 */
	private void init(){
		status = 0;
		
		String text = Util.openReadFile(Paths.get(path));
		String[] s = text.split(":");
		
		try {
			// Create a new socket with its I/O
			socket = new Socket();
			socket.connect(new InetSocketAddress(s[0], Integer.parseInt(s[1])), TIMEOUT);
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
			JOptionPane.showMessageDialog(Controller.getInstance().getLauncher(), "Could not connect to server.", "Error", JOptionPane.ERROR_MESSAGE);
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
					case GRANTACCESS:
						System.out.println("Client: GRANTACCESS");
						Controller.getInstance().getLauncher().serverGrantedConecction();
						break;
					case DENYACCESS:
						System.out.println("Client: DENYACCESS");
						Controller.getInstance().getLauncher().serverDeniedConnection();
						break;
					case NEWUSER:
						System.out.println("Client: NEWUSER");
						Controller.getInstance().updateUserList(Packets.NEWUSER, (String)argsList.get(1));
						Controller.getInstance().getLauncher().updatePane("User " + argsList.get(1) + " connected to the server.");
						break;
					case DISCONNECTUSER:
						System.out.println("Client: DISCONNECTUSER");
						Controller.getInstance().updateUserList(Packets.DISCONNECTUSER, (String)argsList.get(1));
						Controller.getInstance().getLauncher().updatePane("User " + argsList.get(1) + " disconnected from the server.");
						break;
					case STARTCOLLABORATION:
						System.out.println("Client: STARTCOLLABORATION");
						// Open prompt for user input
						ResponseDialog rd = new ResponseDialog(new JFrame(), "User Connection", (String)argsList.get(1), (String)argsList.get(2), Controller.getInstance().getLauncher());
						rd.pack();
						rd.setVisible(true);
						
						// Wait for response from the dialog
						while(status == 0){}
						if(status == 1){
							send(Packets.ACKUSERCONNECT, argsList.get(1));
							Controller.getInstance().userConnected((String)argsList.get(1), true);
							friend = (String) argsList.get(1);
						}
						else{
							send(Packets.DENYUSERCONNECT, argsList.get(1));
						}
						status = 0;
						break;
					case ACKUSERCONNECT:
						System.out.println("Client: ACKUSERCONNECT");
						Controller.getInstance().getLauncher().connectToClient((String)argsList.get(1));
						break;
					case DENYUSERCONNECT:
						System.out.println("Client: DENYUSERCONNECT: " + argsList.get(1));
						JOptionPane.showMessageDialog(Controller.getInstance().getLauncher(), argsList.get(1), "Error", JOptionPane.ERROR_MESSAGE);
						break;
					case STOPCOLLABORATION:
						System.out.println("Client: STOPCOLLABORATION");
						Controller.getInstance().userConnected((String)argsList.get(1), false);
						friend = null;
						break;
					case SYNCFILE:
						System.out.println("Client: SYNCFILE");
						while(Controller.getInstance().getSynchronizeRoot() == null){
							System.out.println("BUGG: Root is null");
							while(true){
								continue;
							}
						}
						Controller.getInstance().getSynchronizeRoot().applyDiffs((LinkedList<SynchronizeDocument>) argsList.get(1));
						break;
					case CHAT:
						System.out.println("Client: CHAT");
						Controller.getInstance().msgToGUI((String)argsList.get(1), (String)argsList.get(2));
						break;
					case CREATEFILE:
						System.out.println("Client: CREATEFILE");
						while(Controller.getInstance().getSynchronizeRoot() == null){
							System.out.println("BUGG: Root is null");
							while(true){
								continue;
							}
						}
						
						Util.createFile(Controller.getInstance().getSynchronizeRoot().getRepo().resolve((String) argsList.get(1)));
						Util.openWriteFile(Controller.getInstance().getSynchronizeRoot().getRepo().resolve((String) argsList.get(1)), (String) argsList.get(2));
						break;
					case DELETEFILE:
						System.out.println("Client: DELETEFILE");
						while(Controller.getInstance().getSynchronizeRoot() == null){
							System.out.println("BUGG: Root is null");
							while(true){
								continue;
							}
						}
						
						Controller.getInstance().getSynchronizeRoot().getRepo().resolve((String) argsList.get(1));
						File f = new File(Controller.getInstance().getSynchronizeRoot().getRepo().resolve((String) argsList.get(1)).toString());
						Util.deleteDir(f);
						break;
					case ERROR:
						System.out.println("Client: ERROR: " + argsList.get(1));
						JOptionPane.showMessageDialog(Controller.getInstance().getLauncher(), argsList.get(1), "Error", JOptionPane.ERROR_MESSAGE);
						break;
					case OK:
						System.out.println("Client: OK");
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
			if(receiveThread != null){
				receiveThread.interrupt();
			}
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}