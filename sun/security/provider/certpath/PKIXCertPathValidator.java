package sun.security.provider.certpath;

import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CertPath;
import java.security.cert.CertPathChecker;
import java.security.cert.CertPathParameters;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.CertPathValidatorSpi;
import java.security.cert.CertificateException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXReason;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import sun.security.util.Debug;
import sun.security.x509.X509CertImpl;

public final class PKIXCertPathValidator extends CertPathValidatorSpi {
  private static final Debug debug = Debug.getInstance("certpath");
  
  public CertPathChecker engineGetRevocationChecker() { return new RevocationChecker(); }
  
  public CertPathValidatorResult engineValidate(CertPath paramCertPath, CertPathParameters paramCertPathParameters) throws CertPathValidatorException, InvalidAlgorithmParameterException {
    PKIX.ValidatorParams validatorParams = PKIX.checkParams(paramCertPath, paramCertPathParameters);
    return validate(validatorParams);
  }
  
  private static PKIXCertPathValidatorResult validate(PKIX.ValidatorParams paramValidatorParams) throws CertPathValidatorException {
    if (debug != null)
      debug.println("PKIXCertPathValidator.engineValidate()..."); 
    AdaptableX509CertSelector adaptableX509CertSelector = null;
    List list = paramValidatorParams.certificates();
    if (!list.isEmpty()) {
      adaptableX509CertSelector = new AdaptableX509CertSelector();
      X509Certificate x509Certificate = (X509Certificate)list.get(0);
      adaptableX509CertSelector.setSubject(x509Certificate.getIssuerX500Principal());
      try {
        X509CertImpl x509CertImpl = X509CertImpl.toImpl(x509Certificate);
        adaptableX509CertSelector.setSkiAndSerialNumber(x509CertImpl.getAuthorityKeyIdentifierExtension());
      } catch (CertificateException|java.io.IOException certificateException) {}
    } 
    CertPathValidatorException certPathValidatorException = null;
    for (TrustAnchor trustAnchor : paramValidatorParams.trustAnchors()) {
      X509Certificate x509Certificate = trustAnchor.getTrustedCert();
      if (x509Certificate != null) {
        if (adaptableX509CertSelector != null && !adaptableX509CertSelector.match(x509Certificate)) {
          if (debug != null)
            debug.println("NO - don't try this trustedCert"); 
          continue;
        } 
        if (debug != null) {
          debug.println("YES - try this trustedCert");
          debug.println("anchor.getTrustedCert().getSubjectX500Principal() = " + x509Certificate.getSubjectX500Principal());
        } 
      } else if (debug != null) {
        debug.println("PKIXCertPathValidator.engineValidate(): anchor.getTrustedCert() == null");
      } 
      try {
        return validate(trustAnchor, paramValidatorParams);
      } catch (CertPathValidatorException certPathValidatorException1) {
        certPathValidatorException = certPathValidatorException1;
      } 
    } 
    if (certPathValidatorException != null)
      throw certPathValidatorException; 
    throw new CertPathValidatorException("Path does not chain with any of the trust anchors", null, null, -1, PKIXReason.NO_TRUST_ANCHOR);
  }
  
  private static PKIXCertPathValidatorResult validate(TrustAnchor paramTrustAnchor, PKIX.ValidatorParams paramValidatorParams) throws CertPathValidatorException {
    UntrustedChecker untrustedChecker = new UntrustedChecker();
    X509Certificate x509Certificate = paramTrustAnchor.getTrustedCert();
    if (x509Certificate != null)
      untrustedChecker.check(x509Certificate); 
    int i = paramValidatorParams.certificates().size();
    ArrayList arrayList = new ArrayList();
    arrayList.add(untrustedChecker);
    arrayList.add(new AlgorithmChecker(paramTrustAnchor, null, paramValidatorParams.date(), paramValidatorParams.timestamp(), paramValidatorParams.variant()));
    arrayList.add(new KeyChecker(i, paramValidatorParams.targetCertConstraints()));
    arrayList.add(new ConstraintsChecker(i));
    PolicyNodeImpl policyNodeImpl = new PolicyNodeImpl(null, "2.5.29.32.0", null, false, Collections.singleton("2.5.29.32.0"), false);
    PolicyChecker policyChecker = new PolicyChecker(paramValidatorParams.initialPolicies(), i, paramValidatorParams.explicitPolicyRequired(), paramValidatorParams.policyMappingInhibited(), paramValidatorParams.anyPolicyInhibited(), paramValidatorParams.policyQualifiersRejected(), policyNodeImpl);
    arrayList.add(policyChecker);
    Date date = null;
    if ((paramValidatorParams.variant() == "code signing" || paramValidatorParams.variant() == "plugin code signing") && paramValidatorParams.timestamp() != null) {
      date = paramValidatorParams.timestamp().getTimestamp();
    } else {
      date = paramValidatorParams.date();
    } 
    BasicChecker basicChecker = new BasicChecker(paramTrustAnchor, date, paramValidatorParams.sigProvider(), false);
    arrayList.add(basicChecker);
    boolean bool = false;
    List list = paramValidatorParams.certPathCheckers();
    for (PKIXCertPathChecker pKIXCertPathChecker : list) {
      if (pKIXCertPathChecker instanceof java.security.cert.PKIXRevocationChecker) {
        if (bool)
          throw new CertPathValidatorException("Only one PKIXRevocationChecker can be specified"); 
        bool = true;
        if (pKIXCertPathChecker instanceof RevocationChecker)
          ((RevocationChecker)pKIXCertPathChecker).init(paramTrustAnchor, paramValidatorParams); 
      } 
    } 
    if (paramValidatorParams.revocationEnabled() && !bool)
      arrayList.add(new RevocationChecker(paramTrustAnchor, paramValidatorParams)); 
    arrayList.addAll(list);
    PKIXMasterCertPathValidator.validate(paramValidatorParams.certPath(), paramValidatorParams.certificates(), arrayList);
    return new PKIXCertPathValidatorResult(paramTrustAnchor, policyChecker.getPolicyTree(), basicChecker.getPublicKey());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\PKIXCertPathValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */