package sun.security.validator;

import java.security.AccessController;
import java.security.AlgorithmConstraints;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.PublicKey;
import java.security.Timestamp;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathValidator;
import java.security.cert.CertStore;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import sun.security.action.GetBooleanAction;
import sun.security.provider.certpath.AlgorithmChecker;
import sun.security.provider.certpath.PKIXExtendedParameters;

public final class PKIXValidator extends Validator {
  private static final boolean checkTLSRevocation = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("com.sun.net.ssl.checkRevocation"))).booleanValue();
  
  private static final boolean TRY_VALIDATOR = true;
  
  private final Set<X509Certificate> trustedCerts;
  
  private final PKIXBuilderParameters parameterTemplate;
  
  private int certPathLength = -1;
  
  private final Map<X500Principal, List<PublicKey>> trustedSubjects;
  
  private final CertificateFactory factory;
  
  private final boolean plugin;
  
  PKIXValidator(String paramString, Collection<X509Certificate> paramCollection) {
    super("PKIX", paramString);
    if (paramCollection instanceof Set) {
      this.trustedCerts = (Set)paramCollection;
    } else {
      this.trustedCerts = new HashSet(paramCollection);
    } 
    HashSet hashSet = new HashSet();
    for (X509Certificate x509Certificate : paramCollection)
      hashSet.add(new TrustAnchor(x509Certificate, null)); 
    try {
      this.parameterTemplate = new PKIXBuilderParameters(hashSet, null);
    } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
      throw new RuntimeException("Unexpected error: " + invalidAlgorithmParameterException.toString(), invalidAlgorithmParameterException);
    } 
    setDefaultParameters(paramString);
    this.trustedSubjects = new HashMap();
    for (X509Certificate x509Certificate : paramCollection) {
      ArrayList arrayList;
      X500Principal x500Principal = x509Certificate.getSubjectX500Principal();
      if (this.trustedSubjects.containsKey(x500Principal)) {
        arrayList = (List)this.trustedSubjects.get(x500Principal);
      } else {
        arrayList = new ArrayList();
        this.trustedSubjects.put(x500Principal, arrayList);
      } 
      arrayList.add(x509Certificate.getPublicKey());
    } 
    try {
      this.factory = CertificateFactory.getInstance("X.509");
    } catch (CertificateException certificateException) {
      throw new RuntimeException("Internal error", certificateException);
    } 
    this.plugin = paramString.equals("plugin code signing");
  }
  
  PKIXValidator(String paramString, PKIXBuilderParameters paramPKIXBuilderParameters) {
    super("PKIX", paramString);
    this.trustedCerts = new HashSet();
    for (TrustAnchor trustAnchor : paramPKIXBuilderParameters.getTrustAnchors()) {
      X509Certificate x509Certificate = trustAnchor.getTrustedCert();
      if (x509Certificate != null)
        this.trustedCerts.add(x509Certificate); 
    } 
    this.parameterTemplate = paramPKIXBuilderParameters;
    this.trustedSubjects = new HashMap();
    for (X509Certificate x509Certificate : this.trustedCerts) {
      ArrayList arrayList;
      X500Principal x500Principal = x509Certificate.getSubjectX500Principal();
      if (this.trustedSubjects.containsKey(x500Principal)) {
        arrayList = (List)this.trustedSubjects.get(x500Principal);
      } else {
        arrayList = new ArrayList();
        this.trustedSubjects.put(x500Principal, arrayList);
      } 
      arrayList.add(x509Certificate.getPublicKey());
    } 
    try {
      this.factory = CertificateFactory.getInstance("X.509");
    } catch (CertificateException certificateException) {
      throw new RuntimeException("Internal error", certificateException);
    } 
    this.plugin = paramString.equals("plugin code signing");
  }
  
  public Collection<X509Certificate> getTrustedCertificates() { return this.trustedCerts; }
  
  public int getCertPathLength() { return this.certPathLength; }
  
  private void setDefaultParameters(String paramString) {
    if (paramString == "tls server" || paramString == "tls client") {
      this.parameterTemplate.setRevocationEnabled(checkTLSRevocation);
    } else {
      this.parameterTemplate.setRevocationEnabled(false);
    } 
  }
  
  public PKIXBuilderParameters getParameters() { return this.parameterTemplate; }
  
  X509Certificate[] engineValidate(X509Certificate[] paramArrayOfX509Certificate, Collection<X509Certificate> paramCollection, AlgorithmConstraints paramAlgorithmConstraints, Object paramObject) throws CertificateException {
    if (paramArrayOfX509Certificate == null || paramArrayOfX509Certificate.length == 0)
      throw new CertificateException("null or zero-length certificate chain"); 
    PKIXExtendedParameters pKIXExtendedParameters = null;
    try {
      pKIXExtendedParameters = new PKIXExtendedParameters((PKIXBuilderParameters)this.parameterTemplate.clone(), (paramObject instanceof Timestamp) ? (Timestamp)paramObject : null, this.variant);
    } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {}
    if (paramAlgorithmConstraints != null)
      pKIXExtendedParameters.addCertPathChecker(new AlgorithmChecker(paramAlgorithmConstraints, null, this.variant)); 
    X500Principal x500Principal1 = null;
    for (byte b = 0; b < paramArrayOfX509Certificate.length; b++) {
      X509Certificate x509Certificate1 = paramArrayOfX509Certificate[b];
      X500Principal x500Principal = x509Certificate1.getSubjectX500Principal();
      if (b && !x500Principal.equals(x500Principal1))
        return doBuild(paramArrayOfX509Certificate, paramCollection, pKIXExtendedParameters); 
      if (this.trustedCerts.contains(x509Certificate1) || (this.trustedSubjects.containsKey(x500Principal) && ((List)this.trustedSubjects.get(x500Principal)).contains(x509Certificate1.getPublicKey()))) {
        if (!b)
          return new X509Certificate[] { paramArrayOfX509Certificate[0] }; 
        X509Certificate[] arrayOfX509Certificate = new X509Certificate[b];
        System.arraycopy(paramArrayOfX509Certificate, 0, arrayOfX509Certificate, 0, b);
        return doValidate(arrayOfX509Certificate, pKIXExtendedParameters);
      } 
      x500Principal1 = x509Certificate1.getIssuerX500Principal();
    } 
    X509Certificate x509Certificate = paramArrayOfX509Certificate[paramArrayOfX509Certificate.length - 1];
    X500Principal x500Principal2 = x509Certificate.getIssuerX500Principal();
    X500Principal x500Principal3 = x509Certificate.getSubjectX500Principal();
    if (this.trustedSubjects.containsKey(x500Principal2) && isSignatureValid((List)this.trustedSubjects.get(x500Principal2), x509Certificate))
      return doValidate(paramArrayOfX509Certificate, pKIXExtendedParameters); 
    if (this.plugin) {
      if (paramArrayOfX509Certificate.length > 1) {
        X509Certificate[] arrayOfX509Certificate = new X509Certificate[paramArrayOfX509Certificate.length - 1];
        System.arraycopy(paramArrayOfX509Certificate, 0, arrayOfX509Certificate, 0, arrayOfX509Certificate.length);
        try {
          pKIXExtendedParameters.setTrustAnchors(Collections.singleton(new TrustAnchor(paramArrayOfX509Certificate[paramArrayOfX509Certificate.length - 1], null)));
        } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
          throw new CertificateException(invalidAlgorithmParameterException);
        } 
        doValidate(arrayOfX509Certificate, pKIXExtendedParameters);
      } 
      throw new ValidatorException(ValidatorException.T_NO_TRUST_ANCHOR);
    } 
    return doBuild(paramArrayOfX509Certificate, paramCollection, pKIXExtendedParameters);
  }
  
  private boolean isSignatureValid(List<PublicKey> paramList, X509Certificate paramX509Certificate) {
    if (this.plugin) {
      for (PublicKey publicKey : paramList) {
        try {
          paramX509Certificate.verify(publicKey);
          return true;
        } catch (Exception exception) {}
      } 
      return false;
    } 
    return true;
  }
  
  private static X509Certificate[] toArray(CertPath paramCertPath, TrustAnchor paramTrustAnchor) throws CertificateException {
    List list = paramCertPath.getCertificates();
    X509Certificate[] arrayOfX509Certificate = new X509Certificate[list.size() + 1];
    list.toArray(arrayOfX509Certificate);
    X509Certificate x509Certificate = paramTrustAnchor.getTrustedCert();
    if (x509Certificate == null)
      throw new ValidatorException("TrustAnchor must be specified as certificate"); 
    arrayOfX509Certificate[arrayOfX509Certificate.length - 1] = x509Certificate;
    return arrayOfX509Certificate;
  }
  
  private void setDate(PKIXBuilderParameters paramPKIXBuilderParameters) {
    Date date = this.validationDate;
    if (date != null)
      paramPKIXBuilderParameters.setDate(date); 
  }
  
  private X509Certificate[] doValidate(X509Certificate[] paramArrayOfX509Certificate, PKIXBuilderParameters paramPKIXBuilderParameters) throws CertificateException {
    try {
      setDate(paramPKIXBuilderParameters);
      CertPathValidator certPathValidator = CertPathValidator.getInstance("PKIX");
      CertPath certPath = this.factory.generateCertPath(Arrays.asList(paramArrayOfX509Certificate));
      this.certPathLength = paramArrayOfX509Certificate.length;
      PKIXCertPathValidatorResult pKIXCertPathValidatorResult = (PKIXCertPathValidatorResult)certPathValidator.validate(certPath, paramPKIXBuilderParameters);
      return toArray(certPath, pKIXCertPathValidatorResult.getTrustAnchor());
    } catch (GeneralSecurityException generalSecurityException) {
      throw new ValidatorException("PKIX path validation failed: " + generalSecurityException.toString(), generalSecurityException);
    } 
  }
  
  private X509Certificate[] doBuild(X509Certificate[] paramArrayOfX509Certificate, Collection<X509Certificate> paramCollection, PKIXBuilderParameters paramPKIXBuilderParameters) throws CertificateException {
    try {
      setDate(paramPKIXBuilderParameters);
      X509CertSelector x509CertSelector = new X509CertSelector();
      x509CertSelector.setCertificate(paramArrayOfX509Certificate[0]);
      paramPKIXBuilderParameters.setTargetCertConstraints(x509CertSelector);
      ArrayList arrayList = new ArrayList();
      arrayList.addAll(Arrays.asList(paramArrayOfX509Certificate));
      if (paramCollection != null)
        arrayList.addAll(paramCollection); 
      CertStore certStore = CertStore.getInstance("Collection", new CollectionCertStoreParameters(arrayList));
      paramPKIXBuilderParameters.addCertStore(certStore);
      CertPathBuilder certPathBuilder = CertPathBuilder.getInstance("PKIX");
      PKIXCertPathBuilderResult pKIXCertPathBuilderResult = (PKIXCertPathBuilderResult)certPathBuilder.build(paramPKIXBuilderParameters);
      return toArray(pKIXCertPathBuilderResult.getCertPath(), pKIXCertPathBuilderResult.getTrustAnchor());
    } catch (GeneralSecurityException generalSecurityException) {
      throw new ValidatorException("PKIX path building failed: " + generalSecurityException.toString(), generalSecurityException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\validator\PKIXValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */