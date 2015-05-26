package sigmatechnology.se.realtime_file_synchronisation.network;

public enum Packets {
	CONNECTSERVER,		// Client to server: Connecting to the server
	GRANTACCESS,		// Server to client: Allow client to connect to the server
	DENYACCESS,			// Server to client: User already exist on the server
	DISCONNECTSERVER,	// Client to server: Disconnecting from the server
	NEWUSER,			// Server to client: New user on the server
	DISCONNECTUSER,		// Server to client: User disconnected from the server
	STARTCOLLABORATION,	// Client to server to client: Users started to collaborate
	ACKUSERCONNECT,		// Client to server to client: Client granted another client to connect to it
	DENYUSERCONNECT,	// Client to server to client: Client denied another client access to it
	STOPCOLLABORATION,	// Client to server to client: Users stopped the collaboration
	SYNCFILE,			// Client to server to client: Diff object list message
	CHAT,				// Client to server to client: Chat message
	ERROR,				// Server to client: Error message
	OK;					// Server to client: Confirm message
}