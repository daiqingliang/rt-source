package sun.security.provider.certpath;

import java.security.cert.CertPathValidatorException;
import java.security.cert.CertSelector;
import java.security.cert.Certificate;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXReason;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import sun.security.util.Debug;
import sun.security.x509.PKIXExtensions;

class KeyChecker extends PKIXCertPathChecker {
  private static final Debug debug = Debug.getInstance("certpath");
  
  private final int certPathLen;
  
  private final CertSelector targetConstraints;
  
  private int remainingCerts;
  
  private Set<String> supportedExts;
  
  private static final int KEY_CERT_SIGN = 5;
  
  KeyChecker(int paramInt, CertSelector paramCertSelector) {
    this.certPathLen = paramInt;
    this.targetConstraints = paramCertSelector;
  }
  
  public void init(boolean paramBoolean) throws CertPathValidatorException {
    if (!paramBoolean) {
      this.remainingCerts = this.certPathLen;
    } else {
      throw new CertPathValidatorException("forward checking not supported");
    } 
  }
  
  public boolean isForwardCheckingSupported() { return false; }
  
  public Set<String> getSupportedExtensions() {
    if (this.supportedExts == null) {
      this.supportedExts = new HashSet(3);
      this.supportedExts.add(PKIXExtensions.KeyUsage_Id.toString());
      this.supportedExts.add(PKIXExtensions.ExtendedKeyUsage_Id.toString());
      this.supportedExts.add(PKIXExtensions.SubjectAlternativeName_Id.toString());
      this.supportedExts = Collections.unmodifiableSet(this.supportedExts);
    } 
    return this.supportedExts;
  }
  
  public void check(Certificate paramCertificate, Collection<String> paramCollection) throws CertPathValidatorException {
    X509Certificate x509Certificate = (X509Certificate)paramCertificate;
    this.remainingCerts--;
    if (this.remainingCerts == 0) {
      if (this.targetConstraints != null && !this.targetConstraints.match(x509Certificate))
        throw new CertPathValidatorException("target certificate constraints check failed"); 
    } else {
      verifyCAKeyUsage(x509Certificate);
    } 
    if (paramCollection != null && !paramCollection.isEmpty()) {
      paramCollection.remove(PKIXExtensions.KeyUsage_Id.toString());
      paramCollection.remove(PKIXExtensions.ExtendedKeyUsage_Id.toString());
      paramCollection.remove(PKIXExtensions.SubjectAlternativeName_Id.toString());
    } 
  }
  
  static void verifyCAKeyUsage(X509Certificate paramX509Certificate) throws CertPathValidatorException {
    String str = "CA key usage";
    if (debug != null)
      debug.println("KeyChecker.verifyCAKeyUsage() ---checking " + str + "..."); 
    boolean[] arrayOfBoolean = paramX509Certificate.getKeyUsage();
    if (arrayOfBoolean == null)
      return; 
    if (!arrayOfBoolean[5])
      throw new CertPathValidatorException(str + " check failed: keyCertSign bit is not set", null, null, -1, PKIXReason.INVALID_KEY_USAGE); 
    if (debug != null)
      debug.println("KeyChecker.verifyCAKeyUsage() " + str + " verified."); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\KeyChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */