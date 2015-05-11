package sigmatechnology.se.realtime_file_synchronisation.gui;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;


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
	JPanel leftPanel, rightPanel;
	JButton sendChatButton;
	JLabel label;
	JTextField directoryText;
	JTextPane updateText, chatReadPane, chatWritePane;
	JFileChooser fc;
	GridBagConstraints gbc;
	JScrollPane scrollPaneUpdate, scrollPaneChat;
	JScrollBar vertical;
	JSplitPane splitPane;
	
	JMenuBar menuBar;
	JMenu connMenu, helpMenu;
	JMenuItem connServer, discServer, connUser, discUser;
	JCheckBoxMenuItem cbMenuItem;
	
	String serverIP, serverPort, nickname;
	
	
	JFrame serverConnect;
	
	public Client(){
		
		buildGUI();
		
	}
	
	private void buildGUI(){
		gbc = new GridBagConstraints();
		fc = new JFileChooser();
		rightPanel = new JPanel(new GridBagLayout());
		leftPanel = new JPanel();
		
		
		
		//Where the GUI is created:
		

		//Create the menu bar.
		menuBar = new JMenuBar();

		//Build the first menu.
		connMenu = new JMenu("Connections");
		connMenu.setMnemonic(KeyEvent.VK_A);
		//connMenu.getAccessibleContext().setAccessibleDescription("The only menu in this program that has menu items");
		menuBar.add(connMenu);
		
		helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_B);
		//connMenu.getAccessibleContext().setAccessibleDescription("The only menu in this program that has menu items");
		menuBar.add(helpMenu);

		//a group of JMenuItems
		connServer = new JMenuItem("Connect to server");
		connServer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		connServer.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
		connServer.addActionListener(this);
		connMenu.add(connServer);
		
		connUser = new JMenuItem("Connect to user");
		connUser.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.ALT_MASK));
		connUser.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
		connUser.addActionListener(this);
		connMenu.add(connUser);
		
		discServer = new JMenuItem("Disconnect from server");
		discServer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, ActionEvent.ALT_MASK));
		discServer.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
		connMenu.add(discServer);
		
		discUser = new JMenuItem("Disconnect from user");
		discUser.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, ActionEvent.ALT_MASK));
		discUser.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
		connMenu.add(discUser);

		connMenu.addSeparator();
		cbMenuItem = new JCheckBoxMenuItem("Toggle updates (In construction)");
		cbMenuItem.setMnemonic(KeyEvent.VK_C);
		connMenu.add(cbMenuItem);

		setJMenuBar(menuBar);
		
		updateText = new JTextPane();
		updateText.setText("Updates text");
		updateText.setEditable(false);
		updateText.setBorder(new TitledBorder("Updates"));
		
		
		scrollPaneUpdate = new JScrollPane(updateText);
		scrollPaneUpdate.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		vertical = scrollPaneUpdate.getVerticalScrollBar();
		vertical.setValue(vertical.getMaximum());
		
		//ChatReadPane
		chatReadPane = new JTextPane();
		chatReadPane.setText("test chat text");
		chatReadPane.setBorder(new TitledBorder("ReadChat"));
		chatReadPane.setEditable(false);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 3;
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.gridy = 0;
		
		scrollPaneChat = new JScrollPane(chatReadPane);
		scrollPaneChat.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		vertical = scrollPaneChat.getVerticalScrollBar();
		vertical.setValue(vertical.getMaximum());
		rightPanel.add(scrollPaneChat, gbc);
		
		//ChatWritePane
		chatWritePane = new JTextPane();
		chatWritePane.setText("Write chat test");
		//chatWritePane.setBorder(new TitledBorder("WriteChat"));
		chatWritePane.setPreferredSize(new Dimension(0, 72));
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 1;
		
		scrollPaneChat = new JScrollPane(chatWritePane);
		scrollPaneChat.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		rightPanel.add(scrollPaneChat, gbc);
		
		sendChatButton = new JButton();
		sendChatButton.setText("Send");
		sendChatButton.addActionListener(this);
		sendChatButton.setPreferredSize(new Dimension(72, 0));
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridx = 1;
		gbc.gridy = 1;
		rightPanel.add(sendChatButton, gbc);
		
		
		//panel.add(scrollPaneChat, gbc);
		
		//Add to split last
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPaneUpdate, rightPanel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(250);
		
		this.add(splitPane);
		
	}
	
	//Unnecessary at the moment
	private void updateUpdateText(String text) {
		writeInPane(updateText, text);
	}

	/**
	 * Writes text at the bottom of textPane
	 * @param textPane
	 * @param text
	 */
	private void writeInPane(JTextPane textPane, String text){
		StyledDocument doc = textPane.getStyledDocument();
		
		try {
			doc.insertString(doc.getLength(), text, null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	public void actionPerformed(ActionEvent event){
		Object source = event.getSource();
		if(source == sendChatButton){
			System.out.println("Pressed send chat button");
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy H:mm:ss");
			String formattedDate = sdf.format(date);
			
			if((chatWritePane.getText() == null) || (chatWritePane.getText().equals(""))){
				return;
			}
			else{
				writeInPane(chatReadPane, "\n------------------------------\n" 
						+ formattedDate + "\n" 
						+ chatWritePane.getText());
				chatWritePane.setText("");
			}
			
		}
		else if(source == connServer) {
			//Låsa fönster?
			//Kolla om man redan är uppkopplad?
			
			ServerConnDialog scd = new ServerConnDialog(new JFrame(), "Server Connection", this);
			scd.pack();
			scd.setVisible(true);
        }
		else if(source == connUser){
			UserConnDialog ucd = new UserConnDialog(new JFrame(), "title - ucd", this);
			ucd.pack();
			ucd.setVisible(true);
		}
			
	}
	
	public void setServerIP(String serverIP){
		this.serverIP = serverIP;
		updateText.setText(serverIP);
		//Add string to file?
	}
	public void setServerPort(String serverPort){
		this.serverPort = serverPort;
	}
	public void setNickname(String nickname){
		this.nickname = nickname;
	}
	
	public String [] getServerNickList(){
		return new String[]{"Nick9","Nick2","Nick3","Nick4"};
	}
	
	/*
	public void readFromFile(String path){
		String text = Util.openReadFile(Paths.get(path));
		String[] textSplitted = text.split("\n");
				
		// The row after the client config text is interesting for us
		for(int i=0;i<textSplitted.length;i++){
			if(textSplitted[i].toLowerCase().contains("Client".toLowerCase())){
				String[] s = textSplitted[i+1].split(":");
			}
		}
	}
	*/
}
