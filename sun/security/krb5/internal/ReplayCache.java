package sun.security.krb5.internal;

import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import sun.security.krb5.internal.rcache.AuthTimeWithHash;
import sun.security.krb5.internal.rcache.DflCache;
import sun.security.krb5.internal.rcache.MemoryCache;

public abstract class ReplayCache {
  public static ReplayCache getInstance(String paramString) {
    if (paramString == null)
      return new MemoryCache(); 
    if (paramString.equals("dfl") || paramString.startsWith("dfl:"))
      return new DflCache(paramString); 
    if (paramString.equals("none"))
      return new ReplayCache() {
          public void checkAndStore(KerberosTime param1KerberosTime, AuthTimeWithHash param1AuthTimeWithHash) throws KrbApErrException {}
        }; 
    throw new IllegalArgumentException("Unknown type: " + paramString);
  }
  
  public static ReplayCache getInstance() {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.security.krb5.rcache"));
    return getInstance(str);
  }
  
  public abstract void checkAndStore(KerberosTime paramKerberosTime, AuthTimeWithHash paramAuthTimeWithHash) throws KrbApErrException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\ReplayCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */