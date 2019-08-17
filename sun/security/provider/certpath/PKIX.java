package sun.security.provider.certpath;

import java.security.InvalidAlgorithmParameterException;
import java.security.PublicKey;
import java.security.Timestamp;
import java.security.cert.CertPath;
import java.security.cert.CertPathParameters;
import java.security.cert.CertSelector;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAPublicKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import sun.security.util.Debug;

class PKIX {
  private static final Debug debug = Debug.getInstance("certpath");
  
  static boolean isDSAPublicKeyWithoutParams(PublicKey paramPublicKey) { return (paramPublicKey instanceof DSAPublicKey && ((DSAPublicKey)paramPublicKey).getParams() == null); }
  
  static ValidatorParams checkParams(CertPath paramCertPath, CertPathParameters paramCertPathParameters) throws InvalidAlgorithmParameterException {
    if (!(paramCertPathParameters instanceof PKIXParameters))
      throw new InvalidAlgorithmParameterException("inappropriate params, must be an instance of PKIXParameters"); 
    return new ValidatorParams(paramCertPath, (PKIXParameters)paramCertPathParameters);
  }
  
  static BuilderParams checkBuilderParams(CertPathParameters paramCertPathParameters) throws InvalidAlgorithmParameterException {
    if (!(paramCertPathParameters instanceof PKIXBuilderParameters))
      throw new InvalidAlgorithmParameterException("inappropriate params, must be an instance of PKIXBuilderParameters"); 
    return new BuilderParams((PKIXBuilderParameters)paramCertPathParameters);
  }
  
  static class BuilderParams extends ValidatorParams {
    private PKIXBuilderParameters params;
    
    private List<CertStore> stores;
    
    private X500Principal targetSubject;
    
    BuilderParams(PKIXBuilderParameters param1PKIXBuilderParameters) throws InvalidAlgorithmParameterException {
      super(param1PKIXBuilderParameters);
      checkParams(param1PKIXBuilderParameters);
    }
    
    private void checkParams(PKIXBuilderParameters param1PKIXBuilderParameters) throws InvalidAlgorithmParameterException {
      CertSelector certSelector = targetCertConstraints();
      if (!(certSelector instanceof X509CertSelector))
        throw new InvalidAlgorithmParameterException("the targetCertConstraints parameter must be an X509CertSelector"); 
      this.params = param1PKIXBuilderParameters;
      this.targetSubject = getTargetSubject(certStores(), (X509CertSelector)targetCertConstraints());
    }
    
    List<CertStore> certStores() {
      if (this.stores == null) {
        this.stores = new ArrayList(this.params.getCertStores());
        Collections.sort(this.stores, new PKIX.CertStoreComparator(null));
      } 
      return this.stores;
    }
    
    int maxPathLength() { return this.params.getMaxPathLength(); }
    
    PKIXBuilderParameters params() { return this.params; }
    
    X500Principal targetSubject() { return this.targetSubject; }
    
    private static X500Principal getTargetSubject(List<CertStore> param1List, X509CertSelector param1X509CertSelector) throws InvalidAlgorithmParameterException {
      X500Principal x500Principal = param1X509CertSelector.getSubject();
      if (x500Principal != null)
        return x500Principal; 
      X509Certificate x509Certificate = param1X509CertSelector.getCertificate();
      if (x509Certificate != null)
        x500Principal = x509Certificate.getSubjectX500Principal(); 
      if (x500Principal != null)
        return x500Principal; 
      for (CertStore certStore : param1List) {
        try {
          Collection collection = certStore.getCertificates(param1X509CertSelector);
          if (!collection.isEmpty()) {
            X509Certificate x509Certificate1 = (X509Certificate)collection.iterator().next();
            return x509Certificate1.getSubjectX500Principal();
          } 
        } catch (CertStoreException certStoreException) {
          if (debug != null) {
            debug.println("BuilderParams.getTargetSubjectDN: non-fatal exception retrieving certs: " + certStoreException);
            certStoreException.printStackTrace();
          } 
        } 
      } 
      throw new InvalidAlgorithmParameterException("Could not determine unique target subject");
    }
  }
  
  private static class CertStoreComparator extends Object implements Comparator<CertStore> {
    private CertStoreComparator() {}
    
    public int compare(CertStore param1CertStore1, CertStore param1CertStore2) { return (param1CertStore1.getType().equals("Collection") || param1CertStore1.getCertStoreParameters() instanceof java.security.cert.CollectionCertStoreParameters) ? -1 : 1; }
  }
  
  static class CertStoreTypeException extends CertStoreException {
    private static final long serialVersionUID = 7463352639238322556L;
    
    private final String type;
    
    CertStoreTypeException(String param1String, CertStoreException param1CertStoreException) {
      super(param1CertStoreException.getMessage(), param1CertStoreException.getCause());
      this.type = param1String;
    }
    
    String getType() { return this.type; }
  }
  
  static class ValidatorParams {
    private final PKIXParameters params;
    
    private CertPath certPath;
    
    private List<PKIXCertPathChecker> checkers;
    
    private List<CertStore> stores;
    
    private boolean gotDate;
    
    private Date date;
    
    private Set<String> policies;
    
    private boolean gotConstraints;
    
    private CertSelector constraints;
    
    private Set<TrustAnchor> anchors;
    
    private List<X509Certificate> certs;
    
    private Timestamp timestamp;
    
    private String variant;
    
    ValidatorParams(CertPath param1CertPath, PKIXParameters param1PKIXParameters) throws InvalidAlgorithmParameterException {
      this(param1PKIXParameters);
      if (!param1CertPath.getType().equals("X.509") && !param1CertPath.getType().equals("X509"))
        throw new InvalidAlgorithmParameterException("inappropriate CertPath type specified, must be X.509 or X509"); 
      this.certPath = param1CertPath;
    }
    
    ValidatorParams(PKIXParameters param1PKIXParameters) throws InvalidAlgorithmParameterException {
      if (param1PKIXParameters instanceof PKIXExtendedParameters) {
        this.timestamp = ((PKIXExtendedParameters)param1PKIXParameters).getTimestamp();
        this.variant = ((PKIXExtendedParameters)param1PKIXParameters).getVariant();
      } 
      this.anchors = param1PKIXParameters.getTrustAnchors();
      for (TrustAnchor trustAnchor : this.anchors) {
        if (trustAnchor.getNameConstraints() != null)
          throw new InvalidAlgorithmParameterException("name constraints in trust anchor not supported"); 
      } 
      this.params = param1PKIXParameters;
    }
    
    CertPath certPath() { return this.certPath; }
    
    void setCertPath(CertPath param1CertPath) { this.certPath = param1CertPath; }
    
    List<X509Certificate> certificates() {
      if (this.certs == null)
        if (this.certPath == null) {
          this.certs = Collections.emptyList();
        } else {
          ArrayList arrayList = new ArrayList(this.certPath.getCertificates());
          Collections.reverse(arrayList);
          this.certs = arrayList;
        }  
      return this.certs;
    }
    
    List<PKIXCertPathChecker> certPathCheckers() {
      if (this.checkers == null)
        this.checkers = this.params.getCertPathCheckers(); 
      return this.checkers;
    }
    
    List<CertStore> certStores() {
      if (this.stores == null)
        this.stores = this.params.getCertStores(); 
      return this.stores;
    }
    
    Date date() {
      if (!this.gotDate) {
        this.date = this.params.getDate();
        if (this.date == null)
          this.date = new Date(); 
        this.gotDate = true;
      } 
      return this.date;
    }
    
    Set<String> initialPolicies() {
      if (this.policies == null)
        this.policies = this.params.getInitialPolicies(); 
      return this.policies;
    }
    
    CertSelector targetCertConstraints() {
      if (!this.gotConstraints) {
        this.constraints = this.params.getTargetCertConstraints();
        this.gotConstraints = true;
      } 
      return this.constraints;
    }
    
    Set<TrustAnchor> trustAnchors() { return this.anchors; }
    
    boolean revocationEnabled() { return this.params.isRevocationEnabled(); }
    
    boolean policyMappingInhibited() { return this.params.isPolicyMappingInhibited(); }
    
    boolean explicitPolicyRequired() { return this.params.isExplicitPolicyRequired(); }
    
    boolean policyQualifiersRejected() { return this.params.getPolicyQualifiersRejected(); }
    
    String sigProvider() { return this.params.getSigProvider(); }
    
    boolean anyPolicyInhibited() { return this.params.isAnyPolicyInhibited(); }
    
    PKIXParameters getPKIXParameters() { return this.params; }
    
    Timestamp timestamp() { return this.timestamp; }
    
    String variant() { return this.variant; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\PKIX.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */