package com.sun.net.ssl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.SSLSocketFactory;
import javax.security.cert.X509Certificate;

@Deprecated
public abstract class HttpsURLConnection extends HttpURLConnection {
  private static HostnameVerifier defaultHostnameVerifier = new HostnameVerifier() {
      public boolean verify(String param1String1, String param1String2) { return false; }
    };
  
  protected HostnameVerifier hostnameVerifier = defaultHostnameVerifier;
  
  private static SSLSocketFactory defaultSSLSocketFactory = null;
  
  private SSLSocketFactory sslSocketFactory = getDefaultSSLSocketFactory();
  
  public HttpsURLConnection(URL paramURL) throws IOException { super(paramURL); }
  
  public abstract String getCipherSuite();
  
  public abstract X509Certificate[] getServerCertificateChain();
  
  public static void setDefaultHostnameVerifier(HostnameVerifier paramHostnameVerifier) {
    if (paramHostnameVerifier == null)
      throw new IllegalArgumentException("no default HostnameVerifier specified"); 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new SSLPermission("setHostnameVerifier")); 
    defaultHostnameVerifier = paramHostnameVerifier;
  }
  
  public static HostnameVerifier getDefaultHostnameVerifier() { return defaultHostnameVerifier; }
  
  public void setHostnameVerifier(HostnameVerifier paramHostnameVerifier) {
    if (paramHostnameVerifier == null)
      throw new IllegalArgumentException("no HostnameVerifier specified"); 
    this.hostnameVerifier = paramHostnameVerifier;
  }
  
  public HostnameVerifier getHostnameVerifier() { return this.hostnameVerifier; }
  
  public static void setDefaultSSLSocketFactory(SSLSocketFactory paramSSLSocketFactory) {
    if (paramSSLSocketFactory == null)
      throw new IllegalArgumentException("no default SSLSocketFactory specified"); 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkSetFactory(); 
    defaultSSLSocketFactory = paramSSLSocketFactory;
  }
  
  public static SSLSocketFactory getDefaultSSLSocketFactory() {
    if (defaultSSLSocketFactory == null)
      defaultSSLSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault(); 
    return defaultSSLSocketFactory;
  }
  
  public void setSSLSocketFactory(SSLSocketFactory paramSSLSocketFactory) {
    if (paramSSLSocketFactory == null)
      throw new IllegalArgumentException("no SSLSocketFactory specified"); 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkSetFactory(); 
    this.sslSocketFactory = paramSSLSocketFactory;
  }
  
  public SSLSocketFactory getSSLSocketFactory() { return this.sslSocketFactory; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\net\ssl\HttpsURLConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */