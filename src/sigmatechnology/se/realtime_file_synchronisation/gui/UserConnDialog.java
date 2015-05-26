package sigmatechnology.se.realtime_file_synchronisation.gui;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

import sigmatechnology.se.realtime_file_synchronisation.network.Packets;
import sigmatechnology.se.realtime_file_synchronisation.plugin.Controller;


public class UserConnDialog extends JDialog implements ActionListener{

	/**
	 * 
	 */
	
	JLabel label;
	JTextField nickTF, fileDirectoryTF, serverPortTF;
	JPanel panel;
	JButton doneButton, fileDirectoryButton;
	GridBagConstraints gbc;
	Launcher launcher;
	JFileChooser fileChooser;
	JScrollPane nickScrollPane;
	JList<String> nickList;
	DefaultListModel<String> model;
	
	private static final long serialVersionUID = 1L;
	
	public UserConnDialog(JFrame frame, String title, Launcher launcher) {
		super(frame, title);
		this.launcher = launcher;
		
		panel = new JPanel(new GridBagLayout());
		gbc = new GridBagConstraints();
		
		
		//Nickname list from server to choose from
		label = new JLabel();
		label.setText("Nickname");
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.SOUTH;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(20, 20, 0, 20);
		panel.add(label, gbc);
		
		model = new DefaultListModel<String>();
		nickList = new JList<String>(model);
	    nickList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    String testList[] = launcher.getServerNickList();
	    updateNickList(testList);
	    gbc.fill = GridBagConstraints.BOTH;
	    gbc.anchor = GridBagConstraints.CENTER;
	    gbc.weightx = 1;
		gbc.weighty = 1;
	    gbc.gridy = 1;
	    gbc.gridwidth = 2;
	    gbc.insets = new Insets(0, 20, 0, 20);
	    
	    nickScrollPane = new JScrollPane();
	    nickScrollPane = new JScrollPane(nickList);
	    nickScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panel.add(nickScrollPane, gbc);
		
		
		//Choose file of directory label and button
		label = new JLabel();
		label.setText("Choose file/directory");
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(20, 20, 0, 20);
		panel.add(label, gbc);
		
		fileDirectoryTF = new JTextField();
		fileDirectoryTF.setEditable(false);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridy = 3;
		//gbc.gridwidth = 2;
		gbc.insets = new Insets(0, 20, 0, 0);
		panel.add(fileDirectoryTF, gbc);
		
		fileDirectoryButton = new JButton();
		fileDirectoryButton.setText("Select");
		fileDirectoryButton.addActionListener(this);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridx = 1;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(0, 0, 0, 20);
		panel.add(fileDirectoryButton, gbc);
		
		
		//OK Button
		doneButton = new JButton();
		doneButton.setText("Done");
		doneButton.addActionListener(this);
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridy = 4;
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(20, 20, 20, 20);
		panel.add(doneButton, gbc);
		
		getContentPane().add(panel);
		
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModal(true);
		setLocationRelativeTo(launcher);
	}
	
	private void updateNickList(String[] nickList) {
		model.clear();
		if(nickList != null && nickList.length > 0){
			for(int i=0; i<nickList.length; i++){
				model.addElement(nickList[i]);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		JButton action = (JButton)event.getSource();
		if(action == doneButton){
			if(fileDirectoryTF.getText().equals("") || nickList.isSelectionEmpty()){
				JOptionPane.showMessageDialog(null, "The information required has not been chosen");
				return;
			}
			else{
				String selected = nickList.getSelectedValue();
				System.out.println(selected);
				System.out.println(fileDirectoryTF.getText());
				// TODO Add ignore list
				
				launcher.setFriendInfo(fileDirectoryTF.getText(), null);
				Controller.getInstance().getClient().send(Packets.STARTCOLLABORATION, selected, fileDirectoryTF.getText());
				
				dispose();			
			}
		}
		else if (action == fileDirectoryButton){
			fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new java.io.File("."));
			fileChooser.setDialogTitle("Choose Directory/File");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			fileChooser.setAcceptAllFileFilterUsed(false);

		    if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
		    	System.out.println("getCurrentDirectory(): " + fileChooser.getCurrentDirectory());
		    	System.out.println("getSelectedFile() : " + fileChooser.getSelectedFile());
		    	fileDirectoryTF.setText(fileChooser.getSelectedFile().getPath());
		    } 
		    else {
		    	System.out.println("No Selection ");
		    }
		}
	}
}

