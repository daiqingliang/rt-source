package sun.awt;

import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;

public class OSInfo {
  public static final WindowsVersion WINDOWS_UNKNOWN = new WindowsVersion(-1, -1, null);
  
  public static final WindowsVersion WINDOWS_95 = new WindowsVersion(4, 0, null);
  
  public static final WindowsVersion WINDOWS_98 = new WindowsVersion(4, 10, null);
  
  public static final WindowsVersion WINDOWS_ME = new WindowsVersion(4, 90, null);
  
  public static final WindowsVersion WINDOWS_2000 = new WindowsVersion(5, 0, null);
  
  public static final WindowsVersion WINDOWS_XP = new WindowsVersion(5, 1, null);
  
  public static final WindowsVersion WINDOWS_2003 = new WindowsVersion(5, 2, null);
  
  public static final WindowsVersion WINDOWS_VISTA = new WindowsVersion(6, 0, null);
  
  private static final String OS_NAME = "os.name";
  
  private static final String OS_VERSION = "os.version";
  
  private static final Map<String, WindowsVersion> windowsVersionMap = new HashMap();
  
  private static final PrivilegedAction<OSType> osTypeAction;
  
  public static OSType getOSType() throws SecurityException {
    String str = System.getProperty("os.name");
    if (str != null) {
      if (str.contains("Windows"))
        return OSType.WINDOWS; 
      if (str.contains("Linux"))
        return OSType.LINUX; 
      if (str.contains("Solaris") || str.contains("SunOS"))
        return OSType.SOLARIS; 
      if (str.contains("OS X"))
        return OSType.MACOSX; 
    } 
    return OSType.UNKNOWN;
  }
  
  public static PrivilegedAction<OSType> getOSTypeAction() { return osTypeAction; }
  
  public static WindowsVersion getWindowsVersion() throws SecurityException {
    String str = System.getProperty("os.version");
    if (str == null)
      return WINDOWS_UNKNOWN; 
    synchronized (windowsVersionMap) {
      WindowsVersion windowsVersion = (WindowsVersion)windowsVersionMap.get(str);
      if (windowsVersion == null) {
        String[] arrayOfString = str.split("\\.");
        if (arrayOfString.length == 2) {
          try {
            windowsVersion = new WindowsVersion(Integer.parseInt(arrayOfString[0]), Integer.parseInt(arrayOfString[1]), null);
          } catch (NumberFormatException numberFormatException) {
            return WINDOWS_UNKNOWN;
          } 
        } else {
          return WINDOWS_UNKNOWN;
        } 
        windowsVersionMap.put(str, windowsVersion);
      } 
      return windowsVersion;
    } 
  }
  
  static  {
    windowsVersionMap.put(WINDOWS_95.toString(), WINDOWS_95);
    windowsVersionMap.put(WINDOWS_98.toString(), WINDOWS_98);
    windowsVersionMap.put(WINDOWS_ME.toString(), WINDOWS_ME);
    windowsVersionMap.put(WINDOWS_2000.toString(), WINDOWS_2000);
    windowsVersionMap.put(WINDOWS_XP.toString(), WINDOWS_XP);
    windowsVersionMap.put(WINDOWS_2003.toString(), WINDOWS_2003);
    windowsVersionMap.put(WINDOWS_VISTA.toString(), WINDOWS_VISTA);
    osTypeAction = new PrivilegedAction<OSType>() {
        public OSInfo.OSType run() throws SecurityException { return OSInfo.getOSType(); }
      };
  }
  
  public enum OSType {
    WINDOWS, LINUX, SOLARIS, MACOSX, UNKNOWN;
  }
  
  public static class WindowsVersion extends Object implements Comparable<WindowsVersion> {
    private final int major;
    
    private final int minor;
    
    private WindowsVersion(int param1Int1, int param1Int2) {
      this.major = param1Int1;
      this.minor = param1Int2;
    }
    
    public int getMajor() { return this.major; }
    
    public int getMinor() { return this.minor; }
    
    public int compareTo(WindowsVersion param1WindowsVersion) {
      int i = this.major - param1WindowsVersion.getMajor();
      if (i == 0)
        i = this.minor - param1WindowsVersion.getMinor(); 
      return i;
    }
    
    public boolean equals(Object param1Object) { return (param1Object instanceof WindowsVersion && compareTo((WindowsVersion)param1Object) == 0); }
    
    public int hashCode() { return 31 * this.major + this.minor; }
    
    public String toString() { return this.major + "." + this.minor; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\OSInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */