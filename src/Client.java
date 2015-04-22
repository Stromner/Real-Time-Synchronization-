import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;

/**
 * Client - Connection to server
 * @author Magnus Källten
 *
 */
public class Client extends JFrame implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JPanel panel, updatePanel;
	JButton button1, browseButton;
	JLabel label;
	JTextField directoryText;
	JTextArea updateText;
	JFileChooser fc;
	GridBagConstraints gbc;
	JScrollPane scrollPane;
	ClientConnection cc = new ClientConnection();
	
	public Client(){
		buildGUI();
		cc.connect();		
	}
	
	private void buildGUI(){
		gbc = new GridBagConstraints();
		fc = new JFileChooser();
		panel = new JPanel(new GridBagLayout());
		updatePanel = new JPanel(new BorderLayout());
		
		//TestButton
		button1 = new JButton();
		button1.addActionListener(this);
		button1.setText("Button");
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(button1, gbc);
		
		//Test label
		label = new JLabel();
		label.setText("Label1");
		gbc.fill = GridBagConstraints.HORIZONTAL;
		//gbc.anchor = GridBagConstraints.NORTH;
		gbc.weightx = 4;
		gbc.gridx = 1;
		gbc.gridy = 0;
		panel.add(label, gbc);
		
		
		//Button to browse for a file to sync
		browseButton = new JButton();
		browseButton.setText("Browse");
		browseButton.addActionListener(this);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		//gbc.anchor = GridBagConstraints.CENTER;
		gbc.weightx = 0;
		gbc.gridx = 0;
		gbc.gridy = 1;
		panel.add(browseButton, gbc);
		
		//Label to see what directory the file chosen is from
		directoryText = new JTextField();
		directoryText.setText("Directory");
		gbc.fill = GridBagConstraints.HORIZONTAL;
		//gbc.anchor = GridBagConstraints.CENTER;
		gbc.weightx = 4;
		gbc.gridx = 1;
		gbc.gridy = 1;
		panel.add(directoryText, gbc);
		
		
		
		
		updateText = new JTextArea();
		updateText.setText("Directory");
		updateText.setBorder(new TitledBorder("Updates"));
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 0;
		gbc.weighty = 1;
		//gbc.ipady = ;
		gbc.gridwidth = 5;
		gbc.gridheight = 0;
		gbc.gridx = 0;
		gbc.gridy = 2;
		
		scrollPane = new JScrollPane(updateText);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panel.add(scrollPane, gbc);

		this.add(panel);
		
	}
	public void actionPerformed(ActionEvent event){
		Object source = event.getSource();
		if(source == button1){
			label.setText("pressed button");
		}
		else if(source == browseButton){
			int returnVal = fc.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
            	System.out.println("Abs Path: " + fc.getSelectedFile().getAbsolutePath());
            	System.out.println("Path: " + fc.getSelectedFile().getPath());
            	directoryText.setText(fc.getSelectedFile().getPath());
            }
            else {
                
            }
		}
	}
}
