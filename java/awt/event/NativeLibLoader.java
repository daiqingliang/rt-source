package java.awt.event;

import java.security.AccessController;
import java.security.PrivilegedAction;

class NativeLibLoader {
  static void loadLibraries() { AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            System.loadLibrary("awt");
            return null;
          }
        }); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\event\NativeLibLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */