package sun.security.provider.certpath;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXReason;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import sun.security.util.Debug;
import sun.security.x509.X500Name;

class BasicChecker extends PKIXCertPathChecker {
  private static final Debug debug = Debug.getInstance("certpath");
  
  private final PublicKey trustedPubKey;
  
  private final X500Principal caName;
  
  private final Date date;
  
  private final String sigProvider;
  
  private final boolean sigOnly;
  
  private X500Principal prevSubject;
  
  private PublicKey prevPubKey;
  
  BasicChecker(TrustAnchor paramTrustAnchor, Date paramDate, String paramString, boolean paramBoolean) {
    if (paramTrustAnchor.getTrustedCert() != null) {
      this.trustedPubKey = paramTrustAnchor.getTrustedCert().getPublicKey();
      this.caName = paramTrustAnchor.getTrustedCert().getSubjectX500Principal();
    } else {
      this.trustedPubKey = paramTrustAnchor.getCAPublicKey();
      this.caName = paramTrustAnchor.getCA();
    } 
    this.date = paramDate;
    this.sigProvider = paramString;
    this.sigOnly = paramBoolean;
    this.prevPubKey = this.trustedPubKey;
  }
  
  public void init(boolean paramBoolean) throws CertPathValidatorException {
    if (!paramBoolean) {
      this.prevPubKey = this.trustedPubKey;
      if (PKIX.isDSAPublicKeyWithoutParams(this.prevPubKey))
        throw new CertPathValidatorException("Key parameters missing"); 
      this.prevSubject = this.caName;
    } else {
      throw new CertPathValidatorException("forward checking not supported");
    } 
  }
  
  public boolean isForwardCheckingSupported() { return false; }
  
  public Set<String> getSupportedExtensions() { return null; }
  
  public void check(Certificate paramCertificate, Collection<String> paramCollection) throws CertPathValidatorException {
    X509Certificate x509Certificate = (X509Certificate)paramCertificate;
    if (!this.sigOnly) {
      verifyValidity(x509Certificate);
      verifyNameChaining(x509Certificate);
    } 
    verifySignature(x509Certificate);
    updateState(x509Certificate);
  }
  
  private void verifySignature(X509Certificate paramX509Certificate) throws CertPathValidatorException {
    String str = "signature";
    if (debug != null)
      debug.println("---checking " + str + "..."); 
    try {
      paramX509Certificate.verify(this.prevPubKey, this.sigProvider);
    } catch (SignatureException signatureException) {
      throw new CertPathValidatorException(str + " check failed", signatureException, null, -1, CertPathValidatorException.BasicReason.INVALID_SIGNATURE);
    } catch (GeneralSecurityException generalSecurityException) {
      throw new CertPathValidatorException(str + " check failed", generalSecurityException);
    } 
    if (debug != null)
      debug.println(str + " verified."); 
  }
  
  private void verifyValidity(X509Certificate paramX509Certificate) throws CertPathValidatorException {
    String str = "validity";
    if (debug != null)
      debug.println("---checking " + str + ":" + this.date.toString() + "..."); 
    try {
      paramX509Certificate.checkValidity(this.date);
    } catch (CertificateExpiredException certificateExpiredException) {
      throw new CertPathValidatorException(str + " check failed", certificateExpiredException, null, -1, CertPathValidatorException.BasicReason.EXPIRED);
    } catch (CertificateNotYetValidException certificateNotYetValidException) {
      throw new CertPathValidatorException(str + " check failed", certificateNotYetValidException, null, -1, CertPathValidatorException.BasicReason.NOT_YET_VALID);
    } 
    if (debug != null)
      debug.println(str + " verified."); 
  }
  
  private void verifyNameChaining(X509Certificate paramX509Certificate) throws CertPathValidatorException {
    if (this.prevSubject != null) {
      String str = "subject/issuer name chaining";
      if (debug != null)
        debug.println("---checking " + str + "..."); 
      X500Principal x500Principal = paramX509Certificate.getIssuerX500Principal();
      if (X500Name.asX500Name(x500Principal).isEmpty())
        throw new CertPathValidatorException(str + " check failed: empty/null issuer DN in certificate is invalid", null, null, -1, PKIXReason.NAME_CHAINING); 
      if (!x500Principal.equals(this.prevSubject))
        throw new CertPathValidatorException(str + " check failed", null, null, -1, PKIXReason.NAME_CHAINING); 
      if (debug != null)
        debug.println(str + " verified."); 
    } 
  }
  
  private void updateState(X509Certificate paramX509Certificate) throws CertPathValidatorException {
    PublicKey publicKey = paramX509Certificate.getPublicKey();
    if (debug != null)
      debug.println("BasicChecker.updateState issuer: " + paramX509Certificate.getIssuerX500Principal().toString() + "; subject: " + paramX509Certificate.getSubjectX500Principal() + "; serial#: " + paramX509Certificate.getSerialNumber().toString()); 
    if (PKIX.isDSAPublicKeyWithoutParams(publicKey)) {
      publicKey = makeInheritedParamsKey(publicKey, this.prevPubKey);
      if (debug != null)
        debug.println("BasicChecker.updateState Made key with inherited params"); 
    } 
    this.prevPubKey = publicKey;
    this.prevSubject = paramX509Certificate.getSubjectX500Principal();
  }
  
  static PublicKey makeInheritedParamsKey(PublicKey paramPublicKey1, PublicKey paramPublicKey2) throws CertPathValidatorException {
    if (!(paramPublicKey1 instanceof DSAPublicKey) || !(paramPublicKey2 instanceof DSAPublicKey))
      throw new CertPathValidatorException("Input key is not appropriate type for inheriting parameters"); 
    DSAParams dSAParams = ((DSAPublicKey)paramPublicKey2).getParams();
    if (dSAParams == null)
      throw new CertPathValidatorException("Key parameters missing"); 
    try {
      BigInteger bigInteger = ((DSAPublicKey)paramPublicKey1).getY();
      KeyFactory keyFactory = KeyFactory.getInstance("DSA");
      DSAPublicKeySpec dSAPublicKeySpec = new DSAPublicKeySpec(bigInteger, dSAParams.getP(), dSAParams.getQ(), dSAParams.getG());
      return keyFactory.generatePublic(dSAPublicKeySpec);
    } catch (GeneralSecurityException generalSecurityException) {
      throw new CertPathValidatorException("Unable to generate key with inherited parameters: " + generalSecurityException.getMessage(), generalSecurityException);
    } 
  }
  
  PublicKey getPublicKey() { return this.prevPubKey; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\BasicChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */