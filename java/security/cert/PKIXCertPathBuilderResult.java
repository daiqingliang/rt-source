package java.security.cert;

import java.security.PublicKey;

public class PKIXCertPathBuilderResult extends PKIXCertPathValidatorResult implements CertPathBuilderResult {
  private CertPath certPath;
  
  public PKIXCertPathBuilderResult(CertPath paramCertPath, TrustAnchor paramTrustAnchor, PolicyNode paramPolicyNode, PublicKey paramPublicKey) {
    super(paramTrustAnchor, paramPolicyNode, paramPublicKey);
    if (paramCertPath == null)
      throw new NullPointerException("certPath must be non-null"); 
    this.certPath = paramCertPath;
  }
  
  public CertPath getCertPath() { return this.certPath; }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("PKIXCertPathBuilderResult: [\n");
    stringBuffer.append("  Certification Path: " + this.certPath + "\n");
    stringBuffer.append("  Trust Anchor: " + getTrustAnchor().toString() + "\n");
    stringBuffer.append("  Policy Tree: " + String.valueOf(getPolicyTree()) + "\n");
    stringBuffer.append("  Subject Public Key: " + getPublicKey() + "\n");
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\cert\PKIXCertPathBuilderResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */