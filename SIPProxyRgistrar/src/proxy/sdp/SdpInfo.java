package proxy.sdp;


public class SdpInfo {
 
  public String IpAddress;
  public int aport;
  public int aformat;
  public int vport;
  public int vformat;
  public int band;
 
 public SdpInfo() {
 
   IpAddress="";
   aport=0;
   aformat=0;
   vport=0;
   vformat=0;
   band =128;
  
}
 
 
public void setIPAddress(String IP) {
 IpAddress=IP;
}
public void setAPort(int AP) {
 aport=AP;
}

 
public String getIPAddress() {
 return IpAddress;
}
public int getAPort() {
 return aport;
}
 
 
}
