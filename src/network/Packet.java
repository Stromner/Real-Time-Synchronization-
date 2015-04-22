package network;
import java.io.Serializable;
import java.util.LinkedList;

import diff_match_patch.fraser_neil.diff_match_patch.Diff;


/**
 * int - To tell the purpose
 * diff - The diff
 * chat - Message for the chat
 * timestamp - Timestamp for chat
 * 
 * 
 * @author Magnus
 *
 */
public class Packet implements Serializable{
	public int type = 0;
	public LinkedList<Diff> diffs = null;
	public String chatMsg = null;
	public String timeStamp = null;
	public String destinationIP = null;
	public String nickname = null;
	
	//Registration - To server
	public Packet(int type, String nickname){
		this.type = type;
		this.nickname= nickname;
	}
	
	//Diff - To server
	public Packet(int type, String destinationIP, LinkedList<Diff> diffs){
		this.type = type;
		this.diffs = diffs;
		this.destinationIP = destinationIP;
	}
	
	//Chat - To server
	public Packet(int type, String destinationIP, String chatMsg, String timeStamp){
		this.type = type;
		this.chatMsg = chatMsg;
		this.timeStamp = timeStamp;
		this.destinationIP = destinationIP;
	}
}
