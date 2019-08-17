package sun.security.provider.certpath;

import java.io.IOException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXReason;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import sun.security.util.Debug;
import sun.security.x509.NameConstraintsExtension;
import sun.security.x509.PKIXExtensions;
import sun.security.x509.X509CertImpl;

class ConstraintsChecker extends PKIXCertPathChecker {
  private static final Debug debug = Debug.getInstance("certpath");
  
  private final int certPathLength;
  
  private int maxPathLength;
  
  private int i;
  
  private NameConstraintsExtension prevNC;
  
  private Set<String> supportedExts;
  
  ConstraintsChecker(int paramInt) { this.certPathLength = paramInt; }
  
  public void init(boolean paramBoolean) throws CertPathValidatorException {
    if (!paramBoolean) {
      this.i = 0;
      this.maxPathLength = this.certPathLength;
      this.prevNC = null;
    } else {
      throw new CertPathValidatorException("forward checking not supported");
    } 
  }
  
  public boolean isForwardCheckingSupported() { return false; }
  
  public Set<String> getSupportedExtensions() {
    if (this.supportedExts == null) {
      this.supportedExts = new HashSet(2);
      this.supportedExts.add(PKIXExtensions.BasicConstraints_Id.toString());
      this.supportedExts.add(PKIXExtensions.NameConstraints_Id.toString());
      this.supportedExts = Collections.unmodifiableSet(this.supportedExts);
    } 
    return this.supportedExts;
  }
  
  public void check(Certificate paramCertificate, Collection<String> paramCollection) throws CertPathValidatorException {
    X509Certificate x509Certificate = (X509Certificate)paramCertificate;
    this.i++;
    checkBasicConstraints(x509Certificate);
    verifyNameConstraints(x509Certificate);
    if (paramCollection != null && !paramCollection.isEmpty()) {
      paramCollection.remove(PKIXExtensions.BasicConstraints_Id.toString());
      paramCollection.remove(PKIXExtensions.NameConstraints_Id.toString());
    } 
  }
  
  private void verifyNameConstraints(X509Certificate paramX509Certificate) throws CertPathValidatorException {
    String str = "name constraints";
    if (debug != null)
      debug.println("---checking " + str + "..."); 
    if (this.prevNC != null && (this.i == this.certPathLength || !X509CertImpl.isSelfIssued(paramX509Certificate))) {
      if (debug != null)
        debug.println("prevNC = " + this.prevNC + ", currDN = " + paramX509Certificate.getSubjectX500Principal()); 
      try {
        if (!this.prevNC.verify(paramX509Certificate))
          throw new CertPathValidatorException(str + " check failed", null, null, -1, PKIXReason.INVALID_NAME); 
      } catch (IOException iOException) {
        throw new CertPathValidatorException(iOException);
      } 
    } 
    this.prevNC = mergeNameConstraints(paramX509Certificate, this.prevNC);
    if (debug != null)
      debug.println(str + " verified."); 
  }
  
  static NameConstraintsExtension mergeNameConstraints(X509Certificate paramX509Certificate, NameConstraintsExtension paramNameConstraintsExtension) throws CertPathValidatorException {
    X509CertImpl x509CertImpl;
    try {
      x509CertImpl = X509CertImpl.toImpl(paramX509Certificate);
    } catch (CertificateException certificateException) {
      throw new CertPathValidatorException(certificateException);
    } 
    NameConstraintsExtension nameConstraintsExtension = x509CertImpl.getNameConstraintsExtension();
    if (debug != null)
      debug.println("prevNC = " + paramNameConstraintsExtension + ", newNC = " + String.valueOf(nameConstraintsExtension)); 
    if (paramNameConstraintsExtension == null) {
      if (debug != null)
        debug.println("mergedNC = " + String.valueOf(nameConstraintsExtension)); 
      return (nameConstraintsExtension == null) ? nameConstraintsExtension : (NameConstraintsExtension)nameConstraintsExtension.clone();
    } 
    try {
      paramNameConstraintsExtension.merge(nameConstraintsExtension);
    } catch (IOException iOException) {
      throw new CertPathValidatorException(iOException);
    } 
    if (debug != null)
      debug.println("mergedNC = " + paramNameConstraintsExtension); 
    return paramNameConstraintsExtension;
  }
  
  private void checkBasicConstraints(X509Certificate paramX509Certificate) throws CertPathValidatorException {
    String str = "basic constraints";
    if (debug != null) {
      debug.println("---checking " + str + "...");
      debug.println("i = " + this.i + ", maxPathLength = " + this.maxPathLength);
    } 
    if (this.i < this.certPathLength) {
      int j = -1;
      if (paramX509Certificate.getVersion() < 3) {
        if (this.i == 1 && X509CertImpl.isSelfIssued(paramX509Certificate))
          j = Integer.MAX_VALUE; 
      } else {
        j = paramX509Certificate.getBasicConstraints();
      } 
      if (j == -1)
        throw new CertPathValidatorException(str + " check failed: this is not a CA certificate", null, null, -1, PKIXReason.NOT_CA_CERT); 
      if (!X509CertImpl.isSelfIssued(paramX509Certificate)) {
        if (this.maxPathLength <= 0)
          throw new CertPathValidatorException(str + " check failed: pathLenConstraint violated - this cert must be the last cert in the certification path", null, null, -1, PKIXReason.PATH_TOO_LONG); 
        this.maxPathLength--;
      } 
      if (j < this.maxPathLength)
        this.maxPathLength = j; 
    } 
    if (debug != null) {
      debug.println("after processing, maxPathLength = " + this.maxPathLength);
      debug.println(str + " verified.");
    } 
  }
  
  static int mergeBasicConstraints(X509Certificate paramX509Certificate, int paramInt) {
    int j = paramX509Certificate.getBasicConstraints();
    if (!X509CertImpl.isSelfIssued(paramX509Certificate))
      paramInt--; 
    if (j < paramInt)
      paramInt = j; 
    return paramInt;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\ConstraintsChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */