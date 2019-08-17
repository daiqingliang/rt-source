package java.security.cert;

import java.security.AccessController;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.Security;
import sun.security.jca.GetInstance;

public class CertPathBuilder {
  private static final String CPB_TYPE = "certpathbuilder.type";
  
  private final CertPathBuilderSpi builderSpi;
  
  private final Provider provider;
  
  private final String algorithm;
  
  protected CertPathBuilder(CertPathBuilderSpi paramCertPathBuilderSpi, Provider paramProvider, String paramString) {
    this.builderSpi = paramCertPathBuilderSpi;
    this.provider = paramProvider;
    this.algorithm = paramString;
  }
  
  public static CertPathBuilder getInstance(String paramString) throws NoSuchAlgorithmException {
    GetInstance.Instance instance = GetInstance.getInstance("CertPathBuilder", CertPathBuilderSpi.class, paramString);
    return new CertPathBuilder((CertPathBuilderSpi)instance.impl, instance.provider, paramString);
  }
  
  public static CertPathBuilder getInstance(String paramString1, String paramString2) throws NoSuchAlgorithmException, NoSuchProviderException {
    GetInstance.Instance instance = GetInstance.getInstance("CertPathBuilder", CertPathBuilderSpi.class, paramString1, paramString2);
    return new CertPathBuilder((CertPathBuilderSpi)instance.impl, instance.provider, paramString1);
  }
  
  public static CertPathBuilder getInstance(String paramString, Provider paramProvider) throws NoSuchAlgorithmException {
    GetInstance.Instance instance = GetInstance.getInstance("CertPathBuilder", CertPathBuilderSpi.class, paramString, paramProvider);
    return new CertPathBuilder((CertPathBuilderSpi)instance.impl, instance.provider, paramString);
  }
  
  public final Provider getProvider() { return this.provider; }
  
  public final String getAlgorithm() { return this.algorithm; }
  
  public final CertPathBuilderResult build(CertPathParameters paramCertPathParameters) throws CertPathBuilderException, InvalidAlgorithmParameterException { return this.builderSpi.engineBuild(paramCertPathParameters); }
  
  public static final String getDefaultType() {
    String str = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
          public String run() { return Security.getProperty("certpathbuilder.type"); }
        });
    return (str == null) ? "PKIX" : str;
  }
  
  public final CertPathChecker getRevocationChecker() { return this.builderSpi.engineGetRevocationChecker(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\cert\CertPathBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */