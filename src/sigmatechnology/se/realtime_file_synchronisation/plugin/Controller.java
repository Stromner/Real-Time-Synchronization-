package sigmatechnology.se.realtime_file_synchronisation.plugin;

import sigmatechnology.se.realtime_file_synchronisation.diff_match_patch.SynchronizeRoot;
import sigmatechnology.se.realtime_file_synchronisation.network.Client;

public class Controller implements Runnable{
	private Client client;
	private SynchronizeRoot root;
	
	@Override
	public void run() {
		// Controller pseudo workflow
		
		// Start GUI(controller) thread
		// Loop Wait for input from the GUI
			// Create Client(username)
				// Error handle
					// Close client
						// Bad connection
						// Bad user name
				// Ok
					// If user disconnects from server
						// Break out of loop
					// Wait for user list from server that the GUI can fetch through a get method
					// Loop Wait for input from the GUI
						// Connect to the selected user
							// Error handle
								// User not on the list
							// Ok
								// If our users disconnects the user || the user disconnect our user
									// Break out of loop
								// If user disconnects from server
									// Break out of inner loop
									// Break out of outer loop
								// Start Sync thread(root, ignoreList)
								// Wait for input from GUI
									// Send chat message to the user
					// End loop
		// End loop
		
		// Methods
		// getClient
		// getSynchronizedRoot
	}

}

// Client receive thread
// Wait for package
// Handle package
// 	If package of patch type
//	Patch diffs
// 	1) To Eclipse
// 	2) To other files