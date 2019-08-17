package javax.net.ssl;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.SecureRandom;

public abstract class SSLContextSpi {
  protected abstract void engineInit(KeyManager[] paramArrayOfKeyManager, TrustManager[] paramArrayOfTrustManager, SecureRandom paramSecureRandom) throws KeyManagementException;
  
  protected abstract SSLSocketFactory engineGetSocketFactory();
  
  protected abstract SSLServerSocketFactory engineGetServerSocketFactory();
  
  protected abstract SSLEngine engineCreateSSLEngine();
  
  protected abstract SSLEngine engineCreateSSLEngine(String paramString, int paramInt);
  
  protected abstract SSLSessionContext engineGetServerSessionContext();
  
  protected abstract SSLSessionContext engineGetClientSessionContext();
  
  private SSLSocket getDefaultSocket() {
    try {
      SSLSocketFactory sSLSocketFactory = engineGetSocketFactory();
      return (SSLSocket)sSLSocketFactory.createSocket();
    } catch (IOException iOException) {
      throw new UnsupportedOperationException("Could not obtain parameters", iOException);
    } 
  }
  
  protected SSLParameters engineGetDefaultSSLParameters() {
    SSLSocket sSLSocket = getDefaultSocket();
    return sSLSocket.getSSLParameters();
  }
  
  protected SSLParameters engineGetSupportedSSLParameters() {
    SSLSocket sSLSocket = getDefaultSocket();
    SSLParameters sSLParameters = new SSLParameters();
    sSLParameters.setCipherSuites(sSLSocket.getSupportedCipherSuites());
    sSLParameters.setProtocols(sSLSocket.getSupportedProtocols());
    return sSLParameters;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\net\ssl\SSLContextSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */