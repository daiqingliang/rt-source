package sun.nio.fs;

import java.io.IOException;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class RegistryFileTypeDetector extends AbstractFileTypeDetector {
  public String implProbeContentType(Path paramPath) throws IOException {
    if (!(paramPath instanceof Path))
      return null; 
    Path path = paramPath.getFileName();
    if (path == null)
      return null; 
    String str1 = path.toString();
    int i = str1.lastIndexOf('.');
    if (i < 0 || i == str1.length() - 1)
      return null; 
    String str2 = str1.substring(i);
    nativeBuffer1 = WindowsNativeDispatcher.asNativeBuffer(str2);
    nativeBuffer2 = WindowsNativeDispatcher.asNativeBuffer("Content Type");
    try {
      return queryStringValue(nativeBuffer1.address(), nativeBuffer2.address());
    } finally {
      nativeBuffer2.release();
      nativeBuffer1.release();
    } 
  }
  
  private static native String queryStringValue(long paramLong1, long paramLong2);
  
  static  {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            System.loadLibrary("net");
            System.loadLibrary("nio");
            return null;
          }
        });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\fs\RegistryFileTypeDetector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */