package sipclient.ui;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import javax.swing.GroupLayout;
import javax.sip.InvalidArgumentException;
import javax.sip.ObjectInUseException;
import javax.sip.TransportNotSupportedException;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.plaf.SpinnerUI;

import sipclient.settings.Configuration;
import sipclient.sip.SipLayer;


public class SipClientGUI 
	extends JFrame
	implements MessageProcessor
{
    
    JPanel contentPane;
    JTabbedPane jTabbedPane1 = new JTabbedPane();
    JPanel jPanel1 = new JPanel();
    JPanel jPanel2 = new JPanel();
    JPanel jPanel3 = new JPanel();
    
    
    JLabel jlabel_userId = new JLabel();
    JTextField jt_userId = new JTextField();
    JLabel jl_sipproxy = new JLabel();
    JTextField jt_sipproxy = new JTextField();
    JLabel jl_port = new JLabel();
    JTextField jt_port = new JTextField();
    JScrollPane jScrollPane1 = new JScrollPane();
    JTextArea jTextArea1 = new JTextArea();
    JTextField jt_name = new JTextField();
    JLabel jl_name = new JLabel();
  
    JLabel jt_media = new JLabel();
    JComboBox jComboBox1 = new JComboBox();
    JLabel jl_codecAudio = new JLabel();
    JComboBox jComboBox2 = new JComboBox();
    JLabel jl_codecVideo = new JLabel();
    JComboBox jComboBox3 = new JComboBox();
    JButton jb_apply = new JButton();
    JLabel jl_mediaconf = new JLabel();
    JLabel jl_userconf = new JLabel();
    static JTable jTable = new JTable();
    JLabel jl_band = new JLabel();
    JScrollPane jScrollPaneTable = new JScrollPane();
    SpinnerNumberModel spinnerNumberModel  = new SpinnerNumberModel(128, 0, 300, 1);
    JSpinner spinner  = new JSpinner(spinnerNumberModel) ;
    
    private int bandwidth;
   
    private  String ip;
    
    private SipLayer sipLayer;
    
    private JTextField fromAddress;
    private JLabel fromLbl;
    private JLabel receivedLbl;
	private JTextArea Messages;
    private JScrollPane receivedScrollPane;
    private JButton sendBtn;
    private JButton inviteBtn;
    private JButton byeBtn;
    private JButton registerBtn;
    private JLabel sendLbl;
    private JTextField sendMessages;
    private JTextField toAddress;
    private JLabel toLbl;
    
    private JMenuBar menubar;
    private JMenu file;
    private JMenu edit;
    private JMenuItem configurations;
    private JMenuItem exit;
    private  Configuration config;
    
    
   GroupLayout layout1;
   
   public static Connection connect() {
       String DB_URL = "jdbc:mysql://localhost/mydb";
	//  Database credentials
	 String USER = "root";
	 String PASS = "root123";
	  Connection con = null;
	  //Statement statement = null;
   
       try {
         //  Class.forName(driver);
           con = DriverManager.getConnection(DB_URL,USER,PASS);
           if (con == null) {
               System.out.println("Connection cannot be established");
           }
           return con;
       } catch (Exception e) {
           System.out.println(e);
       }
       return null;
   }
  
 
   Statement statement = null;
   
  
    public SipClientGUI() {
	enableEvents(AWTEvent.WINDOW_EVENT_MASK);
	
	
	    try{
	        
		
		connect();
		 ip = InetAddress.getLocalHost().getHostAddress();
	        
	        
	        config=new Configuration();
	        initWindow();
	       // config.sipPort=Integer.parseInt(jt_port.getText());
	       // config.name=jt_name.getText();
	        //config.userID=jt_userId.getText();
	   
	        if (jComboBox2.getSelectedItem().equals("GSM")) {
	   
	          config.audioCodec=3;
	         
	  }
	        else config.audioCodec=4;
	   
	        if (jComboBox1.getSelectedItem().equals("Audio and Video")) {
	   
	        
	          if (jComboBox3.getSelectedItem().equals("JPEG")) {
	   
	            config.videoCodec=26;
	           
	  }
	          else config.videoCodec=34;
	         
	  }
	       bandwidth = (Integer) spinner.getValue();
	       config.bandwidth =bandwidth;
	       
	      
	       sipLayer = new SipLayer(config,this,jt_sipproxy.getText());
	       sipLayer.setMessageProcessor(this);
	       sipLayer.userInput(0, "");
	   
	         
	         
	       
	        
	  }catch (Exception exc){
	  }
	    
	   
    }

 
    private void initWindow() throws UnknownHostException {
	
	
	
	 contentPane = (JPanel) this.getContentPane();
	  contentPane.setLayout(null);
	    this.setSize(new Dimension(918, 686));
	    this.setTitle(" SIP. Softphone1");
	    contentPane.setBackground(SystemColor.control);
	    contentPane.setEnabled(false);
	    contentPane.setForeground(Color.black);
	    contentPane.setAlignmentY((float) 0.5);
	    contentPane.setDebugGraphicsOptions(0);
	    contentPane.setDoubleBuffered(true);
	    contentPane.setMaximumSize(new Dimension(2147483647, 2147483647));
	    contentPane.setMinimumSize(new Dimension(1180, 744));
	    contentPane.setOpaque(true);
	    contentPane.setPreferredSize(new Dimension(1180, 744));
	    contentPane.setRequestFocusEnabled(true);
	    
	    jTabbedPane1.setTabPlacement(JTabbedPane.TOP);
	    jTabbedPane1.setEnabled(true);
	    jTabbedPane1.setForeground(Color.black);
	    jTabbedPane1.setAlignmentY((float) 0.5);
	    jTabbedPane1.setDebugGraphicsOptions(0);
	    jTabbedPane1.setDoubleBuffered(false);
	    jTabbedPane1.setMaximumSize(new Dimension(32767, 32767));
	    jTabbedPane1.setMinimumSize(new Dimension(700, 500));
	    jTabbedPane1.setOpaque(false);
	    jTabbedPane1.setPreferredSize(new Dimension(700, 500));
	    jTabbedPane1.setRequestFocusEnabled(true);
	    jTabbedPane1.setToolTipText("");
	    jTabbedPane1.setVerifyInputWhenFocusTarget(true);
	    jTabbedPane1.setBounds(new Rectangle(162, 31, 342, 317));//DÜZELT
	    
	    
	    
	    jlabel_userId.setText("My user ID");
	    jt_userId.setSelectionEnd(18);
	    jt_userId.setText(config.name+"@"+ip+":"+config.getSipPort());
	    jl_port.setText("My SIP port                                                                           ");
	    jt_port.setToolTipText("");
	    jt_port.setText(""+config.getSipPort());
	    jl_sipproxy.setText("My SIP proxy");
	    jt_sipproxy.setText(ip+":5061");
	    jl_name.setText("My Name                                                                              ");
	    jt_name.setText(config.getName());
	    jt_media.setText("Media");
	    jl_band.setText("Bandwidth ");
	    jl_codecAudio.setText("Codec audio");
	    jl_codecVideo.setText("Codec video");
	    jb_apply.setFont(new java.awt.Font("SansSerif", 0, 12));
	    jb_apply.setText("Apply media config.");
	    jb_apply.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	        	try {
			    jb_apply_BtnActionPerformed(evt);
			} catch (TransportNotSupportedException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			} catch (ObjectInUseException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			} catch (InvalidArgumentException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			}
	            }
	        });
	    jb_apply.addActionListener(new zphone2GUI_jb_apply_actionAdapter(this));
	    jl_mediaconf.setText("----------------------media configuration----------------------");
	    jl_userconf.setText("----------------------user configuration----------------------");
	
	    
	   layout1 = new GroupLayout(jPanel1);
	    layout1.setAutoCreateGaps(true);
	        layout1.setAutoCreateContainerGaps(true);
	    
	   
	    
    jPanel2.setLayout(null);
	    
	          
    jPanel3.setLayout(null);
    jPanel3.add(jScrollPaneTable,null);
    jScrollPaneTable.getViewport().add(jTable,null);
    jScrollPaneTable.setBounds(new Rectangle(2, 3, 1032, 685));
   
   
   jPanel1.setLayout(layout1 );
   jScrollPane1.setBounds(new Rectangle(2, 3, 832, 685));
   
	    jTabbedPane1.add(jPanel2,    "Tracer");
	    jPanel2.add(jScrollPane1, null);
	    jTabbedPane1.add(jPanel1,   "Configuration");
	    jScrollPane1.getViewport().add(jTextArea1, null);
	    jTextArea1.setVisible(true);
	    jTextArea1.setEditable(true);
	    jTextArea1.setLineWrap(true);
	    jTextArea1.setWrapStyleWord(true);
	    
	    jTabbedPane1.add(jPanel3,    "Messages");
	    jPanel1.add(jl_mediaconf, null);
	    jPanel1.add(jl_userconf, null);
	    jPanel1.add(jComboBox2, null);
	    jPanel1.add(jl_name, null);
	    jPanel1.add(jt_name, null);
	    jPanel1.add(jt_port, null);
	    jPanel1.add(jComboBox3, null);
	    jPanel1.add(jlabel_userId, null);
	    jPanel1.add(jt_userId, null);
	    jPanel1.add(jb_apply, null);
	    jPanel1.add(jl_codecAudio, null);
	    jPanel1.add(jl_codecVideo, null);
	    jPanel1.add(jComboBox1, null);
	    jPanel1.add(jt_media, null);
	    jPanel1.add(jl_port, null);
	    jComboBox1.addItem("Audio only");
	    jComboBox1.addItem("Audio and Video");
	    jComboBox2.addItem("GSM");
	    jComboBox2.addItem("G723");
	    jComboBox3.addItem("JPEG");
	    jComboBox3.addItem("H263");
	    jPanel1.add(jl_band, null);
	    jPanel1.add(spinner, null);
	    
	
	   
	    String [][] table_rows=new String [jTable.getRowCount()][jTable.getColumnCount()];
	   
	    
	    jTable.setModel(new javax.swing.table.DefaultTableModel(
	                new Object [][] {

	                },
	                new String [] {
	                    "Time", "URI", "From", "To", "Call-ID", "CSeq", "Dialog", "Transaction", "Type", "Request/Response","Media-Description","Bandwitdh"
	                }//"SDP-Version","SDP-Session-Identifier","Session-Name",
	            ) {
	                Class[] types = new Class [] {
	                    java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class,java.lang.String.class,java.lang.String.class//,java.lang.String.class,java.lang.String.class
	                };
	                boolean[] canEdit = new boolean [] {
	                    false, false, false, false, false, false, false, false, false, false,false,false//,false,false
	                };

	                public Class getColumnClass(int columnIndex) {
	                    return types [columnIndex];
	                }

	                public boolean isCellEditable(int rowIndex, int columnIndex) {
	                    return canEdit [columnIndex];
	                }
	            });
	   
	 
	    layout1.setHorizontalGroup(layout1.createParallelGroup(Alignment.LEADING)
		 .addComponent(jl_userconf)
		 .addGroup(layout1.createSequentialGroup()
			.addComponent(jl_name)
			.addComponent(jlabel_userId))
		.addGroup(layout1.createSequentialGroup()
			.addComponent(jt_name)
			.addComponent(jt_userId))
		.addGroup(layout1.createSequentialGroup()
			.addComponent(jl_port)
			.addComponent(jl_sipproxy))
		.addGroup(layout1.createSequentialGroup()
			.addComponent(jt_port)//,javax.swing.GroupLayout.DEFAULT_SIZE,javax.swing.GroupLayout.DEFAULT_SIZE,100
			.addComponent(jt_sipproxy))
		.addComponent(jl_mediaconf)
		.addGroup(layout1.createSequentialGroup()
			.addComponent(jt_media)
			.addComponent(jComboBox1)
			.addComponent(jl_band)
			.addComponent(spinner)
			.addComponent(jb_apply))
		.addGroup(layout1.createSequentialGroup()
	
			.addComponent(jl_codecAudio)
			.addComponent(jComboBox2)
			.addComponent(jl_codecVideo)
			.addComponent(jComboBox3)));
		
		 
		 
	    layout1.setVerticalGroup(layout1.createSequentialGroup()
	       		 .addComponent(jl_userconf)
	       		 .addGroup(layout1.createParallelGroup(GroupLayout.Alignment.BASELINE)
	       			.addComponent(jl_name)
				.addComponent(jlabel_userId))
			.addGroup(layout1.createParallelGroup(GroupLayout.Alignment.BASELINE)
	       			.addComponent(jt_name)
				.addComponent(jt_userId))
			.addGroup(layout1.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(jl_port)
				.addComponent(jl_sipproxy))
			.addGroup(layout1.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(jt_port)//,javax.swing.GroupLayout.DEFAULT_SIZE,javax.swing.GroupLayout.DEFAULT_SIZE,20
				.addComponent(jt_sipproxy))
			.addComponent(jl_mediaconf)
			.addGroup(layout1.createParallelGroup(GroupLayout.Alignment.BASELINE)
	       			.addComponent(jt_media)
				.addComponent(jComboBox1)
				.addComponent(jl_band)
				.addComponent(spinner)
				.addComponent(jb_apply))
			.addGroup(layout1.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(jl_codecAudio)
				.addComponent(jComboBox2)
				.addComponent(jl_codecVideo)
				.addComponent(jComboBox3)));

pack();
    
	    
	    
	    
	    
        receivedLbl = new JLabel();
        sendLbl = new JLabel();
        sendMessages = new JTextField();
        receivedScrollPane = new JScrollPane();
        Messages = new JTextArea();
        fromLbl = new JLabel();
        fromAddress = new JTextField();
        toLbl = new JLabel();
        toAddress = new JTextField();
        sendBtn = new JButton();
        inviteBtn = new JButton();
        byeBtn = new JButton();
        registerBtn = new JButton();
        
      String from = "sip:" + jt_userId.getText() ;//+ "@" +  InetAddress.getLocalHost().getHostAddress()+ ":" + jt_port.getText();
        this.fromAddress.setText(from);
        
        

        jScrollPaneTable = new javax.swing.JScrollPane();
        //jTable = new javax.swing.JTable();
        
        menubar = new JMenuBar();
        this.setJMenuBar(menubar);
        
        file = new JMenu("File");
        menubar.add(file);
        exit = new JMenuItem("Exit");
        file.add(exit);
        edit = new JMenu("Edit");
        menubar.add(edit);
        configurations = new JMenuItem("Configurations");
        edit.add(configurations);


        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SIP Client 1");
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
            	
            	
                System.exit(0);
            }
        });
        
        exit.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                  System.exit(0);
                 
            }
      });
     
    
        
      

        Messages.setAlignmentX(0.0F);
        Messages.setEditable(false);
        Messages.setLineWrap(true);
        Messages.setWrapStyleWord(true);
       // receivedScrollPane.setViewportView(Messages);
 
        fromLbl.setText("From:");
  
        toLbl.setText("To:     ");
  
            
            
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
 
       layout.setHorizontalGroup(layout.createSequentialGroup()
            		
            		 .addGroup(layout.createParallelGroup(Alignment.LEADING)
            				.addGroup(layout.createSequentialGroup()
            						.addComponent(fromLbl)
            						.addComponent(fromAddress))
            				.addGroup(layout.createSequentialGroup()
            						.addComponent(toLbl)
            						.addComponent(toAddress))
            				.addGroup(layout.createSequentialGroup()
            				.addComponent(registerBtn)
                    			.addComponent(inviteBtn)
                    			.addComponent(byeBtn)))
            		.addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                   
                      
        );
        
 
        layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING)
        	    .addGroup(layout.createSequentialGroup() 
        	  
        	        				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        	        						.addComponent(fromLbl)
        	        						.addComponent(fromAddress))
        	        				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        	        						.addComponent(toLbl)
        	        						.addComponent(toAddress))
        	        				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        	        						.addComponent(registerBtn)
        	        						.addComponent(inviteBtn)
        	        						.addComponent(byeBtn))) 
        	  .addComponent(jTabbedPane1,javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE)
        	);

        pack();
   
          
        
        registerBtn.setText("REGISTER");
        registerBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                registerBtnActionPerformed(evt);
            }
        });
        
        inviteBtn.setText("INVITE");
        inviteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                inviteBtnActionPerformed(evt);
            }
        });

       
        byeBtn.setText("BYE");
        byeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                byeBtnActionPerformed(evt);
            }
        });
        
        configurations.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
            	
        	//configi =new Configurations();
            	//configi.setVisible(true);
        	sipLayer.editor.setVisible(true);
                  //System.exit(0);
                 
            }
      });

        
        
     
  
    }

 
    
    
    private void registerBtnActionPerformed(ActionEvent evt) {

        try
        {
            String to = this.toAddress.getText();

        } catch (Throwable e)
        {
            e.printStackTrace();
            jTextArea1.append("ERROR sending message: " + e.getMessage() + "\n");
        }
        			
    }
    
    private void inviteBtnActionPerformed(ActionEvent evt) {

        try
        {
            String to = this.toAddress.getText();
          
            sipLayer.userInput(0,to);
         
        } catch (Throwable e)
        {
            e.printStackTrace();
            jTextArea1.append("ERROR sending message: " + e.getMessage() + "\n");
        }
        			
    }
  
    private void byeBtnActionPerformed(ActionEvent evt) {

        try
        {
           
           sipLayer.sendingBYE();
        } catch (Throwable e)
        {
            e.printStackTrace();
            jTextArea1.append("ERROR sending message: " + e.getMessage() + "\n");
        }
        			
    }
    void jb_apply_BtnActionPerformed(ActionEvent e) throws TransportNotSupportedException, ObjectInUseException, InvalidArgumentException {
	 
	 
	    Configuration config=new Configuration();
	    config.sipPort=Integer.parseInt(jt_port.getText());
	    config.name=jt_name.getText();
	    config.userID=jt_name.getText();
	 //   config.audioPort=Integer.parseInt(jt_voiceport.getText());
	    if (jComboBox2.getSelectedItem().equals("GSM")) {
	 
	      config.audioCodec=3;
	     
	}
	    else config.audioCodec=4;
	 
	    if (jComboBox1.getSelectedItem().equals("Audio and Video")) {
	 
	   //   config.videoPort=Integer.parseInt(jt_videoport.getText());
	      if (jComboBox3.getSelectedItem().equals("JPEG")) {
	 
	        config.videoCodec=26;
	       
	}
	      else config.videoCodec=34;
	     
	}
	   // else config.videoPort=-1;
	    bandwidth = (Integer) spinner.getValue();
	       config.bandwidth =bandwidth;
	 
	     sipLayer.updateConfiguration(config);
	 
	   
	}

   
   
  
    public void processInfo(String infoMessage)
    {
        this.jTextArea1.append(
                infoMessage + "\n");
    }
    public static javax.swing.JTable getjTable() {
		return jTable;
	}


	public void setjTable(javax.swing.JTable jTable) {
		this.jTable = jTable;
	}




 
class zphone2GUI_jb_apply_actionAdapter implements java.awt.event.ActionListener {
  
	SipClientGUI adaptee;
 
  zphone2GUI_jb_apply_actionAdapter(SipClientGUI adaptee) {
 
    this.adaptee = adaptee;
   
}
  public void actionPerformed(ActionEvent e) {
 
    try {
	adaptee.jb_apply_BtnActionPerformed(e);
    } catch (TransportNotSupportedException e1) {
	// TODO Auto-generated catch block
	e1.printStackTrace();
    } catch (ObjectInUseException e1) {
	// TODO Auto-generated catch block
	e1.printStackTrace();
    } catch (InvalidArgumentException e1) {
	// TODO Auto-generated catch block
	e1.printStackTrace();
    }
   
}
 
}
 
class SoftphoneGUI_jt_userId_actionAdapter implements java.awt.event.ActionListener {
 
	SipClientGUI adaptee;
 
  SoftphoneGUI_jt_userId_actionAdapter(SipClientGUI adaptee) {
 
    this.adaptee = adaptee;
   
}
  public void actionPerformed(ActionEvent e) {
 
    //adaptee.jTextField1_actionPerformed(e);
   
}
 
}
 
  
}

