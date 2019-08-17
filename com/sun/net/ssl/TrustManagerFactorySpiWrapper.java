package com.sun.net.ssl;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

final class TrustManagerFactorySpiWrapper extends TrustManagerFactorySpi {
  private TrustManagerFactory theTrustManagerFactory;
  
  TrustManagerFactorySpiWrapper(String paramString, Provider paramProvider) throws NoSuchAlgorithmException { this.theTrustManagerFactory = TrustManagerFactory.getInstance(paramString, paramProvider); }
  
  protected void engineInit(KeyStore paramKeyStore) throws KeyStoreException { this.theTrustManagerFactory.init(paramKeyStore); }
  
  protected TrustManager[] engineGetTrustManagers() {
    TrustManager[] arrayOfTrustManager = this.theTrustManagerFactory.getTrustManagers();
    TrustManager[] arrayOfTrustManager1 = new TrustManager[arrayOfTrustManager.length];
    byte b2 = 0;
    byte b1 = 0;
    while (b2 < arrayOfTrustManager.length) {
      if (!(arrayOfTrustManager[b2] instanceof TrustManager)) {
        if (arrayOfTrustManager[b2] instanceof X509TrustManager) {
          arrayOfTrustManager1[b1] = new X509TrustManagerComSunWrapper((X509TrustManager)arrayOfTrustManager[b2]);
          b1++;
        } 
      } else {
        arrayOfTrustManager1[b1] = (TrustManager)arrayOfTrustManager[b2];
        b1++;
      } 
      b2++;
    } 
    if (b1 != b2)
      arrayOfTrustManager1 = (TrustManager[])SSLSecurity.truncateArray(arrayOfTrustManager1, new TrustManager[b1]); 
    return arrayOfTrustManager1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\net\ssl\TrustManagerFactorySpiWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */