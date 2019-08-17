package sun.security.x509;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class SerialNumber {
  private BigInteger serialNum;
  
  private void construct(DerValue paramDerValue) throws IOException {
    this.serialNum = paramDerValue.getBigInteger();
    if (paramDerValue.data.available() != 0)
      throw new IOException("Excess SerialNumber data"); 
  }
  
  public SerialNumber(BigInteger paramBigInteger) { this.serialNum = paramBigInteger; }
  
  public SerialNumber(int paramInt) { this.serialNum = BigInteger.valueOf(paramInt); }
  
  public SerialNumber(DerInputStream paramDerInputStream) throws IOException {
    DerValue derValue = paramDerInputStream.getDerValue();
    construct(derValue);
  }
  
  public SerialNumber(DerValue paramDerValue) throws IOException { construct(paramDerValue); }
  
  public SerialNumber(InputStream paramInputStream) throws IOException {
    DerValue derValue = new DerValue(paramInputStream);
    construct(derValue);
  }
  
  public String toString() { return "SerialNumber: [" + Debug.toHexString(this.serialNum) + "]"; }
  
  public void encode(DerOutputStream paramDerOutputStream) throws IOException { paramDerOutputStream.putInteger(this.serialNum); }
  
  public BigInteger getNumber() { return this.serialNum; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\SerialNumber.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */