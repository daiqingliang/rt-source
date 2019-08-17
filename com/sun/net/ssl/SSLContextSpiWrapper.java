package com.sun.net.ssl;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

final class SSLContextSpiWrapper extends SSLContextSpi {
  private SSLContext theSSLContext;
  
  SSLContextSpiWrapper(String paramString, Provider paramProvider) throws NoSuchAlgorithmException { this.theSSLContext = SSLContext.getInstance(paramString, paramProvider); }
  
  protected void engineInit(KeyManager[] paramArrayOfKeyManager, TrustManager[] paramArrayOfTrustManager, SecureRandom paramSecureRandom) throws KeyManagementException {
    TrustManager[] arrayOfTrustManager;
    KeyManager[] arrayOfKeyManager;
    if (paramArrayOfKeyManager != null) {
      arrayOfKeyManager = new KeyManager[paramArrayOfKeyManager.length];
      byte b2 = 0;
      byte b1 = 0;
      while (b2 < paramArrayOfKeyManager.length) {
        if (!(paramArrayOfKeyManager[b2] instanceof KeyManager)) {
          if (paramArrayOfKeyManager[b2] instanceof X509KeyManager) {
            arrayOfKeyManager[b1] = new X509KeyManagerJavaxWrapper((X509KeyManager)paramArrayOfKeyManager[b2]);
            b1++;
          } 
        } else {
          arrayOfKeyManager[b1] = (KeyManager)paramArrayOfKeyManager[b2];
          b1++;
        } 
        b2++;
      } 
      if (b1 != b2)
        arrayOfKeyManager = (KeyManager[])SSLSecurity.truncateArray(arrayOfKeyManager, new KeyManager[b1]); 
    } else {
      arrayOfKeyManager = null;
    } 
    if (paramArrayOfTrustManager != null) {
      arrayOfTrustManager = new TrustManager[paramArrayOfTrustManager.length];
      byte b2 = 0;
      byte b1 = 0;
      while (b2 < paramArrayOfTrustManager.length) {
        if (!(paramArrayOfTrustManager[b2] instanceof TrustManager)) {
          if (paramArrayOfTrustManager[b2] instanceof X509TrustManager) {
            arrayOfTrustManager[b1] = new X509TrustManagerJavaxWrapper((X509TrustManager)paramArrayOfTrustManager[b2]);
            b1++;
          } 
        } else {
          arrayOfTrustManager[b1] = (TrustManager)paramArrayOfTrustManager[b2];
          b1++;
        } 
        b2++;
      } 
      if (b1 != b2)
        arrayOfTrustManager = (TrustManager[])SSLSecurity.truncateArray(arrayOfTrustManager, new TrustManager[b1]); 
    } else {
      arrayOfTrustManager = null;
    } 
    this.theSSLContext.init(arrayOfKeyManager, arrayOfTrustManager, paramSecureRandom);
  }
  
  protected SSLSocketFactory engineGetSocketFactory() { return this.theSSLContext.getSocketFactory(); }
  
  protected SSLServerSocketFactory engineGetServerSocketFactory() { return this.theSSLContext.getServerSocketFactory(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\net\ssl\SSLContextSpiWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */