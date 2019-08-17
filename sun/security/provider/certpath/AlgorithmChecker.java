package sun.security.provider.certpath;

import java.math.BigInteger;
import java.security.AlgorithmConstraints;
import java.security.AlgorithmParameters;
import java.security.CryptoPrimitive;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Timestamp;
import java.security.cert.CRLException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXReason;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.Set;
import sun.security.util.AnchorCertificates;
import sun.security.util.ConstraintsParameters;
import sun.security.util.Debug;
import sun.security.util.DisabledAlgorithmConstraints;
import sun.security.util.KeyUtil;
import sun.security.x509.AlgorithmId;
import sun.security.x509.X509CRLImpl;
import sun.security.x509.X509CertImpl;

public final class AlgorithmChecker extends PKIXCertPathChecker {
  private static final Debug debug = Debug.getInstance("certpath");
  
  private final AlgorithmConstraints constraints;
  
  private final PublicKey trustedPubKey;
  
  private final Date pkixdate;
  
  private PublicKey prevPubKey;
  
  private final Timestamp jarTimestamp;
  
  private final String variant;
  
  private static final Set<CryptoPrimitive> SIGNATURE_PRIMITIVE_SET = Collections.unmodifiableSet(EnumSet.of(CryptoPrimitive.SIGNATURE));
  
  private static final Set<CryptoPrimitive> KU_PRIMITIVE_SET = Collections.unmodifiableSet(EnumSet.of(CryptoPrimitive.SIGNATURE, CryptoPrimitive.KEY_ENCAPSULATION, CryptoPrimitive.PUBLIC_KEY_ENCRYPTION, CryptoPrimitive.KEY_AGREEMENT));
  
  private static final DisabledAlgorithmConstraints certPathDefaultConstraints = new DisabledAlgorithmConstraints("jdk.certpath.disabledAlgorithms");
  
  private static final boolean publicCALimits = certPathDefaultConstraints.checkProperty("jdkCA");
  
  private boolean trustedMatch = false;
  
  public AlgorithmChecker(TrustAnchor paramTrustAnchor, String paramString) { this(paramTrustAnchor, certPathDefaultConstraints, null, null, paramString); }
  
  public AlgorithmChecker(AlgorithmConstraints paramAlgorithmConstraints, Timestamp paramTimestamp, String paramString) { this(null, paramAlgorithmConstraints, null, paramTimestamp, paramString); }
  
  public AlgorithmChecker(TrustAnchor paramTrustAnchor, AlgorithmConstraints paramAlgorithmConstraints, Date paramDate, Timestamp paramTimestamp, String paramString) {
    if (paramTrustAnchor != null) {
      if (paramTrustAnchor.getTrustedCert() != null) {
        this.trustedPubKey = paramTrustAnchor.getTrustedCert().getPublicKey();
        this.trustedMatch = checkFingerprint(paramTrustAnchor.getTrustedCert());
        if (this.trustedMatch && debug != null)
          debug.println("trustedMatch = true"); 
      } else {
        this.trustedPubKey = paramTrustAnchor.getCAPublicKey();
      } 
    } else {
      this.trustedPubKey = null;
      if (debug != null)
        debug.println("TrustAnchor is null, trustedMatch is false."); 
    } 
    this.prevPubKey = this.trustedPubKey;
    this.constraints = (paramAlgorithmConstraints == null) ? certPathDefaultConstraints : paramAlgorithmConstraints;
    this.pkixdate = (paramTimestamp != null) ? paramTimestamp.getTimestamp() : paramDate;
    this.jarTimestamp = paramTimestamp;
    this.variant = (paramString == null) ? "generic" : paramString;
  }
  
  public AlgorithmChecker(TrustAnchor paramTrustAnchor, Date paramDate, String paramString) { this(paramTrustAnchor, certPathDefaultConstraints, paramDate, null, paramString); }
  
  private static boolean checkFingerprint(X509Certificate paramX509Certificate) {
    if (!publicCALimits)
      return false; 
    if (debug != null)
      debug.println("AlgorithmChecker.contains: " + paramX509Certificate.getSigAlgName()); 
    return AnchorCertificates.contains(paramX509Certificate);
  }
  
  public void init(boolean paramBoolean) throws CertPathValidatorException {
    if (!paramBoolean) {
      if (this.trustedPubKey != null) {
        this.prevPubKey = this.trustedPubKey;
      } else {
        this.prevPubKey = null;
      } 
    } else {
      throw new CertPathValidatorException("forward checking not supported");
    } 
  }
  
  public boolean isForwardCheckingSupported() { return false; }
  
  public Set<String> getSupportedExtensions() { return null; }
  
  public void check(Certificate paramCertificate, Collection<String> paramCollection) throws CertPathValidatorException {
    AlgorithmId algorithmId;
    X509CertImpl x509CertImpl;
    if (!(paramCertificate instanceof X509Certificate) || this.constraints == null)
      return; 
    boolean[] arrayOfBoolean = ((X509Certificate)paramCertificate).getKeyUsage();
    if (arrayOfBoolean != null && arrayOfBoolean.length < 9)
      throw new CertPathValidatorException("incorrect KeyUsage extension", null, null, -1, PKIXReason.INVALID_KEY_USAGE); 
    try {
      x509CertImpl = X509CertImpl.toImpl((X509Certificate)paramCertificate);
      algorithmId = (AlgorithmId)x509CertImpl.get("x509.algorithm");
    } catch (CertificateException certificateException) {
      throw new CertPathValidatorException(certificateException);
    } 
    AlgorithmParameters algorithmParameters = algorithmId.getParameters();
    PublicKey publicKey = paramCertificate.getPublicKey();
    String str = x509CertImpl.getSigAlgName();
    if (!this.constraints.permits(SIGNATURE_PRIMITIVE_SET, str, algorithmParameters))
      throw new CertPathValidatorException("Algorithm constraints check failed on signature algorithm: " + str, null, null, -1, CertPathValidatorException.BasicReason.ALGORITHM_CONSTRAINED); 
    Set set = KU_PRIMITIVE_SET;
    if (arrayOfBoolean != null) {
      set = EnumSet.noneOf(CryptoPrimitive.class);
      if (arrayOfBoolean[0] || arrayOfBoolean[1] || arrayOfBoolean[5] || arrayOfBoolean[6])
        set.add(CryptoPrimitive.SIGNATURE); 
      if (arrayOfBoolean[2])
        set.add(CryptoPrimitive.KEY_ENCAPSULATION); 
      if (arrayOfBoolean[3])
        set.add(CryptoPrimitive.PUBLIC_KEY_ENCRYPTION); 
      if (arrayOfBoolean[4])
        set.add(CryptoPrimitive.KEY_AGREEMENT); 
      if (set.isEmpty())
        throw new CertPathValidatorException("incorrect KeyUsage extension bits", null, null, -1, PKIXReason.INVALID_KEY_USAGE); 
    } 
    ConstraintsParameters constraintsParameters = new ConstraintsParameters((X509Certificate)paramCertificate, this.trustedMatch, this.pkixdate, this.jarTimestamp, this.variant);
    if (this.constraints instanceof DisabledAlgorithmConstraints) {
      ((DisabledAlgorithmConstraints)this.constraints).permits(str, constraintsParameters);
    } else {
      certPathDefaultConstraints.permits(str, constraintsParameters);
      if (!this.constraints.permits(set, publicKey))
        throw new CertPathValidatorException("Algorithm constraints check failed on key " + publicKey.getAlgorithm() + " with size of " + KeyUtil.getKeySize(publicKey) + "bits", null, null, -1, CertPathValidatorException.BasicReason.ALGORITHM_CONSTRAINED); 
    } 
    if (this.prevPubKey == null) {
      this.prevPubKey = publicKey;
      return;
    } 
    if (!this.constraints.permits(SIGNATURE_PRIMITIVE_SET, str, this.prevPubKey, algorithmParameters))
      throw new CertPathValidatorException("Algorithm constraints check failed on signature algorithm: " + str, null, null, -1, CertPathValidatorException.BasicReason.ALGORITHM_CONSTRAINED); 
    if (PKIX.isDSAPublicKeyWithoutParams(publicKey)) {
      if (!(this.prevPubKey instanceof DSAPublicKey))
        throw new CertPathValidatorException("Input key is not of a appropriate type for inheriting parameters"); 
      DSAParams dSAParams = ((DSAPublicKey)this.prevPubKey).getParams();
      if (dSAParams == null)
        throw new CertPathValidatorException("Key parameters missing from public key."); 
      try {
        BigInteger bigInteger = ((DSAPublicKey)publicKey).getY();
        KeyFactory keyFactory = KeyFactory.getInstance("DSA");
        DSAPublicKeySpec dSAPublicKeySpec = new DSAPublicKeySpec(bigInteger, dSAParams.getP(), dSAParams.getQ(), dSAParams.getG());
        publicKey = keyFactory.generatePublic(dSAPublicKeySpec);
      } catch (GeneralSecurityException generalSecurityException) {
        throw new CertPathValidatorException("Unable to generate key with inherited parameters: " + generalSecurityException.getMessage(), generalSecurityException);
      } 
    } 
    this.prevPubKey = publicKey;
  }
  
  void trySetTrustAnchor(TrustAnchor paramTrustAnchor) {
    if (this.prevPubKey == null) {
      if (paramTrustAnchor == null)
        throw new IllegalArgumentException("The trust anchor cannot be null"); 
      if (paramTrustAnchor.getTrustedCert() != null) {
        this.prevPubKey = paramTrustAnchor.getTrustedCert().getPublicKey();
        this.trustedMatch = checkFingerprint(paramTrustAnchor.getTrustedCert());
        if (this.trustedMatch && debug != null)
          debug.println("trustedMatch = true"); 
      } else {
        this.prevPubKey = paramTrustAnchor.getCAPublicKey();
      } 
    } 
  }
  
  static void check(PublicKey paramPublicKey, X509CRL paramX509CRL, String paramString) throws CertPathValidatorException {
    X509CRLImpl x509CRLImpl = null;
    try {
      x509CRLImpl = X509CRLImpl.toImpl(paramX509CRL);
    } catch (CRLException cRLException) {
      throw new CertPathValidatorException(cRLException);
    } 
    AlgorithmId algorithmId = x509CRLImpl.getSigAlgId();
    check(paramPublicKey, algorithmId, paramString);
  }
  
  static void check(PublicKey paramPublicKey, AlgorithmId paramAlgorithmId, String paramString) throws CertPathValidatorException {
    String str = paramAlgorithmId.getName();
    AlgorithmParameters algorithmParameters = paramAlgorithmId.getParameters();
    certPathDefaultConstraints.permits(new ConstraintsParameters(str, algorithmParameters, paramPublicKey, paramString));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\AlgorithmChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */