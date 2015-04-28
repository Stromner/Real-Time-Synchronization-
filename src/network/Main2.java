package network;

import java.util.Scanner;

public class Main2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String data, data2;
		
		(new Thread(new Server())).start();
		Scanner scanInput = new Scanner(System.in);
		data = scanInput.nextLine();
		ClientConnection cc3 = new ClientConnection(data);
		data2 = scanInput.nextLine();
		ClientConnection cc32 = new ClientConnection(data2);
		scanInput.close();
		cc3.send(new Packet(PacketType.CHAT, data, data2, "TestMessage"));
		
		
		/*
		try {
			Thread.sleep(5000);
			System.out.println("5 sec sleep(Between second send)");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cc2 = new ClientConnection();
		*/
		
		//server.run();
		//cc.connect();
		
		/*
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GridBagLayoutDemo gbld = new GridBagLayoutDemo(); 
                gbld.createAndShowGUI();
            }
        });
        */
		
		/*
		Client client = new Client();
		client.setTitle("TestTitle");
		client.setSize(500, 300);
		client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//client.pack();
		client.setVisible(true);
		*/	
	}
}
