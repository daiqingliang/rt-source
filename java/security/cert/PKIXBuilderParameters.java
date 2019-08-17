package java.security.cert;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Set;

public class PKIXBuilderParameters extends PKIXParameters {
  private int maxPathLength = 5;
  
  public PKIXBuilderParameters(Set<TrustAnchor> paramSet, CertSelector paramCertSelector) throws InvalidAlgorithmParameterException {
    super(paramSet);
    setTargetCertConstraints(paramCertSelector);
  }
  
  public PKIXBuilderParameters(KeyStore paramKeyStore, CertSelector paramCertSelector) throws KeyStoreException, InvalidAlgorithmParameterException {
    super(paramKeyStore);
    setTargetCertConstraints(paramCertSelector);
  }
  
  public void setMaxPathLength(int paramInt) {
    if (paramInt < -1)
      throw new InvalidParameterException("the maximum path length parameter can not be less than -1"); 
    this.maxPathLength = paramInt;
  }
  
  public int getMaxPathLength() { return this.maxPathLength; }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[\n");
    stringBuffer.append(super.toString());
    stringBuffer.append("  Maximum Path Length: " + this.maxPathLength + "\n");
    stringBuffer.append("]\n");
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\cert\PKIXBuilderParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */