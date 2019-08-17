package sun.security.validator;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class ValidatorException extends CertificateException {
  private static final long serialVersionUID = -2836879718282292155L;
  
  public static final Object T_NO_TRUST_ANCHOR = "No trusted certificate found";
  
  public static final Object T_EE_EXTENSIONS = "End entity certificate extension check failed";
  
  public static final Object T_CA_EXTENSIONS = "CA certificate extension check failed";
  
  public static final Object T_CERT_EXPIRED = "Certificate expired";
  
  public static final Object T_SIGNATURE_ERROR = "Certificate signature validation failed";
  
  public static final Object T_NAME_CHAINING = "Certificate chaining error";
  
  public static final Object T_ALGORITHM_DISABLED = "Certificate signature algorithm disabled";
  
  public static final Object T_UNTRUSTED_CERT = "Untrusted certificate";
  
  private Object type;
  
  private X509Certificate cert;
  
  public ValidatorException(String paramString) { super(paramString); }
  
  public ValidatorException(String paramString, Throwable paramThrowable) {
    super(paramString);
    initCause(paramThrowable);
  }
  
  public ValidatorException(Object paramObject) { this(paramObject, null); }
  
  public ValidatorException(Object paramObject, X509Certificate paramX509Certificate) {
    super((String)paramObject);
    this.type = paramObject;
    this.cert = paramX509Certificate;
  }
  
  public ValidatorException(Object paramObject, X509Certificate paramX509Certificate, Throwable paramThrowable) {
    this(paramObject, paramX509Certificate);
    initCause(paramThrowable);
  }
  
  public ValidatorException(String paramString, Object paramObject, X509Certificate paramX509Certificate) {
    super(paramString);
    this.type = paramObject;
    this.cert = paramX509Certificate;
  }
  
  public ValidatorException(String paramString, Object paramObject, X509Certificate paramX509Certificate, Throwable paramThrowable) {
    this(paramString, paramObject, paramX509Certificate);
    initCause(paramThrowable);
  }
  
  public Object getErrorType() { return this.type; }
  
  public X509Certificate getErrorCertificate() { return this.cert; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\validator\ValidatorException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */