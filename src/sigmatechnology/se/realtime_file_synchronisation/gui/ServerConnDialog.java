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

import org.eclipse.core.runtime.Platform;

import sigmatechnology.se.realtime_file_synchronisation.Util;


public class ServerConnDialog extends JDialog implements ActionListener{	
	private static final long serialVersionUID = 1L;
	
	private JLabel label;
	private JTextField nickTF, serverIpTF, serverPortTF;
	private JPanel panel;
	private JButton connectButton;
	private GridBagConstraints gbc;
	private Launcher launcher;
	
	private String path;
	private String[] ipAndPort;
	
	public ServerConnDialog(JFrame frame, String title, Launcher launcher) {
		super(frame, title);
		this.launcher = launcher;
		
		path = Platform.getInstallLocation().getURL().toString().substring(6) + "plugins/sigmatechnology.se.realtime_file_synchronisation/config.txt";
		
		panel = new JPanel(new GridBagLayout());
		gbc = new GridBagConstraints();
		
		label = new JLabel();
		nickTF = new JTextField();
		serverIpTF = new JTextField();
		serverPortTF = new JTextField();
		connectButton = new JButton();
		//connectButton.setEnabled(false);
		
		File f = new File(path);
		if(f.exists()){
			ipAndPort = Util.openReadFile(Paths.get(path)).split(":");
		}
		else{
			ipAndPort = null;
		}
		
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
		setLocationRelativeTo(launcher);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		// TODO Call the controller from here?
		if(event.getSource() == connectButton){
			if(nickTF.getText().equals("") || serverIpTF.getText().equals("") || serverPortTF.getText().equals("")){
				JOptionPane.showMessageDialog(null, "One or more of the inputs are empty.");
				return;
			}
			else{
				launcher.setServerInformation(serverIpTF.getText(), serverPortTF.getText(), nickTF.getText());
				//TODO Controller.connectToServer(serverIpTF.getText(), serverPortTF.getText());
				//If the IP or port has changed the new values will be written to the config.txt
				if(ipAndPort == null || serverIpTF.getText().compareTo(ipAndPort[0]) != 0 || serverPortTF.getText().compareTo(ipAndPort[1]) != 0){
					prepareFile(serverIpTF.getText(), serverPortTF.getText());
				}
			}
			
			launcher.connectToServer();
		}
		dispose();
	}
	
	private void prepareFile(String ip, String port){
		// Create the file and folders if they don't exist from a previous run of the program
		File f = new File(path.substring(0, path.length()-10));
		f.mkdir();
		
        f = new File(path);
        try{
        	if(!f.exists()){
        		f.createNewFile();
        	}
        }
        catch(IOException e){
        	e.printStackTrace();
        }
       
        String s = ip + ":" + port;
        
        Util.openWriteFile(Paths.get(path), s);
	}
}
