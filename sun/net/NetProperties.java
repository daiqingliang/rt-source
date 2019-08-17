package sun.net;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;

public class NetProperties {
  private static Properties props = new Properties();
  
  private static void loadDefaultProperties() {
    String str = System.getProperty("java.home");
    if (str == null)
      throw new Error("Can't find java.home ??"); 
    try {
      File file = new File(str, "lib");
      file = new File(file, "net.properties");
      str = file.getCanonicalPath();
      FileInputStream fileInputStream = new FileInputStream(str);
      BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
      props.load(bufferedInputStream);
      bufferedInputStream.close();
    } catch (Exception exception) {}
  }
  
  public static String get(String paramString) {
    String str = props.getProperty(paramString);
    try {
      return System.getProperty(paramString, str);
    } catch (IllegalArgumentException illegalArgumentException) {
    
    } catch (NullPointerException nullPointerException) {}
    return null;
  }
  
  public static Integer getInteger(String paramString, int paramInt) {
    String str = null;
    try {
      str = System.getProperty(paramString, props.getProperty(paramString));
    } catch (IllegalArgumentException illegalArgumentException) {
    
    } catch (NullPointerException nullPointerException) {}
    if (str != null)
      try {
        return Integer.decode(str);
      } catch (NumberFormatException numberFormatException) {} 
    return new Integer(paramInt);
  }
  
  public static Boolean getBoolean(String paramString) {
    String str = null;
    try {
      str = System.getProperty(paramString, props.getProperty(paramString));
    } catch (IllegalArgumentException illegalArgumentException) {
    
    } catch (NullPointerException nullPointerException) {}
    if (str != null)
      try {
        return Boolean.valueOf(str);
      } catch (NumberFormatException numberFormatException) {} 
    return null;
  }
  
  static  {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            NetProperties.loadDefaultProperties();
            return null;
          }
        });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\NetProperties.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */