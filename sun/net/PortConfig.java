package sun.net;

import java.security.AccessController;
import java.security.PrivilegedAction;

public final class PortConfig {
  private static int defaultUpper;
  
  private static int defaultLower;
  
  private static final int upper;
  
  private static final int lower;
  
  static native int getLower0();
  
  static native int getUpper0();
  
  public static int getLower() { return lower; }
  
  public static int getUpper() { return upper; }
  
  static  {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            System.loadLibrary("net");
            return null;
          }
        });
    int i = getLower0();
    if (i == -1)
      i = defaultLower; 
    lower = i;
    i = getUpper0();
    if (i == -1)
      i = defaultUpper; 
    upper = i;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\PortConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */