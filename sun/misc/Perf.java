package sun.misc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.PrivilegedAction;

public final class Perf {
  private static Perf instance;
  
  private static final int PERF_MODE_RO = 0;
  
  private static final int PERF_MODE_RW = 1;
  
  public static Perf getPerf() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      RuntimePermission runtimePermission = new RuntimePermission("sun.misc.Perf.getPerf");
      securityManager.checkPermission(runtimePermission);
    } 
    return instance;
  }
  
  public ByteBuffer attach(int paramInt, String paramString) throws IllegalArgumentException, IOException {
    if (paramString.compareTo("r") == 0)
      return attachImpl(null, paramInt, 0); 
    if (paramString.compareTo("rw") == 0)
      return attachImpl(null, paramInt, 1); 
    throw new IllegalArgumentException("unknown mode");
  }
  
  public ByteBuffer attach(String paramString1, int paramInt, String paramString2) throws IllegalArgumentException, IOException {
    if (paramString2.compareTo("r") == 0)
      return attachImpl(paramString1, paramInt, 0); 
    if (paramString2.compareTo("rw") == 0)
      return attachImpl(paramString1, paramInt, 1); 
    throw new IllegalArgumentException("unknown mode");
  }
  
  private ByteBuffer attachImpl(String paramString, int paramInt1, int paramInt2) throws IllegalArgumentException, IOException {
    final ByteBuffer b = attach(paramString, paramInt1, paramInt2);
    if (paramInt1 == 0)
      return byteBuffer1; 
    ByteBuffer byteBuffer2 = byteBuffer1.duplicate();
    Cleaner.create(byteBuffer2, new Runnable() {
          public void run() {
            try {
              instance.detach(b);
            } catch (Throwable throwable) {
              assert false : throwable.toString();
            } 
          }
        });
    return byteBuffer2;
  }
  
  private native ByteBuffer attach(String paramString, int paramInt1, int paramInt2) throws IllegalArgumentException, IOException;
  
  private native void detach(ByteBuffer paramByteBuffer);
  
  public native ByteBuffer createLong(String paramString, int paramInt1, int paramInt2, long paramLong);
  
  public ByteBuffer createString(String paramString1, int paramInt1, int paramInt2, String paramString2, int paramInt3) {
    byte[] arrayOfByte1 = getBytes(paramString2);
    byte[] arrayOfByte2 = new byte[arrayOfByte1.length + 1];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, arrayOfByte1.length);
    arrayOfByte2[arrayOfByte1.length] = 0;
    return createByteArray(paramString1, paramInt1, paramInt2, arrayOfByte2, Math.max(arrayOfByte2.length, paramInt3));
  }
  
  public ByteBuffer createString(String paramString1, int paramInt1, int paramInt2, String paramString2) {
    byte[] arrayOfByte1 = getBytes(paramString2);
    byte[] arrayOfByte2 = new byte[arrayOfByte1.length + 1];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, arrayOfByte1.length);
    arrayOfByte2[arrayOfByte1.length] = 0;
    return createByteArray(paramString1, paramInt1, paramInt2, arrayOfByte2, arrayOfByte2.length);
  }
  
  public native ByteBuffer createByteArray(String paramString, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3);
  
  private static byte[] getBytes(String paramString) {
    byte[] arrayOfByte = null;
    try {
      arrayOfByte = paramString.getBytes("UTF-8");
    } catch (UnsupportedEncodingException unsupportedEncodingException) {}
    return arrayOfByte;
  }
  
  public native long highResCounter();
  
  public native long highResFrequency();
  
  private static native void registerNatives();
  
  static  {
    registerNatives();
    instance = new Perf();
  }
  
  public static class GetPerfAction extends Object implements PrivilegedAction<Perf> {
    public Perf run() { return Perf.getPerf(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\Perf.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */