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
	
	static ServerSocket socket1;
	protected final static int port = 20001;//19999;
	static Socket connection;

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
			System.out.println(111);
			socket1 = new ServerSocket(port);
			System.out.println(222);
			System.out.println("SingleSocketServer Initialized");
			int character;
		
			while (true) {
				// 	System.out.println(222);
		        connection = socket1.accept();
		        
		        BufferedInputStream is = new BufferedInputStream(connection.getInputStream());
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
		        	System.out.println("1 sec sleep");
		        	Thread.sleep(1000);
		        }
		        catch (Exception e){System.out.println(e);}
		        timeStamp = new java.util.Date().toString();
		        String returnCode = "SingleSocketServer repsonded at "+ timeStamp + (char) 13;
		        
		        BufferedOutputStream os = new BufferedOutputStream(connection.getOutputStream());
		        OutputStreamWriter osw = new OutputStreamWriter(os, "US-ASCII");
		        
		        osw.write(returnCode);
		        osw.flush();
		    }
	    }
	    catch (IOException e) {System.out.println("1 IOE: "+e);}
		try{connection.close();
			socket1.close();
		}
		catch(IOException e){}
	}
	
}	
