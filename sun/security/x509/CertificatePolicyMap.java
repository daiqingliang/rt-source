package sun.security.x509;

import java.io.IOException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class CertificatePolicyMap {
  private CertificatePolicyId issuerDomain;
  
  private CertificatePolicyId subjectDomain;
  
  public CertificatePolicyMap(CertificatePolicyId paramCertificatePolicyId1, CertificatePolicyId paramCertificatePolicyId2) {
    this.issuerDomain = paramCertificatePolicyId1;
    this.subjectDomain = paramCertificatePolicyId2;
  }
  
  public CertificatePolicyMap(DerValue paramDerValue) throws IOException {
    if (paramDerValue.tag != 48)
      throw new IOException("Invalid encoding for CertificatePolicyMap"); 
    this.issuerDomain = new CertificatePolicyId(paramDerValue.data.getDerValue());
    this.subjectDomain = new CertificatePolicyId(paramDerValue.data.getDerValue());
  }
  
  public CertificatePolicyId getIssuerIdentifier() { return this.issuerDomain; }
  
  public CertificatePolicyId getSubjectIdentifier() { return this.subjectDomain; }
  
  public String toString() { return "CertificatePolicyMap: [\nIssuerDomain:" + this.issuerDomain.toString() + "SubjectDomain:" + this.subjectDomain.toString() + "]\n"; }
  
  public void encode(DerOutputStream paramDerOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    this.issuerDomain.encode(derOutputStream);
    this.subjectDomain.encode(derOutputStream);
    paramDerOutputStream.write((byte)48, derOutputStream);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\CertificatePolicyMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */