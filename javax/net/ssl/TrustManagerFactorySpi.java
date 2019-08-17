package javax.net.ssl;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;

public abstract class TrustManagerFactorySpi {
  protected abstract void engineInit(KeyStore paramKeyStore) throws KeyStoreException;
  
  protected abstract void engineInit(ManagerFactoryParameters paramManagerFactoryParameters) throws InvalidAlgorithmParameterException;
  
  protected abstract TrustManager[] engineGetTrustManagers();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\net\ssl\TrustManagerFactorySpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */