package sun.awt;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringBufferInputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import sun.util.logging.PlatformLogger;

final class DebugSettings {
  private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.debug.DebugSettings");
  
  static final String PREFIX = "awtdebug";
  
  static final String PROP_FILE = "properties";
  
  private static final String[] DEFAULT_PROPS = { "awtdebug.assert=true", "awtdebug.trace=false", "awtdebug.on=true", "awtdebug.ctrace=false" };
  
  private static DebugSettings instance = null;
  
  private Properties props = new Properties();
  
  private static final String PROP_CTRACE = "ctrace";
  
  private static final int PROP_CTRACE_LEN = "ctrace".length();
  
  static void init() {
    if (instance != null)
      return; 
    NativeLibLoader.loadLibraries();
    instance = new DebugSettings();
    instance.loadNativeSettings();
  }
  
  private DebugSettings() { AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            DebugSettings.this.loadProperties();
            return null;
          }
        }); }
  
  private void loadProperties() {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            DebugSettings.this.loadDefaultProperties();
            DebugSettings.this.loadFileProperties();
            DebugSettings.this.loadSystemProperties();
            return null;
          }
        });
    if (log.isLoggable(PlatformLogger.Level.FINE))
      log.fine("DebugSettings:\n{0}", new Object[] { this }); 
  }
  
  public String toString() {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(byteArrayOutputStream);
    for (String str1 : this.props.stringPropertyNames()) {
      String str2 = this.props.getProperty(str1, "");
      printStream.println(str1 + " = " + str2);
    } 
    return new String(byteArrayOutputStream.toByteArray());
  }
  
  private void loadDefaultProperties() {
    try {
      for (byte b = 0; b < DEFAULT_PROPS.length; b++) {
        StringBufferInputStream stringBufferInputStream = new StringBufferInputStream(DEFAULT_PROPS[b]);
        this.props.load(stringBufferInputStream);
        stringBufferInputStream.close();
      } 
    } catch (IOException iOException) {}
  }
  
  private void loadFileProperties() {
    String str = System.getProperty("awtdebug.properties", "");
    if (str.equals(""))
      str = System.getProperty("user.home", "") + File.separator + "awtdebug" + "." + "properties"; 
    File file = new File(str);
    try {
      println("Reading debug settings from '" + file.getCanonicalPath() + "'...");
      FileInputStream fileInputStream = new FileInputStream(file);
      this.props.load(fileInputStream);
      fileInputStream.close();
    } catch (FileNotFoundException fileNotFoundException) {
      println("Did not find settings file.");
    } catch (IOException iOException) {
      println("Problem reading settings, IOException: " + iOException.getMessage());
    } 
  }
  
  private void loadSystemProperties() {
    Properties properties = System.getProperties();
    for (String str1 : properties.stringPropertyNames()) {
      String str2 = properties.getProperty(str1, "");
      if (str1.startsWith("awtdebug"))
        this.props.setProperty(str1, str2); 
    } 
  }
  
  public boolean getBoolean(String paramString, boolean paramBoolean) {
    String str = getString(paramString, String.valueOf(paramBoolean));
    return str.equalsIgnoreCase("true");
  }
  
  public int getInt(String paramString, int paramInt) {
    String str = getString(paramString, String.valueOf(paramInt));
    return Integer.parseInt(str);
  }
  
  public String getString(String paramString1, String paramString2) {
    String str = "awtdebug." + paramString1;
    return this.props.getProperty(str, paramString2);
  }
  
  private List<String> getPropertyNames() {
    LinkedList linkedList = new LinkedList();
    for (String str : this.props.stringPropertyNames()) {
      str = str.substring("awtdebug".length() + 1);
      linkedList.add(str);
    } 
    return linkedList;
  }
  
  private void println(Object paramObject) {
    if (log.isLoggable(PlatformLogger.Level.FINER))
      log.finer(paramObject.toString()); 
  }
  
  private native void setCTracingOn(boolean paramBoolean);
  
  private native void setCTracingOn(boolean paramBoolean, String paramString);
  
  private native void setCTracingOn(boolean paramBoolean, String paramString, int paramInt);
  
  private void loadNativeSettings() {
    boolean bool = getBoolean("ctrace", false);
    setCTracingOn(bool);
    LinkedList linkedList = new LinkedList();
    for (String str : getPropertyNames()) {
      if (str.startsWith("ctrace") && str.length() > PROP_CTRACE_LEN)
        linkedList.add(str); 
    } 
    Collections.sort(linkedList);
    for (String str1 : linkedList) {
      String str2 = str1.substring(PROP_CTRACE_LEN + 1);
      int i = str2.indexOf('@');
      String str3 = (i != -1) ? str2.substring(0, i) : str2;
      String str4 = (i != -1) ? str2.substring(i + 1) : "";
      boolean bool1 = getBoolean(str1, false);
      if (str4.length() == 0) {
        setCTracingOn(bool1, str3);
        continue;
      } 
      int j = Integer.parseInt(str4, 10);
      setCTracingOn(bool1, str3, j);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\DebugSettings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */