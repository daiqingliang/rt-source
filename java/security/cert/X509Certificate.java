package java.security.cert;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.security.auth.x500.X500Principal;
import sun.security.x509.X509CertImpl;

public abstract class X509Certificate extends Certificate implements X509Extension {
  private static final long serialVersionUID = -2491127588187038216L;
  
  private X500Principal subjectX500Principal;
  
  private X500Principal issuerX500Principal;
  
  protected X509Certificate() { super("X.509"); }
  
  public abstract void checkValidity();
  
  public abstract void checkValidity(Date paramDate) throws CertificateExpiredException, CertificateNotYetValidException;
  
  public abstract int getVersion();
  
  public abstract BigInteger getSerialNumber();
  
  public abstract Principal getIssuerDN();
  
  public X500Principal getIssuerX500Principal() {
    if (this.issuerX500Principal == null)
      this.issuerX500Principal = X509CertImpl.getIssuerX500Principal(this); 
    return this.issuerX500Principal;
  }
  
  public abstract Principal getSubjectDN();
  
  public X500Principal getSubjectX500Principal() {
    if (this.subjectX500Principal == null)
      this.subjectX500Principal = X509CertImpl.getSubjectX500Principal(this); 
    return this.subjectX500Principal;
  }
  
  public abstract Date getNotBefore();
  
  public abstract Date getNotAfter();
  
  public abstract byte[] getTBSCertificate() throws CertificateEncodingException;
  
  public abstract byte[] getSignature() throws CertificateEncodingException;
  
  public abstract String getSigAlgName();
  
  public abstract String getSigAlgOID();
  
  public abstract byte[] getSigAlgParams() throws CertificateEncodingException;
  
  public abstract boolean[] getIssuerUniqueID();
  
  public abstract boolean[] getSubjectUniqueID();
  
  public abstract boolean[] getKeyUsage();
  
  public List<String> getExtendedKeyUsage() throws CertificateParsingException { return X509CertImpl.getExtendedKeyUsage(this); }
  
  public abstract int getBasicConstraints();
  
  public Collection<List<?>> getSubjectAlternativeNames() throws CertificateParsingException { return X509CertImpl.getSubjectAlternativeNames(this); }
  
  public Collection<List<?>> getIssuerAlternativeNames() throws CertificateParsingException { return X509CertImpl.getIssuerAlternativeNames(this); }
  
  public void verify(PublicKey paramPublicKey, Provider paramProvider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException { X509CertImpl.verify(this, paramPublicKey, paramProvider); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\cert\X509Certificate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */