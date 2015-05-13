package sigmatechnology.se.realtime_file_synchronisation.gui;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import sigmatechnology.se.Util;


public class ServerConnDialog extends JDialog implements ActionListener{

	/**
	 * 
	 */
	String path = "src/sigmatechnology/se/realtime_file_synchronisation/config.txt";
	
	JLabel label;
	JTextField nickTF, serverIpTF, serverPortTF;
	JPanel panel;
	JButton connectButton;
	GridBagConstraints gbc;
	Client client;
	
	String[] ipAndPort;
	
	private static final long serialVersionUID = 1L;
	
	public ServerConnDialog(JFrame frame, String title, Client client) {
		super(frame, title);
		this.client = client;
		
		panel = new JPanel(new GridBagLayout());
		gbc = new GridBagConstraints();
		
		label = new JLabel();
		nickTF = new JTextField();
		serverIpTF = new JTextField();
		serverPortTF = new JTextField();
		serverIpTF = new JTextField();
		connectButton = new JButton();
		//connectButton.setEnabled(false);
		
		ipAndPort = readIpPortFromFile(path);
		
		//Nickname
		label = new JLabel();
		label.setText("Nickname");
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(20, 20, 0, 20);
		panel.add(label, gbc);
		
		
		nickTF.setText("");
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = new Insets(0, 20, 0, 20);
		panel.add(nickTF, gbc);
		
		
		//Server IP
		label = new JLabel();
		label.setText("Server IP");
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.insets = new Insets(20, 20, 0, 20);
		panel.add(label, gbc);
		
		serverIpTF.setText("");
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.insets = new Insets(0, 20, 0, 20);
		panel.add(serverIpTF, gbc);
		
		
		//Server Port
		label = new JLabel();
		label.setText("Server Port");
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.insets = new Insets(20, 20, 0, 20);
		panel.add(label, gbc);
		
		
		serverPortTF.setText("");
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.insets = new Insets(0, 20, 20, 20);
		panel.add(serverPortTF, gbc);
		
		try{
			serverIpTF.setText(ipAndPort[0]);
			serverPortTF.setText(ipAndPort[1]);
		}
		catch(Exception e){}
		
		//Connect Button
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
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		// TODO Call the controller from here?
		/*
		if(event.getSource() == connectButton){
			if(nickTF.getText().equals("") || serverIpTF.getText().equals("") || serverPortTF.getText().equals("")){
				JOptionPane.showMessageDialog(null, "One or more of the inputs are empty.");
				return;
			}
			else{
				//client.setServerInformation(serverIpTF.getText(), serverPortTF.getText(), nickTF.getText());
				System.out.println("here1");
				//TODO Controller.connectToServer(serverIpTF.getText(), serverPortTF.getText());
				//If the IP or port has changed the new values will be written to the config.txt
				if(serverIpTF.getText().compareTo(ipAndPort[0]) != 0 || serverPortTF.getText().compareTo(ipAndPort[1]) != 0){
					prepareFile(serverIpTF.getText(), serverPortTF.getText());
				}
			}
		}
		System.out.println("here2");
		*/
		dispose();
		System.out.println("here3");
		
	}
	
	/**
	 * 
	 * @param path
	 */
	public String[] readIpPortFromFile(String path){
		File file = new File(path);
		String[] textSplitted = {"",""};
		if(file.exists()){
			String text = Util.openReadFile(Paths.get(path));
			
			//If the text file is empty
			if(!text.equals("")){
				textSplitted = text.split("\n");
				// The row after the client config text is interesting for us
				for(int i=0;i<textSplitted.length;i++){
					if(textSplitted[i].toLowerCase().contains("Client".toLowerCase())){
						return textSplitted[i+1].split(":");
					}
				}
			}
		}
		return textSplitted;
	}
	
	private void prepareFile(String ip, String port){
        // Create the file if it doesn't exist from a previous run of the program
        File f = new File(path);
        try{
        	if(!f.exists()){
        		f.createNewFile();
        	}
        }
        catch(IOException e){e.printStackTrace();}
       
        String s = "# Client config\n" + ip + ":" + port;
        
        Util.openWriteFile(Paths.get(path), s);
	}
}
