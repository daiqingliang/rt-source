package com.sun.net.ssl;

import java.security.KeyManagementException;
import java.security.SecureRandom;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;

@Deprecated
public abstract class SSLContextSpi {
  protected abstract void engineInit(KeyManager[] paramArrayOfKeyManager, TrustManager[] paramArrayOfTrustManager, SecureRandom paramSecureRandom) throws KeyManagementException;
  
  protected abstract SSLSocketFactory engineGetSocketFactory();
  
  protected abstract SSLServerSocketFactory engineGetServerSocketFactory();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\net\ssl\SSLContextSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */