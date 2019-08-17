package jdk.xml.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Properties;

class SecuritySupport {
  static final Properties cacheProps = new Properties();
  
  public static String getSystemProperty(final String propName) { return (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
          public String run() { return System.getProperty(propName); }
        }); }
  
  public static <T> T getJAXPSystemProperty(Class<T> paramClass, String paramString1, String paramString2) {
    String str = getJAXPSystemProperty(paramString1);
    if (str == null)
      str = paramString2; 
    return Integer.class.isAssignableFrom(paramClass) ? (T)paramClass.cast(Integer.valueOf(Integer.parseInt(str))) : (Boolean.class.isAssignableFrom(paramClass) ? (T)paramClass.cast(Boolean.valueOf(Boolean.parseBoolean(str))) : (T)paramClass.cast(str));
  }
  
  public static String getJAXPSystemProperty(String paramString) {
    String str = getSystemProperty(paramString);
    if (str == null)
      str = readJAXPProperty(paramString); 
    return str;
  }
  
  public static String readJAXPProperty(String paramString) {
    String str = null;
    fileInputStream = null;
    try {
      if (firstTime)
        synchronized (cacheProps) {
          if (firstTime) {
            String str1 = getSystemProperty("java.home") + File.separator + "lib" + File.separator + "jaxp.properties";
            File file = new File(str1);
            if (getFileExists(file)) {
              fileInputStream = getFileInputStream(file);
              cacheProps.load(fileInputStream);
            } 
            firstTime = false;
          } 
        }  
      str = cacheProps.getProperty(paramString);
    } catch (IOException iOException) {
    
    } finally {
      if (fileInputStream != null)
        try {
          fileInputStream.close();
        } catch (IOException iOException) {} 
    } 
    return str;
  }
  
  static boolean getFileExists(final File f) { return ((Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
          public Boolean run() { return f.exists() ? Boolean.TRUE : Boolean.FALSE; }
        })).booleanValue(); }
  
  static FileInputStream getFileInputStream(final File file) throws FileNotFoundException {
    try {
      return (FileInputStream)AccessController.doPrivileged(new PrivilegedExceptionAction<FileInputStream>() {
            public FileInputStream run() throws Exception { return new FileInputStream(file); }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      throw (FileNotFoundException)privilegedActionException.getException();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\xml\internal\SecuritySupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */