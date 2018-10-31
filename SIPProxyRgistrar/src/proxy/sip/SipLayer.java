package proxy.sip;


import java.net.InetAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.TooManyListenersException;

import javax.sdp.SdpException;
import javax.sdp.SdpFactory;
import javax.sdp.SdpParseException;
import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.InvalidArgumentException;
import javax.sip.ListeningPoint;
import javax.sip.ObjectInUseException;
import javax.sip.PeerUnavailableException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.TimeoutEvent;
import javax.sip.Transaction;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.TransportNotSupportedException;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ExpiresHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.RecordRouteHeader;
import javax.sip.header.RouteHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;










import proxy.settings.Configuration;
import proxy.ui.MessageProcessor;
import proxy.ui.SIPProxyGUI;

import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;




public class SipLayer implements SipListener {

	  
    private MessageProcessor messageProcessor;

    private String username;

    private SipStack sipStack;

    private SipFactory sipFactory;

    private AddressFactory addressFactory;

    private HeaderFactory headerFactory;

    private MessageFactory messageFactory;

    protected SipProvider sipProvider;
    
    protected Dialog sipdialog;
  
    
    protected Transaction inviteTid ;
   
    protected SdpFactory sdpFactory;

  //  public RulesEditor editor;

    ContactHeader contactHeader;
   
    private ClientTransaction myClientTransaction;
    private ServerTransaction myServerTransaction;

    private String ip;

    
    public String myUserID;
    public String myName;
    private int port;

    
    ListeningPoint udp ;
 
    
    SIPProxyGUI gui;
    
    private String myDomain;
    private String mySipURI;
    boolean recordRoute;
    public static HashMap locationService;
    private ArrayList transactionContext;
    public static String stat2= "UNBLOCKED";
    public static int stat_block =0; 
    protected java.sql.PreparedStatement pstmt = null;

    
	/** Here we initialize the SIP stack. 
	 * @throws SdpException 
	// * @throws ParseException */
    public SipLayer(String domain,Configuration conf,SIPProxyGUI GUI , boolean recroute  ) //,boolean route
	    throws PeerUnavailableException, TransportNotSupportedException,
	    InvalidArgumentException, ObjectInUseException,
	    TooManyListenersException, SdpException, ParseException {
    	
        try{
    	
        	
        	
       	myDomain=domain;
     
        	
	      ip = InetAddress.getLocalHost().getHostAddress();
	      port = conf.sipPort;
	      myUserID = conf.userID;
	      myName= conf.name;
	   
	      mySipURI="sip:"+myName+"@"+ip+":"+Integer.toString(port);
	      
	  /*
	      editor = new RulesEditor();
	      editor.getRecords();
	 
	      editor.RulesTree();*/
	      
    	setUsername(username);
    	sipFactory = SipFactory.getInstance();
		sipFactory.setPathName("gov.nist");
		Properties properties = new Properties();
		properties.setProperty("javax.sip.STACK_NAME", "SIP Client 1");
		properties.setProperty("javax.sip.IP_ADDRESS", ip);

		//DEBUGGING: Information will go to files 
		//textclient.log and textclientdebug.log
		properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "32");
		properties.setProperty("gov.nist.javax.sip.SERVER_LOG","textclient.txt");
		properties.setProperty("gov.nist.javax.sip.DEBUG_LOG","textclientdebug.log");

		properties.setProperty("javax.sip.AUTOMATIC_DIALOG_SUPPORT", "OFF");
		
		sipStack = sipFactory.createSipStack(properties);
		headerFactory = sipFactory.createHeaderFactory();
		addressFactory = sipFactory.createAddressFactory();
		messageFactory = sipFactory.createMessageFactory();

		ListeningPoint udp = sipStack.createListeningPoint(port, "udp");

		sipProvider = sipStack.createSipProvider(udp);
		sipProvider.addSipListener(this);
		
	
		SipURI contactURI = addressFactory.createSipURI(getUsername(),getHost());
		contactURI.setPort(getPort());
		Address contactAddress = addressFactory.createAddress(contactURI);
		
		contactHeader = headerFactory.createContactHeader(contactAddress);
		
		locationService=new HashMap();
	      transactionContext=new ArrayList();
		
		
        }catch (Exception e) {
   		 
		     e.printStackTrace();
		     
		}
		   
    }

    
    public void setRR(boolean val) {
     
      recordRoute=val;
     
    }
     
    public void setOff(){
     
      try{
     
     
      sipProvider.removeSipListener(this);
      sipProvider.removeListeningPoint(udp);
      sipStack.deleteListeningPoint(udp);
      sipStack.deleteSipProvider(sipProvider);
      udp=null;
      sipProvider=null;
      sipStack=null;
       
    }
      catch(Exception e){
     
    }
     
     
     
    }
     
    public void updateConfiguration(Configuration conf) throws TransportNotSupportedException, InvalidArgumentException, ObjectInUseException {
		 
  		try{
  		    
  		    
  		    sipProvider.removeSipListener(this);
  		    sipProvider.removeListeningPoint(udp);
  		    sipStack.deleteListeningPoint(udp);
  		    sipStack.deleteSipProvider(sipProvider);
  		    udp=null;
  		    sipProvider=null;
  		    sipStack=null;
  		
  		   // System.out.println("-----------Updated Setting -------\n");
  		    
  		  
  		      ip = InetAddress.getLocalHost().getHostAddress();
  		      port = conf.sipPort;
  		      myUserID = conf.userID;
  		
  	    	setUsername(username);
  	    	sipFactory = SipFactory.getInstance();
  			sipFactory.setPathName("gov.nist");
  			Properties properties = new Properties();
  			properties.setProperty("javax.sip.STACK_NAME", "SIP Client 1");
  			properties.setProperty("javax.sip.IP_ADDRESS", ip);

  			//DEBUGGING: Information will go to files 
  			//textclient.log and textclientdebug.log
  			properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "32");
  			properties.setProperty("gov.nist.javax.sip.SERVER_LOG","textclient.txt");
  			properties.setProperty("gov.nist.javax.sip.DEBUG_LOG","textclientdebug.log");

  			sipStack = sipFactory.createSipStack(properties);
  			headerFactory = sipFactory.createHeaderFactory();
  			addressFactory = sipFactory.createAddressFactory();
  			messageFactory = sipFactory.createMessageFactory();

  			
  			udp = sipStack.createListeningPoint(port, "udp");

  			
  			sipProvider = sipStack.createSipProvider(udp);
  			sipProvider.addSipListener(this);
  			
  			
  			SipURI contactURI = addressFactory.createSipURI(getUsername(),getHost());
  			contactURI.setPort(getPort());
  			Address contactAddress = addressFactory.createAddress(contactURI);
  			
  			contactHeader = headerFactory.createContactHeader(contactAddress);
  			
  		}
  		    catch(Exception e){
  		 
  		}
  		
  		 
  		}
 
    
    /** This method is called by the SIP stack when a response arrives. */
    public void processResponse(ResponseEvent responseEvent) {
    	
   
	 try{
	     
	     
	     Response myResponse=responseEvent.getResponse();
	     messageProcessor.displayClient("<<< "+myResponse.toString()+"\n");
	     ClientTransaction thisClientTransaction=responseEvent.getClientTransaction();
	  
		 myClientTransaction = thisClientTransaction;
	 
	     inviteTid = myClientTransaction;
	     int myStatusCode=myResponse.getStatusCode();
	     
	     CSeqHeader originalCSeq=(CSeqHeader) myClientTransaction.getRequest().getHeader(CSeqHeader.NAME);
	   
	     String method=originalCSeq.getMethod();
	    
	     if ( (myStatusCode == 100)||(myStatusCode==487) ) return;
	     if ( method.equals("CANCEL") ) return;
	 
	     if (myClientTransaction != null) {
	    	 
	         Response newResponse = (Response) myResponse.clone();
	         newResponse.removeFirst(ViaHeader.NAME);
	    
	         Iterator iter = transactionContext.iterator();
	    
	        while (iter.hasNext()) {
	    
	          Context con=(Context) iter.next();
	          if (con.clientTrans.equals(myClientTransaction)) {
	    
	            con.serverTrans.sendResponse(newResponse);
	            messageProcessor.displayServer(">>> " + newResponse.toString());
	            break;
	           
	   }
	          
	   }
	    
	          
	   } else {
	    
	         Response newResponse = (Response) myResponse.clone();
	         newResponse.removeFirst(ViaHeader.NAME);
	         sipProvider.sendResponse(newResponse);
	         messageProcessor.displayServer(">>> "+ newResponse.toString()+"\n");
	    
	        
	   }
	// }
	   }catch (Exception e){
	    
	   e.printStackTrace();
	    
	   }
	    
	 }
    
    
    public void insertLocationService(javax.sip.address.URI addressOfRecord,javax.sip.address.URI contactAddress) throws SQLException{
    	  String selextSql,insertSql ;
    	  
    	  selextSql ="SELECT contactAddress FROM locationService";
    		 gui.statement = gui.con.createStatement();
    	  ResultSet rs = gui.statement.executeQuery(selextSql);
			int status_inserted=0;
		 	       while(rs.next()){
		 	          //Retrieve by column name
		 	     
		 	        if( contactAddress.equals(rs.getString("contactAddress"))&&addressOfRecord.equals(rs.getString("addressOfRecord"))) {
		 	        	status_inserted=1;
		 	        }
		 	       }
		 	       if(status_inserted!=1){
    	  insertSql = "INSERT INTO locationService (addressOfRecord , contactAddress  )VALUES (?,?)";
	       pstmt = gui.con.prepareStatement(insertSql); // create a statement
	       
	      
	       pstmt.setString(1,addressOfRecord.toString() ); 
	       pstmt.setString(2, contactAddress.toString()); 
	      
	       
	       pstmt.executeUpdate(); // execute insert statement*/
		 	       }
    }
 
    /** 
     * This method is called by the SIP stack when a new request arrives. 
     */
    public void processRequest(RequestEvent requestEvent) {

    SipProvider sipProvider = (SipProvider) requestEvent.getSource();
	  Request myRequest=requestEvent.getRequest();
	  String method=myRequest.getMethod();
	 
	  if (!method.equals("REGISTER")) {
		  messageProcessor.displayServer("<<< "+myRequest.toString()+"\n");

	  }
	
	  
	 if (!method.equals("CANCEL")) {  myServerTransaction=requestEvent.getServerTransaction();}
	 
	  try{
	
		  if(method.equals("REGISTER")){
	 
	        if (myServerTransaction == null) {
	 
	    	     myServerTransaction = sipProvider.getNewServerTransaction(myRequest);
	                inviteTid = myServerTransaction;
	              
	       
	}
	                ToHeader registerToHeader = (ToHeader) myRequest.getHeader(ToHeader.NAME);
	                javax.sip.address.URI addressOfRecord = registerToHeader.getAddress().getURI();
	                ContactHeader registerContactHeader = (ContactHeader) myRequest.getHeader(ContactHeader.NAME);
	                javax.sip.address.URI contactAddress = registerContactHeader.getAddress().getURI();
	                ExpiresHeader expH=(ExpiresHeader) myRequest.getHeader(ExpiresHeader.NAME);
	                int exp=expH.getExpires();
	           
	                if (exp==0) locationService.remove(addressOfRecord);
	                else locationService.put(addressOfRecord, contactAddress);
	           
	                Set keys=locationService.keySet();
	                Iterator iter=keys.iterator();
	                messageProcessor.clearLocationServiceDisplay();
	           
	                while(iter.hasNext()) {
	           
	                  javax.sip.address.URI aor=(javax.sip.address.URI) iter.next();
	                  messageProcessor.appendLocationServiceDisplay(aor+ "  "+locationService.get(aor)+"\n");
	               
	          }
	                
	              
	       // Response myResponse=messageFactory.createResponse(180,myRequest);
	         Response myResponse=messageFactory.createResponse(200,myRequest);
	       // myResponse.addHeader(contactHeader);
	        ToHeader myToHeader = (ToHeader) myResponse.getHeader("To");
	        myToHeader.setTag("454326");
	        myResponse.addHeader(registerContactHeader);
	        myResponse.addHeader(expH);
	        myServerTransaction.sendResponse(myResponse);
	        sipdialog=myServerTransaction.getDialog();
	  
	      //  messageProcessor.displayClient(">>> "+ myResponse.toString());
	       
	}
	  
		  else if (method.equals("BYE")) {
	 
	        Response myResponse=messageFactory.createResponse(200,myRequest);
	        myResponse.addHeader(contactHeader);
	        ServerTransaction serverTransaction=requestEvent.getServerTransaction();
	        inviteTid = myServerTransaction;
	       myServerTransaction.sendResponse(myResponse);
	        serverTransaction.sendResponse(myResponse);
	       
	        messageProcessor.displayServer(">>> "+myResponse.toString());
	 
	
	}
	
		  else if (method.equals("CANCEL")) {
	 
	    	  if (myServerTransaction==null) {
	    		  
	    	       myServerTransaction=sipProvider.getNewServerTransaction(myRequest);
	    	      
	    	}
	     
	        Iterator iter = transactionContext.iterator();
	        
	        while (iter.hasNext()) {
	        	 
	        	 
	            Context con=(Context) iter.next();
	      
	            if (con.serverTrans.getBranchId().equals(myServerTransaction.getBranchId())) {
	      
	      
	              Request originalRequest = (Request) con.requestIn;
	              Response originalTransactionResponse = messageFactory.
	                  createResponse(487, originalRequest);
	              Response cancelResponse = messageFactory.createResponse(200,
	                  myRequest);
	      
	              Request newCancelRequest = con.clientTrans.createCancel();
	      
	              con.serverTrans.sendResponse(originalTransactionResponse);
	              messageProcessor.displayServer(">>> " + originalTransactionResponse.toString());
	      
	              myServerTransaction.sendResponse(cancelResponse);
	              messageProcessor.displayServer(">>> " + cancelResponse.toString());
	      
	              ClientTransaction cancelClientTransaction = sipProvider.
	                  getNewClientTransaction(newCancelRequest);
	              cancelClientTransaction.sendRequest();
	              messageProcessor.displayClient(">>> " + newCancelRequest.toString());
	              break;
	             
	     }
	           
	     }
	      }
	    
		  else if (method.equals("ACK")) {
			 
			  
			  RouteHeader receivedRouteHeader = (RouteHeader)myRequest.getHeader(RouteHeader.NAME);
	            
		         
	            SipURI receivedRouteHeaderSipURI=(SipURI) receivedRouteHeader.getAddress().getURI();
	            String receivedRouteHeaderDomain=receivedRouteHeaderSipURI.getHost();
	            
	            Request newRequest = (Request) myRequest.clone();
	        
	            if (receivedRouteHeaderDomain.equals(ip)) {
	        
	             newRequest.removeFirst(RouteHeader.NAME);
	            
	       }
	        
	           
	            SipURI receivedRequestURI=(SipURI)myRequest.getRequestURI();
	            String receivedRequestURIDomain=receivedRequestURI.getHost();
	        
	        
	            if (receivedRequestURIDomain.equals(myDomain)) {
	        
	            javax.sip.address.URI newRequestURI=(javax.sip.address.URI) locationService.get(receivedRequestURI);
	            newRequest.setRequestURI(newRequestURI);
	             
	       }
	        
	            MaxForwardsHeader newMaxForwardsHeader=(MaxForwardsHeader)newRequest.getHeader(MaxForwardsHeader.NAME);
	            newMaxForwardsHeader.decrementMaxForwards();
	        
	          //  if (recordRoute) {
	        
	            Address proxyAddress = addressFactory.createAddress(mySipURI);
	            RecordRouteHeader recordRouteHeader = headerFactory.createRecordRouteHeader(proxyAddress);
	            newRequest.addHeader(recordRouteHeader);
	             
	   //    }
	        
	            ViaHeader vH = headerFactory.createViaHeader(ip, port,"udp", null);
	          //  newRequest.removeFirst(ViaHeader.NAME);
	            newRequest.addFirst(vH);
	        
	            ClientTransaction myClientTransaction= sipProvider.getNewClientTransaction(newRequest);
	            String bid=myClientTransaction.getBranchId();
	            myClientTransaction.sendRequest();
	            
	            messageProcessor.displayClient(">>> "+ newRequest.toString()+"\n");
	        
	            Context ctxt=new Context();
	            ctxt.clientTrans=myClientTransaction;
	            ctxt.serverTrans=myServerTransaction;
	            ctxt.method=method;
	            ctxt.requestIn=myRequest;
	            ctxt.requestOut=newRequest;
	        
	        
	            transactionContext.add(ctxt);
			  
			  
	             
	       }
	        
	          else {
	        
	        
	            if (myServerTransaction==null) {
	        
	              myServerTransaction=sipProvider.getNewServerTransaction(myRequest);
	             
	       }
	        
	           
	            RouteHeader receivedRouteHeader = (RouteHeader)myRequest.getHeader(RouteHeader.NAME);
	            
	         
	            SipURI receivedRouteHeaderSipURI=(SipURI) receivedRouteHeader.getAddress().getURI();
	            String receivedRouteHeaderDomain=receivedRouteHeaderSipURI.getHost();
	            
	            Request newRequest = (Request) myRequest.clone();
	        
	            if (receivedRouteHeaderDomain.equals(ip)) {
	        
	             newRequest.removeFirst(RouteHeader.NAME);
	            
	       }
	        
	           
	            SipURI receivedRequestURI=(SipURI)myRequest.getRequestURI();
	            String receivedRequestURIDomain=receivedRequestURI.getHost();
	        
	        
	            if (receivedRequestURIDomain.equals(myDomain)) {
	        
	            javax.sip.address.URI newRequestURI=(javax.sip.address.URI) locationService.get(receivedRequestURI);
	            newRequest.setRequestURI(newRequestURI);
	             
	       }
	        
	            MaxForwardsHeader newMaxForwardsHeader=(MaxForwardsHeader)newRequest.getHeader(MaxForwardsHeader.NAME);
	            newMaxForwardsHeader.decrementMaxForwards();
	        
	          //  if (recordRoute) {
	        
	            Address proxyAddress = addressFactory.createAddress(mySipURI);
	            RecordRouteHeader recordRouteHeader = headerFactory.createRecordRouteHeader(proxyAddress);
	            newRequest.addHeader(recordRouteHeader);
	             
	   //    }
	        
	            ViaHeader vH = headerFactory.createViaHeader(ip, port,"udp", null);
	          //  newRequest.removeFirst(ViaHeader.NAME);
	            newRequest.addFirst(vH);
	        
	            ClientTransaction myClientTransaction= sipProvider.getNewClientTransaction(newRequest);
	            String bid=myClientTransaction.getBranchId();
	            myClientTransaction.sendRequest();
	            messageProcessor.displayClient(">>> "+ newRequest.toString()+"\n");
	        
	            Context ctxt=new Context();
	            ctxt.clientTrans=myClientTransaction;
	            ctxt.serverTrans=myServerTransaction;
	            ctxt.method=method;
	            ctxt.requestIn=myRequest;
	            ctxt.requestOut=newRequest;
	        
	        
	            transactionContext.add(ctxt);
	        
	        
	        
	       }
	        
	// }
	 
	   
	}catch (Exception e) {
	 
	    e.printStackTrace();
	   
	}
	 
    }
    
 

    

 
    /** 
     * This method is called by the SIP stack when there's no answer 
     * to a message. Note that this is treated differently from an error
     * message. 
     */
    public void processTimeout(TimeoutEvent evt) {
	//messageProcessor.processError("Previous message not sent: " + "timeout");
    }

    /** 
     * This method is called by the SIP stack when there's an asynchronous
     * message transmission error.  
     */
    public void processIOException(IOExceptionEvent evt) {
//	messageProcessor.processError("Previous message not sent: "+ "I/O Exception");
    }
    /** 
     * This method is called by the SIP stack when a dialog (session) ends. 
     */
    public void processDialogTerminated(DialogTerminatedEvent evt) {
    }

    /** 
     * This method is called by the SIP stack when a transaction ends. 
     */
    public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {
    	 
    	 
    	  if (transactionTerminatedEvent.isServerTransaction()) {
    	 
    	    ServerTransaction st = transactionTerminatedEvent.getServerTransaction();
    	    Iterator iter = transactionContext.iterator();
    	    
    	    while(iter.hasNext()) {
    	 
    	      Context con=(Context) iter.next();
    	      if (con.serverTrans.equals(st)) {
    	 
    	        iter.remove();
    	       
    	}
    	   
    	}
    	   
    	}
    	 
    	}

    public String getHost() {
	int port = sipProvider.getListeningPoint().getPort();
	String host = sipStack.getIPAddress();
	return host;
    }

    public int getPort() {
	int port = sipProvider.getListeningPoint().getPort();
	return port;
    }

    public String getUsername() {
	return username;
    }

    public void setUsername(String newUsername) {
	username = newUsername;
    }

    public MessageProcessor getMessageProcessor() {
	return messageProcessor;
    }

    public void setMessageProcessor(MessageProcessor newMessageProcessor) {
	messageProcessor = newMessageProcessor;
    }
    public Transaction getInviteTid() {
  		return inviteTid;
  	}

  	public void setInviteTid(Transaction inviteTid) {
  		this.inviteTid = inviteTid;
  	}

}
	
	class Context {
		 
		 
		  ClientTransaction clientTrans;
		  ServerTransaction serverTrans;
		  String method;
		  Request requestIn;
		  Request requestOut;
		 
		  public Context () {
		 
		 
		    clientTrans=null;
		    serverTrans=null;
		   
		}

	}
