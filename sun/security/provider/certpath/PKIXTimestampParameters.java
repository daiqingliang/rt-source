package sun.security.provider.certpath;

import java.security.InvalidAlgorithmParameterException;
import java.security.Timestamp;
import java.security.cert.CertSelector;
import java.security.cert.CertStore;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.TrustAnchor;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class PKIXTimestampParameters extends PKIXBuilderParameters {
  private final PKIXBuilderParameters p;
  
  private Timestamp jarTimestamp;
  
  public PKIXTimestampParameters(PKIXBuilderParameters paramPKIXBuilderParameters, Timestamp paramTimestamp) throws InvalidAlgorithmParameterException {
    super(paramPKIXBuilderParameters.getTrustAnchors(), null);
    this.p = paramPKIXBuilderParameters;
    this.jarTimestamp = paramTimestamp;
  }
  
  public Timestamp getTimestamp() { return this.jarTimestamp; }
  
  public void setTimestamp(Timestamp paramTimestamp) { this.jarTimestamp = paramTimestamp; }
  
  public void setDate(Date paramDate) { this.p.setDate(paramDate); }
  
  public void addCertPathChecker(PKIXCertPathChecker paramPKIXCertPathChecker) { this.p.addCertPathChecker(paramPKIXCertPathChecker); }
  
  public void setMaxPathLength(int paramInt) { this.p.setMaxPathLength(paramInt); }
  
  public int getMaxPathLength() { return this.p.getMaxPathLength(); }
  
  public String toString() { return this.p.toString(); }
  
  public Set<TrustAnchor> getTrustAnchors() { return this.p.getTrustAnchors(); }
  
  public void setTrustAnchors(Set<TrustAnchor> paramSet) throws InvalidAlgorithmParameterException {
    if (this.p == null)
      return; 
    this.p.setTrustAnchors(paramSet);
  }
  
  public Set<String> getInitialPolicies() { return this.p.getInitialPolicies(); }
  
  public void setInitialPolicies(Set<String> paramSet) { this.p.setInitialPolicies(paramSet); }
  
  public void setCertStores(List<CertStore> paramList) { this.p.setCertStores(paramList); }
  
  public void addCertStore(CertStore paramCertStore) { this.p.addCertStore(paramCertStore); }
  
  public List<CertStore> getCertStores() { return this.p.getCertStores(); }
  
  public void setRevocationEnabled(boolean paramBoolean) { this.p.setRevocationEnabled(paramBoolean); }
  
  public boolean isRevocationEnabled() { return this.p.isRevocationEnabled(); }
  
  public void setExplicitPolicyRequired(boolean paramBoolean) { this.p.setExplicitPolicyRequired(paramBoolean); }
  
  public boolean isExplicitPolicyRequired() { return this.p.isExplicitPolicyRequired(); }
  
  public void setPolicyMappingInhibited(boolean paramBoolean) { this.p.setPolicyMappingInhibited(paramBoolean); }
  
  public boolean isPolicyMappingInhibited() { return this.p.isPolicyMappingInhibited(); }
  
  public void setAnyPolicyInhibited(boolean paramBoolean) { this.p.setAnyPolicyInhibited(paramBoolean); }
  
  public boolean isAnyPolicyInhibited() { return this.p.isAnyPolicyInhibited(); }
  
  public void setPolicyQualifiersRejected(boolean paramBoolean) { this.p.setPolicyQualifiersRejected(paramBoolean); }
  
  public boolean getPolicyQualifiersRejected() { return this.p.getPolicyQualifiersRejected(); }
  
  public Date getDate() { return this.p.getDate(); }
  
  public void setCertPathCheckers(List<PKIXCertPathChecker> paramList) { this.p.setCertPathCheckers(paramList); }
  
  public List<PKIXCertPathChecker> getCertPathCheckers() { return this.p.getCertPathCheckers(); }
  
  public String getSigProvider() { return this.p.getSigProvider(); }
  
  public void setSigProvider(String paramString) { this.p.setSigProvider(paramString); }
  
  public CertSelector getTargetCertConstraints() { return this.p.getTargetCertConstraints(); }
  
  public void setTargetCertConstraints(CertSelector paramCertSelector) {
    if (this.p == null)
      return; 
    this.p.setTargetCertConstraints(paramCertSelector);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\PKIXTimestampParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */