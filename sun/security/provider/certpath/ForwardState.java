package sun.security.provider.certpath;

import java.io.IOException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import javax.security.auth.x500.X500Principal;
import sun.security.util.Debug;
import sun.security.x509.GeneralName;
import sun.security.x509.GeneralNameInterface;
import sun.security.x509.GeneralNames;
import sun.security.x509.SubjectAlternativeNameExtension;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;

class ForwardState implements State {
  private static final Debug debug = Debug.getInstance("certpath");
  
  X500Principal issuerDN;
  
  X509CertImpl cert;
  
  HashSet<GeneralNameInterface> subjectNamesTraversed;
  
  int traversedCACerts;
  
  private boolean init = true;
  
  UntrustedChecker untrustedChecker;
  
  ArrayList<PKIXCertPathChecker> forwardCheckers;
  
  boolean keyParamsNeededFlag = false;
  
  public boolean isInitial() { return this.init; }
  
  public boolean keyParamsNeeded() { return this.keyParamsNeededFlag; }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("State [");
    stringBuilder.append("\n  issuerDN of last cert: ").append(this.issuerDN);
    stringBuilder.append("\n  traversedCACerts: ").append(this.traversedCACerts);
    stringBuilder.append("\n  init: ").append(String.valueOf(this.init));
    stringBuilder.append("\n  keyParamsNeeded: ").append(String.valueOf(this.keyParamsNeededFlag));
    stringBuilder.append("\n  subjectNamesTraversed: \n").append(this.subjectNamesTraversed);
    stringBuilder.append("]\n");
    return stringBuilder.toString();
  }
  
  public void initState(List<PKIXCertPathChecker> paramList) throws CertPathValidatorException {
    this.subjectNamesTraversed = new HashSet();
    this.traversedCACerts = 0;
    this.forwardCheckers = new ArrayList();
    for (PKIXCertPathChecker pKIXCertPathChecker : paramList) {
      if (pKIXCertPathChecker.isForwardCheckingSupported()) {
        pKIXCertPathChecker.init(true);
        this.forwardCheckers.add(pKIXCertPathChecker);
      } 
    } 
    this.init = true;
  }
  
  public void updateState(X509Certificate paramX509Certificate) throws CertificateException, IOException, CertPathValidatorException {
    if (paramX509Certificate == null)
      return; 
    X509CertImpl x509CertImpl = X509CertImpl.toImpl(paramX509Certificate);
    if (PKIX.isDSAPublicKeyWithoutParams(x509CertImpl.getPublicKey()))
      this.keyParamsNeededFlag = true; 
    this.cert = x509CertImpl;
    this.issuerDN = paramX509Certificate.getIssuerX500Principal();
    if (!X509CertImpl.isSelfIssued(paramX509Certificate) && !this.init && paramX509Certificate.getBasicConstraints() != -1)
      this.traversedCACerts++; 
    if (this.init || !X509CertImpl.isSelfIssued(paramX509Certificate)) {
      X500Principal x500Principal = paramX509Certificate.getSubjectX500Principal();
      this.subjectNamesTraversed.add(X500Name.asX500Name(x500Principal));
      try {
        SubjectAlternativeNameExtension subjectAlternativeNameExtension = x509CertImpl.getSubjectAlternativeNameExtension();
        if (subjectAlternativeNameExtension != null) {
          GeneralNames generalNames = subjectAlternativeNameExtension.get("subject_name");
          for (GeneralName generalName : generalNames.names())
            this.subjectNamesTraversed.add(generalName.getName()); 
        } 
      } catch (IOException iOException) {
        if (debug != null) {
          debug.println("ForwardState.updateState() unexpected exception");
          iOException.printStackTrace();
        } 
        throw new CertPathValidatorException(iOException);
      } 
    } 
    this.init = false;
  }
  
  public Object clone() {
    try {
      ForwardState forwardState = (ForwardState)super.clone();
      forwardState.forwardCheckers = (ArrayList)this.forwardCheckers.clone();
      ListIterator listIterator = forwardState.forwardCheckers.listIterator();
      while (listIterator.hasNext()) {
        PKIXCertPathChecker pKIXCertPathChecker = (PKIXCertPathChecker)listIterator.next();
        if (pKIXCertPathChecker instanceof Cloneable)
          listIterator.set((PKIXCertPathChecker)pKIXCertPathChecker.clone()); 
      } 
      forwardState.subjectNamesTraversed = (HashSet)this.subjectNamesTraversed.clone();
      return forwardState;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException.toString(), cloneNotSupportedException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\ForwardState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */