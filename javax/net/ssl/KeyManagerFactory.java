package javax.net.ssl;

import java.security.AccessController;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import sun.security.jca.GetInstance;

public class KeyManagerFactory {
  private Provider provider;
  
  private KeyManagerFactorySpi factorySpi;
  
  private String algorithm;
  
  public static final String getDefaultAlgorithm() {
    String str = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
          public String run() { return Security.getProperty("ssl.KeyManagerFactory.algorithm"); }
        });
    if (str == null)
      str = "SunX509"; 
    return str;
  }
  
  protected KeyManagerFactory(KeyManagerFactorySpi paramKeyManagerFactorySpi, Provider paramProvider, String paramString) {
    this.factorySpi = paramKeyManagerFactorySpi;
    this.provider = paramProvider;
    this.algorithm = paramString;
  }
  
  public final String getAlgorithm() { return this.algorithm; }
  
  public static final KeyManagerFactory getInstance(String paramString) throws NoSuchAlgorithmException {
    GetInstance.Instance instance = GetInstance.getInstance("KeyManagerFactory", KeyManagerFactorySpi.class, paramString);
    return new KeyManagerFactory((KeyManagerFactorySpi)instance.impl, instance.provider, paramString);
  }
  
  public static final KeyManagerFactory getInstance(String paramString1, String paramString2) throws NoSuchAlgorithmException, NoSuchProviderException {
    GetInstance.Instance instance = GetInstance.getInstance("KeyManagerFactory", KeyManagerFactorySpi.class, paramString1, paramString2);
    return new KeyManagerFactory((KeyManagerFactorySpi)instance.impl, instance.provider, paramString1);
  }
  
  public static final KeyManagerFactory getInstance(String paramString, Provider paramProvider) throws NoSuchAlgorithmException {
    GetInstance.Instance instance = GetInstance.getInstance("KeyManagerFactory", KeyManagerFactorySpi.class, paramString, paramProvider);
    return new KeyManagerFactory((KeyManagerFactorySpi)instance.impl, instance.provider, paramString);
  }
  
  public final Provider getProvider() { return this.provider; }
  
  public final void init(KeyStore paramKeyStore, char[] paramArrayOfChar) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException { this.factorySpi.engineInit(paramKeyStore, paramArrayOfChar); }
  
  public final void init(ManagerFactoryParameters paramManagerFactoryParameters) throws InvalidAlgorithmParameterException { this.factorySpi.engineInit(paramManagerFactoryParameters); }
  
  public final KeyManager[] getKeyManagers() { return this.factorySpi.engineGetKeyManagers(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\net\ssl\KeyManagerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */