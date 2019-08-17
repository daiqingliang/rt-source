package java.security.cert;

public interface CertPathChecker {
  void init(boolean paramBoolean) throws CertPathValidatorException;
  
  boolean isForwardCheckingSupported();
  
  void check(Certificate paramCertificate) throws CertPathValidatorException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\cert\CertPathChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */