package com.sun.net.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

final class X509TrustManagerComSunWrapper implements X509TrustManager {
  private X509TrustManager theX509TrustManager;
  
  X509TrustManagerComSunWrapper(X509TrustManager paramX509TrustManager) { this.theX509TrustManager = paramX509TrustManager; }
  
  public boolean isClientTrusted(X509Certificate[] paramArrayOfX509Certificate) {
    try {
      this.theX509TrustManager.checkClientTrusted(paramArrayOfX509Certificate, "UNKNOWN");
      return true;
    } catch (CertificateException certificateException) {
      return false;
    } 
  }
  
  public boolean isServerTrusted(X509Certificate[] paramArrayOfX509Certificate) {
    try {
      this.theX509TrustManager.checkServerTrusted(paramArrayOfX509Certificate, "UNKNOWN");
      return true;
    } catch (CertificateException certificateException) {
      return false;
    } 
  }
  
  public X509Certificate[] getAcceptedIssuers() { return this.theX509TrustManager.getAcceptedIssuers(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\net\ssl\X509TrustManagerComSunWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */