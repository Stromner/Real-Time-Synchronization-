package sigmatechnology.se.realtime_file_synchronisation.network;

public enum Packets {
	NEWUSER, // New user on the server
	DELETEUSER, // User disconnected from the server
	DISCONNECTSERVER, // Confirmation from the server to the client
	CONNECTUSER, // User connects to another user
	DISCONNECTUSER, // User disconnects from another user
	SYNCFILE, // Diff object list sent from one user to another
	CHAT, // Chat message to be displayed from one user to another user
	ERROR, // Error message sent from the server
	OK; // Ok message sent from the server
}