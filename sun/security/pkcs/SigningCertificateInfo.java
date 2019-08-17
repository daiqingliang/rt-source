package sun.security.pkcs;

import java.io.IOException;
import sun.security.util.DerValue;

public class SigningCertificateInfo {
  private byte[] ber = null;
  
  private ESSCertId[] certId = null;
  
  public SigningCertificateInfo(byte[] paramArrayOfByte) throws IOException { parse(paramArrayOfByte); }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[\n");
    for (byte b = 0; b < this.certId.length; b++)
      stringBuffer.append(this.certId[b].toString()); 
    stringBuffer.append("\n]");
    return stringBuffer.toString();
  }
  
  public void parse(byte[] paramArrayOfByte) throws IOException {
    DerValue derValue = new DerValue(paramArrayOfByte);
    if (derValue.tag != 48)
      throw new IOException("Bad encoding for signingCertificate"); 
    DerValue[] arrayOfDerValue = derValue.data.getSequence(1);
    this.certId = new ESSCertId[arrayOfDerValue.length];
    for (byte b = 0; b < arrayOfDerValue.length; b++)
      this.certId[b] = new ESSCertId(arrayOfDerValue[b]); 
    if (derValue.data.available() > 0) {
      DerValue[] arrayOfDerValue1 = derValue.data.getSequence(1);
      for (byte b1 = 0; b1 < arrayOfDerValue1.length; b1++);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\pkcs\SigningCertificateInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */