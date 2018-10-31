package sipclient.sip;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;

import javax.sdp.MediaDescription;
import javax.sdp.SdpConstants;
import javax.sdp.SdpException;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import sipclient.ui.SipClientGUI;


public class RulesEditor extends JFrame {

	
	private JLabel methods;
	private JLabel requestMethod;
	private JList requestList;
	private JLabel responseMethod;
	private JList responseList;
	private JLabel sourceIP;
	private JTextField source;
	private JLabel destinationIP;
	private JTextField destination;
	
	private JLabel sourcePort;
	private JTextField source_port;
	private JLabel destinationPort;
	private JTextField destination_port;
	
	private JLabel attributes;
	private JLabel mediaCodec;
	private JList media; 
	private JLabel bandwidth;
	private JTextField bandw;
	private JLabel actionl;
	private JComboBox actionList;
	//private JRadioButton allowed;
	private JRadioButton blocked;
	private JButton addRules;
	private JButton delete;
	private JLabel rules;
	private JScrollPane jScrollPaneTable;
	private static JTable jTable;
	protected String requestSelecteditem;
	protected String responseSelecteditem;
	protected String mediaSelecteditem;
	protected String table_sourceIP; 
	protected String table_destinationIP ;
	protected String table_sourcePort;
	protected String table_destinationPort;
	protected String table_bandwith ;
	Properties prop;
	SipLayer sipLayer;
	SipClientGUI gui;
	Hashtable<String, Integer> codecs ;
	

	static Statement statement = null;
	Connection conn ;
	protected PreparedStatement pstmt = null;

	public ArrayList<ArrayList<String>> blockedList = new ArrayList<ArrayList<String>>();
	    
	
	public RulesEditor()
	{
	    
	setSize(400, 700);
	setTitle("Rule Editor");
	
		codecs = new Hashtable<String, Integer>();
		codecs.put("G723", SdpConstants.G723);
		codecs.put("GSM", SdpConstants.GSM);
		codecs.put("PCMU", SdpConstants.PCMU);
		codecs.put("DVI4_8000", SdpConstants.DVI4_8000);
		codecs.put("DVI4_16000", SdpConstants.DVI4_16000);
		codecs.put("PCMA", SdpConstants.PCMA);
		codecs.put("G728", SdpConstants.G728);
		codecs.put("H263", SdpConstants.H263);
		codecs.put("JPEG", SdpConstants.JPEG);
		codecs.put("H261", SdpConstants.H261);
        
        try {
	    init();
	    System.out.println("Creating statement...");
	    conn = gui.connect();
	     statement = conn.createStatement();
	     
	//     getRecords();
        } catch (SQLException e) {
 	    // TODO Auto-generated catch block
 	    e.printStackTrace();
 	
	} catch (SdpException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
        
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
             //   System.exit(0);
            }
        });
        
	prop = new Properties();
	
		
	}

	public  ArrayList<String> list =new ArrayList<String>();
	private String  action;
	private String source_ip;
	private String source_p;
	private String destination_ip;
	private String destination_p;
	private String request_method;
	private String response_method;
	private String  codec;
	private String band;
	private RulesGroup t_action;
	    public void getRecords() {
	
	 try {
	     DefaultTableModel tableModel = (DefaultTableModel) jTable.getModel();
	     String sql2;
		    sql2="SELECT * FROM blockedlist";
		    ResultSet rs;
		 
		     rs = statement.executeQuery(sql2);

		     System.out.println("get record");
		     
		   
		     t_action = new RulesGroup("");
	          
	          int i=1;
		 	       while(rs.next()){
		 	     
		 	         action = rs.getString("ACTION");
		 	          source_ip = rs.getString("SOURCE_IP");
		 	          source_p =rs.getString("SOURCE_PORT");
		 	        
		 	          destination_ip= rs.getString("DESTINATION_IP");
		 	          destination_p =rs.getString("DESTINATION_PORT");
		 	          request_method= rs.getString("REQUEST_METHOD");
		 	          response_method= rs.getString("RESPONSE_METHOD");
		 	          codec= rs.getString("CODEC");
		 	          band= rs.getString("BANDWITH");	
		 	          
		 	    
		 	          list.add(0, action);
		 	          list.add(1,source_ip);
		 	          list.add(2,source_p);
		 	          list.add(3, destination_ip != null ? rs.getString("DESTINATION_IP") : "(None)");
		 	          list.add(4,destination_p);
		 	          list.add(5,request_method != null ?request_method : "(None)");
		 	          list.add(6,response_method);
		 	          list.add(7,codec);
		 	          list.add(8, band);
		 	          
		 	      if(i ==1){
					   t_action.setLabel(action);
			         
					
			          i++;
		 	      }
		 	 	
		 	
				
		 	    
		 	    	 RulesGroup t_sourceIp = new RulesGroup(source_ip != null ? source_ip : "(None)");
			          RulesGroup t_sourcePort = new RulesGroup(source_p != null ? source_p : "(None)");
			          RulesGroup t_destIp = new RulesGroup(destination_ip != null ? destination_ip : "(None)");
			          RulesGroup t_destPort = new RulesGroup(destination_p != null ? destination_p : "(None)");
			          RulesGroup t_requestMes = new RulesGroup(request_method != null ? request_method : "(None)");
			         RulesGroup t_responseMes = new RulesGroup(response_method != null ? response_method : "(None)");
			          RulesGroup t_codec = new RulesGroup( (codecs.get(codec) != null ? codecs.get(codec).toString() : "(None)"));
			          RulesGroup t_band = new RulesGroup(band != null ? band : "(None)");
			          
			        
			         
			          t_action.addChild(t_sourceIp);
			          t_sourceIp.addChild(t_sourcePort);
			          t_sourcePort.addChild(t_destIp);
			          t_destIp.addChild(t_destPort);
			          t_destPort.addChild(t_requestMes);
			          t_requestMes.addChild(t_responseMes);
			          t_responseMes.addChild(t_codec);
			          t_codec.addChild(t_band);
		 	    
		 	        System.out.println(" ************   PREORDER ***************");
		 	       
		 	       t_action.displayPreorder1(0);
		 	          //Display values
		 	      
		 	          System.out.println("list :   "+list.toString());
		 	          tableModel.addRow(new Object[] {
		 	        	action,source_ip,source_p,destination_ip,destination_p,request_method,response_method,codec,band});
		 	       
		 	       }
	    
	
	 	} catch (SQLException e) {
	 	    // TODO Auto-generated catch block
	 	    e.printStackTrace();
	 	}
	   }

	    public RulesGroup RulesTree(){
	    
	 	       return t_action;
	    }
	    
	public void init() throws SdpException {
	    
		String[] requestData = { "REGISTER", "INVITE", "ACK", "CANCEL","OPTIONS","BYE" };
		String[] responseData = { "200", "100", "101", "108","180","406","486"};
		String[] mediaData = {"G723","GSM","PCMU","PCMA","DVI4_8000","DVI4_16000","G728","H263","JPEG","H261"};
		String[] actionData = { "Allowed", "Blocked"};
		
		methods = new JLabel();
		requestMethod = new JLabel();
		requestMethod.setText("Request Method  : ");
		requestList = new JList(requestData);
		requestList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		requestList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		requestList.setVisibleRowCount(-1);
		JScrollPane requestlistScroller = new JScrollPane(requestList);
		requestlistScroller.setPreferredSize(new Dimension(105, 65));
		
		responseMethod = new JLabel();
		responseMethod.setText("Response Method : ");
		responseList = new JList(responseData);
		responseList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		responseList.setLayoutOrientation(JList.VERTICAL);
		responseList.setVisibleRowCount(-1);
		JScrollPane responselistScroller = new JScrollPane(responseList);
		responselistScroller.setPreferredSize(new Dimension(105, 65));
		sourceIP = new JLabel();
		sourceIP.setText("Source IP         : ");
		source = new JTextField();
		sourcePort = new JLabel();
		sourcePort.setText("Source Port        : ");
		source_port= new JTextField();
		destinationIP = new JLabel();
		destinationIP.setText("Destination IP : ");
		destination = new JTextField();
		destinationPort = new JLabel();
		destinationPort.setText("Destination Port : ");
		destination_port = new JTextField();
		attributes = new JLabel();
		mediaCodec = new JLabel();
		mediaCodec.setText("Media Codec : ");
		media = new JList(mediaData); 
		media.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		media.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		media.setVisibleRowCount(-1);
		JScrollPane medialistScroller = new JScrollPane(media);
		medialistScroller.setPreferredSize(new Dimension(135, 220));
		
		bandwidth = new JLabel();
		bandwidth.setText("Bandwith : ");
		bandw = new JTextField();
		actionl = new JLabel();
		actionl.setText("Action        : ");
		//	allowed = new JRadioButton("Allowed");
		//allowed.setMnemonic(KeyEvent.VK_B);
		//allowed.setActionCommand("Allowed");
		//allowed.setSelected(true);
		blocked = new JRadioButton("Blocked");
		blocked.setMnemonic(KeyEvent.VK_C);
		blocked.setActionCommand("Blocked");
    	    	rules = new JLabel();
		rules.setText("Rules   ");
		addRules = new JButton();
		addRules.setText("Add Rule");
		delete = new JButton();
		delete.setText("Delete");
    		jScrollPaneTable = new javax.swing.JScrollPane();
    		jTable = new javax.swing.JTable();
		
	        
		 jTable.setModel(new javax.swing.table.DefaultTableModel(
	                new Object [][] {},
	                new String [] {
	                   "Action", " Source IP"," Source Port "," Destination IP","Destination Port", "Request Method","Response Method", "Codec", "Bandwith"}
	            ) {
	                Class[] types = new Class [] { java.lang.String.class,java.lang.String.class,java.lang.String.class,java.lang.String.class,java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class };
	                boolean[] canEdit = new boolean [] {false,false,false,false, false, false,false, false, false};

	                public Class getColumnClass(int columnIndex) {
	                    return types [columnIndex];
	                }

	                public boolean isCellEditable(int rowIndex, int columnIndex) {
	                    return canEdit [columnIndex];
	                }
	            });
	            jScrollPaneTable.setViewportView(jTable);
		
	          
	            requestList.addListSelectionListener(new ListSelectionListener(){
	                public void valueChanged(ListSelectionEvent e){
	                      requestSelecteditem = requestList.getSelectedValue().toString();
	                }
	          });
	            
	            responseList.addListSelectionListener(new ListSelectionListener(){
	                public void valueChanged(ListSelectionEvent e){
	                      responseSelecteditem = responseList.getSelectedValue().toString();
	                }
	          });
	            
	            media.addListSelectionListener(new ListSelectionListener(){
	                public void valueChanged(ListSelectionEvent e){
	                      mediaSelecteditem = media.getSelectedValue().toString();
	                }
	          });
		
		
    GroupLayout layout = new GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setAutoCreateGaps(true);
    layout.setAutoCreateContainerGaps(true);

    layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING)
    		.addGroup(layout.createSequentialGroup()
    				.addGroup(layout.createParallelGroup(Alignment.LEADING)
        				.addGroup(layout.createSequentialGroup()
        						.addComponent(requestMethod)
        						.addComponent(requestlistScroller ,GroupLayout.PREFERRED_SIZE ,GroupLayout.PREFERRED_SIZE ,GroupLayout.PREFERRED_SIZE))
        				.addGroup(layout.createSequentialGroup()
        						.addComponent(responseMethod)
        						.addComponent(responselistScroller,GroupLayout.PREFERRED_SIZE ,GroupLayout.PREFERRED_SIZE ,GroupLayout.PREFERRED_SIZE))
        				.addGroup(layout.createSequentialGroup()
        						.addComponent(sourceIP)
        						.addComponent(source))
        				.addGroup(layout.createSequentialGroup()
        						.addComponent(sourcePort)
        						.addComponent(source_port))
        				.addGroup(layout.createSequentialGroup()
        					.addComponent(destinationIP)
        					.addComponent(destination))
        				.addGroup(layout.createSequentialGroup()
        					.addComponent(destinationPort)
        					.addComponent(destination_port))
        				.addGroup(layout.createSequentialGroup()
        						.addComponent(mediaCodec)
        						.addComponent(medialistScroller,GroupLayout.PREFERRED_SIZE ,GroupLayout.PREFERRED_SIZE ,GroupLayout.PREFERRED_SIZE))
        				.addGroup(layout.createSequentialGroup()
        						.addComponent(bandwidth)
        						.addComponent(bandw))
        				.addGroup(layout.createSequentialGroup()
                			.addComponent(actionl)
             //   			.addComponent(allowed)
                			.addComponent(blocked))
                			.addGroup(layout.createSequentialGroup()
                				.addComponent(addRules)
                				.addComponent(delete)))
            .addGroup(layout.createParallelGroup(Alignment.LEADING) 
            		.addComponent(rules)
            		.addComponent(jScrollPaneTable ,javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
            		))         
    );
    

    layout.setVerticalGroup(layout.createSequentialGroup()
    		.addGroup(layout.createParallelGroup(Alignment.LEADING)
    		.addGroup(layout.createSequentialGroup() 
    	  
    	        				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
    	        					.addComponent(requestMethod)
    	        					.addComponent(requestlistScroller ,GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE))
    	        				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
    	        					.addComponent(responseMethod)
    	        					.addComponent(responselistScroller,GroupLayout.PREFERRED_SIZE ,GroupLayout.PREFERRED_SIZE ,GroupLayout.PREFERRED_SIZE))
    	        				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
    	        					.addComponent(sourceIP)
    	        					.addComponent(source))
    	        				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
    	        					.addComponent(sourcePort)
    	        					.addComponent(source_port))
    	        				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
    	        					.addComponent(destinationIP)
    	        					.addComponent(destination))
    	        				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
    	        					.addComponent(destinationPort)
    	        					.addComponent(destination_port))
    	        				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
    	        					.addComponent(mediaCodec)
    	            	      			.addComponent(medialistScroller,GroupLayout.PREFERRED_SIZE ,GroupLayout.PREFERRED_SIZE ,GroupLayout.PREFERRED_SIZE))
    	            	      			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
    	            	      				.addComponent(bandwidth)
    	            	      				.addComponent(bandw))
    	            	      			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
    	            	      				.addComponent(actionl)
    	            	    //  				.addComponent(allowed)
    	            	      				.addComponent(blocked))
    	            	      			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
    	            	      				.addComponent(addRules)
    	            	      				.addComponent(delete)))//)
    	   .addGroup(layout.createSequentialGroup() 
            		.addComponent(rules)
            		.addComponent(jScrollPaneTable ,javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE))
    	            	        	    )	);
    
    pack();
    
    
    addRules.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
            addRulesBtnActionPerformed(evt);
        }
    });
}

