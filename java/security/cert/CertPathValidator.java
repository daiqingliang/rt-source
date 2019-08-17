package java.security.cert;

import java.security.AccessController;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.Security;
import sun.security.jca.GetInstance;

public class CertPathValidator {
  private static final String CPV_TYPE = "certpathvalidator.type";
  
  private final CertPathValidatorSpi validatorSpi;
  
  private final Provider provider;
  
  private final String algorithm;
  
  protected CertPathValidator(CertPathValidatorSpi paramCertPathValidatorSpi, Provider paramProvider, String paramString) {
    this.validatorSpi = paramCertPathValidatorSpi;
    this.provider = paramProvider;
    this.algorithm = paramString;
  }
  
  public static CertPathValidator getInstance(String paramString) throws NoSuchAlgorithmException {
    GetInstance.Instance instance = GetInstance.getInstance("CertPathValidator", CertPathValidatorSpi.class, paramString);
    return new CertPathValidator((CertPathValidatorSpi)instance.impl, instance.provider, paramString);
  }
  
  public static CertPathValidator getInstance(String paramString1, String paramString2) throws NoSuchAlgorithmException, NoSuchProviderException {
    GetInstance.Instance instance = GetInstance.getInstance("CertPathValidator", CertPathValidatorSpi.class, paramString1, paramString2);
    return new CertPathValidator((CertPathValidatorSpi)instance.impl, instance.provider, paramString1);
  }
  
  public static CertPathValidator getInstance(String paramString, Provider paramProvider) throws NoSuchAlgorithmException {
    GetInstance.Instance instance = GetInstance.getInstance("CertPathValidator", CertPathValidatorSpi.class, paramString, paramProvider);
    return new CertPathValidator((CertPathValidatorSpi)instance.impl, instance.provider, paramString);
  }
  
  public final Provider getProvider() { return this.provider; }
  
  public final String getAlgorithm() { return this.algorithm; }
  
  public final CertPathValidatorResult validate(CertPath paramCertPath, CertPathParameters paramCertPathParameters) throws CertPathValidatorException, InvalidAlgorithmParameterException { return this.validatorSpi.engineValidate(paramCertPath, paramCertPathParameters); }
  
  public static final String getDefaultType() {
    String str = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
          public String run() { return Security.getProperty("certpathvalidator.type"); }
        });
    return (str == null) ? "PKIX" : str;
  }
  
  public final CertPathChecker getRevocationChecker() { return this.validatorSpi.engineGetRevocationChecker(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\cert\CertPathValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */