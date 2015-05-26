package sigmatechnology.se.realtime_file_synchronisation.network;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sigmatechnology.se.realtime_file_synchronisation.plugin.Controller;

public class NetworkTest{
	public static final int WAITTIME = 200;
	private String user1 = "Abba", user2 = "Dabba", user3 = "Cabba";
	private static Server server;
	private Client client1, client2;
	private static Controller controller;
	
	@BeforeClass
	public static void oneTimeSetUp(){
		server = new Server();
		controller = Controller.getInstance(); // Init the controller.
		controller.start();
	}
	
	@Before
	public void setUp(){
		client1 = new Client();
		client2 = new Client();
	}
	
	@After
	public void cleanUp(){
		System.out.println("\t\t--- CLEANUP ---");
		if(server.getMap().get((user1)) != null){
			client1.send(Packets.DISCONNECTSERVER);
			client1.disconnect();
		}
			
		if(server.getMap().get((user2)) != null){
			client2.send(Packets.DISCONNECTSERVER);
			client2.disconnect();
		}

		System.out.println("\t\t--- END CLEANUP ---");
	}
	
	@Test
	public void testConnection() throws InterruptedException{
		System.out.println("\t --- testConnection ---");
		// Connect
		client1.send(Packets.CONNECTSERVER, user1);
		Thread.sleep(WAITTIME);
		ClientThread ct = (ClientThread)server.getMap().get(user1);
		assertTrue(ct.getLastPackage().get(0) == Packets.CONNECTSERVER);
		assertTrue(client1.getLastPackage().get(0) == Packets.GRANTACCESS);
		assertNotNull(server.getMap().get(user1));
	
		// Disconnect
		client1.send(Packets.DISCONNECTSERVER);
		Thread.sleep(WAITTIME);
		assertNull(server.getMap().get(user1));
	}
	
	@Test
	public void testDisconnectBeforeConnect() throws InterruptedException{
		System.out.println("\t --- testDisconnectBeforeConnect ---");
		// Disconnect
		client1.send(Packets.DISCONNECTSERVER);
		Thread.sleep(WAITTIME);
		assertNull(client1.getLastPackage());
	}
	
	@Test
	public void testDoubbleConnect() throws InterruptedException{
		System.out.println("\t --- testDoubbleConnect ---");
		// Connect
		client1.send(Packets.CONNECTSERVER, user1);
		client1.send(Packets.CONNECTSERVER, user1);
		Thread.sleep(WAITTIME);
		assertTrue(client1.getLastPackage().get(0) == Packets.DENYACCESS);
	}
	
	@Test
	public void testDoubbleDisconnect() throws InterruptedException{
		System.out.println("\t --- testDoubbleDisconnect ---");
		// Connect
		client1.send(Packets.CONNECTSERVER, user1);
		// Disconnect
		client1.send(Packets.DISCONNECTSERVER);
		
		Thread.sleep(WAITTIME);
		assertFalse(client1.send(Packets.DISCONNECTSERVER));
	}
	
	@Test
	public void testMultipleConnections() throws InterruptedException{
		System.out.println("\t --- testMultipleConnections ---");
		// Connect to server
		client1.send(Packets.CONNECTSERVER, user1);
		Thread.sleep(WAITTIME);
		assertTrue(client1.getLastPackage().get(0) == Packets.GRANTACCESS);
		
		// Connect to server
		client2.send(Packets.CONNECTSERVER, user2);
		Thread.sleep(WAITTIME);
		assertTrue(client2.getLastPackage().get(0) == Packets.GRANTACCESS);
	}
	
	@Test
	public void testConnectUser() throws InterruptedException{
		System.out.println("\t --- testConnectUser ---");
		// Connect to server
		client1.send(Packets.CONNECTSERVER, user1);
		client2.send(Packets.CONNECTSERVER, user2);
		Thread.sleep(WAITTIME);
		
		// Connect to user
		client1.send(Packets.STARTCOLLABORATION, user2);
		Thread.sleep(WAITTIME);
		assertTrue(client1.getLastPackage().get(0) == Packets.STARTCOLLABORATION);
		
		// Disconnect from user
		client1.send(Packets.STOPCOLLABORATION);
		Thread.sleep(WAITTIME);
		assertTrue(client1.getLastPackage().get(0) == Packets.STOPCOLLABORATION);
	}
	
	@Test
	public void testConnectNonexistingUser() throws InterruptedException{
		System.out.println("\t --- testConnectNonexistingUser ---");
		// Connect to server
		client1.send(Packets.CONNECTSERVER, user1);
		client2.send(Packets.CONNECTSERVER, user2);
		Thread.sleep(WAITTIME);
		
		// Connect to user
		client1.send(Packets.STARTCOLLABORATION, user3);
		Thread.sleep(WAITTIME);
		assertTrue(client1.getLastPackage().get(0) == Packets.ERROR);
	}
	
	@Test
	public void testDisconnectToUserBeforeConnect() throws InterruptedException{
		System.out.println("\t --- testDisconnectToUserBeforeConnect ---");
		// Connect to server
		client1.send(Packets.CONNECTSERVER, user1);
		client2.send(Packets.CONNECTSERVER, user2);
		Thread.sleep(WAITTIME);
		
		// Disconnect from user
		client1.send(Packets.STOPCOLLABORATION);
		Thread.sleep(WAITTIME);
		assertTrue(client1.getLastPackage().get(0) == Packets.ERROR);
	}
	
	@Test
	public void testDoubleConnectToUser() throws InterruptedException{
		System.out.println("\t --- testDoubleConnectToUser ---");
		// Connect to server
		client1.send(Packets.CONNECTSERVER, user1);
		client2.send(Packets.CONNECTSERVER, user2);
		Thread.sleep(WAITTIME);
		
		// Connect to user
		client1.send(Packets.STARTCOLLABORATION, user2);
		Thread.sleep(WAITTIME);
		assertTrue(client1.getLastPackage().get(0) == Packets.OK);
		
		// Connect to user
		client1.send(Packets.STARTCOLLABORATION, user2);
		Thread.sleep(WAITTIME);
		assertTrue(client1.getLastPackage().get(0) == Packets.ERROR);
	}
	
	@Test
	public void testDoubleDisconnectToUser() throws InterruptedException{
		System.out.println("\t --- testDoubleConnectToUser ---");
		// Connect to server
		client1.send(Packets.CONNECTSERVER, user1);
		client2.send(Packets.CONNECTSERVER, user2);
		Thread.sleep(WAITTIME);
		
		// Connect to user
		client1.send(Packets.STARTCOLLABORATION, user2);
		Thread.sleep(WAITTIME);
		assertTrue(client1.getLastPackage().get(0) == Packets.OK);
		
		// Disconnect from user
		client1.send(Packets.STOPCOLLABORATION);
		Thread.sleep(WAITTIME);
		assertTrue(client1.getLastPackage().get(0) == Packets.OK);
		// Disconnect from user
		client1.send(Packets.STOPCOLLABORATION);
		Thread.sleep(WAITTIME);
		assertTrue(client1.getLastPackage().get(0) == Packets.ERROR);
	}
	
	@Test
	public void testSendDiff() throws InterruptedException{
		System.out.println("\t --- testSendDiff ---");
		// Connect to server
		client1.send(Packets.NEWUSER, user1);
		client2.send(Packets.NEWUSER, user2);
		Thread.sleep(WAITTIME);
		
		// Connect to user
		client1.send(Packets.STARTCOLLABORATION, user2);
		Thread.sleep(WAITTIME);
		
		// Send diff
		client1.send(Packets.SYNCFILE, Controller.getInstance().getSynchronizeRoot().getDiffs());
		assertTrue(client2.getLastPackage().get(0) == Packets.SYNCFILE);
	}
	
	@Test
	public void testSendDiffUnconnected() throws InterruptedException{
		System.out.println("\t --- testSendDiffUnconnected ---");
		// Connect to server
		client1.send(Packets.NEWUSER, user1);
		client2.send(Packets.NEWUSER, user2);
		Thread.sleep(WAITTIME);
		
		// Send diff
		client1.send(Packets.SYNCFILE, Controller.getInstance().getSynchronizeRoot().getDiffs());
		assertTrue(client2.getLastPackage().get(0) == Packets.ERROR);
	}
	
	@Test
	public void testSendMessage() throws InterruptedException{
		System.out.println("\t --- testSendMessage ---");
		// Connect to server
		client1.send(Packets.NEWUSER, user1);
		client2.send(Packets.NEWUSER, user2);
		Thread.sleep(WAITTIME);
		
		// Connect to user
		client1.send(Packets.STARTCOLLABORATION, user2);
		Thread.sleep(WAITTIME);
		
		// Send Message
		client1.send(Packets.CHAT, "Hello");
		Thread.sleep(WAITTIME);
		System.out.println(client2.getLastPackage().get(0));
		assertTrue(client2.getLastPackage().get(0) == Packets.CHAT);
	}
	
	@Test
	public void testSendMessageUnconnected() throws InterruptedException{
		System.out.println("\t --- testSendMessageUnconnected ---");
		// Connect to server
		client1.send(Packets.NEWUSER, user1);
		client2.send(Packets.NEWUSER, user2);
		Thread.sleep(WAITTIME);
		
		// Send Message
		client1.send(Packets.CHAT, "Hello");
		Thread.sleep(WAITTIME);
		System.out.println(client2.getLastPackage().get(0));
		assertTrue(client2.getLastPackage().get(0) == Packets.ERROR);
	}
}
