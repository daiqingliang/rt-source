package sun.security.pkcs;

import java.io.IOException;
import sun.misc.HexDumpEncoder;
import sun.security.util.DerValue;
import sun.security.x509.GeneralNames;
import sun.security.x509.SerialNumber;

class ESSCertId {
  private byte[] certHash;
  
  private GeneralNames issuer;
  
  private SerialNumber serialNumber;
  
  ESSCertId(DerValue paramDerValue) throws IOException {
    this.certHash = paramDerValue.data.getDerValue().toByteArray();
    if (paramDerValue.data.available() > 0) {
      DerValue derValue = paramDerValue.data.getDerValue();
      this.issuer = new GeneralNames(derValue.data.getDerValue());
      this.serialNumber = new SerialNumber(derValue.data.getDerValue());
    } 
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[\n\tCertificate hash (SHA-1):\n");
    if (hexDumper == null)
      hexDumper = new HexDumpEncoder(); 
    stringBuffer.append(hexDumper.encode(this.certHash));
    if (this.issuer != null && this.serialNumber != null) {
      stringBuffer.append("\n\tIssuer: " + this.issuer + "\n");
      stringBuffer.append("\t" + this.serialNumber);
    } 
    stringBuffer.append("\n]");
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\pkcs\ESSCertId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */