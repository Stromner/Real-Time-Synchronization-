

public class Main implements Runnable {

	public static void main(String[] args) {
		//Server server = new Server();
		ClientConnection cc, cc2;
		(new Thread(new Server())).start();
		cc = new ClientConnection();
		
		try {
			Thread.sleep(5000);
			System.out.println("5 sec sleep");
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

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("hello from thread");
	}
}
