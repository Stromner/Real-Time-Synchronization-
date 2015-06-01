package sigmatechnology.se.realtime_file_synchronisation.gui;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import sigmatechnology.se.realtime_file_synchronisation.plugin.Controller;


public class ResponseDialog extends JDialog implements ActionListener{
	private JLabel label;
	private JTextField fileDirectoryTF, nicknameTF, fileDirTF1;
	private JPanel panel, panel1, panel2, panel3;
	private JButton doneButton, cancelButton, fileDirTF2;
	private GridBagConstraints gbc;
	private JFileChooser fileChooser;
	private static final long serialVersionUID = 1L;
	private int width = 90, height = 20, columns = 22;
	
	public ResponseDialog(JFrame frame, String title, String nickname, String fileDir, Launcher launcher) {
		super(frame, title);

		setResizable(false);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		panel = new JPanel(new GridBagLayout());
		panel1 = new JPanel(new GridBagLayout());
		panel2 = new JPanel(new GridBagLayout());
		panel3 = new JPanel(new GridBagLayout());
		
		gbc = new GridBagConstraints();
		
		label = new JLabel();
		label.setText("Nickname:");
		label.setMinimumSize(new Dimension(width, height));
		label.setPreferredSize(new Dimension(width, height));
		label.setMaximumSize(new Dimension(width, height));
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		panel1.add(label, gbc);
		
		nicknameTF = new JTextField();
		nicknameTF.setText(nickname);
		nicknameTF.setEditable(false);
		nicknameTF.setColumns(columns);
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		panel1.add(nicknameTF, gbc);
		
		
		label = new JLabel();
		label.setText("File directory:");
		label.setMinimumSize(new Dimension(width, height));
		label.setPreferredSize(new Dimension(width, height));
		label.setMaximumSize(new Dimension(width, height));
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		panel1.add(label, gbc);
		
		fileDirTF1 = new JTextField();
		fileDirTF1.setText(fileDir);
		fileDirTF1.setEditable(false);
		nicknameTF.setColumns(columns);
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		panel1.add(fileDirTF1, gbc);
		
		
		gbc = new GridBagConstraints();
		
		//Choose file of directory label and button
		label = new JLabel();
		label.setText("Choose file/directory:");
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		//gbc.insets = new Insets(0, 20, 0, 20);
		panel2.add(label, gbc);
		
		fileDirectoryTF = new JTextField();
		fileDirectoryTF.setColumns(columns+2);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		//gbc.insets = new Insets(0, 20, 20, 0);
		panel2.add(fileDirectoryTF, gbc);
		
		fileDirTF2 = new JButton();
		fileDirTF2.setText("Select");
		fileDirTF2.addActionListener(this);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		//gbc.insets = new Insets(0, 0, 20, 20);
		panel2.add(fileDirTF2, gbc);
		
		
		gbc = new GridBagConstraints();
		//OK Button
		doneButton = new JButton();
		doneButton.setText("Done");
		doneButton.addActionListener(this);
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(0, 0, 0, 20);
		panel3.add(doneButton, gbc);
		
		// Cancel button
		cancelButton = new JButton();
		cancelButton.setText("Cancel");
		cancelButton.addActionListener(this);
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(0, 20, 0, 0);
		panel3.add(cancelButton, gbc);
		
		
		gbc = new GridBagConstraints();
		gbc.gridy = 0;
		gbc.insets = new Insets(20, 20, 0, 20);
		panel.add(panel1,gbc);
		gbc.gridy = 1;
		gbc.insets = new Insets(20, 20, 0, 20);
		panel.add(panel2,gbc);
		gbc.gridy = 2;
		gbc.insets = new Insets(20, 20, 20, 20);
		panel.add(panel3,gbc);
		
		getContentPane().add(panel);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModal(true);
		setLocationRelativeTo(null);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		JButton action = (JButton)event.getSource();
		if(action == doneButton){
			if(fileDirectoryTF.getText().equals("")){
				JOptionPane.showMessageDialog(null, "The information required has not been chosen");
				return;
			}
			else{
				System.out.println(fileDirectoryTF.getText());
				// TODO Add ignore list
				
				Controller.getInstance().getClient().setResponseFromDialog(1);
				Controller.getInstance().getLauncher().setFilePath(fileDirectoryTF.getText());
				
				dispose();			
			}
		}
		else if (action == cancelButton){
			Controller.getInstance().getClient().setResponseFromDialog(2);
			dispose();
		}
		else if (action == fileDirTF2){
			fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new java.io.File("."));
			fileChooser.setDialogTitle("choosertitle");
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

