package proxy.ui;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import javax.swing.*;

import proxy.settings.Configuration;
import proxy.sip.SipLayer;



 
public class SIPProxyGUI extends JFrame implements MessageProcessor{
 
  JPanel contentPane;
  JLabel jLabel4 = new JLabel();
  JTextField jTextField4 = new JTextField();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTextArea jTextArea1 = new JTextArea();
  JLabel jLabel5 = new JLabel();
  JScrollPane jScrollPane2 = new JScrollPane();
  JTextArea jTextArea2 = new JTextArea();
  JScrollPane jScrollPane3 = new JScrollPane();
  JTextArea jTextArea3 = new JTextArea();
  JScrollPane jScrollPane4 = new JScrollPane();
  JTextArea jTextArea4 = new JTextArea();
  JTabbedPane jTabbedPane1 = new JTabbedPane();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
 // JPanel jPanel3 = new JPanel();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JTextField jTextField1 = new JTextField();
  JLabel jLabel6 = new JLabel();
  JLabel jLabel7 = new JLabel();
  SipLayer siplayer;
  Configuration config;
  
  private JMenuBar menubar;
  private JMenu file;
  private JMenu edit;
  private JMenuItem configurations;
  public static Connection con = null;
  public static Statement statement = null;

 
  public SIPProxyGUI() {
 

    try {
 
      jbInit();
      connect();
      String t = jTextField4.getText();
      
      config=new Configuration();
       siplayer = new SipLayer(jTextField1.getText(),config,this,true);
       siplayer.setMessageProcessor(this);
       siplayer.setRR(true);
}
    catch(Exception e) {
 
      e.printStackTrace();
     
}
   
   
}
 
