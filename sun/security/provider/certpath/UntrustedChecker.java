package sun.security.provider.certpath;

import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Set;
import sun.security.util.Debug;
import sun.security.util.UntrustedCertificates;

public final class UntrustedChecker extends PKIXCertPathChecker {
  private static final Debug debug = Debug.getInstance("certpath");
  
  public void init(boolean paramBoolean) throws CertPathValidatorException {}
  
  public boolean isForwardCheckingSupported() { return true; }
  
  public Set<String> getSupportedExtensions() { return null; }
  
  public void check(Certificate paramCertificate, Collection<String> paramCollection) throws CertPathValidatorException {
    X509Certificate x509Certificate = (X509Certificate)paramCertificate;
    if (UntrustedCertificates.isUntrusted(x509Certificate)) {
      if (debug != null)
        debug.println("UntrustedChecker: untrusted certificate " + x509Certificate.getSubjectX500Principal()); 
      throw new CertPathValidatorException("Untrusted certificate: " + x509Certificate.getSubjectX500Principal());
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\UntrustedChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */