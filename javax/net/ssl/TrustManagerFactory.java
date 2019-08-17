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
import sun.security.jca.GetInstance;

public class TrustManagerFactory {
  private Provider provider;
  
  private TrustManagerFactorySpi factorySpi;
  
  private String algorithm;
  
  public static final String getDefaultAlgorithm() {
    String str = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
          public String run() { return Security.getProperty("ssl.TrustManagerFactory.algorithm"); }
        });
    if (str == null)
      str = "SunX509"; 
    return str;
  }
  
  protected TrustManagerFactory(TrustManagerFactorySpi paramTrustManagerFactorySpi, Provider paramProvider, String paramString) {
    this.factorySpi = paramTrustManagerFactorySpi;
    this.provider = paramProvider;
    this.algorithm = paramString;
  }
  
  public final String getAlgorithm() { return this.algorithm; }
  
  public static final TrustManagerFactory getInstance(String paramString) throws NoSuchAlgorithmException {
    GetInstance.Instance instance = GetInstance.getInstance("TrustManagerFactory", TrustManagerFactorySpi.class, paramString);
    return new TrustManagerFactory((TrustManagerFactorySpi)instance.impl, instance.provider, paramString);
  }
  
  public static final TrustManagerFactory getInstance(String paramString1, String paramString2) throws NoSuchAlgorithmException, NoSuchProviderException {
    GetInstance.Instance instance = GetInstance.getInstance("TrustManagerFactory", TrustManagerFactorySpi.class, paramString1, paramString2);
    return new TrustManagerFactory((TrustManagerFactorySpi)instance.impl, instance.provider, paramString1);
  }
  
  public static final TrustManagerFactory getInstance(String paramString, Provider paramProvider) throws NoSuchAlgorithmException {
    GetInstance.Instance instance = GetInstance.getInstance("TrustManagerFactory", TrustManagerFactorySpi.class, paramString, paramProvider);
    return new TrustManagerFactory((TrustManagerFactorySpi)instance.impl, instance.provider, paramString);
  }
  
  public final Provider getProvider() { return this.provider; }
  
  public final void init(KeyStore paramKeyStore) throws KeyStoreException { this.factorySpi.engineInit(paramKeyStore); }
  
  public final void init(ManagerFactoryParameters paramManagerFactoryParameters) throws InvalidAlgorithmParameterException { this.factorySpi.engineInit(paramManagerFactoryParameters); }
  
  public final TrustManager[] getTrustManagers() { return this.factorySpi.engineGetTrustManagers(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\net\ssl\TrustManagerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */