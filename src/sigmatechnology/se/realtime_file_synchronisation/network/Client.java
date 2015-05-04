package sigmatechnology.se.realtime_file_synchronisation.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

public class Client {
	Socket socket;
	String serverIP = "127.0.0.1";
	int port = 20001;
	String nickname;
	ClientReceiver cr;
	ObjectOutputStream out;
	
	public Client(String nickname){
		try {
			socket = new Socket(serverIP, port);
			out = new ObjectOutputStream(socket.getOutputStream());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		this.nickname = nickname;
		// Register at server
		send(Packets.REGISTER, nickname);
		
		// Start listening/waiting for packets on the socket
		cr = new ClientReceiver(socket);
		cr.start();
	}
	
	/**
	 * 
	 * @param type
	 * @param args
	 */
	public void send(Packets type, Object... args) {
		LinkedList<Object> argsList = new LinkedList<Object>();
		argsList.add(type);
		for (Object o : args) {
			argsList.add(o);
		}
		
		try {
			System.out.println("Sending to server");
			out.writeObject(argsList);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private class ClientReceiver extends Thread{
		ObjectInputStream in;
		
		public ClientReceiver(Socket socket){
			try {
				this.in = new ObjectInputStream(socket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
	    public void run(){
	    	while(true){
		    	try{
		    		LinkedList<?> argsList = (LinkedList<?>)in.readObject();
		    		switch((Packets)argsList.get(0)){
		    		case DIFF:
		    			System.out.println("Client Diff");
		    			// TODO diffReceived(packet);
		    			break;
		    		case CHAT:
		    			System.out.println("Client Chat");
		    			// TODO chatReceived(packet);
		    			break;
		    		case END:
		    			System.out.println("Client End");
		    			close();
		    			break;
		    		case SYNCFILE:
		    			System.out.println("Client SyncFile");
		    			break;
					case REGISTER:
						System.out.println("Client Register");
						// TODO testReceived(packet);
						break;
					default:
						break;
		    		}
		    	} catch (ClassNotFoundException | IOException e) {
		    		e.printStackTrace();
		    	}
	    	}
	    }
	    
		//TODO
		/*
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
		}*/

		private void close() {
	    	try{
	    		System.out.println("CT close");
	    		in.close();
	    		this.interrupt();
	    	}
	    	catch(Exception e){
	    		e.printStackTrace();
	    	}
		}
	}
}