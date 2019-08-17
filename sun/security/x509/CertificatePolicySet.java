package sun.security.x509;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class CertificatePolicySet {
  private final Vector<CertificatePolicyId> ids;
  
  public CertificatePolicySet(Vector<CertificatePolicyId> paramVector) { this.ids = paramVector; }
  
  public CertificatePolicySet(DerInputStream paramDerInputStream) throws IOException {
    this.ids = new Vector();
    DerValue[] arrayOfDerValue = paramDerInputStream.getSequence(5);
    for (byte b = 0; b < arrayOfDerValue.length; b++) {
      CertificatePolicyId certificatePolicyId = new CertificatePolicyId(arrayOfDerValue[b]);
      this.ids.addElement(certificatePolicyId);
    } 
  }
  
  public String toString() { return "CertificatePolicySet:[\n" + this.ids.toString() + "]\n"; }
  
  public void encode(DerOutputStream paramDerOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    for (byte b = 0; b < this.ids.size(); b++)
      ((CertificatePolicyId)this.ids.elementAt(b)).encode(derOutputStream); 
    paramDerOutputStream.write((byte)48, derOutputStream);
  }
  
  public List<CertificatePolicyId> getCertPolicyIds() { return Collections.unmodifiableList(this.ids); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\CertificatePolicySet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */