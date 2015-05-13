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
	JButton chatButton;
	JLabel label;
	JTextField directoryText;
	JTextPane updatePane, chatReadPane, chatWritePane;
	JFileChooser fc;
	GridBagConstraints gbc;
	JScrollPane scrollPaneUpdate, scrollPaneChat;
	JScrollBar vertical;
	JSplitPane splitPane;
	
	JMenuBar menuBar;
	JMenu menuConn, helpMenu;
	JMenuItem serverConn, serverDisc, userConn, userDisc;
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
		menuConn = new JMenu("Connections");
		menuConn.setMnemonic(KeyEvent.VK_A);
		//connMenu.getAccessibleContext().setAccessibleDescription("The only menu in this program that has menu items");
		menuBar.add(menuConn);
		
		helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_B);
		//connMenu.getAccessibleContext().setAccessibleDescription("The only menu in this program that has menu items");
		menuBar.add(helpMenu);

		//a group of JMenuItems
		serverConn = new JMenuItem("Connect to server");
		serverConn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		serverConn.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
		serverConn.addActionListener(this);
		menuConn.add(serverConn);
		
		userConn = new JMenuItem("Connect to user");
		userConn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.ALT_MASK));
		userConn.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
		userConn.addActionListener(this);
		menuConn.add(userConn);
		
		serverDisc = new JMenuItem("Disconnect from server");
		serverDisc.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, ActionEvent.ALT_MASK));
		serverDisc.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
		serverDisc.addActionListener(this);
		menuConn.add(serverDisc);
		
		userDisc = new JMenuItem("Disconnect from user");
		userDisc.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, ActionEvent.ALT_MASK));
		userDisc.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
		userDisc.addActionListener(this);
		menuConn.add(userDisc);

		menuConn.addSeparator();
		cbMenuItem = new JCheckBoxMenuItem("Toggle updates (In construction)");
		cbMenuItem.setMnemonic(KeyEvent.VK_C);
		menuConn.add(cbMenuItem);

		setJMenuBar(menuBar);
		
		updatePane = new JTextPane();
		updatePane.setText("Updates text");
		updatePane.setEditable(false);
		updatePane.setBorder(new TitledBorder("Updates"));
		
		
		scrollPaneUpdate = new JScrollPane(updatePane);
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
		
		chatButton = new JButton();
		chatButton.setText("Send");
		//TODO chat button when fixed
		//chatButton.setEnabled(false);
		chatButton.addActionListener(this);
		chatButton.setPreferredSize(new Dimension(72, 0));
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridx = 1;
		gbc.gridy = 1;
		rightPanel.add(chatButton, gbc);
		
		//Add to split last
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPaneUpdate, rightPanel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(250);
		
		this.add(splitPane);
	}

	/**
	 * Writes text at the bottom of textPane
	 * @param textPane
	 * @param msg
	 */
	private void writeInPane(JTextPane textPane, String msg){
		if(textPane == updatePane){
			//TODO change msg, add time stamp or whatever
		}
		
		StyledDocument doc = textPane.getStyledDocument();
		try {
			doc.insertString(doc.getLength(), msg, null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	public void actionPerformed(ActionEvent event){
		Object source = event.getSource();
		if(source == chatButton){
			if(chatWritePane.getText().equals("") || chatWritePane.getText() == null){
				return;
			}
			else{
				writeInPane(chatReadPane, chatWritePane.getText());
				//TODO set to "" when done testing.
				chatWritePane.setText("Test text");
			}
			
		}
		else if(source == serverConn) {
			//TODO Kolla om man redan är uppkopplad?
			System.out.println("balle1");
			ServerConnDialog scd = new ServerConnDialog(new JFrame(), "Server Connection", this);
			scd.pack();
			scd.setVisible(true);
        }
		else if(source == userConn){
			UserConnDialog ucd = new UserConnDialog(new JFrame(), "User Connection", this);
			ucd.pack();
			ucd.setVisible(true);
		}
		else if(source == serverDisc){
			//TODO
		}
		else if(source == userDisc){
			//TODO
		}
	}
	
	/**
	 * Sends the msg to chat with time stamp
	 * @param msg
	 */
	public void addToChat(String msg, String from){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy H:mm:ss");
		String formattedDate = sdf.format(date);
		
		//TODO Add from who
		msg = "\n------------------------------\n" 
				+ formattedDate + "\n" 
				+ msg;
		writeInPane(chatReadPane, msg);
	}
	
	public void setServerInformation(String serverIP, String serverPort, String nickname){
		this.serverIP = serverIP;
		this.serverPort = serverPort;
		this.nickname = nickname;
	}
	
	public void diffRecieved(){
		//TODO Something to put in the updatePane
		String update = "";
		writeInPane(updatePane, update);
	}
	
	public void diffSent(){
		//TODO Something to put in the updatePane
		String update = "";
		writeInPane(updatePane, update);
	}
	
	public void serverConnected(){
		//TODO server conn msg
		serverConn.setEnabled(false);
		serverDisc.setEnabled(true);
		userConn.setEnabled(true);
		String msg = "";
		writeInPane(updatePane, msg);
	}
	
	public void serverDisconnected(){
		//TODO server disc msg
		serverDisc.setEnabled(false);
		String msg = "";
		writeInPane(updatePane, msg);
	}
	
	public void userConnected(){
		//TODO user conn msg
		chatButton.setEnabled(true);
		userConn.setEnabled(false);
		userDisc.setEnabled(true);
		String msg = "";
		writeInPane(updatePane, msg);
	}
	
	public void userDisconnected(){
		//TODO user disc msg
		chatButton.setEnabled(false);
		userConn.setEnabled(true);
		userDisc.setEnabled(false);
		String msg = "";
		writeInPane(updatePane, msg);
	}
	
	public String [] getServerNickList(){
		//TODO Use nicknames from server
		return new String[]{"Nick9","Nick2","Nick3","Nick4"};
	}

}
