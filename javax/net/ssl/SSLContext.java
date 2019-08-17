package javax.net.ssl;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import sun.security.jca.GetInstance;

public class SSLContext {
  private final Provider provider;
  
  private final SSLContextSpi contextSpi;
  
  private final String protocol;
  
  private static SSLContext defaultContext;
  
  protected SSLContext(SSLContextSpi paramSSLContextSpi, Provider paramProvider, String paramString) {
    this.contextSpi = paramSSLContextSpi;
    this.provider = paramProvider;
    this.protocol = paramString;
  }
  
  public static SSLContext getDefault() throws NoSuchAlgorithmException {
    if (defaultContext == null)
      defaultContext = getInstance("Default"); 
    return defaultContext;
  }
  
  public static void setDefault(SSLContext paramSSLContext) {
    if (paramSSLContext == null)
      throw new NullPointerException(); 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new SSLPermission("setDefaultSSLContext")); 
    defaultContext = paramSSLContext;
  }
  
  public static SSLContext getInstance(String paramString) throws NoSuchAlgorithmException {
    GetInstance.Instance instance = GetInstance.getInstance("SSLContext", SSLContextSpi.class, paramString);
    return new SSLContext((SSLContextSpi)instance.impl, instance.provider, paramString);
  }
  
  public static SSLContext getInstance(String paramString1, String paramString2) throws NoSuchAlgorithmException, NoSuchProviderException {
    GetInstance.Instance instance = GetInstance.getInstance("SSLContext", SSLContextSpi.class, paramString1, paramString2);
    return new SSLContext((SSLContextSpi)instance.impl, instance.provider, paramString1);
  }
  
  public static SSLContext getInstance(String paramString, Provider paramProvider) throws NoSuchAlgorithmException {
    GetInstance.Instance instance = GetInstance.getInstance("SSLContext", SSLContextSpi.class, paramString, paramProvider);
    return new SSLContext((SSLContextSpi)instance.impl, instance.provider, paramString);
  }
  
  public final String getProtocol() { return this.protocol; }
  
  public final Provider getProvider() { return this.provider; }
  
  public final void init(KeyManager[] paramArrayOfKeyManager, TrustManager[] paramArrayOfTrustManager, SecureRandom paramSecureRandom) throws KeyManagementException { this.contextSpi.engineInit(paramArrayOfKeyManager, paramArrayOfTrustManager, paramSecureRandom); }
  
  public final SSLSocketFactory getSocketFactory() { return this.contextSpi.engineGetSocketFactory(); }
  
  public final SSLServerSocketFactory getServerSocketFactory() { return this.contextSpi.engineGetServerSocketFactory(); }
  
  public final SSLEngine createSSLEngine() {
    try {
      return this.contextSpi.engineCreateSSLEngine();
    } catch (AbstractMethodError abstractMethodError) {
      UnsupportedOperationException unsupportedOperationException = new UnsupportedOperationException("Provider: " + getProvider() + " doesn't support this operation");
      unsupportedOperationException.initCause(abstractMethodError);
      throw unsupportedOperationException;
    } 
  }
  
  public final SSLEngine createSSLEngine(String paramString, int paramInt) {
    try {
      return this.contextSpi.engineCreateSSLEngine(paramString, paramInt);
    } catch (AbstractMethodError abstractMethodError) {
      UnsupportedOperationException unsupportedOperationException = new UnsupportedOperationException("Provider: " + getProvider() + " does not support this operation");
      unsupportedOperationException.initCause(abstractMethodError);
      throw unsupportedOperationException;
    } 
  }
  
  public final SSLSessionContext getServerSessionContext() { return this.contextSpi.engineGetServerSessionContext(); }
  
  public final SSLSessionContext getClientSessionContext() { return this.contextSpi.engineGetClientSessionContext(); }
  
  public final SSLParameters getDefaultSSLParameters() { return this.contextSpi.engineGetDefaultSSLParameters(); }
  
  public final SSLParameters getSupportedSSLParameters() { return this.contextSpi.engineGetSupportedSSLParameters(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\net\ssl\SSLContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */