package sun.security.validator;

import java.security.AlgorithmConstraints;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Date;

public abstract class Validator {
  static final X509Certificate[] CHAIN0 = new X509Certificate[0];
  
  public static final String TYPE_SIMPLE = "Simple";
  
  public static final String TYPE_PKIX = "PKIX";
  
  public static final String VAR_GENERIC = "generic";
  
  public static final String VAR_CODE_SIGNING = "code signing";
  
  public static final String VAR_JCE_SIGNING = "jce signing";
  
  public static final String VAR_TLS_CLIENT = "tls client";
  
  public static final String VAR_TLS_SERVER = "tls server";
  
  public static final String VAR_TSA_SERVER = "tsa server";
  
  public static final String VAR_PLUGIN_CODE_SIGNING = "plugin code signing";
  
  private final String type;
  
  final EndEntityChecker endEntityChecker;
  
  final String variant;
  
  Validator(String paramString1, String paramString2) {
    this.type = paramString1;
    this.variant = paramString2;
    this.endEntityChecker = EndEntityChecker.getInstance(paramString1, paramString2);
  }
  
  public static Validator getInstance(String paramString1, String paramString2, KeyStore paramKeyStore) { return getInstance(paramString1, paramString2, KeyStores.getTrustedCerts(paramKeyStore)); }
  
  public static Validator getInstance(String paramString1, String paramString2, Collection<X509Certificate> paramCollection) {
    if (paramString1.equals("Simple"))
      return new SimpleValidator(paramString2, paramCollection); 
    if (paramString1.equals("PKIX"))
      return new PKIXValidator(paramString2, paramCollection); 
    throw new IllegalArgumentException("Unknown validator type: " + paramString1);
  }
  
  public static Validator getInstance(String paramString1, String paramString2, PKIXBuilderParameters paramPKIXBuilderParameters) {
    if (!paramString1.equals("PKIX"))
      throw new IllegalArgumentException("getInstance(PKIXBuilderParameters) can only be used with PKIX validator"); 
    return new PKIXValidator(paramString2, paramPKIXBuilderParameters);
  }
  
  public final X509Certificate[] validate(X509Certificate[] paramArrayOfX509Certificate) throws CertificateException { return validate(paramArrayOfX509Certificate, null, null); }
  
  public final X509Certificate[] validate(X509Certificate[] paramArrayOfX509Certificate, Collection<X509Certificate> paramCollection) throws CertificateException { return validate(paramArrayOfX509Certificate, paramCollection, null); }
  
  public final X509Certificate[] validate(X509Certificate[] paramArrayOfX509Certificate, Collection<X509Certificate> paramCollection, Object paramObject) throws CertificateException { return validate(paramArrayOfX509Certificate, paramCollection, null, paramObject); }
  
  public final X509Certificate[] validate(X509Certificate[] paramArrayOfX509Certificate, Collection<X509Certificate> paramCollection, AlgorithmConstraints paramAlgorithmConstraints, Object paramObject) throws CertificateException {
    paramArrayOfX509Certificate = engineValidate(paramArrayOfX509Certificate, paramCollection, paramAlgorithmConstraints, paramObject);
    if (paramArrayOfX509Certificate.length > 1) {
      boolean bool = !(this.type == "PKIX");
      this.endEntityChecker.check(paramArrayOfX509Certificate[0], paramObject, bool);
    } 
    return paramArrayOfX509Certificate;
  }
  
  abstract X509Certificate[] engineValidate(X509Certificate[] paramArrayOfX509Certificate, Collection<X509Certificate> paramCollection, AlgorithmConstraints paramAlgorithmConstraints, Object paramObject) throws CertificateException;
  
  public abstract Collection<X509Certificate> getTrustedCertificates();
  
  @Deprecated
  public void setValidationDate(Date paramDate) { this.validationDate = paramDate; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\validator\Validator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */