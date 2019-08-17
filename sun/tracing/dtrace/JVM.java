package sun.tracing.dtrace;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

class JVM {
  static long activate(String paramString, DTraceProvider[] paramArrayOfDTraceProvider) { return activate0(paramString, paramArrayOfDTraceProvider); }
  
  static void dispose(long paramLong) { dispose0(paramLong); }
  
  static boolean isEnabled(Method paramMethod) { return isEnabled0(paramMethod); }
  
  static boolean isSupported() { return isSupported0(); }
  
  static Class<?> defineClass(ClassLoader paramClassLoader, String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2) { return defineClass0(paramClassLoader, paramString, paramArrayOfByte, paramInt1, paramInt2); }
  
  private static native long activate0(String paramString, DTraceProvider[] paramArrayOfDTraceProvider);
  
  private static native void dispose0(long paramLong);
  
  private static native boolean isEnabled0(Method paramMethod);
  
  private static native boolean isSupported0();
  
  private static native Class<?> defineClass0(ClassLoader paramClassLoader, String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  static  {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            System.loadLibrary("jsdt");
            return null;
          }
        });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\tracing\dtrace\JVM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */