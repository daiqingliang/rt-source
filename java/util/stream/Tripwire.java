package java.util.stream;

import java.security.AccessController;
import sun.util.logging.PlatformLogger;

final class Tripwire {
  private static final String TRIPWIRE_PROPERTY = "org.openjdk.java.util.stream.tripwire";
  
  static final boolean ENABLED = ((Boolean)AccessController.doPrivileged(() -> Boolean.valueOf(Boolean.getBoolean("org.openjdk.java.util.stream.tripwire")))).booleanValue();
  
  static void trip(Class<?> paramClass, String paramString) { PlatformLogger.getLogger(paramClass.getName()).warning(paramString, new Object[] { paramClass.getName() }); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\stream\Tripwire.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */