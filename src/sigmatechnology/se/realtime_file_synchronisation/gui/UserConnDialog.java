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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;


public class UserConnDialog extends JDialog implements ActionListener{

	/**
	 * 
	 */
	
	JLabel nickLabel, fileDirectoryLabel, serverPortLabel;
	JTextField nickTF, fileDirectoryField, serverPortTF;
	JPanel panel;
	JButton doneButton, fileDirectoryButton;
	GridBagConstraints gbc;
	Client client;
	JFileChooser fileChooser;
	JScrollPane nickScrollPane;
	JList<String> nickList;
	DefaultListModel<String> model;
	
	private static final long serialVersionUID = 1L;
	
	public UserConnDialog(JFrame frame, String title, Client client) {
		super(frame, title);
		System.out.println("userconndialog");
		this.client = client;
		
		panel = new JPanel(new GridBagLayout());
		gbc = new GridBagConstraints();
		
		
		//Nickname list from server to choose from
		nickLabel = new JLabel();
		nickLabel.setText("Nickname");
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(20, 20, 0, 20);
		panel.add(nickLabel, gbc);
		
		model = new DefaultListModel<String>();
		nickList = new JList<String>(model);
	    String testList[] = client.getServerNickList();
	    updateNickList(testList);
	    gbc.gridy = 1;
	    gbc.gridwidth = 2;
	    gbc.insets = new Insets(0, 20, 0, 20);
	    
	    nickScrollPane = new JScrollPane();
	    nickScrollPane = new JScrollPane(nickList);
	    nickScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panel.add(nickScrollPane, gbc);
		
		
		//Choose file of directory label and button
		fileDirectoryLabel = new JLabel();
		fileDirectoryLabel.setText("Choose file/directory");
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(20, 20, 0, 20);
		panel.add(fileDirectoryLabel, gbc);
		
		fileDirectoryField = new JTextField();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridy = 3;
		//gbc.gridwidth = 2;
		gbc.insets = new Insets(0, 20, 0, 0);
		panel.add(fileDirectoryField, gbc);
		
		fileDirectoryButton = new JButton();
		fileDirectoryButton.setText("Select");
		fileDirectoryButton.addActionListener(this);
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 1;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(0, 0, 0, 20);
		panel.add(fileDirectoryButton, gbc);
		
		
		//OK Button
		doneButton = new JButton();
		doneButton.setText("Done");
		doneButton.addActionListener(this);
		gbc.gridy = 4;
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(20, 20, 20, 20);
		panel.add(doneButton, gbc);
		
		getContentPane().add(panel);
		
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModal(true);
		setLocationRelativeTo(null);
	}
	
	private void updateNickList(String[] nickList) {
		model.clear();
		for(int i=0; i<nickList.length; i++){
			model.addElement(nickList[i]);
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		JButton action = (JButton)event.getSource();
		if(action == doneButton){
			System.out.println("pressed done button");
			updateNickList(new String[] {"fisk", "båt"});
		}
		else if (action == fileDirectoryButton){
			fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new java.io.File("."));
			fileChooser.setDialogTitle("choosertitle");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			fileChooser.setAcceptAllFileFilterUsed(false);

		    if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
		      System.out.println("getCurrentDirectory(): " + fileChooser.getCurrentDirectory());
		      System.out.println("getSelectedFile() : " + fileChooser.getSelectedFile());
		      fileDirectoryField.setText(fileChooser.getSelectedFile().getPath());
		    } 
		    else {
		      System.out.println("No Selection ");
		    }
		}
	}
}

