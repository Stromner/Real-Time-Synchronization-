package network;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ConnectionThread extends Thread{
	Socket socket;
	String timeStamp;
	public ConnectionThread(Socket socket){
		this.socket = socket;
	}


	@Override
    public void run() {
    	// TODO Auto-generated method stub
    	super.run();
    	try{
	    	InputStream is = socket.getInputStream();
	        ObjectInputStream ois = new ObjectInputStream(is);
	        //testobject to = (testobject)ois.readObject();
	        
	        timeStamp = new java.util.Date().toString();
	        
	        try {
	    		Packet packet = (Packet) ois.readObject();
	    		InetAddress ip = socket.getInetAddress();
	    		switch(packet.type){
	    			case 1:
	    				Server2.nickAndIP.put(packet.nickname, ip.getHostAddress());
	    				close();
	    				break;
	    			case 2:
	    				forward(packet);
	    				break;
	    			case 3:
	    				forward(packet);
	    				break;
	    		}
	    	} catch (ClassNotFoundException e1) {
	    		// TODO Auto-generated catch block
	    		e1.printStackTrace();
	    	}
        
        //Call when exiting program?
        is.close();
        socket.close();	
        //serverSocket.close();
    	}
    	catch(IOException e){System.out.println(e);}
    }
    
    private void close() {
		// TODO Auto-generated method stub
		
	}


	private void forward(Packet packet) {
		try{
			//Diffrent socket with new address?
			OutputStream os = socket.getOutputStream();
	        ObjectOutputStream oos = new ObjectOutputStream(os);
	        oos.writeObject(packet);
	        oos.close();
	        os.close();
	        socket.close();
		}
		catch(IOException e){
			System.out.println(e);
		}
	}
	@Override
	public void interrupt() {
		// TODO Auto-generated method stub
		super.interrupt();
	}
}
