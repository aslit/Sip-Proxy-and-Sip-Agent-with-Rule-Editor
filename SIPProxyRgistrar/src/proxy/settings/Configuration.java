package proxy.settings;

public class Configuration {
 
 
public int sipPort;
public String name;
public String userID;

 
  public Configuration() {
 
    sipPort=5061;
    name="proxy";
    userID="proxy";
  
}
 
  public void setSipPort(int sp) { sipPort=sp;}
  public void setName(String nm) {name=nm;}
  public void setUserID(String UID) {userID=UID;}

  public int getSipPort() {return sipPort;}
  public String getName() {return name;}
  public String getUserID() {return userID;}
}