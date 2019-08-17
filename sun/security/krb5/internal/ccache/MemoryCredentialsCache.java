package sun.security.krb5.internal.ccache;

import java.io.File;
import sun.security.krb5.PrincipalName;

public abstract class MemoryCredentialsCache extends CredentialsCache {
  private static CredentialsCache getCCacheInstance(PrincipalName paramPrincipalName) { return null; }
  
  private static CredentialsCache getCCacheInstance(PrincipalName paramPrincipalName, File paramFile) { return null; }
  
  public abstract boolean exists(String paramString);
  
  public abstract void update(Credentials paramCredentials);
  
  public abstract void save();
  
  public abstract Credentials[] getCredsList();
  
  public abstract Credentials getCreds(PrincipalName paramPrincipalName);
  
  public abstract PrincipalName getPrimaryPrincipal();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\ccache\MemoryCredentialsCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */