package network;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import diff_match_patch.fraser_neil.diff_match_patch.Diff;


/**
 * Single socket server
 * @author Magnus Källtén
 * http://edn.embarcadero.com/article/31995
 */
public class Server2 implements Runnable{
	
	public final static char REG  = 1;
	public final static char DIFF = 2;
	public final static char CHAT = 3;
	protected final static int port_in = 20001;//19999;
	protected final static int port_send = 19999;
	static ServerSocket serverSocket;
	static Socket socket;

	static boolean first;
	static StringBuffer process;
	static String timeStamp;
	public static Map<String, String> nickAndIP;
	public Packet packet;
	private ArrayList<ConnectionThread> connectionThreads;
	
	public Server2(){
		nickAndIP = new HashMap<String, String>();
		connectionThreads = new ArrayList<ConnectionThread>();
		//run();
	}
	public void run(){
		try{
			socket.close();
		}
		catch(Exception e){
			System.out.println("socket.close();");
		}
		try{
			serverSocket.close();
		}
		catch(Exception e){
			System.out.println("serverSocket.close();");
		}
		try{
			serverSocket = new ServerSocket(port_in);
			System.out.println("SingleSocketServer Initialized");
			int character;
		
			while (true) {
				//New socket for every new thread?
		        socket = serverSocket.accept();
		        
		        
		        connectionThreads.add(new ConnectionThread(socket));
		    }
	    }
	    catch (IOException e) {System.out.println("1 IOE: "+e);}
		try{socket.close();
			serverSocket.close();
		}
		catch(IOException e){}
	}
	
}	
