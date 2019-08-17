package java.security.cert;

import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import sun.security.jca.GetInstance;

public class CertificateFactory {
  private String type;
  
  private Provider provider;
  
  private CertificateFactorySpi certFacSpi;
  
  protected CertificateFactory(CertificateFactorySpi paramCertificateFactorySpi, Provider paramProvider, String paramString) {
    this.certFacSpi = paramCertificateFactorySpi;
    this.provider = paramProvider;
    this.type = paramString;
  }
  
  public static final CertificateFactory getInstance(String paramString) throws CertificateException {
    try {
      GetInstance.Instance instance = GetInstance.getInstance("CertificateFactory", CertificateFactorySpi.class, paramString);
      return new CertificateFactory((CertificateFactorySpi)instance.impl, instance.provider, paramString);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new CertificateException(paramString + " not found", noSuchAlgorithmException);
    } 
  }
  
  public static final CertificateFactory getInstance(String paramString1, String paramString2) throws CertificateException, NoSuchProviderException {
    try {
      GetInstance.Instance instance = GetInstance.getInstance("CertificateFactory", CertificateFactorySpi.class, paramString1, paramString2);
      return new CertificateFactory((CertificateFactorySpi)instance.impl, instance.provider, paramString1);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new CertificateException(paramString1 + " not found", noSuchAlgorithmException);
    } 
  }
  
  public static final CertificateFactory getInstance(String paramString, Provider paramProvider) throws CertificateException {
    try {
      GetInstance.Instance instance = GetInstance.getInstance("CertificateFactory", CertificateFactorySpi.class, paramString, paramProvider);
      return new CertificateFactory((CertificateFactorySpi)instance.impl, instance.provider, paramString);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new CertificateException(paramString + " not found", noSuchAlgorithmException);
    } 
  }
  
  public final Provider getProvider() { return this.provider; }
  
  public final String getType() { return this.type; }
  
  public final Certificate generateCertificate(InputStream paramInputStream) throws CertificateException { return this.certFacSpi.engineGenerateCertificate(paramInputStream); }
  
  public final Iterator<String> getCertPathEncodings() { return this.certFacSpi.engineGetCertPathEncodings(); }
  
  public final CertPath generateCertPath(InputStream paramInputStream) throws CertificateException { return this.certFacSpi.engineGenerateCertPath(paramInputStream); }
  
  public final CertPath generateCertPath(InputStream paramInputStream, String paramString) throws CertificateException { return this.certFacSpi.engineGenerateCertPath(paramInputStream, paramString); }
  
  public final CertPath generateCertPath(List<? extends Certificate> paramList) throws CertificateException { return this.certFacSpi.engineGenerateCertPath(paramList); }
  
  public final Collection<? extends Certificate> generateCertificates(InputStream paramInputStream) throws CertificateException { return this.certFacSpi.engineGenerateCertificates(paramInputStream); }
  
  public final CRL generateCRL(InputStream paramInputStream) throws CRLException { return this.certFacSpi.engineGenerateCRL(paramInputStream); }
  
  public final Collection<? extends CRL> generateCRLs(InputStream paramInputStream) throws CRLException { return this.certFacSpi.engineGenerateCRLs(paramInputStream); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\cert\CertificateFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */