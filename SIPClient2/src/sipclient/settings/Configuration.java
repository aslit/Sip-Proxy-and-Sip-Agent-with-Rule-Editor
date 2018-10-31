package sipclient.settings;


public class Configuration {
 
 
public int sipPort;
public String name;
public String userID;
//public int audioPort;
//public int videoPort;
public int audioCodec;
public int videoCodec;
public int bandwidth;
 
  public Configuration() {
 
    sipPort=5082;
    name="user1";
    userID="user1@sipproxy.com";
    bandwidth = 128;
   
}
 
  public int getBandwidth() {
	return bandwidth;
}

public void setBandwidth(int bandwidth) {
	this.bandwidth = bandwidth;
}

public void setSipPort(int sp) { sipPort=sp;}
  public void setName(String nm) {name=nm;}
  public void setUserID(String UID) {userID=UID;}
 
  public int getSipPort() {return sipPort;}
  public String getName() {return name;}
  public String getUserID() {return userID;}
 
}