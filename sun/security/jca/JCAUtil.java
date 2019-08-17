package sun.security.jca;

import java.security.SecureRandom;

public final class JCAUtil {
  private static final int ARRAY_SIZE = 4096;
  
  public static int getTempArraySize(int paramInt) { return Math.min(4096, paramInt); }
  
  public static SecureRandom getSecureRandom() { return CachedSecureRandomHolder.instance; }
  
  private static class CachedSecureRandomHolder {
    public static SecureRandom instance = new SecureRandom();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jca\JCAUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */