package sigmatechnology.se.realtime_file_synchronisation.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientReceiver extends Thread{
	
	Socket socket;
	//BufferedInputStream bis;
	ObjectInputStream ois;
	
	public ClientReceiver(Socket socket){
		this.socket = socket;
		
		try {
			this.ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ClientReceiver(Socket socket, ObjectInputStream ois) {
		this.socket = socket;
		this.ois = ois;
	}

	@Override
    public void run(){
    	while(true){
	    	try{
	    		//System.out.println("CR before readObject");
	    		Packet packet = (Packet) ois.readObject();
	    		System.out.println("CR after readObject");
	    		switch(packet.type){
	    		case DIFF:
	    			diffReceived(packet);
	    			break;
	    		case CHAT:
	    			chatReceived(packet);
	    			break;
	    		case END:
	    			close();
	    			break;
	    		case SYNCFILE:
	    			break;
				case REGISTER:
					testReceived(packet);
					break;
				default:
					break;
	    		}
	    	} catch (ClassNotFoundException e1) {
	    		System.out.println(e1);e1.printStackTrace();
	    	}
	    	catch(IOException e){
	    		//e.printStackTrace();System.out.println(e);
	    	}
    	}
    }
    
    private void testReceived(Packet packet) {
    	System.out.println("CR: TestREGReceived, Source: " + packet.sourceNick);
	}

	private void chatReceived(Packet packet) {
		System.out.println(packet.chatMsg);
		System.out.println("chatRec");
	}

	private void diffReceived(Packet packet) {
		System.out.println("diffRec");
		System.out.println("CR: "+packet.sourceNick);
		System.out.println(packet.destinationNick);
	}

	private void close() {
    	try{
    		System.out.println("CT close");
    		ois.close();
    		//bis.close();
    		//isRunning = false;
    		this.interrupt();
    		//socket.close();
    		//ServerSocket?
    	}
    	catch(Exception e){System.out.println(e);}
	}
}
