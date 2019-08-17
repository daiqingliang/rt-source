package java.util.prefs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.AllPermission;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

public abstract class Preferences {
  private static final PreferencesFactory factory = factory();
  
  public static final int MAX_KEY_LENGTH = 80;
  
  public static final int MAX_VALUE_LENGTH = 8192;
  
  public static final int MAX_NAME_LENGTH = 80;
  
  private static Permission prefsPerm = new RuntimePermission("preferences");
  
  private static PreferencesFactory factory() {
    String str = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
          public String run() { return System.getProperty("java.util.prefs.PreferencesFactory"); }
        });
    if (str != null)
      try {
        return (PreferencesFactory)Class.forName(str, false, ClassLoader.getSystemClassLoader()).newInstance();
      } catch (Exception exception) {
        try {
          SecurityManager securityManager = System.getSecurityManager();
          if (securityManager != null)
            securityManager.checkPermission(new AllPermission()); 
          return (PreferencesFactory)Class.forName(str, false, Thread.currentThread().getContextClassLoader()).newInstance();
        } catch (Exception exception1) {
          throw new InternalError("Can't instantiate Preferences factory " + str, exception1);
        } 
      }  
    return (PreferencesFactory)AccessController.doPrivileged(new PrivilegedAction<PreferencesFactory>() {
          public PreferencesFactory run() { return Preferences.factory1(); }
        });
  }
  
  private static PreferencesFactory factory1() {
    String str2;
    Iterator iterator = ServiceLoader.load(PreferencesFactory.class, ClassLoader.getSystemClassLoader()).iterator();
    while (iterator.hasNext()) {
      try {
        return (PreferencesFactory)iterator.next();
      } catch (ServiceConfigurationError serviceConfigurationError) {
        if (serviceConfigurationError.getCause() instanceof SecurityException)
          continue; 
        throw serviceConfigurationError;
      } 
    } 
    String str1 = System.getProperty("os.name");
    if (str1.startsWith("Windows")) {
      str2 = "java.util.prefs.WindowsPreferencesFactory";
    } else if (str1.contains("OS X")) {
      str2 = "java.util.prefs.MacOSXPreferencesFactory";
    } else {
      str2 = "java.util.prefs.FileSystemPreferencesFactory";
    } 
    try {
      return (PreferencesFactory)Class.forName(str2, false, Preferences.class.getClassLoader()).newInstance();
    } catch (Exception exception) {
      throw new InternalError("Can't instantiate platform default Preferences factory " + str2, exception);
    } 
  }
  
  public static Preferences userNodeForPackage(Class<?> paramClass) { return userRoot().node(nodeName(paramClass)); }
  
  public static Preferences systemNodeForPackage(Class<?> paramClass) { return systemRoot().node(nodeName(paramClass)); }
  
  private static String nodeName(Class<?> paramClass) {
    if (paramClass.isArray())
      throw new IllegalArgumentException("Arrays have no associated preferences node."); 
    String str1 = paramClass.getName();
    int i = str1.lastIndexOf('.');
    if (i < 0)
      return "/<unnamed>"; 
    String str2 = str1.substring(0, i);
    return "/" + str2.replace('.', '/');
  }
  
  public static Preferences userRoot() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(prefsPerm); 
    return factory.userRoot();
  }
  
  public static Preferences systemRoot() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(prefsPerm); 
    return factory.systemRoot();
  }
  
  public abstract void put(String paramString1, String paramString2);
  
  public abstract String get(String paramString1, String paramString2);
  
  public abstract void remove(String paramString);
  
  public abstract void clear();
  
  public abstract void putInt(String paramString, int paramInt);
  
  public abstract int getInt(String paramString, int paramInt);
  
  public abstract void putLong(String paramString, long paramLong);
  
  public abstract long getLong(String paramString, long paramLong);
  
  public abstract void putBoolean(String paramString, boolean paramBoolean);
  
  public abstract boolean getBoolean(String paramString, boolean paramBoolean);
  
  public abstract void putFloat(String paramString, float paramFloat);
  
  public abstract float getFloat(String paramString, float paramFloat);
  
  public abstract void putDouble(String paramString, double paramDouble);
  
  public abstract double getDouble(String paramString, double paramDouble);
  
  public abstract void putByteArray(String paramString, byte[] paramArrayOfByte);
  
  public abstract byte[] getByteArray(String paramString, byte[] paramArrayOfByte);
  
  public abstract String[] keys() throws BackingStoreException;
  
  public abstract String[] childrenNames() throws BackingStoreException;
  
  public abstract Preferences parent();
  
  public abstract Preferences node(String paramString);
  
  public abstract boolean nodeExists(String paramString) throws BackingStoreException;
  
  public abstract void removeNode();
  
  public abstract String name();
  
  public abstract String absolutePath();
  
  public abstract boolean isUserNode();
  
  public abstract String toString();
  
  public abstract void flush();
  
  public abstract void sync();
  
  public abstract void addPreferenceChangeListener(PreferenceChangeListener paramPreferenceChangeListener);
  
  public abstract void removePreferenceChangeListener(PreferenceChangeListener paramPreferenceChangeListener);
  
  public abstract void addNodeChangeListener(NodeChangeListener paramNodeChangeListener);
  
  public abstract void removeNodeChangeListener(NodeChangeListener paramNodeChangeListener);
  
  public abstract void exportNode(OutputStream paramOutputStream) throws IOException, BackingStoreException;
  
  public abstract void exportSubtree(OutputStream paramOutputStream) throws IOException, BackingStoreException;
  
  public static void importPreferences(InputStream paramInputStream) throws IOException, InvalidPreferencesFormatException { XmlSupport.importPreferences(paramInputStream); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\prefs\Preferences.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */