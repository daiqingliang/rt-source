package sun.net;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Security;

public final class InetAddressCachePolicy {
  private static final String cachePolicyProp = "networkaddress.cache.ttl";
  
  private static final String cachePolicyPropFallback = "sun.net.inetaddr.ttl";
  
  private static final String negativeCachePolicyProp = "networkaddress.cache.negative.ttl";
  
  private static final String negativeCachePolicyPropFallback = "sun.net.inetaddr.negative.ttl";
  
  public static final int FOREVER = -1;
  
  public static final int NEVER = 0;
  
  public static final int DEFAULT_POSITIVE = 30;
  
  private static int cachePolicy = -1;
  
  private static int negativeCachePolicy = 0;
  
  private static boolean propertySet;
  
  private static boolean propertyNegativeSet;
  
  public static int get() { return cachePolicy; }
  
  public static int getNegative() { return negativeCachePolicy; }
  
  public static void setIfNotSet(int paramInt) {
    if (!propertySet) {
      checkValue(paramInt, cachePolicy);
      cachePolicy = paramInt;
    } 
  }
  
  public static void setNegativeIfNotSet(int paramInt) {
    if (!propertyNegativeSet)
      negativeCachePolicy = paramInt; 
  }
  
  private static void checkValue(int paramInt1, int paramInt2) {
    if (paramInt1 == -1)
      return; 
    if (paramInt2 == -1 || paramInt1 < paramInt2 || paramInt1 < -1)
      throw new SecurityException("can't make InetAddress cache more lax"); 
  }
  
  static  {
    Integer integer = (Integer)AccessController.doPrivileged(new PrivilegedAction<Integer>() {
          public Integer run() {
            try {
              String str = Security.getProperty("networkaddress.cache.ttl");
              if (str != null)
                return Integer.valueOf(str); 
            } catch (NumberFormatException numberFormatException) {}
            try {
              String str = System.getProperty("sun.net.inetaddr.ttl");
              if (str != null)
                return Integer.decode(str); 
            } catch (NumberFormatException numberFormatException) {}
            return null;
          }
        });
    if (integer != null) {
      cachePolicy = integer.intValue();
      if (cachePolicy < 0)
        cachePolicy = -1; 
      propertySet = true;
    } else if (System.getSecurityManager() == null) {
      cachePolicy = 30;
    } 
    integer = (Integer)AccessController.doPrivileged(new PrivilegedAction<Integer>() {
          public Integer run() {
            try {
              String str = Security.getProperty("networkaddress.cache.negative.ttl");
              if (str != null)
                return Integer.valueOf(str); 
            } catch (NumberFormatException numberFormatException) {}
            try {
              String str = System.getProperty("sun.net.inetaddr.negative.ttl");
              if (str != null)
                return Integer.decode(str); 
            } catch (NumberFormatException numberFormatException) {}
            return null;
          }
        });
    if (integer != null) {
      negativeCachePolicy = integer.intValue();
      if (negativeCachePolicy < 0)
        negativeCachePolicy = -1; 
      propertyNegativeSet = true;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\InetAddressCachePolicy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */