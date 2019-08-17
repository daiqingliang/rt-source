package sun.font;

import java.security.AccessController;
import java.security.PrivilegedAction;

public class FontManagerNativeLibrary {
  public static void load() {}
  
  static  {
    AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            System.loadLibrary("awt");
            if (FontUtilities.isOpenJDK && System.getProperty("os.name").startsWith("Windows"))
              System.loadLibrary("freetype"); 
            System.loadLibrary("fontmanager");
            return null;
          }
        });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\FontManagerNativeLibrary.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */