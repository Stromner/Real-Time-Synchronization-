package sigmatechnology.se.realtime_file_synchronisation.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ServerConnectionThread extends Thread {

	Socket socket, socket2;
	ObjectInputStream ois;
	ObjectOutputStream oos;
	Packet packet;
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
				packet = (Packet) inFromClient.readObject();
				System.out.println("Packet read in SCT2");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			switch(packet.type){
			case REGISTER:
				register(packet);
				//close();
				break;
			case DIFF:
				diff(packet);
				break;
			case CHAT:
				chat(packet);
				break;
			case END:
				end();
				break;
			case SYNCFILE:
				break;
			
			}
		}
	}
	private void end() {
		// TODO Auto-generated method stub
	}
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
	
	private void forward(Packet packet) {
		try{
			//Different socket with new address?
			
			//Get the connection thread of the destination connection
			ServerConnectionThread sct = Server.nickAndThread.get(packet.destinationNick);
			//Use that threads ObjectOutputStream to send via its socket 
			sct.outToClient.writeObject(packet);
			sct.outToClient.flush();
			
			// TODO Think this needs to be removed 
			//sct.outToClient.close();
			
			/*
	        ObjectOutputStream oos = new ObjectOutputStream(sct.socket.getOutputStream());
	        
	        oos.writeObject(packet);
	        oos.flush();
	        oos.close();
	        */
	        
	        //outToClient.writeObject(packet);
	        //outToClient.flush();
	        
	        //outToClient2.writeObject(packet);
	        //outToClient2.flush();
	        //socket.close();
		}
		catch(IOException e){
			System.out.println(e);
		}
	}
}
