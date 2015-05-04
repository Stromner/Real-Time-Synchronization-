package sigmatechnology.se.realtime_file_synchronisation.network;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
	//static Socket socket;
	String timeStamp;
	static Map<String, Socket> nickAndSocket;
	static Map<String, ServerConnectionThread> nickAndThread;
	ArrayList<ServerConnectionThread> connectionThreads; //Can administer from nickAndThread?
	Iterator<ServerConnectionThread> it;
	
	public Server(){
		nickAndSocket = new HashMap<String, Socket>();
		nickAndThread = new HashMap<String, ServerConnectionThread>();
		connectionThreads = new ArrayList<ServerConnectionThread>();
		//run();
	}
	public void run(){
		try{
			//Check if port available 
			//Inside loop?
			serverSocket = new ServerSocket(portIn);
			System.out.println("SingleSocketServer Initialized");
		
			int i = 0;
			while (true) {
				Socket socket = serverSocket.accept();
		        //Do connectionThreads check and remove threads no longer in use from the list.
		        //removeFinishedThreads();
		        System.out.println(i);
		        //When are they removed from the list? 
		        ServerConnectionThread ct = new ServerConnectionThread(socket);
		        ct.start();
		        System.out.println("here");
		        i++;
		        connectionThreads.add(ct);
		        
		    }
	    }
	    catch (IOException e) {e.printStackTrace();}
		catch (Exception e) {e.printStackTrace();}
		finally{
			try{
				//serverSocket.close();
			}
			catch(Exception e){e.printStackTrace();}
		}

	}
	//Better to call when they get interrupted
	/*
	private void removeFinishedThreads() {
		it = connectionThreads.iterator();
		while(it.hasNext()){
			if(!it.next().isRunning || !it.next().isInterrupted()){
				it.remove();
			}
		}
	}
	*/
}	
