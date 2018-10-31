//sipclient 2


package sipclient.sip;


import java.net.InetAddress;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.TooManyListenersException;

import javax.sdp.SdpException;
import javax.sdp.SdpFactory;
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
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.ExpiresHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.Header;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.RecordRouteHeader;
import javax.sip.header.RouteHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.swing.table.DefaultTableModel;

import sipclient.sdp.SdpInfo;
import sipclient.sdp.SdpManager;
import sipclient.settings.Configuration;
import sipclient.ui.MessageProcessor;
import sipclient.ui.SipClientGUI;




//import Softphone2Listener.MyTimerTask;

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
    
    private int CSeq=1;
    
    private CallIdHeader callID= null;
    
    protected Transaction inviteTid ;
   
    protected SdpFactory sdpFactory;


    ContactHeader contactHeader;
   
    private ClientTransaction myClientTransaction;
    private ServerTransaction myServerTransaction;
    private int status;
    private String ip;
    private String toTag;
    private SdpManager mySdpManager;
 
    private SdpInfo answerInfo;
    private SdpInfo offerInfo;
    private Address fromAddress;
    public String myUserID;
    public String myName;
    private int port;
    private int myAudioPort;
    private int myVideoPort;
    private int myAudioCodec;
    private int myVideoCodec;
    private int myBand;
    private String mySipProxy; 
    
    static final int IDLE=0;
    static final int WAIT_PROV=1;
    static final int WAIT_FINAL=2;
    static final int ESTABLISHED=4;
    static final int RINGING=5;
    static final int WAIT_ACK=6;
    
    //static final int UNREGISTERED=7;
    static final int REGISTERED=7;
    
    static final int YES=0;
    static final int NO=1;
    
    ListeningPoint udp ;
    public RulesEditor editor;
    
    SipClientGUI gui;
     

    
	/** Here we initialize the SIP stack. 
	 * @throws SdpException 
	// * @throws ParseException */
    public SipLayer(Configuration conf,SipClientGUI GUI ,String sipserver) 
	    throws PeerUnavailableException, TransportNotSupportedException,
	    InvalidArgumentException, ObjectInUseException,
	    TooManyListenersException, SdpException, ParseException {
    	
        try{
    	
	      ip = InetAddress.getLocalHost().getHostAddress();
	      port = conf.sipPort;
	      myUserID = conf.userID;
	      myName= conf.name;
	      setUsername(myName);
	   
	      myAudioCodec=conf.audioCodec;
	      myVideoCodec=conf.videoCodec;
	      myBand=conf.bandwidth;
	      mySipProxy=sipserver;
	      
	 
	      mySdpManager=new SdpManager();
	
	      answerInfo=new SdpInfo();
	      offerInfo=new SdpInfo();
	      editor = new RulesEditor();
	      
	      editor.getRecords();
	 
	      editor.RulesTree();
	      
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

		//ListeningPoint tcp = sipStack.createListeningPoint(port, "tcp");
		ListeningPoint udp = sipStack.createListeningPoint(port, "udp");

		//sipProvider = sipStack.createSipProvider(tcp);
		//sipProvider.addSipListener(this);
		sipProvider = sipStack.createSipProvider(udp);
		sipProvider.addSipListener(this);
	
		SipURI contactURI = addressFactory.createSipURI(getUsername(),getHost());
		contactURI.setPort(getPort());
		Address contactAddress = addressFactory.createAddress(contactURI);
		contactHeader = headerFactory.createContactHeader(contactAddress);
		
		status =IDLE;
		
        }catch (Exception e) {
   		 
		     e.printStackTrace();
		     
		}
		   
    }

    public SipLayer() {
		// TODO Auto-generated constructor stub
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
  		    messageProcessor.processInfo("-----------Updated Setting -------\n");
  		    
  		      ip = InetAddress.getLocalHost().getHostAddress();
  		      port = conf.sipPort;
  		      myUserID = conf.userID;
  		      myAudioCodec=conf.audioCodec;
  		      myVideoCodec=conf.videoCodec;
  		      myBand=conf.bandwidth;
  		 
  		     mySdpManager=new SdpManager();
  		      answerInfo=new SdpInfo();
  		      offerInfo=new SdpInfo();
  		 
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

  			//ListeningPoint tcp = sipStack.createListeningPoint(port, "tcp");
  			udp = sipStack.createListeningPoint(port, "udp");

  			//sipProvider = sipStack.createSipProvider(tcp);
  			//sipProvider.addSipListener(this);
  			sipProvider = sipStack.createSipProvider(udp);
  			sipProvider.addSipListener(this);
  			
  			////////
  			SipURI contactURI = addressFactory.createSipURI(getUsername(),getHost());
  			contactURI.setPort(getPort());
  			Address contactAddress = addressFactory.createAddress(contactURI);
  			
  			contactHeader = headerFactory.createContactHeader(contactAddress);
  			
  			status=REGISTERED;
  			
  		}
  		    catch(Exception e){
  		 
  		}
  		
  		 
  		}
    
    
    /**
     * This method uses the SIP stack to send a message. 
     * @throws SdpException 
     */
    public void userInput(int type, String destination){
	 
	     try {
	 
	       switch (status) {
	       case IDLE:
	    	   if(type == YES) {
	    		   Address fromAddress=addressFactory.createAddress("sip:"+myName+"@sipproxy.com");
	   		    FromHeader myFromHeader = headerFactory.createFromHeader(fromAddress,"56438"); /// textclientýn yerine "123408805"
	   	
	   		    Address toAddress = addressFactory.createAddress(mySipProxy);
	   		Address registrarAddress=addressFactory.createAddress( "sip:"+mySipProxy);
	   		Address registerToAddress = fromAddress;
	   			Address registerFromAddress=fromAddress;
	   			ToHeader myToHeader = headerFactory.createToHeader(registerToAddress, "4321");
	   			
	   			ViaHeader myViaHeader = headerFactory.createViaHeader(ip,port, "udp", null);
	   			ArrayList myViaHeaders = new ArrayList();
	   			myViaHeaders.add(myViaHeader);
	   			MaxForwardsHeader myMaxForwardsHeader = headerFactory.createMaxForwardsHeader(70);
	   			CSeqHeader myCSeqHeader = headerFactory.createCSeqHeader(1,"REGISTER" );
	   			ExpiresHeader myExpiresHeader=headerFactory.
	   			createExpiresHeader(60000);
	   			CallIdHeader myCallIDHeader = sipProvider.getNewCallId();
	   			SipURI myRequestURI = (SipURI) registrarAddress.getURI();
	   			
	   			
	   			Request myRegisterRequest = messageFactory.createRequest(myRequestURI, "REGISTER",
	   				myCallIDHeader, myCSeqHeader, myFromHeader, myToHeader,
	   				myViaHeaders,
	   				myMaxForwardsHeader);
	   				myRegisterRequest.addHeader(contactHeader);
	   				myRegisterRequest.addHeader(myExpiresHeader);
	   				myClientTransaction = sipProvider.getNewClientTransaction(myRegisterRequest);
	   				String bid=myClientTransaction.getBranchId();
	   				myClientTransaction.sendRequest();
	   				status=REGISTERED;
	   				 messageProcessor.processInfo(">>> "+myRegisterRequest);
	   				 System.out.println(myRegisterRequest.toString());
	    	   }
	 
	    	   break;
	         case REGISTERED:
	           if (type == YES) {
	        	   //yeni
	        	   String username = destination.substring(destination.indexOf(":") + 1, destination.indexOf("@"));
	        		String address = destination.substring(destination.indexOf("@") + 1);
	        		SipURI toNameAddress = addressFactory.createSipURI(username, address);
	        		
	        		Address toAddress = addressFactory.createAddress(toNameAddress);
	        		toAddress.setDisplayName(username);
	        		ToHeader myToHeader = headerFactory.createToHeader(toAddress, "4321");	
	        
	        		SipURI from = addressFactory.createSipURI(getUsername(), getHost()+ ":" + getPort());
	        		Address fromAddress = addressFactory.createAddress(from);
	        		fromAddress.setDisplayName(getUsername());
	        		FromHeader myFromHeader = headerFactory.createFromHeader(fromAddress,"56438"); /// textclientýn yerine "123408805"

	        		
	        		
	        	ViaHeader  myViaHeader = headerFactory.createViaHeader(getHost(),getPort(), "udp", "branch1");
	             ArrayList myViaHeaders = new ArrayList();
	             myViaHeaders.add(myViaHeader);
	             
	             MaxForwardsHeader myMaxForwardsHeader = headerFactory.createMaxForwardsHeader(70);
	           
	             CSeqHeader myCSeqHeader = headerFactory.createCSeqHeader(1,"INVITE");
	             CallIdHeader myCallIDHeader = sipProvider.getNewCallId();
	           
	             Address routeAddress__ = addressFactory.createAddress( "sip:" +mySipProxy+ " ;lr "  );//+ " ;lr " 8/mySipProxy
	        	System.out.println("invite proxy :"+mySipProxy);
	            RouteHeader myRouteHeader__= headerFactory.createRouteHeader(routeAddress__);//routeAddress__
	            javax.sip.address.URI myRequestURI = toAddress.getURI();
	            System.out.println("myRouteHeader__"+myRouteHeader__);
	             
	              Request myRequest = messageFactory.createRequest(myRequestURI,
	                 "INVITE",
	                 myCallIDHeader, myCSeqHeader, myFromHeader, myToHeader,
	                 myViaHeaders, myMaxForwardsHeader);
	           
	             System.out.println("myRouteHeader__"+myRouteHeader__);
	          
	             myRequest.addFirst(myRouteHeader__);
	            
	           
	           System.out.println("myRouteHeader__"+myRouteHeader__);
	            
	           myClientTransaction = sipProvider.getNewClientTransaction(myRequest);
	             myRequest.setRequestURI(myRequestURI);
	         
	             
	             
	             myRequest.setHeader(myRouteHeader__);
	            
	            offerInfo=new SdpInfo();
	             offerInfo.IpAddress=ip;
	             offerInfo.aport=myAudioPort;
	             offerInfo.aformat=myAudioCodec;
	             offerInfo.vport=myVideoPort;
	             offerInfo.vformat=myVideoCodec;
	             offerInfo.band=myBand;
	 
	             ContentTypeHeader contentTypeHeader=headerFactory.createContentTypeHeader("application","sdp");
	            
	             
	             byte[] content=mySdpManager.createSdp(offerInfo);
	             myRequest.setContent(content,contentTypeHeader);
	         
	             inviteTid = myClientTransaction;
	             String bid=myClientTransaction.getBranchId();
	        
	             myClientTransaction.sendRequest();
	           
	             sipdialog = myClientTransaction.getDialog();
	             messageProcessor.processInfo(">>> " + myRequest.toString());
	             status = WAIT_PROV;
	             messageProcessor.processInfo("Status: WAIT_PROV");
	             break;
	            
	}
	     
    
	 
	         case WAIT_FINAL:
	           if (type == NO) {
	 
	             Request myCancelRequest = myClientTransaction.createCancel();
	             ClientTransaction myCancelClientTransaction = sipProvider.
	                 getNewClientTransaction(myCancelRequest);
	             inviteTid = myCancelClientTransaction;
	             myCancelClientTransaction.sendRequest();
	             messageProcessor.processInfo(">>> " + myCancelRequest.toString());
	     
	             status = REGISTERED;
	             messageProcessor.processInfo("Status: REGISTERED");
	             break;
	 
	            
	}
	 
	         case ESTABLISHED:
	             if (type == NO) {
	        	 
	        	  
	             Request myBye = sipdialog.createRequest("BYE");
	             myBye.addHeader(contactHeader);
	             myClientTransaction= sipProvider.getNewClientTransaction(myBye);
	           
	             myClientTransaction.sendRequest();
	             messageProcessor.processInfo(">>> " + myBye.toString());
	             status = REGISTERED;
	 
	             messageProcessor.processInfo("Status: REGISTERED");
	             break;
	            
	}
	 
	         case RINGING:
	           if (type == NO) {
	 
	             Request originalRequest = myServerTransaction.getRequest();
	             Response myResponse = messageFactory.createResponse(486,originalRequest);
	             myServerTransaction.sendResponse(myResponse);
	             messageProcessor.processInfo(">>> " + myResponse.toString());

	 
	             status = REGISTERED;
	             messageProcessor.processInfo("Status: REGISTERED");
	             break;
	            
	}
	           else if (type == YES) {
	 
	 
	             Request originalRequest = myServerTransaction.getRequest();
	             Response myResponse = messageFactory.createResponse(200,originalRequest);
	             ToHeader myToHeader = (ToHeader) myResponse.getHeader("To");
	             myToHeader.setTag("454326");
	             myResponse.addHeader(contactHeader);
	 
	
	 
	            ContentTypeHeader contentTypeHeader=headerFactory.createContentTypeHeader("application","sdp");
	             byte[] content=mySdpManager.createSdp(answerInfo);
	             myResponse.setContent(content,contentTypeHeader);
	 
	
	 
	             myServerTransaction.sendResponse(myResponse);
	             sipdialog = myServerTransaction.getDialog();
	             messageProcessor.processInfo(">>> " + myResponse.toString());
	            
	             status = WAIT_ACK;
	             messageProcessor.processInfo("Status: WAIT_ACK");
	             
	             break;
	            
	}
	        
	}
	      
	}
	     catch (Exception e){
	 
	     e.printStackTrace();
	    
	}
	 
	    
	}
    
  
    public void sendingBYE() throws SipException {
	
	Dialog dialog = inviteTid.getDialog();
	   	
	   	Request request= dialog.createRequest("BYE");
	    messageProcessor.processInfo("Sending an BYE message : \n " + request);
	    ClientTransaction clientTransaction= sipProvider.getNewClientTransaction(request);
	    inviteTid = clientTransaction;
	   	dialog.sendRequest(clientTransaction);
	   }
    
    
  
    /** This method is called by the SIP stack when a response arrives. */
    public void processResponse(ResponseEvent responseEvent) {
    	
   
	 try{
	     
	     
	     Response myResponse=responseEvent.getResponse();
	     messageProcessor.processInfo("<<< "+myResponse.toString());
	     ClientTransaction thisClientTransaction=responseEvent.getClientTransaction();
	     if (!thisClientTransaction.equals(myClientTransaction)) {
	  // return;
		 myClientTransaction = thisClientTransaction;
	   }
	     inviteTid = myClientTransaction;
	     this.responseUpdateTable(responseEvent, myResponse, myClientTransaction);
	     int myStatusCode=myResponse.getStatusCode();
	     CSeqHeader originalCSeq=(CSeqHeader) myClientTransaction.getRequest().getHeader(CSeqHeader.NAME);
	     long numseq=originalCSeq.getSeqNumber();
	     
	     
	     
	  /*   editor.RulesTree().searchResponse(0,0,responseEvent);
	     
	      if(stat2 =="BLOCKED" ){
	    	  sipdialog=thisClientTransaction.getDialog();
		        // Request myAck = sipdialog.createAck(numseq);
		         Request myCancel = sipdialog.createRequest("BYE");
		         myCancel.addHeader(contactHeader);
		        // myAck.addHeader(myRouteHeader__);
		         myClientTransaction= sipProvider.getNewClientTransaction(myCancel);
		           sipdialog.sendRequest(myClientTransaction);
		         
	            // myClientTransaction.sendRequest();
	            
		         //sipdialog.sendAck(myCancel);
		         messageProcessor.processInfo(">>> "+myCancel.toString());
		         status=REGISTERED;
		         messageProcessor.processInfo("Status: REGISTERED");
		    
	    	  
		    	
		 
			 
			 }
		 else{*/
	    
	   switch(status){
	    
	    
	     case WAIT_PROV:
	       if (myStatusCode<200) {
	    
	         status=WAIT_FINAL;
	         sipdialog=thisClientTransaction.getDialog();
	         messageProcessor.processInfo("Status: ALERTING");
	        
	   }
	       else if (myStatusCode<300) {
	    
	    	
	         sipdialog=thisClientTransaction.getDialog();
	         Request myAck = sipdialog.createAck(numseq);
	         myAck.addHeader(contactHeader);
	         sipdialog.sendAck(myAck);
	         messageProcessor.processInfo(">>> "+myAck.toString());
	         status=ESTABLISHED;
	         messageProcessor.processInfo("Status: ESTABLISHED");
	    
	        byte[] cont=(byte[]) myResponse.getContent();
	        
	   }
	       else {
	    
	    
	         status=REGISTERED;
	         Request myAck = sipdialog.createAck(numseq);
	         myAck.addHeader(contactHeader);
	         sipdialog.sendAck(myAck);
	         messageProcessor.processInfo(">>> "+myAck.toString());
	         messageProcessor.processInfo("Status: REGISTERED");
	    
	        
	   }
	       break;
	       
	     case WAIT_FINAL:
	       if (myStatusCode<200) {
	    
	         status=WAIT_FINAL;
	         sipdialog=thisClientTransaction.getDialog();
	    //     myRingTool.playTone();
	         messageProcessor.processInfo("Status: ALERTING");
	        
	   }
	       else if (myStatusCode<300) {
	    

	              
	         status=ESTABLISHED;
	         sipdialog=thisClientTransaction.getDialog();
	        
	        
	         Request myAck = sipdialog.createAck(numseq);
	         myAck.addHeader(contactHeader);
	     
	         sipdialog.sendAck(myAck);
	         messageProcessor.processInfo(">>> "+myAck.toString());
	         messageProcessor.processInfo("Status: ESTABLISHED");
	       
	    
	         byte[] cont=(byte[]) myResponse.getContent();
	         answerInfo=mySdpManager.getSdp(cont);
	         
	         
	         
	         
	    
	      
	   }
	       else {
	    
	         status=REGISTERED;
	         messageProcessor.processInfo("Status: REGISTERED");
	        
	   }
	       break;
	    
	   }
	//	 }
	   }catch(Exception excep){
	    
	       excep.printStackTrace();
	      
	   }
	 }

 
    public static String stat2= "UNBLOCKED";
    public static int stat_block =0; 
    /** 
     * This method is called by the SIP stack when a new request arrives. 
     */
    public void processRequest(RequestEvent requestEvent) {
	
	SipProvider sipProvider = (SipProvider) requestEvent.getSource();
	  Request myRequest=requestEvent.getRequest();
	  String method=myRequest.getMethod();
	  messageProcessor.processInfo("<<< "+myRequest.toString());
	 if (!method.equals("CANCEL")) {
	  myServerTransaction=requestEvent.getServerTransaction();
	   
	}
	 
	  try{
	 
	      System.out.println("Stat2()"+stat2);
		//editor.RulesTree().searchRequest(0,0,requestEvent);

	/*  if(stat2 =="BLOCKED"){
		
			 myServerTransaction = sipProvider.getNewServerTransaction(myRequest);
	         inviteTid = myServerTransaction;
	         
	         Response myResponse=messageFactory.createResponse(406,myRequest);
		        myResponse.addHeader(contactHeader);
		        ToHeader myToHeader = (ToHeader) myResponse.getHeader("To");
		        myToHeader.setTag("454326");
		        myServerTransaction.sendResponse(myResponse);
		        sipdialog=myServerTransaction.getDialog();
		        messageProcessor.processInfo(">>> "+myResponse.toString());
		      
	           status = REGISTERED;
	           messageProcessor.processInfo("Status: REGISTERED");
		 
		 }
	 else{*/
	  switch (status) {
	 
	 
	  
	    case REGISTERED:
	   
		  if(method.equals("INVITE")){
	 
	 
	    	     myServerTransaction = sipProvider.getNewServerTransaction(myRequest);
	                inviteTid = myServerTransaction;
	                this.updateTable(requestEvent, myRequest, myServerTransaction);
	              
	       
	 
	        byte[] cont=(byte[]) myRequest.getContent();
	        offerInfo=mySdpManager.getSdp(cont);
	 
	        answerInfo.IpAddress=ip;
	        answerInfo.aport=myAudioPort;
	        answerInfo.aformat=offerInfo.aformat;
	        answerInfo.band=offerInfo.band;
	 
	        if (offerInfo.vport==-1) {
	 
	          answerInfo.vport=-1;
	         
	}
	        else if (myVideoPort==-1) {
	 
	          answerInfo.vport=0;
	          answerInfo.vformat=offerInfo.vformat;
	         
	}
	        else {
	 
	          answerInfo.vport=myVideoPort;
	          answerInfo.vformat=offerInfo.vformat;
	         //
	}
	 
	        Response myResponse=messageFactory.createResponse(180,myRequest);
	          //      Response myResponse=messageFactory.createResponse(200,myRequest);
	        myResponse.addHeader(contactHeader);
	        ToHeader myToHeader = (ToHeader) myResponse.getHeader("To");
	        myToHeader.setTag("454326");
	        

           
	        myServerTransaction.sendResponse(myResponse);
	        sipdialog=myServerTransaction.getDialog();
	        messageProcessor.processInfo(">>> "+myResponse.toString());
	        status=RINGING;
	        messageProcessor.processInfo("Status: RINGING");
	       
	}
	     break;
	    case ESTABLISHED:
	      if (method.equals("BYE")) {
	 
	        Response myResponse=messageFactory.createResponse(200,myRequest);
	        myResponse.addHeader(contactHeader);
	        ServerTransaction serverTransaction=requestEvent.getServerTransaction();
	        inviteTid = myServerTransaction;
	        serverTransaction.sendResponse(myResponse);
	       this.updateTable(requestEvent, myRequest, myServerTransaction);
	        messageProcessor.processInfo(">>> "+myResponse.toString());
	 
	
	 
	        status=REGISTERED;
	        messageProcessor.processInfo("Status: REGISTERED");
	       
	}
	    break;
	 
	    case RINGING:
	      if (method.equals("CANCEL")) {
	 
	        ServerTransaction myCancelServerTransaction=requestEvent.getServerTransaction();
	        Request originalRequest=myServerTransaction.getRequest();
	        Response myResponse=messageFactory.createResponse(487,originalRequest);
	        inviteTid = myServerTransaction;
	        myServerTransaction.sendResponse(myResponse);
	        this.updateTable(requestEvent, myRequest, myServerTransaction);
	        Response myCancelResponse=messageFactory.createResponse(200,myRequest);
	        myCancelServerTransaction.sendResponse(myCancelResponse);
	 
	 
	        messageProcessor.processInfo(">>> "+myResponse.toString());
	        messageProcessor.processInfo(">>> "+myCancelResponse.toString());
	 
	        status=REGISTERED;
	        messageProcessor.processInfo("Status: REGISTERED");
	       
	}
	      break;
	 
	      case WAIT_ACK:
	    	 // messageProcessor.processInfo("ASASASSAASA");
	        if (method.equals("ACK")) {
	 
	    //    messageProcessor.processInfo(">>> "+myRequest.toString());
	        status=ESTABLISHED;
	        messageProcessor.processInfo("Status: ESTABLISHED");
	        this.updateTable(requestEvent, myRequest, myServerTransaction);
	}
	 
	   
	}
	// }
	 
	   
	}catch (Exception e) {
	 
	    e.printStackTrace();
	   
	}
	 
    }
    
 

     public void createResponseHeader(Response response) throws ParseException  {
	 ToHeader toHeader = (ToHeader) response.getHeader(ToHeader.NAME);
		toHeader.setTag("4321"); // Application is supposed to set.
		
		SipURI contactURI = addressFactory.createSipURI(getUsername(),getHost());
		contactURI.setPort(getPort());
		Address contactAddress = addressFactory.createAddress(contactURI);
		
		contactAddress.setDisplayName(getUsername());
		ContactHeader contactHeader = headerFactory.createContactHeader(contactAddress);
		response.addHeader(contactHeader);
		
     }
    public void processRegister(RequestEvent requestEvent) {
	  SipProvider sipProvider = (SipProvider) requestEvent.getSource();
  	  Request request = requestEvent.getRequest();
  	  try {
  	      	ServerTransaction serverTransaction= sipProvider.getNewServerTransaction(request);
		inviteTid = serverTransaction;
		messageProcessor.processInfo("Received a REGISTER message : \n " + request );
		Response response = messageFactory.createResponse(401,request);
		createResponseHeader(response);
		
		
    messageProcessor.processInfo("Send a 401 Unauthorized message :\n" + response );
			
	  // Update the SIP message table.
   this.updateTable(requestEvent, request, serverTransaction);
    
		serverTransaction.sendResponse(response);
		
	} catch (Exception ex) {
           ex.printStackTrace();
           System.exit(0);
	}
	
    }
  
    
    private void updateTable(RequestEvent requestEvent, Request request, Transaction transaction) throws SdpException {
        // Get the table model.
       DefaultTableModel tableModel = (DefaultTableModel) SipClientGUI.getjTable().getModel();
        // Get the headers.
        FromHeader from = (FromHeader)request.getHeader("From");
        ToHeader to = (ToHeader)request.getHeader("To");
        CallIdHeader callId = (CallIdHeader)request.getHeader("Call-Id");
        CSeqHeader cSeq = (CSeqHeader)request.getHeader("CSeq");
        // Get the SIP dialog.
        Dialog dialog = transaction.getDialog();
        
        String sdpMediaDescription= request.getContentLength().getContentLength()
        				!= 0 ? request.toString().substring(request.toString().indexOf("m=")) :"(None)";
        String sdpBandwidth=  request.getContentLength().getContentLength()
						!= 0 ? request.toString().substring(request.toString().indexOf("b=")) :"(None)" ;
				
				tableModel.addRow(new Object[] {
            (new Date()).toString(),
            request.getRequestURI() != null ? request.getRequestURI().toString() : "(unknown)",
            from != null ? from.getAddress() : "(unknown)",
            to != null ? to.getAddress() : "(unknown)",
            callId != null ? callId.getCallId() : "(unknown)",
            cSeq != null ? cSeq.getSeqNumber() + " " + cSeq.getMethod() : "(unknown)",
            dialog != null ? dialog.getDialogId() : "",
            transaction.getBranchId(),
            "Request",
            request.getMethod(),
            sdpMediaDescription != null ? sdpMediaDescription : "",
            sdpBandwidth != null ? sdpBandwidth : ""
            });
    }
    
    
    private void responseUpdateTable(ResponseEvent responseEvent, Response response, ClientTransaction transaction) throws SdpException {
        // Get the table model.
        DefaultTableModel tableModel = (DefaultTableModel) SipClientGUI.getjTable().getModel();
      
        FromHeader from = (FromHeader)response.getHeader("From");
        ToHeader to = (ToHeader)response.getHeader("To");
        CallIdHeader callId = (CallIdHeader)response.getHeader("Call-Id");
        CSeqHeader cSeq = (CSeqHeader)response.getHeader("CSeq");
      
        Dialog dialog = transaction.getDialog();
     
        String sdpMediaDescription= response.getContentLength().getContentLength()
					!= 0 ? response.toString().substring(response.toString().indexOf("m=")) :"(None)";
		String sdpBandwidth=  response.getContentLength().getContentLength()
					!= 0 ? response.toString().substring(response.toString().indexOf("b=")) :"(None)" ;
        // Add a new line to the table.
        tableModel.addRow(new Object[] {
            (new Date()).toString(),
            "",
            from != null ? from.getAddress() : "(unknown)",
            to != null ? to.getAddress() : "(unknown)",
            callId != null ? callId.getCallId() : "(unknown)",
            cSeq != null ? cSeq.getSeqNumber() + " " + cSeq.getMethod() : "(unknown)",
            dialog != null ? dialog.getDialogId() : "",
            transaction.getBranchId(),
            "Response",
            response.getStatusCode(),
            sdpMediaDescription!= null ? sdpMediaDescription : "",
            sdpBandwidth != null ? sdpBandwidth : ""
            });
       
    }
   
    /** 
     * This method is called by the SIP stack when there's no answer 
     * to a message. Note that this is treated differently from an error
     * message. 
     */
    public void processTimeout(TimeoutEvent evt) {
	//messageProcessor.processInfo("Previous message not sent: " + "timeout");
    }

    /** 
     * This method is called by the SIP stack when there's an asynchronous
     * message transmission error.  
     */
    public void processIOException(IOExceptionEvent evt) {
	//messageProcessor.processInfo("Previous message not sent: "+ "I/O Exception");
    }
    /** 
     * This method is called by the SIP stack when a dialog (session) ends. 
     */
    public void processDialogTerminated(DialogTerminatedEvent evt) {
    }

    /** 
     * This method is called by the SIP stack when a transaction ends. 
     */
    public void processTransactionTerminated(TransactionTerminatedEvent evt) {
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

  	
  	
	public String generateTag() {
		String tag = new Integer((int) (Math.random() * 10000)).toString();
		return tag;
	}

	
	


}
