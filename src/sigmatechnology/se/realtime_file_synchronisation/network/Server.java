package sigmatechnology.se.realtime_file_synchronisation.network;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Single socket server
 * @author Magnus Källtén
 * http://edn.embarcadero.com/article/31995
 */
public class Server implements Runnable{

	int portIn  = 20001;//19999;
	int portOut = 19999;
	ServerSocket serverSocket;
	String timeStamp;
	static Map<String, Socket> nickAndSocket;
	static Map<String, ServerConnectionThread> nickAndThread;
	Iterator<ServerConnectionThread> it;
	List<ServerConnectionThread> connectionThreads;
	
	public Server(){
		nickAndSocket = new HashMap<String, Socket>();
		nickAndThread = new HashMap<String, ServerConnectionThread>();
		connectionThreads = new ArrayList<ServerConnectionThread>();
	}
	
	public void run(){
		try{
			//Check if port available 
			serverSocket = new ServerSocket(portIn);
			System.out.println("Server Initialized");
		
			while (true) {
				Socket socket = serverSocket.accept();
		        System.out.println("New client");
		        ServerConnectionThread ct = new ServerConnectionThread(socket);
		        ct.start();
		        connectionThreads.add(ct);
		    }
	    }
	    catch (IOException e) {
	    	e.printStackTrace();
	    }
	}
	
	public class ServerConnectionThread extends Thread {
		Socket socket;
		ObjectInputStream ois;
		ObjectOutputStream oos;
		InetAddress address;
		ObjectOutputStream outToClient;
		ObjectInputStream inFromClient;
		
		
		public ServerConnectionThread(Socket socket) {
			this.socket = socket;
			try {
				outToClient = new ObjectOutputStream(socket.getOutputStream());
				inFromClient = new ObjectInputStream(socket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
	        
		}
		@Override
		public void run() {
			while(true){				
				try {
					LinkedList<?> argsList = (LinkedList<?>)inFromClient.readObject();			
					switch((Packets)argsList.get(0)){
					case REGISTER:
						System.out.println("Server Register");
						// TODO register(packet);
						break;
					case DIFF:
						System.out.println("Server Diff");
						// TODO diff(packet);
						break;
					case CHAT:
						System.out.println("Server Chat");
						// TODO chat(packet);
						break;
					case END:
						System.out.println("Server End");
						end();
						break;
					case SYNCFILE:
						System.out.println("Server SyncFile");
						break;
					}
				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		private void end() {
		}
		
		/*
		private void diff(Packet packet2) {
			// TODO Auto-generated method stub
			
		}
		private void chat(Packet packet) {
			System.out.println("Msg: "+packet.chatMsg + 
			" from: "+packet.sourceNick+" to: "+packet.destinationNick);
			forward(packet);
		}
		
		private void register(Packet packet) {
			//Server2.nickAndSocket.put(packet.nickname, socket);
			
			// TODO Check if name exists, if it does, send back "Name exists" and stop/interrupt thread
			Server.nickAndThread.put(packet.sourceNick, this);
			System.out.println("SCT: Source: "+packet.sourceNick +	
			" Dest: " +packet.destinationNick);
		}
		*/
		/*
		private void forward(Packet packet) {
			try{
				//Different socket with new address?
				
				//Get the connection thread of the destination connection
				ServerConnectionThread sct = Server.nickAndThread.get(packet.destinationNick);
				//Use that threads ObjectOutputStream to send via its socket 
				sct.outToClient.writeObject(packet);
				sct.outToClient.flush();
			}
			catch(IOException e){
				System.out.println(e);
			}
		}*/
	}
}	
