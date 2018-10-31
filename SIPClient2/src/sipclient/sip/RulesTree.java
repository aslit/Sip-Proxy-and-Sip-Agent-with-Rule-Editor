package sipclient.sip;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

import javax.sdp.MediaDescription;
import javax.sdp.Origin;
import javax.sdp.SdpFactory;
import javax.sdp.SdpParseException;
import javax.sdp.SessionDescription;
import javax.sdp.SessionName;
import javax.sdp.Version;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.SipProvider;
import javax.sip.address.SipURI;
import javax.sip.header.ToHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

import sipclient.sdp.SdpInfo;
import sipclient.sdp.SdpManager;


//import com.ibm.media.codec.audio.AudioCodec;





// Tree.java
// This implements a basic generic tree, with labels of type T,
// pointer to the parent node, and a singly linked list of children nodes.
// It provides iterators that loop over children, that loop up to the root,
// and that traverse the tree in prefix order.
//

class RulesTree<T>  implements Iterable<T> {
	SipLayer siplayer ;
	 private SdpManager mySdpManager =new SdpManager();
	 
	    private SdpInfo answerInfo = new SdpInfo();
	    private SdpInfo offerInfo = new SdpInfo();
	   private SdpFactory mySdpFactory = SdpFactory.getInstance();
    private T label;
    private RulesTree<T> parent;
    private RulesTree<T> nextSibling; // next node on the list of parents's  children
    protected RulesTree<T> firstChild;  // first in the linked list of children
                                 
    //  Getters and setters
    public T getLabel() { return label; }  
    public void setLabel(T v) { label = v; }
    public RulesTree<T> getParent() { return parent;}
    public RulesTree<T> getNextSibling() { return nextSibling;}
    public RulesTree<T> getFirstChild() { return firstChild;}

    // Add C to the front of the children of this
    public void addChild(RulesTree<T> c) {
         c.parent = this;
         if (firstChild == null) 
           firstChild = c;
         else {
             c.nextSibling = firstChild;
             firstChild = c;
            }
    }

    // Check if the node is a leaf
    public boolean Leaf() { return firstChild==null; }

    // `Convert the tree into a string. The structure is indicated by
    // square brackets. Uses a preorder listing.
    public String toString() {
       String S = "[ " + label.toString();
       RulesTree<T> N = firstChild;
       while (N != null) {
          S = S + " " + N.toString();
          N = N.nextSibling;
        }
       return S+" ]";
     }

    //display he tree in prefix order
     public void displayPreorder1(int Indent) {
         for (int I = 0; I < Indent; I++) System.out.print(" ");
         if(label != null){
         System.out.println(label.toString()); }
         RulesTree<T> N = firstChild;
         while (N != null) { 
            N.displayPreorder1(Indent+3);
            N = N.nextSibling;
           }
         }
  
	int k= 0;
     
	
	
	public String selectLocationService(String to){
		 String sql2;
		 String contactAdress= null;
		    sql2="SELECT * FROM locationservice";
		    ResultSet rs;
		 
		     try {
				rs = RulesEditor.statement.executeQuery(sql2);
			

		     System.out.println("get record");
		     
		  //STEP 5: Extract data from result set
		 	       while(rs.next()){
		 	          //Retrieve by column name
		 	     
		 	        if( to.equals(rs.getString("addressOfRecord"))) {
		 	        	contactAdress = rs.getString("contactAddress");
		 	        }
		 	       }
		 	       } catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		 	        return contactAdress;
	}
	//compare fields between RulesTree fields and fields of incoming Request message
     public void searchRequest(int m,int Indent, RequestEvent requestEvent) throws SdpParseException {
    	 System.out.println("--------------------------------------search treeee");
    	 SipProvider sipProvider = (SipProvider) requestEvent.getSource();
    	 ArrayList<String> headers= new ArrayList<String>() ;
    	 Request request=requestEvent.getRequest();
    	 
    	 String  toHeader =    request.getHeader("To").toString();
     	 String to_ = toHeader.substring(toHeader.indexOf("<") + 1,toHeader.indexOf(">") );
    	 
    	  SipURI receivedRequestURI=(SipURI)request.getRequestURI();
          String receivedRequestURIDomain=receivedRequestURI.getHost();
          String to =selectLocationService(to_);
          System.out.println("toURI"+to.toString());
 
    	 String from =request.getHeader("From").toString();
    	
    	 String bandwidth =  request.getContentLength().getContentLength() != 0 ?String.valueOf(offerInfo.band)  :"(None)";
    	 byte[] content=(byte[]) request.getContent();
    	 String s = new String(content);
    	 SessionDescription recSdp = mySdpFactory.createSessionDescription(s);
	     offerInfo=mySdpManager.getSdp(content);
	     String sdpCodec = request.getContentLength().getContentLength() != 0 ?String.valueOf(offerInfo.aformat)  :"(None)";
	        
      	 headers.add("Blocked");
      	 headers.add(from.substring(from.indexOf("@") + 1,from.indexOf(":5")) );
     	 headers.add(from.substring(from.indexOf(":5") + 1,from.indexOf(">;t")));
      	 headers.add(to.substring(to.indexOf("@") + 1,to.indexOf(":5")) );
      	 headers.add(to.substring(to.indexOf(":5") + 1));
      	 headers.add(request.getMethod() != null ? request.getMethod() : "(None)");
      	 headers.add("(None)");
      	 headers.add(sdpCodec != null ? sdpCodec :"(None)");
      	 headers.add(bandwidth != null ? bandwidth :"(None)");
      	 System.out.println(" new headers"+headers.toString());
      	System.out.println("stat2 :"+SipLayer.stat2 );
      	 
      	 
         for (int I = 0; I < Indent; I++) System.out.print(" ");
        
         if(label.toString().equals(headers.get(m))){
        	 SipLayer.stat_block= SipLayer.stat_block+1;
        		//	 System.out.println(label.toString());
        	    //     System.out.println("in request  :"+headers.get(m));
        	    //     System.out.println("stat_block :"+SipLayer.stat_block );
        	    	 
         }
    	  else {
    	  // System.out.println(label.toString());
 	      // System.out.println("in request  :"+headers.get(m)+"a");
 	      // System.out.println("stat_block :"+SipLayer.stat_block );
	 
    	      
         }
         
        
         RulesTree<T> N = firstChild;
         while (N != null && headers.get(m)!=null) { 
            N.searchRequest(m+1,Indent+3,requestEvent);
            N = N.nextSibling;
            System.out.println(" m : " +m );
            if(m == 7 && SipLayer.stat_block == 8){
            	SipLayer.stat2 ="BLOCKED";
            	SipLayer.stat_block = 0;
            }
           else if  (m == 7 ){ 
            	System.out.println(" m : " +m );
            	 System.out.println("stat2 :"+SipLayer.stat2 );
            	 SipLayer.stat_block = 0;
            }
            System.out.println("stat2 :"+SipLayer.stat2 );
           }
         
                    
         }

   //compare fields between RulesTree fields and fields of incoming Request message
     public void searchResponse(int m,int Indent, ResponseEvent responseEvent) throws SdpParseException ,NullPointerException{
    	 SipProvider sipProvider = (SipProvider) responseEvent.getSource();
    	 ArrayList<String> headers= new ArrayList<String>() ;
    	 Response response=responseEvent.getResponse();
    	 
    	 
    	String  toHeader =    response.getHeader("To").toString();
    	String to_ = toHeader.substring(toHeader.indexOf("<") + 1,toHeader.indexOf(">") );
    	String to =selectLocationService(to_);
 
    	 String from =response.getHeader("From").toString();
    	
    	 String bandwidth =  response.getContentLength().getContentLength() != 0 ?String.valueOf(offerInfo.band)  :"(None)";
    	 byte[] content= (byte[]) response.getContent();
    	 String s= new String((String) (content != null ? content: ""));
    	 
    	
    	 SessionDescription recSdp = mySdpFactory.createSessionDescription(s);
	     offerInfo=mySdpManager.getSdp(content);
	     String sdpCodec = response.getContentLength().getContentLength() != 0 ?String.valueOf(offerInfo.aformat)  :"(None)";
	        
	        
      	 headers.add("Blocked");
      	 headers.add(from.substring(from.indexOf("@") + 1,from.indexOf(":5")) );
     	 headers.add(from.substring(from.indexOf(":5") + 1,from.indexOf(">;t")));
      	 headers.add(to.substring(to.indexOf("@") + 1,to.indexOf(":5")) );
      	 headers.add(to.substring(to.indexOf(":5") + 1));
      	 headers.add("(None)");
      	 headers.add(String.valueOf(response.getStatusCode() )!= null ? String.valueOf(response.getStatusCode()) : "(None)");
      	 headers.add(sdpCodec != null ? sdpCodec :"(None)");
      	 headers.add(bandwidth != null ? bandwidth :"(None)");
      	 System.out.println(" new headers"+headers.toString());
      	System.out.println("stat2 :"+SipLayer.stat2 );
      	 
      	 
         for (int I = 0; I < Indent; I++) System.out.print(" ");
         if(label.toString().equals(headers.get(m))){
        	 SipLayer.stat_block= SipLayer.stat_block+1;
        	 
        			 System.out.println(label.toString());
        	         System.out.println("in response  :"+headers.get(m));
        	       //  System.out.println("stat2 :"+SipLayer.stat2 );
        	         System.out.println("stat_block :"+SipLayer.stat_block );
        	    	 
         }
    	  else {
    		  System.out.println(label.toString());
 	         System.out.println("in response  :"+headers.get(m)+"a");
 	        System.out.println("stat_block :"+SipLayer.stat_block );
	 
         }
         
        
         RulesTree<T> N = firstChild;
         while (N != null && headers.get(m)!=null) { 
            N.searchResponse(m+1,Indent+3,responseEvent);
            N = N.nextSibling;
            System.out.println(" m : " +m );
            if(m == 7 && SipLayer.stat_block >= 8){
            	SipLayer.stat2 ="BLOCKED";
            	SipLayer.stat_block = 0;
            }
           else if  (m == 7 ){ 
            	System.out.println(" m : " +m );
            	 System.out.println("stat2 :"+SipLayer.stat2 );
            	 SipLayer.stat_block = 0;
            }
            System.out.println("stat2 :"+SipLayer.stat2 );
           }
         
              
                    
         }

 
	public void forEach(Consumer<? super T> arg0) {
		// TODO Auto-generated method stub
		
	}
	public Iterator<T> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
	public Spliterator<T> spliterator() {
		// TODO Auto-generated method stub
		return null;
	}

   
}