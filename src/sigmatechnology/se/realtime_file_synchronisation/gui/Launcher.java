package sigmatechnology.se.realtime_file_synchronisation.gui;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import sigmatechnology.se.realtime_file_synchronisation.network.Packets;
import sigmatechnology.se.realtime_file_synchronisation.plugin.Controller;

/**
 * Client - Connection to server
 * @author Magnus Källten
 *
 */
public class Launcher extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	private JPanel rightPanel;
	private JButton chatButton;
	private JTextPane updatePane, chatReadPane, chatWritePane;
	private GridBagConstraints gbc;
	private JScrollPane scrollPaneUpdate, scrollPaneChat;
	private JScrollBar vertical;
	private JSplitPane splitPane;
	
	private JMenuBar menuBar;
	private JMenu menuConn, helpMenu;
	private JMenuItem serverConn, serverDisc, userConn, userDisc;
	private JCheckBoxMenuItem cbMenuItem;
	
	private String nickname, friend, filePath;
	private String[] nickList;
	private List<Path> ignoreList;
	
	
	JFrame serverConnect;
	
	public Launcher(){
		// Code from gui.Main
		//this.setTitle("Synchronizer 1.0");
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		buildGUI();
		setSize(new Dimension(600,400));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setVisible(true);
	}
	
	private void buildGUI(){
		gbc = new GridBagConstraints();
		new JFileChooser();
		rightPanel = new JPanel(new GridBagLayout());
		//new JPanel();
		
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
		updatePane.setText("");
		updatePane.setEditable(false);
		updatePane.setBorder(new TitledBorder("Updates"));
		
		
		scrollPaneUpdate = new JScrollPane(updatePane);
		scrollPaneUpdate.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		vertical = scrollPaneUpdate.getVerticalScrollBar();
		vertical.setValue(vertical.getMaximum());
		
		//ChatReadPane
		chatReadPane = new JTextPane();
		chatReadPane.setText("");
		chatReadPane.setBorder(new TitledBorder("Chat"));
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
		chatWritePane.setText("");
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
		
		serverConn.setEnabled(true);
		serverDisc.setEnabled(false);
		userConn.setEnabled(false);
		userDisc.setEnabled(false);
		chatButton.setEnabled(false);
		
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
		
		Document doc = textPane.getDocument();
		// run will be executed on the EDT
		SwingUtilities.invokeLater(new Runnable(){
		    @Override
		    public void run()
		    {
		    	try {
					doc.insertString(doc.getLength(), msg, null);
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		});
		//doc.insertString(doc.getLength(), msg, null);
	}
	
	public void updatePane(String s){
		writeInPane(updatePane, "\n" + s);
	}
	
	public void actionPerformed(ActionEvent event){
		Object source = event.getSource();
		if(source == chatButton){
			if(chatWritePane.getText().equals("") || chatWritePane.getText() == null){
				return;
			}
			else{
				addToChat(chatWritePane.getText(), nickname);
				Controller.getInstance().getClient().send(Packets.CHAT, chatWritePane.getText());
				chatWritePane.setText("");
			}
			
		}
		else if(source == serverConn) {
			ServerConnDialog scd = new ServerConnDialog(new JFrame(), "Server Connection", this);
			scd.setPreferredSize(new Dimension(250,275));
			scd.setResizable(false);
			scd.pack();
			scd.setVisible(true);
        }
		else if(source == userConn){
			UserConnDialog ucd = new UserConnDialog(new JFrame(), "User Connection", this);
			ucd.setPreferredSize(new Dimension(350,375));
			ucd.setResizable(false);
			ucd.pack();
			ucd.setVisible(true);
		}
		else if(source == serverDisc){
			disconnectFromServer();
		}
		else if(source == userDisc){
			disconnectFromClient();
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
		
		msg = "\n------------------------------\n" 
				+ from + ": "
				+ formattedDate + "\n" 
				+ msg;
		writeInPane(chatReadPane, msg);
	}
	
	public void setServerInformation(String serverIP, String serverPort, String nickname){
		this.nickname = nickname;
	}
	
	public void connectToServer(){			
		// Start the client
		Controller.getInstance().initClient();
		Controller.getInstance().getClient().send(Packets.CONNECTSERVER, nickname);
	}
	
	public void serverGrantedConecction(){
		serverConn.setEnabled(false);
		serverDisc.setEnabled(true);
		userConn.setEnabled(true);
		userDisc.setEnabled(false);
		chatButton.setEnabled(false);
		
		updatePane("Connected to the server.");
	}
	
	public void serverDeniedConnection(){
		updatePane("Server denied access.");
		
		// Disconnect the client from the server
		Controller.getInstance().killClient();
	}
	
	public void disconnectFromServer(){
		//TODO server disc msg
		serverConn.setEnabled(true);
		serverDisc.setEnabled(false);
		userConn.setEnabled(false);
		userDisc.setEnabled(false);
		chatButton.setEnabled(false);

		updatePane("Disconnected from the server.");
		
		// Disconnect the client from the server
		Controller.getInstance().killClient();
	}
	
	public void connectToClient(String friend){
		serverConn.setEnabled(false);
		serverDisc.setEnabled(false);
		userConn.setEnabled(false);
		userDisc.setEnabled(true);
		chatButton.setEnabled(true);
		
		// Connect to user
		this.friend = friend;
		updatePane("Connected to " + friend + ".");
		
		Controller.getInstance().initSynchronizeRoot();
	}
	
	public void clientConnectedToUs(String friend){
		serverConn.setEnabled(false);
		serverDisc.setEnabled(false);
		userConn.setEnabled(false);
		userDisc.setEnabled(true);
		chatButton.setEnabled(true);
		
		// Connect to user
		this.friend = friend;
		updatePane(friend + " connected to us.");
		
		// Connect to user
		Controller.getInstance().initSynchronizeRoot();
	}
	
	public void disconnectFromClient(){
		//TODO user disc msg
		serverConn.setEnabled(false);
		serverDisc.setEnabled(true);
		userConn.setEnabled(true);
		userDisc.setEnabled(false);
		chatButton.setEnabled(false);

		updatePane("Disconnected from " + friend + ".");
		
		// Disconnect us from the user
		Controller.getInstance().getClient().send(Packets.STOPCOLLABORATION);
		Controller.getInstance().killSynchronizeRoot();
		friend = null;
	}
	
	public void clientDisconnectedFromUs(){
		//TODO user disc msg
		serverConn.setEnabled(false);
		serverDisc.setEnabled(true);
		userConn.setEnabled(true);
		userDisc.setEnabled(false);
		chatButton.setEnabled(false);
		
		updatePane(friend + " disconnected from us.");
		
		Controller.getInstance().killSynchronizeRoot();
		friend = null;
	}
	
	public void setServerNickList(String[] nickList){
		this.nickList = nickList;
	}
	
	public String[] getServerNickList(){
		return nickList;
	}

	public void setFriendInfo(String filePath, List<Path> ignoreList) {
		this.filePath = filePath;
		this.ignoreList = ignoreList;
	}
	
	public void setFilePath(String filePath){
		this.filePath = filePath;
	}

	public String getFilePath() {
		return filePath;
	}

	public List<Path> getIgnoreList() {
		return ignoreList;
	}
	
	public String getFriend(){
		return friend;
	}	
}
