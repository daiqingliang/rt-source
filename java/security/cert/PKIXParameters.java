package java.security.cert;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PKIXParameters implements CertPathParameters {
  private Set<TrustAnchor> unmodTrustAnchors;
  
  private Date date;
  
  private List<PKIXCertPathChecker> certPathCheckers;
  
  private String sigProvider;
  
  private boolean revocationEnabled = true;
  
  private Set<String> unmodInitialPolicies;
  
  private boolean explicitPolicyRequired = false;
  
  private boolean policyMappingInhibited = false;
  
  private boolean anyPolicyInhibited = false;
  
  private boolean policyQualifiersRejected = true;
  
  private List<CertStore> certStores;
  
  private CertSelector certSelector;
  
  public PKIXParameters(Set<TrustAnchor> paramSet) throws InvalidAlgorithmParameterException {
    setTrustAnchors(paramSet);
    this.unmodInitialPolicies = Collections.emptySet();
    this.certPathCheckers = new ArrayList();
    this.certStores = new ArrayList();
  }
  
  public PKIXParameters(KeyStore paramKeyStore) throws KeyStoreException, InvalidAlgorithmParameterException {
    if (paramKeyStore == null)
      throw new NullPointerException("the keystore parameter must be non-null"); 
    HashSet hashSet = new HashSet();
    Enumeration enumeration = paramKeyStore.aliases();
    while (enumeration.hasMoreElements()) {
      String str = (String)enumeration.nextElement();
      if (paramKeyStore.isCertificateEntry(str)) {
        Certificate certificate = paramKeyStore.getCertificate(str);
        if (certificate instanceof X509Certificate)
          hashSet.add(new TrustAnchor((X509Certificate)certificate, null)); 
      } 
    } 
    setTrustAnchors(hashSet);
    this.unmodInitialPolicies = Collections.emptySet();
    this.certPathCheckers = new ArrayList();
    this.certStores = new ArrayList();
  }
  
  public Set<TrustAnchor> getTrustAnchors() { return this.unmodTrustAnchors; }
  
  public void setTrustAnchors(Set<TrustAnchor> paramSet) throws InvalidAlgorithmParameterException {
    if (paramSet == null)
      throw new NullPointerException("the trustAnchors parameters must be non-null"); 
    if (paramSet.isEmpty())
      throw new InvalidAlgorithmParameterException("the trustAnchors parameter must be non-empty"); 
    Iterator iterator = paramSet.iterator();
    while (iterator.hasNext()) {
      if (!(iterator.next() instanceof TrustAnchor))
        throw new ClassCastException("all elements of set must be of type java.security.cert.TrustAnchor"); 
    } 
    this.unmodTrustAnchors = Collections.unmodifiableSet(new HashSet(paramSet));
  }
  
  public Set<String> getInitialPolicies() { return this.unmodInitialPolicies; }
  
  public void setInitialPolicies(Set<String> paramSet) {
    if (paramSet != null) {
      Iterator iterator = paramSet.iterator();
      while (iterator.hasNext()) {
        if (!(iterator.next() instanceof String))
          throw new ClassCastException("all elements of set must be of type java.lang.String"); 
      } 
      this.unmodInitialPolicies = Collections.unmodifiableSet(new HashSet(paramSet));
    } else {
      this.unmodInitialPolicies = Collections.emptySet();
    } 
  }
  
  public void setCertStores(List<CertStore> paramList) {
    if (paramList == null) {
      this.certStores = new ArrayList();
    } else {
      Iterator iterator = paramList.iterator();
      while (iterator.hasNext()) {
        if (!(iterator.next() instanceof CertStore))
          throw new ClassCastException("all elements of list must be of type java.security.cert.CertStore"); 
      } 
      this.certStores = new ArrayList(paramList);
    } 
  }
  
  public void addCertStore(CertStore paramCertStore) {
    if (paramCertStore != null)
      this.certStores.add(paramCertStore); 
  }
  
  public List<CertStore> getCertStores() { return Collections.unmodifiableList(new ArrayList(this.certStores)); }
  
  public void setRevocationEnabled(boolean paramBoolean) { this.revocationEnabled = paramBoolean; }
  
  public boolean isRevocationEnabled() { return this.revocationEnabled; }
  
  public void setExplicitPolicyRequired(boolean paramBoolean) { this.explicitPolicyRequired = paramBoolean; }
  
  public boolean isExplicitPolicyRequired() { return this.explicitPolicyRequired; }
  
  public void setPolicyMappingInhibited(boolean paramBoolean) { this.policyMappingInhibited = paramBoolean; }
  
  public boolean isPolicyMappingInhibited() { return this.policyMappingInhibited; }
  
  public void setAnyPolicyInhibited(boolean paramBoolean) { this.anyPolicyInhibited = paramBoolean; }
  
  public boolean isAnyPolicyInhibited() { return this.anyPolicyInhibited; }
  
  public void setPolicyQualifiersRejected(boolean paramBoolean) { this.policyQualifiersRejected = paramBoolean; }
  
  public boolean getPolicyQualifiersRejected() { return this.policyQualifiersRejected; }
  
  public Date getDate() { return (this.date == null) ? null : (Date)this.date.clone(); }
  
  public void setDate(Date paramDate) {
    if (paramDate != null) {
      this.date = (Date)paramDate.clone();
    } else {
      paramDate = null;
    } 
  }
  
  public void setCertPathCheckers(List<PKIXCertPathChecker> paramList) {
    if (paramList != null) {
      ArrayList arrayList = new ArrayList();
      for (PKIXCertPathChecker pKIXCertPathChecker : paramList)
        arrayList.add((PKIXCertPathChecker)pKIXCertPathChecker.clone()); 
      this.certPathCheckers = arrayList;
    } else {
      this.certPathCheckers = new ArrayList();
    } 
  }
  
  public List<PKIXCertPathChecker> getCertPathCheckers() {
    ArrayList arrayList = new ArrayList();
    for (PKIXCertPathChecker pKIXCertPathChecker : this.certPathCheckers)
      arrayList.add((PKIXCertPathChecker)pKIXCertPathChecker.clone()); 
    return Collections.unmodifiableList(arrayList);
  }
  
  public void addCertPathChecker(PKIXCertPathChecker paramPKIXCertPathChecker) {
    if (paramPKIXCertPathChecker != null)
      this.certPathCheckers.add((PKIXCertPathChecker)paramPKIXCertPathChecker.clone()); 
  }
  
  public String getSigProvider() { return this.sigProvider; }
  
  public void setSigProvider(String paramString) { this.sigProvider = paramString; }
  
  public CertSelector getTargetCertConstraints() { return (this.certSelector != null) ? (CertSelector)this.certSelector.clone() : null; }
  
  public void setTargetCertConstraints(CertSelector paramCertSelector) {
    if (paramCertSelector != null) {
      this.certSelector = (CertSelector)paramCertSelector.clone();
    } else {
      this.certSelector = null;
    } 
  }
  
  public Object clone() {
    try {
      PKIXParameters pKIXParameters = (PKIXParameters)super.clone();
      if (this.certStores != null)
        pKIXParameters.certStores = new ArrayList(this.certStores); 
      if (this.certPathCheckers != null) {
        pKIXParameters.certPathCheckers = new ArrayList(this.certPathCheckers.size());
        for (PKIXCertPathChecker pKIXCertPathChecker : this.certPathCheckers)
          pKIXParameters.certPathCheckers.add((PKIXCertPathChecker)pKIXCertPathChecker.clone()); 
      } 
      return pKIXParameters;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException.toString(), cloneNotSupportedException);
    } 
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[\n");
    if (this.unmodTrustAnchors != null)
      stringBuffer.append("  Trust Anchors: " + this.unmodTrustAnchors.toString() + "\n"); 
    if (this.unmodInitialPolicies != null)
      if (this.unmodInitialPolicies.isEmpty()) {
        stringBuffer.append("  Initial Policy OIDs: any\n");
      } else {
        stringBuffer.append("  Initial Policy OIDs: [" + this.unmodInitialPolicies.toString() + "]\n");
      }  
    stringBuffer.append("  Validity Date: " + String.valueOf(this.date) + "\n");
    stringBuffer.append("  Signature Provider: " + String.valueOf(this.sigProvider) + "\n");
    stringBuffer.append("  Default Revocation Enabled: " + this.revocationEnabled + "\n");
    stringBuffer.append("  Explicit Policy Required: " + this.explicitPolicyRequired + "\n");
    stringBuffer.append("  Policy Mapping Inhibited: " + this.policyMappingInhibited + "\n");
    stringBuffer.append("  Any Policy Inhibited: " + this.anyPolicyInhibited + "\n");
    stringBuffer.append("  Policy Qualifiers Rejected: " + this.policyQualifiersRejected + "\n");
    stringBuffer.append("  Target Cert Constraints: " + String.valueOf(this.certSelector) + "\n");
    if (this.certPathCheckers != null)
      stringBuffer.append("  Certification Path Checkers: [" + this.certPathCheckers.toString() + "]\n"); 
    if (this.certStores != null)
      stringBuffer.append("  CertStores: [" + this.certStores.toString() + "]\n"); 
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\cert\PKIXParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */