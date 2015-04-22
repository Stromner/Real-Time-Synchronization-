import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


/**
 * Single socket server
 * @author Magnus Källtén
 * http://edn.embarcadero.com/article/31995
 */
public class Server implements Runnable{
	
	public final static char REG  = 1;
	public final static char DIFF = 2;
	public final static char CHAT = 3;
	protected final static int port = 20001;//19999;
	static ServerSocket serverSocket;
	static Socket socket;

	static boolean first;
	static StringBuffer process;
	static String timeStamp;
	static Map<String, String> nickAndIP;
	
	public Server(){
		nickAndIP = new HashMap<String, String>();
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
			serverSocket = new ServerSocket(port);
			System.out.println("SingleSocketServer Initialized");
			int character;
		
			while (true) {
				// 	System.out.println(222);
		        socket = serverSocket.accept();
		        
		        BufferedInputStream is = new BufferedInputStream(socket.getInputStream());
		        InputStreamReader isr = new InputStreamReader(is);

		        process = new StringBuffer();
		        while((character = isr.read()) != 13) {
		        	process.append((char)character);
		        }
		        System.out.println(process);
		        
		        //need to wait 10 seconds for the app to update database
		        try {
		        	/**
		        	 * All I’m doing here is putting the current thread to sleep for 
		        	 * 10 seconds. I added this piece of code is purely for the purpose 
		        	 * of demonstrating socket connections. It would not be used in a 
		        	 * real-world server application.
		        	 */
		        	System.out.println("1 sec sleep (Server)");
		        	Thread.sleep(1000);
		        }
		        catch (Exception e){System.out.println(e);}
		        timeStamp = new java.util.Date().toString();
		        String returnCode = "SingleSocketServer repsonded at "+ timeStamp + (char) 13;
		        
		        BufferedOutputStream os = new BufferedOutputStream(socket.getOutputStream());
		        OutputStreamWriter osw = new OutputStreamWriter(os, "US-ASCII");
		        
		        osw.write(returnCode);
		        osw.flush();
		    }
	    }
	    catch (IOException e) {System.out.println("1 IOE: "+e);}
		try{socket.close();
			serverSocket.close();
		}
		catch(IOException e){}
	}
	
}	
