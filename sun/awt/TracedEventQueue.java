package sun.awt;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.util.StringTokenizer;

public class TracedEventQueue extends EventQueue {
  static boolean trace = false;
  
  static int[] suppressedIDs = null;
  
  public void postEvent(AWTEvent paramAWTEvent) {
    boolean bool = true;
    int i = paramAWTEvent.getID();
    for (byte b = 0; b < suppressedIDs.length; b++) {
      if (i == suppressedIDs[b]) {
        bool = false;
        break;
      } 
    } 
    if (bool)
      System.out.println(Thread.currentThread().getName() + ": " + paramAWTEvent); 
    super.postEvent(paramAWTEvent);
  }
  
  static  {
    String str = Toolkit.getProperty("AWT.IgnoreEventIDs", "");
    if (str.length() > 0) {
      StringTokenizer stringTokenizer = new StringTokenizer(str, ",");
      int i = stringTokenizer.countTokens();
      suppressedIDs = new int[i];
      for (byte b = 0; b < i; b++) {
        String str1 = stringTokenizer.nextToken();
        try {
          suppressedIDs[b] = Integer.parseInt(str1);
        } catch (NumberFormatException numberFormatException) {
          System.err.println("Bad ID listed in AWT.IgnoreEventIDs in awt.properties: \"" + str1 + "\" -- skipped");
          suppressedIDs[b] = 0;
        } 
      } 
    } else {
      suppressedIDs = new int[0];
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\TracedEventQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */