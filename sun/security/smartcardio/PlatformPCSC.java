package sun.security.smartcardio;

import java.security.AccessController;
import java.security.PrivilegedAction;

class PlatformPCSC {
  static final Throwable initException = loadLibrary();
  
  static final int SCARD_PROTOCOL_T0 = 1;
  
  static final int SCARD_PROTOCOL_T1 = 2;
  
  static final int SCARD_PROTOCOL_RAW = 65536;
  
  static final int SCARD_UNKNOWN = 0;
  
  static final int SCARD_ABSENT = 1;
  
  static final int SCARD_PRESENT = 2;
  
  static final int SCARD_SWALLOWED = 3;
  
  static final int SCARD_POWERED = 4;
  
  static final int SCARD_NEGOTIABLE = 5;
  
  static final int SCARD_SPECIFIC = 6;
  
  private static Throwable loadLibrary() {
    try {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
              System.loadLibrary("j2pcsc");
              return null;
            }
          });
      return null;
    } catch (Throwable null) {
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\smartcardio\PlatformPCSC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */