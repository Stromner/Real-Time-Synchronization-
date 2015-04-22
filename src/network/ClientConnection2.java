package network;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;


public class ClientConnection2 {
	public final static char REG  = 1;
	public final static char DIFF = 2;
	public final static char CHAT = 3;
	
	private String[] arr = {"a","b","c"};
	
	public ClientConnection2(){
		connect();
	}
	
	//Temp method name
	public void connect(){
		String host = "127.0.0.1";
	    /** Define a port */
	    int port = 20001;

	    StringBuffer instr = new StringBuffer();
	    String timeStamp;
	    System.out.println("SocketClient initialized");
	    
	    try {
	        /** Obtain an address object of the server */
	        InetAddress address = InetAddress.getByName(host);
	        /** Establish a socket connetion */
	        Socket socket = new Socket(address, port);
	        /** Instantiate a ObjectOutputStream object */
	        OutputStream os = socket.getOutputStream();
	        
	        ObjectOutputStream oos = new ObjectOutputStream(os);
	        
	        /** Instantiate an OutputStreamWriter object with the optional character
	         * encoding.
	         * with OutputStreamWriter you can pass objects such as Strings without converting to byte, byte arrays, or int values…ok I’m lazy…so what.
	         */
	        
	        timeStamp = new java.util.Date().toString();
	        Packet packet = new Packet(1, "Magnus");
	        
	        oos.writeObject(packet); //Send?
	        oos.close();
	        os.close();
	        socket.close();
	        
	        
	        /*
	        Socket s = new Socket("localhost",2002);
			OutputStream os = s.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);
			testobject to = new testobject(1,"object from client");
			oos.writeObject(to);
			oos.writeObject(new String("another object from the client"));
			oos.close();
			os.close();
			s.close();
	        */
	        
	        BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
	        
	        /**Instantiate an InputStreamReader with the optional
	         * character encoding.
	         */

	        InputStreamReader isr = new InputStreamReader(bis, "US-ASCII");
	        
	        /**Read the socket's InputStream and append to a StringBuffer */
	        int c;
	        while ( (c = isr.read()) != 13){
	        	instr.append( (char) c);
	        }
	        /** Close the socket socket. */
	        socket.close();
	        System.out.println(instr);
	    }
	    catch (IOException f) {
	    	System.out.println("3 IOException: " + f);
	    }
	    catch (Exception g) {
	        System.out.println("4 Exception: " + g);
	    }

	}
	
	public void sendRegistration(String nick, String ip){
		
	}

}
