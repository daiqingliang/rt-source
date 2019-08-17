package java.security.cert;

import java.security.PublicKey;

public class PKIXCertPathValidatorResult implements CertPathValidatorResult {
  private TrustAnchor trustAnchor;
  
  private PolicyNode policyTree;
  
  private PublicKey subjectPublicKey;
  
  public PKIXCertPathValidatorResult(TrustAnchor paramTrustAnchor, PolicyNode paramPolicyNode, PublicKey paramPublicKey) {
    if (paramPublicKey == null)
      throw new NullPointerException("subjectPublicKey must be non-null"); 
    if (paramTrustAnchor == null)
      throw new NullPointerException("trustAnchor must be non-null"); 
    this.trustAnchor = paramTrustAnchor;
    this.policyTree = paramPolicyNode;
    this.subjectPublicKey = paramPublicKey;
  }
  
  public TrustAnchor getTrustAnchor() { return this.trustAnchor; }
  
  public PolicyNode getPolicyTree() { return this.policyTree; }
  
  public PublicKey getPublicKey() { return this.subjectPublicKey; }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException.toString(), cloneNotSupportedException);
    } 
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("PKIXCertPathValidatorResult: [\n");
    stringBuffer.append("  Trust Anchor: " + this.trustAnchor.toString() + "\n");
    stringBuffer.append("  Policy Tree: " + String.valueOf(this.policyTree) + "\n");
    stringBuffer.append("  Subject Public Key: " + this.subjectPublicKey + "\n");
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\cert\PKIXCertPathValidatorResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */