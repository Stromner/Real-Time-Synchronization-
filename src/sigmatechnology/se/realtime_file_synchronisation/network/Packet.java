package sigmatechnology.se.realtime_file_synchronisation.network;
import java.io.Serializable;
import java.util.LinkedList;

import sigmatechnology.se.realtime_file_synchronisation.diff_match_patch.fraser_neil.diff_match_patch.Diff;


/**
 * int - To tell the purpose
 * diff - The diff
 * chat - Message for the chat
 * timestamp - Timestamp for chat
 * 
 * @author Magnus
 *
 */


public class Packet implements Serializable{
	
	public enum PacketType {
		REGISTER, DIFF, CHAT, END, SYNCFILE;
	}
	
	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -4289306338701482203L;
	PacketType type = null;
	LinkedList<Diff> diffs = null;
	String chatMsg = null;
	String timeStamp = null;
	String sourceNick = null;
	String destinationNick = null;
	
	//Request nickAndIPs?
	public Packet(){
	}
	
	//Registration - To server
	public Packet(PacketType type, String sourceNick){
		this.type = type;
		this.sourceNick= sourceNick;
	}
	
	//Diff - To server
	public Packet(PacketType type, String sourceNick, String destinationNick, LinkedList<Diff> diffs){
		this.type = type;
		this.diffs = diffs;
		this.sourceNick = sourceNick;
		this.destinationNick = destinationNick;
	}
	
	//Chat - To server
	public Packet(PacketType type, String sourceNick, String destinationNick, String chatMsg){
		this.type = type;
		this.chatMsg = chatMsg;
		this.sourceNick = sourceNick;
		this.destinationNick = destinationNick;
		this.timeStamp = new java.util.Date().toString();
	}
}
