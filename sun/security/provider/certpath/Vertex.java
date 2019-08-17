package sun.security.provider.certpath;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import sun.security.util.Debug;
import sun.security.x509.AuthorityKeyIdentifierExtension;
import sun.security.x509.KeyIdentifier;
import sun.security.x509.SubjectKeyIdentifierExtension;
import sun.security.x509.X509CertImpl;

public class Vertex {
  private static final Debug debug = Debug.getInstance("certpath");
  
  private X509Certificate cert;
  
  private int index;
  
  private Throwable throwable;
  
  Vertex(X509Certificate paramX509Certificate) {
    this.cert = paramX509Certificate;
    this.index = -1;
  }
  
  public X509Certificate getCertificate() { return this.cert; }
  
  public int getIndex() { return this.index; }
  
  void setIndex(int paramInt) { this.index = paramInt; }
  
  public Throwable getThrowable() { return this.throwable; }
  
  void setThrowable(Throwable paramThrowable) { this.throwable = paramThrowable; }
  
  public String toString() { return certToString() + throwableToString() + indexToString(); }
  
  public String certToString() {
    StringBuilder stringBuilder = new StringBuilder();
    X509CertImpl x509CertImpl = null;
    try {
      x509CertImpl = X509CertImpl.toImpl(this.cert);
    } catch (CertificateException certificateException) {
      if (debug != null) {
        debug.println("Vertex.certToString() unexpected exception");
        certificateException.printStackTrace();
      } 
      return stringBuilder.toString();
    } 
    stringBuilder.append("Issuer:     ").append(x509CertImpl.getIssuerX500Principal()).append("\n");
    stringBuilder.append("Subject:    ").append(x509CertImpl.getSubjectX500Principal()).append("\n");
    stringBuilder.append("SerialNum:  ").append(x509CertImpl.getSerialNumber().toString(16)).append("\n");
    stringBuilder.append("Expires:    ").append(x509CertImpl.getNotAfter().toString()).append("\n");
    boolean[] arrayOfBoolean1 = x509CertImpl.getIssuerUniqueID();
    if (arrayOfBoolean1 != null) {
      stringBuilder.append("IssuerUID:  ");
      for (boolean bool : arrayOfBoolean1)
        stringBuilder.append(bool ? 1 : 0); 
      stringBuilder.append("\n");
    } 
    boolean[] arrayOfBoolean2 = x509CertImpl.getSubjectUniqueID();
    if (arrayOfBoolean2 != null) {
      stringBuilder.append("SubjectUID: ");
      for (boolean bool : arrayOfBoolean2)
        stringBuilder.append(bool ? 1 : 0); 
      stringBuilder.append("\n");
    } 
    try {
      SubjectKeyIdentifierExtension subjectKeyIdentifierExtension = x509CertImpl.getSubjectKeyIdentifierExtension();
      if (subjectKeyIdentifierExtension != null) {
        KeyIdentifier keyIdentifier = subjectKeyIdentifierExtension.get("key_id");
        stringBuilder.append("SubjKeyID:  ").append(keyIdentifier.toString());
      } 
      AuthorityKeyIdentifierExtension authorityKeyIdentifierExtension = x509CertImpl.getAuthorityKeyIdentifierExtension();
      if (authorityKeyIdentifierExtension != null) {
        KeyIdentifier keyIdentifier = (KeyIdentifier)authorityKeyIdentifierExtension.get("key_id");
        stringBuilder.append("AuthKeyID:  ").append(keyIdentifier.toString());
      } 
    } catch (IOException iOException) {
      if (debug != null) {
        debug.println("Vertex.certToString() unexpected exception");
        iOException.printStackTrace();
      } 
    } 
    return stringBuilder.toString();
  }
  
  public String throwableToString() {
    StringBuilder stringBuilder = new StringBuilder("Exception:  ");
    if (this.throwable != null) {
      stringBuilder.append(this.throwable.toString());
    } else {
      stringBuilder.append("null");
    } 
    stringBuilder.append("\n");
    return stringBuilder.toString();
  }
  
  public String moreToString() {
    StringBuilder stringBuilder = new StringBuilder("Last cert?  ");
    stringBuilder.append((this.index == -1) ? "Yes" : "No");
    stringBuilder.append("\n");
    return stringBuilder.toString();
  }
  
  public String indexToString() { return "Index:      " + this.index + "\n"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\Vertex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */