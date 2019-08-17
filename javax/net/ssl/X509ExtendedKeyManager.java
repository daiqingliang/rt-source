package javax.net.ssl;

import java.security.Principal;

public abstract class X509ExtendedKeyManager implements X509KeyManager {
  public String chooseEngineClientAlias(String[] paramArrayOfString, Principal[] paramArrayOfPrincipal, SSLEngine paramSSLEngine) { return null; }
  
  public String chooseEngineServerAlias(String paramString, Principal[] paramArrayOfPrincipal, SSLEngine paramSSLEngine) { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\net\ssl\X509ExtendedKeyManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */