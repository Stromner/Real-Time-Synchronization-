package network;

public class Main2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ClientConnection cc, cc2;
		//(new Thread(new Server())).start();
		cc = new ClientConnection();
		Thread thread = new Thread(new Server());
		thread.start();
		
		try {
			Thread.sleep(5000);
			System.out.println("5 sec sleep(Between second send)");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cc2 = new ClientConnection();
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
