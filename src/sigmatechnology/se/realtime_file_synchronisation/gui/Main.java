package sigmatechnology.se.realtime_file_synchronisation.gui;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		Client client = new Client();
		client.setSize(new Dimension(600,400));
		client.setLocationRelativeTo(null);
		client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	

		//client.pack();
		client.setVisible(true);
	}
}
