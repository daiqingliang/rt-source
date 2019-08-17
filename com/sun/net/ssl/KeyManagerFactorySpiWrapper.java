package com.sun.net.ssl;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.UnrecoverableKeyException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.X509KeyManager;

final class KeyManagerFactorySpiWrapper extends KeyManagerFactorySpi {
  private KeyManagerFactory theKeyManagerFactory;
  
  KeyManagerFactorySpiWrapper(String paramString, Provider paramProvider) throws NoSuchAlgorithmException { this.theKeyManagerFactory = KeyManagerFactory.getInstance(paramString, paramProvider); }
  
  protected void engineInit(KeyStore paramKeyStore, char[] paramArrayOfChar) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException { this.theKeyManagerFactory.init(paramKeyStore, paramArrayOfChar); }
  
  protected KeyManager[] engineGetKeyManagers() {
    KeyManager[] arrayOfKeyManager = this.theKeyManagerFactory.getKeyManagers();
    KeyManager[] arrayOfKeyManager1 = new KeyManager[arrayOfKeyManager.length];
    byte b2 = 0;
    byte b1 = 0;
    while (b2 < arrayOfKeyManager.length) {
      if (!(arrayOfKeyManager[b2] instanceof KeyManager)) {
        if (arrayOfKeyManager[b2] instanceof X509KeyManager) {
          arrayOfKeyManager1[b1] = new X509KeyManagerComSunWrapper((X509KeyManager)arrayOfKeyManager[b2]);
          b1++;
        } 
      } else {
        arrayOfKeyManager1[b1] = (KeyManager)arrayOfKeyManager[b2];
        b1++;
      } 
      b2++;
    } 
    if (b1 != b2)
      arrayOfKeyManager1 = (KeyManager[])SSLSecurity.truncateArray(arrayOfKeyManager1, new KeyManager[b1]); 
    return arrayOfKeyManager1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\net\ssl\KeyManagerFactorySpiWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */