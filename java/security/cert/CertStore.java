package java.security.cert;

import java.security.AccessController;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.Security;
import java.util.Collection;
import sun.security.jca.GetInstance;

public class CertStore {
  private static final String CERTSTORE_TYPE = "certstore.type";
  
  private CertStoreSpi storeSpi;
  
  private Provider provider;
  
  private String type;
  
  private CertStoreParameters params;
  
  protected CertStore(CertStoreSpi paramCertStoreSpi, Provider paramProvider, String paramString, CertStoreParameters paramCertStoreParameters) {
    this.storeSpi = paramCertStoreSpi;
    this.provider = paramProvider;
    this.type = paramString;
    if (paramCertStoreParameters != null)
      this.params = (CertStoreParameters)paramCertStoreParameters.clone(); 
  }
  
  public final Collection<? extends Certificate> getCertificates(CertSelector paramCertSelector) throws CertStoreException { return this.storeSpi.engineGetCertificates(paramCertSelector); }
  
  public final Collection<? extends CRL> getCRLs(CRLSelector paramCRLSelector) throws CertStoreException { return this.storeSpi.engineGetCRLs(paramCRLSelector); }
  
  public static CertStore getInstance(String paramString, CertStoreParameters paramCertStoreParameters) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
    try {
      GetInstance.Instance instance = GetInstance.getInstance("CertStore", CertStoreSpi.class, paramString, paramCertStoreParameters);
      return new CertStore((CertStoreSpi)instance.impl, instance.provider, paramString, paramCertStoreParameters);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      return handleException(noSuchAlgorithmException);
    } 
  }
  
  private static CertStore handleException(NoSuchAlgorithmException paramNoSuchAlgorithmException) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
    Throwable throwable = paramNoSuchAlgorithmException.getCause();
    if (throwable instanceof InvalidAlgorithmParameterException)
      throw (InvalidAlgorithmParameterException)throwable; 
    throw paramNoSuchAlgorithmException;
  }
  
  public static CertStore getInstance(String paramString1, CertStoreParameters paramCertStoreParameters, String paramString2) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
    try {
      GetInstance.Instance instance = GetInstance.getInstance("CertStore", CertStoreSpi.class, paramString1, paramCertStoreParameters, paramString2);
      return new CertStore((CertStoreSpi)instance.impl, instance.provider, paramString1, paramCertStoreParameters);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      return handleException(noSuchAlgorithmException);
    } 
  }
  
  public static CertStore getInstance(String paramString, CertStoreParameters paramCertStoreParameters, Provider paramProvider) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
    try {
      GetInstance.Instance instance = GetInstance.getInstance("CertStore", CertStoreSpi.class, paramString, paramCertStoreParameters, paramProvider);
      return new CertStore((CertStoreSpi)instance.impl, instance.provider, paramString, paramCertStoreParameters);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      return handleException(noSuchAlgorithmException);
    } 
  }
  
  public final CertStoreParameters getCertStoreParameters() { return (this.params == null) ? null : (CertStoreParameters)this.params.clone(); }
  
  public final String getType() { return this.type; }
  
  public final Provider getProvider() { return this.provider; }
  
  public static final String getDefaultType() {
    String str = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
          public String run() { return Security.getProperty("certstore.type"); }
        });
    if (str == null)
      str = "LDAP"; 
    return str;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\cert\CertStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */