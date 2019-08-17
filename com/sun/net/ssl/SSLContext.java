package com.sun.net.ssl;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;

@Deprecated
public class SSLContext {
  private Provider provider;
  
  private SSLContextSpi contextSpi;
  
  private String protocol;
  
  protected SSLContext(SSLContextSpi paramSSLContextSpi, Provider paramProvider, String paramString) {
    this.contextSpi = paramSSLContextSpi;
    this.provider = paramProvider;
    this.protocol = paramString;
  }
  
  public static SSLContext getInstance(String paramString) throws NoSuchAlgorithmException {
    try {
      Object[] arrayOfObject = SSLSecurity.getImpl(paramString, "SSLContext", (String)null);
      return new SSLContext((SSLContextSpi)arrayOfObject[0], (Provider)arrayOfObject[1], paramString);
    } catch (NoSuchProviderException noSuchProviderException) {
      throw new NoSuchAlgorithmException(paramString + " not found");
    } 
  }
  
  public static SSLContext getInstance(String paramString1, String paramString2) throws NoSuchAlgorithmException, NoSuchProviderException {
    if (paramString2 == null || paramString2.length() == 0)
      throw new IllegalArgumentException("missing provider"); 
    Object[] arrayOfObject = SSLSecurity.getImpl(paramString1, "SSLContext", paramString2);
    return new SSLContext((SSLContextSpi)arrayOfObject[0], (Provider)arrayOfObject[1], paramString1);
  }
  
  public static SSLContext getInstance(String paramString, Provider paramProvider) throws NoSuchAlgorithmException {
    if (paramProvider == null)
      throw new IllegalArgumentException("missing provider"); 
    Object[] arrayOfObject = SSLSecurity.getImpl(paramString, "SSLContext", paramProvider);
    return new SSLContext((SSLContextSpi)arrayOfObject[0], (Provider)arrayOfObject[1], paramString);
  }
  
  public final String getProtocol() { return this.protocol; }
  
  public final Provider getProvider() { return this.provider; }
  
  public final void init(KeyManager[] paramArrayOfKeyManager, TrustManager[] paramArrayOfTrustManager, SecureRandom paramSecureRandom) throws KeyManagementException { this.contextSpi.engineInit(paramArrayOfKeyManager, paramArrayOfTrustManager, paramSecureRandom); }
  
  public final SSLSocketFactory getSocketFactory() { return this.contextSpi.engineGetSocketFactory(); }
  
  public final SSLServerSocketFactory getServerSocketFactory() { return this.contextSpi.engineGetServerSocketFactory(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\net\ssl\SSLContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */