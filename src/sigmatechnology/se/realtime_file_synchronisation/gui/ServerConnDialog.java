package sigmatechnology.se.realtime_file_synchronisation.gui;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class ServerConnDialog extends JDialog implements ActionListener{

	/**
	 * 
	 */
	
	JLabel nickLabel, serverIpLabel, serverPortLabel;
	JTextField nickTF, serverIpTF, serverPortTF;
	JPanel panel;
	JButton connectButton;
	GridBagConstraints gbc;
	Client client;
	
	private static final long serialVersionUID = 1L;
	
	public ServerConnDialog(JFrame frame, String title, Client client) {
		super(frame, title);
		this.client = client;
		
		panel = new JPanel(new GridBagLayout());
		gbc = new GridBagConstraints();
		
		//Nickname
		nickLabel = new JLabel();
		nickLabel.setText("Nickname");
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(20, 20, 0, 20);
		panel.add(nickLabel, gbc);
		
		nickTF = new JTextField();
		nickTF.setText("nickname");
		gbc.gridy = 1;
		gbc.insets = new Insets(0, 20, 0, 20);
		panel.add(nickTF, gbc);
		
		
		//Server IP
		serverIpLabel = new JLabel();
		serverIpLabel.setText("Server IP");
		gbc.gridy = 2;
		gbc.insets = new Insets(20, 20, 0, 20);
		panel.add(serverIpLabel, gbc);
		
		serverIpTF = new JTextField();
		serverIpTF.setText("127.0.0.1");
		gbc.gridy = 3;
		gbc.insets = new Insets(0, 20, 0, 20);
		panel.add(serverIpTF, gbc);
		
		
		//Server Port
		serverPortLabel = new JLabel();
		serverPortLabel.setText("Server Port");
		gbc.gridy = 4;
		gbc.insets = new Insets(20, 20, 0, 20);
		panel.add(serverPortLabel, gbc);
		
		serverPortTF = new JTextField();
		serverPortTF.setText("19999");
		gbc.gridy = 5;
		gbc.insets = new Insets(0, 20, 20, 20);
		panel.add(serverPortTF, gbc);
		
		
		//Connect Button
		connectButton = new JButton();
		connectButton.setText("Connect");
		connectButton.addActionListener(this);
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridy = 6;
		gbc.insets = new Insets(0, 20, 20, 20);
		panel.add(connectButton, gbc);
		
		getContentPane().add(panel);
		
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModal(true);
		setLocationRelativeTo(null);
		//pack();
		
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		// TODO Auto-generated method stub
		// Call the controller from here?
		if(event.getSource() == connectButton){
			client.setServerIP(serverIpTF.getText());
			client.setServerPort(serverPortTF.getText());
			client.setNickname(nickTF.getText());
		}
		dispose();
	}
}
