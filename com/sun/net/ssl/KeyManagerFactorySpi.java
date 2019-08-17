package com.sun.net.ssl;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

@Deprecated
public abstract class KeyManagerFactorySpi {
  protected abstract void engineInit(KeyStore paramKeyStore, char[] paramArrayOfChar) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException;
  
  protected abstract KeyManager[] engineGetKeyManagers();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\net\ssl\KeyManagerFactorySpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */