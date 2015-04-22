import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;


public class ClientConnection {
	/** Define a host server */
	public ClientConnection(){
		System.out.println("ClientConnection");
		connect();
	}
	
	//Temp method name
	public void connect(){
		String host = "127.0.0.1";
	    /** Define a port */
	    int port = 20001;

	    StringBuffer instr = new StringBuffer();
	    String TimeStamp;
	    System.out.println("SocketClient initialized");
	    
	    try {
	        /** Obtain an address object of the server */
	        InetAddress address = InetAddress.getByName(host);
	        /** Establish a socket connetion */
	        
	        Socket connection = new Socket(address, port);
	        /** Instantiate a BufferedOutputStream object */
	        
	        BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());
	        
	        /** Instantiate an OutputStreamWriter object with the optional character
	         * encoding.
	         * with OutputStreamWriter you can pass objects such as Strings without converting to byte, byte arrays, or int values…ok I’m lazy…so what.
	         */
	        OutputStreamWriter osw = new OutputStreamWriter(bos, "US-ASCII");
	        
	        TimeStamp = new java.util.Date().toString();
	        String process = "Calling the Socket Server on "+ host + " port " + port + " at " + TimeStamp +  (char) 13;
	        
	        /** Write across the socket connection and flush the buffer */
	        osw.write(process);
	        
	        osw.flush();
	        
	        /** Instantiate a BufferedInputStream object for reading
	        /** Instantiate a BufferedInputStream object for reading
	         * incoming socket streams.
	         */

	        BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
	        
	        /**Instantiate an InputStreamReader with the optional
	         * character encoding.
	         */

	        InputStreamReader isr = new InputStreamReader(bis, "US-ASCII");
	        
	        /**Read the socket's InputStream and append to a StringBuffer */
	        int c;
	        while ( (c = isr.read()) != 13){
	        	instr.append( (char) c);
	        }
	        /** Close the socket connection. */
	        connection.close();
	        System.out.println(instr);
	    }
	    catch (IOException f) {
	    	System.out.println("3 IOException: " + f);
	    }
	    catch (Exception g) {
	        System.out.println("4 Exception: " + g);
	    }

	}
}