private void addRulesBtnActionPerformed(ActionEvent evt) {

    try
    {
        updateTable();
    } catch (Throwable e)
    {
        e.printStackTrace();
    }
    			
}

	 private void updateTable() throws SdpException {
	        
	        DefaultTableModel tableModel = (DefaultTableModel) jTable.getModel();
	        String table_action = "";
	     
	        if(blocked.isSelected()) {
	        	table_action = blocked.getActionCommand();
	        }
	        table_sourceIP = source.getText(); 
	        table_sourcePort = source_port.getText();
	        table_destinationIP = destination.getText();
	        table_destinationPort = destination_port.getText();
	        table_bandwith = bandw.getText();
	        
	    
	        // Add a new line to the table.
	        tableModel.addRow(new Object[] {
	        		table_action,table_sourceIP,table_sourcePort,table_destinationIP,destination_port,requestSelecteditem,responseSelecteditem,mediaSelecteditem,table_bandwith});
	    
		    try{
		    
		       String sql ;
		       sql = "INSERT INTO blockedlist (ACTION , SOURCE_IP , SOURCE_PORT,DESTINATION_IP ,DESTINATION_PORT, REQUEST_METHOD, RESPONSE_METHOD , CODEC, BANDWITH )VALUES (?,?,?,?,?,?,?,?,?)";
		       pstmt = conn.prepareStatement(sql); // create a statement
		       
		      
		       pstmt.setString(1,table_action ); // set input parameter 2
		       pstmt.setString(2, table_sourceIP); // set input parameter 3
		       pstmt.setString(3,table_sourcePort);
		       pstmt.setString(4, table_destinationIP); 
		       pstmt.setString(5,table_destinationPort);
		       pstmt.setString(6,requestSelecteditem ); 
		       pstmt.setString(7, responseSelecteditem); 
		       pstmt.setString(8, mediaSelecteditem); 
		       pstmt.setString(9, table_bandwith); 
		       
		     
		       
		       pstmt.executeUpdate(); // execute insert statement
		       
		     //  getRecords();
		 
		    } catch (SQLException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			}
		
	 
	 }


	public static JTable getjTable() {
	    return jTable;
	}

	public static void setjTable(JTable jTable) {
	    RulesEditor.jTable = jTable;
	}

	
}

class RulesGroup extends RulesTree<String> { 
    RulesGroup(String Name) {      // Constructor
          super.setLabel(Name);
         }
   
   }