  public static Connection connect() {
	    String DB_URL = "jdbc:mysql://localhost/mydb";
		//  Database credentials
		 String USER = "root";
		 String PASS = "root123";
		 // Connection con = null;

	    try {
	      //  Class.forName(driver);
	        con = DriverManager.getConnection(DB_URL,USER,PASS);
	        System.out.println("Connection is established");
	        if (con == null) {
	            System.out.println("Connection cannot be established");
	        }
	        statement = null;
	        return con;
	    } catch (Exception e) {
	        System.out.println(e);
	    }
	    return null;
	}

	 
	  private void jbInit() throws Exception  {
	 
	    contentPane = (JPanel) this.getContentPane();

	    contentPane.setLayout(null);
	    this.setSize(new Dimension(1087, 667));
	    this.setTitle("Sipproxy");
	    contentPane.setBackground(SystemColor.control);
	    contentPane.setEnabled(false);
	    contentPane.setForeground(Color.black);
	    contentPane.setAlignmentY((float) 0.5);
	    contentPane.setDebugGraphicsOptions(0);
	    contentPane.setDoubleBuffered(true);
	    contentPane.setMaximumSize(new Dimension(2147483647, 2147483647));
	    contentPane.setMinimumSize(new Dimension(680, 544));
	    contentPane.setOpaque(true);
	    contentPane.setPreferredSize(new Dimension(780, 544));
	    contentPane.setRequestFocusEnabled(true);
	    jLabel4.setText("Proxy port");
	    jLabel4.setBounds(new Rectangle(33, 12, 70, 19));
	    jTextField4.setToolTipText("");
	    jTextField4.setText("5061");
	    jTextField4.setBounds(new Rectangle(107, 12, 59, 23));
	
	    jTextArea1.setFont(new java.awt.Font("MS Sans Serif", 0, 12));
	    jTextArea1.setColumns(0);
	    jTextArea2.setFont(new java.awt.Font("MS Sans Serif", 0, 12));
	    jTextArea2.setText("");
	    jTextArea3.setText("");
	    jPanel1.setLayout(null);
	    jPanel2.setLayout(null);
	 //   jPanel3.setLayout(null);
	  //  jPanel3.setDebugGraphicsOptions(0);
	    jLabel5.setForeground(Color.red);
	    jLabel5.setHorizontalAlignment(SwingConstants.LEADING);
	    jLabel5.setText("");
	    jLabel5.setBounds(new Rectangle(272, 49, 244, 25));
	    jTabbedPane1.setFont(new java.awt.Font("MS Sans Serif", 0, 12));
	    jTabbedPane1.setBounds(new Rectangle(12, 68, 963, 562));
	    
	    jLabel2.setText("Domain");
	    jLabel2.setBounds(new Rectangle(209, 14, 83, 18));
	    jTextField1.setText("sipproxy.com");
	    jTextField1.setBounds(new Rectangle(279, 16, 88, 18));
	    jLabel6.setText("Server Transaction");
	    jLabel6.setBounds(new Rectangle(23, 0, 159, 13));
	    jLabel7.setText("Client Transaction");
	    jLabel7.setBounds(new Rectangle(493, 0, 116, 13));
	    jScrollPane2.setBounds(new Rectangle(482, 16, 473, 517));
	    jScrollPane1.setBounds(new Rectangle(0, 16, 477, 517));
	    jScrollPane4.setBounds(new Rectangle(13, 10, 929, 515));
	    jScrollPane3.setBounds(new Rectangle(10, 9, 939, 520));
	    contentPane.add(jTabbedPane1, null);
	    jTabbedPane1.add(jPanel1,  "Tracer");
	    jPanel1.add(jScrollPane1, null);
	    jPanel1.add(jScrollPane2, null);
	    jPanel1.add(jLabel6, null);
	    jPanel1.add(jLabel7, null);
	    jScrollPane2.getViewport().add(jTextArea2, null);
	    jScrollPane1.getViewport().add(jTextArea1, null);
	    jTabbedPane1.add(jPanel2,  "LocationService");
	    jPanel2.add(jScrollPane4, null);
	   // jTabbedPane1.add(jPanel3,  "Transactions");
	    //jPanel3.add(jScrollPane3, null);
	    jScrollPane3.getViewport().add(jTextArea3, null);
	    jScrollPane4.getViewport().add(jTextArea4, null);
	    contentPane.add(jTextField4, null);
	    contentPane.add(jLabel4, null);
	    contentPane.add(jLabel2, null);
	    contentPane.add(jTextField1, null);
	    contentPane.add(jLabel5, null);
	    contentPane.add(jLabel1, null);
	    jTabbedPane1.setSelectedComponent(jPanel1);
	    
	    
	    menubar = new JMenuBar();
        this.setJMenuBar(menubar);
        
        file = new JMenu("File");
        menubar.add(file);
        edit = new JMenu("Edit");
        menubar.add(edit);
     /*   configurations = new JMenuItem("Configurations");
        edit.add(configurations);
	 
        
  configurations.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
            	
        	siplayer.editor.setVisible(true);
                  //System.exit(0);
                 
            }
      });*/
	 
	   
	}
 
public void displayServer(String text) {
 
  jTextArea1.append(text);
 
}
 
public void displayClient(String text) {
 
  jTextArea2.append(text);
 
}
 
public void clearLocationServiceDisplay() {
 
   jTextArea4.setText("");
 
}
 
public void appendLocationServiceDisplay(String text) {
 
  jTextArea4.append(text);
 
}
 
public void clearOngoingTransactionsDisplay() {
 
   jTextArea3.setText("");
 
}
 
public void appendOngoingTransactionsDisplay(String text) {
 
  jTextArea3.append(text);
 
}

 
  protected void processWindowEvent(WindowEvent e) {
 
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
 
      System.exit(0);
     
}
   
}
 
 
  void jButton2_actionPerformed(ActionEvent e) {
 
    try{
 
      String t = jTextField4.getText();
      siplayer = new SipLayer(jTextField1.getText(),config,this,true);
     
}catch (Exception exc){
 
 
}
}
   
 
  void jButton1_actionPerformed(ActionEvent e) {
 
    siplayer.setOff();
    siplayer=null;
    jLabel5.setText("");
   
}
  
  

}
 
class SipproxyGUI_jButton2_actionAdapter implements java.awt.event.ActionListener {
 
	SIPProxyGUI adaptee;
 
  SipproxyGUI_jButton2_actionAdapter(SIPProxyGUI adaptee) {
 
    this.adaptee = adaptee;
   
}
  public void actionPerformed(ActionEvent e) {
 
    adaptee.jButton2_actionPerformed(e);
   
}
 
}