package com.sun.net.ssl;

import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509KeyManager;

final class X509KeyManagerComSunWrapper implements X509KeyManager {
  private X509KeyManager theX509KeyManager;
  
  X509KeyManagerComSunWrapper(X509KeyManager paramX509KeyManager) { this.theX509KeyManager = paramX509KeyManager; }
  
  public String[] getClientAliases(String paramString, Principal[] paramArrayOfPrincipal) { return this.theX509KeyManager.getClientAliases(paramString, paramArrayOfPrincipal); }
  
  public String chooseClientAlias(String paramString, Principal[] paramArrayOfPrincipal) {
    String[] arrayOfString = { paramString };
    return this.theX509KeyManager.chooseClientAlias(arrayOfString, paramArrayOfPrincipal, null);
  }
  
  public String[] getServerAliases(String paramString, Principal[] paramArrayOfPrincipal) { return this.theX509KeyManager.getServerAliases(paramString, paramArrayOfPrincipal); }
  
  public String chooseServerAlias(String paramString, Principal[] paramArrayOfPrincipal) { return this.theX509KeyManager.chooseServerAlias(paramString, paramArrayOfPrincipal, null); }
  
  public X509Certificate[] getCertificateChain(String paramString) { return this.theX509KeyManager.getCertificateChain(paramString); }
  
  public PrivateKey getPrivateKey(String paramString) { return this.theX509KeyManager.getPrivateKey(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\net\ssl\X509KeyManagerComSunWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */