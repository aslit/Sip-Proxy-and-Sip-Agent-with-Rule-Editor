package proxy.ui;


import javax.swing.UIManager;


import java.awt.*;
 
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
 
public class SIPProxyApp {
 
  boolean packFrame = false;
 
  //Construct the application
  public SIPProxyApp() {
 
	  SIPProxyGUI frame = new SIPProxyGUI();
    //Validate frames that have preset sizes
    //Pack frames that have useful preferred size info, e.g. from their layout
    if (packFrame) {
 
      frame.pack();
     
}
    else {
 
      frame.validate();
     
}
    //Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = frame.getSize();
    if (frameSize.height > screenSize.height) {
 
      frameSize.height = screenSize.height;
     
}
    if (frameSize.width > screenSize.width) {
 
      frameSize.width = screenSize.width;
     
}
    frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    frame.setVisible(true);
   
}
 
  //Main method
  public static void main(String[] args) {
 
 
    new SIPProxyApp();
   
}
 
}