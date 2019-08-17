package com.sun.net.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

final class X509TrustManagerJavaxWrapper implements X509TrustManager {
  private X509TrustManager theX509TrustManager;
  
  X509TrustManagerJavaxWrapper(X509TrustManager paramX509TrustManager) { this.theX509TrustManager = paramX509TrustManager; }
  
  public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString) throws CertificateException {
    if (!this.theX509TrustManager.isClientTrusted(paramArrayOfX509Certificate))
      throw new CertificateException("Untrusted Client Certificate Chain"); 
  }
  
  public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString) throws CertificateException {
    if (!this.theX509TrustManager.isServerTrusted(paramArrayOfX509Certificate))
      throw new CertificateException("Untrusted Server Certificate Chain"); 
  }
  
  public X509Certificate[] getAcceptedIssuers() { return this.theX509TrustManager.getAcceptedIssuers(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\net\ssl\X509TrustManagerJavaxWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */