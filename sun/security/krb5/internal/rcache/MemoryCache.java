package sun.security.krb5.internal.rcache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.ReplayCache;

public class MemoryCache extends ReplayCache {
  private static final int lifespan = KerberosTime.getDefaultSkew();
  
  private static final boolean DEBUG = Krb5.DEBUG;
  
  private final Map<String, AuthList> content = new ConcurrentHashMap();
  
  public void checkAndStore(KerberosTime paramKerberosTime, AuthTimeWithHash paramAuthTimeWithHash) throws KrbApErrException {
    String str = paramAuthTimeWithHash.client + "|" + paramAuthTimeWithHash.server;
    ((AuthList)this.content.computeIfAbsent(str, paramString -> new AuthList(lifespan))).put(paramAuthTimeWithHash, paramKerberosTime);
    if (DEBUG)
      System.out.println("MemoryCache: add " + paramAuthTimeWithHash + " to " + str); 
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    for (AuthList authList : this.content.values())
      stringBuilder.append(authList.toString()); 
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\rcache\MemoryCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */