package sun.security.x509;

import java.io.IOException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class CertificatePolicyId {
  private ObjectIdentifier id;
  
  public CertificatePolicyId(ObjectIdentifier paramObjectIdentifier) { this.id = paramObjectIdentifier; }
  
  public CertificatePolicyId(DerValue paramDerValue) throws IOException { this.id = paramDerValue.getOID(); }
  
  public ObjectIdentifier getIdentifier() { return this.id; }
  
  public String toString() { return "CertificatePolicyId: [" + this.id.toString() + "]\n"; }
  
  public void encode(DerOutputStream paramDerOutputStream) throws IOException { paramDerOutputStream.putOID(this.id); }
  
  public boolean equals(Object paramObject) { return (paramObject instanceof CertificatePolicyId) ? this.id.equals(((CertificatePolicyId)paramObject).getIdentifier()) : 0; }
  
  public int hashCode() { return this.id.hashCode(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\CertificatePolicyId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */