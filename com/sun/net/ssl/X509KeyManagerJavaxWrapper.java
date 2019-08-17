package com.sun.net.ssl;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509KeyManager;

final class X509KeyManagerJavaxWrapper implements X509KeyManager {
  private X509KeyManager theX509KeyManager;
  
  X509KeyManagerJavaxWrapper(X509KeyManager paramX509KeyManager) { this.theX509KeyManager = paramX509KeyManager; }
  
  public String[] getClientAliases(String paramString, Principal[] paramArrayOfPrincipal) { return this.theX509KeyManager.getClientAliases(paramString, paramArrayOfPrincipal); }
  
  public String chooseClientAlias(String[] paramArrayOfString, Principal[] paramArrayOfPrincipal, Socket paramSocket) {
    if (paramArrayOfString == null)
      return null; 
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      String str;
      if ((str = this.theX509KeyManager.chooseClientAlias(paramArrayOfString[b], paramArrayOfPrincipal)) != null)
        return str; 
    } 
    return null;
  }
  
  public String chooseEngineClientAlias(String[] paramArrayOfString, Principal[] paramArrayOfPrincipal, SSLEngine paramSSLEngine) {
    if (paramArrayOfString == null)
      return null; 
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      String str;
      if ((str = this.theX509KeyManager.chooseClientAlias(paramArrayOfString[b], paramArrayOfPrincipal)) != null)
        return str; 
    } 
    return null;
  }
  
  public String[] getServerAliases(String paramString, Principal[] paramArrayOfPrincipal) { return this.theX509KeyManager.getServerAliases(paramString, paramArrayOfPrincipal); }
  
  public String chooseServerAlias(String paramString, Principal[] paramArrayOfPrincipal, Socket paramSocket) { return (paramString == null) ? null : this.theX509KeyManager.chooseServerAlias(paramString, paramArrayOfPrincipal); }
  
  public String chooseEngineServerAlias(String paramString, Principal[] paramArrayOfPrincipal, SSLEngine paramSSLEngine) { return (paramString == null) ? null : this.theX509KeyManager.chooseServerAlias(paramString, paramArrayOfPrincipal); }
  
  public X509Certificate[] getCertificateChain(String paramString) { return this.theX509KeyManager.getCertificateChain(paramString); }
  
  public PrivateKey getPrivateKey(String paramString) { return this.theX509KeyManager.getPrivateKey(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\net\ssl\X509KeyManagerJavaxWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */