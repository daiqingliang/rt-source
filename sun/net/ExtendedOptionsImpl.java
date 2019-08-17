package sun.net;

import java.io.FileDescriptor;
import java.net.SocketOption;
import java.security.AccessController;
import jdk.net.NetworkPermission;
import jdk.net.SocketFlow;

public class ExtendedOptionsImpl {
  public static void checkSetOptionPermission(SocketOption<?> paramSocketOption) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager == null)
      return; 
    String str = "setOption." + paramSocketOption.name();
    securityManager.checkPermission(new NetworkPermission(str));
  }
  
  public static void checkGetOptionPermission(SocketOption<?> paramSocketOption) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager == null)
      return; 
    String str = "getOption." + paramSocketOption.name();
    securityManager.checkPermission(new NetworkPermission(str));
  }
  
  public static void checkValueType(Object paramObject, Class<?> paramClass) {
    if (!paramClass.isAssignableFrom(paramObject.getClass())) {
      String str = "Found: " + paramObject.getClass().toString() + " Expected: " + paramClass.toString();
      throw new IllegalArgumentException(str);
    } 
  }
  
  private static native void init();
  
  public static native void setFlowOption(FileDescriptor paramFileDescriptor, SocketFlow paramSocketFlow);
  
  public static native void getFlowOption(FileDescriptor paramFileDescriptor, SocketFlow paramSocketFlow);
  
  public static native boolean flowSupported();
  
  static  {
    AccessController.doPrivileged(() -> {
          System.loadLibrary("net");
          return null;
        });
    init();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\ExtendedOptionsImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */