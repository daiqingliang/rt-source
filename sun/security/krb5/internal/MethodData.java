package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class MethodData {
  private int methodType;
  
  private byte[] methodData = null;
  
  public MethodData(int paramInt, byte[] paramArrayOfByte) {
    this.methodType = paramInt;
    if (paramArrayOfByte != null)
      this.methodData = (byte[])paramArrayOfByte.clone(); 
  }
  
  public MethodData(DerValue paramDerValue) throws Asn1Exception, IOException {
    if (paramDerValue.getTag() != 48)
      throw new Asn1Exception(906); 
    DerValue derValue = paramDerValue.getData().getDerValue();
    if ((derValue.getTag() & 0x1F) == 0) {
      BigInteger bigInteger = derValue.getData().getBigInteger();
      this.methodType = bigInteger.intValue();
    } else {
      throw new Asn1Exception(906);
    } 
    if (paramDerValue.getData().available() > 0) {
      derValue = paramDerValue.getData().getDerValue();
      if ((derValue.getTag() & 0x1F) == 1) {
        this.methodData = derValue.getData().getOctetString();
      } else {
        throw new Asn1Exception(906);
      } 
    } 
    if (paramDerValue.getData().available() > 0)
      throw new Asn1Exception(906); 
  }
  
  public byte[] asn1Encode() throws Asn1Exception, IOException {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream2.putInteger(BigInteger.valueOf(this.methodType));
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)0), derOutputStream2);
    if (this.methodData != null) {
      derOutputStream2 = new DerOutputStream();
      derOutputStream2.putOctetString(this.methodData);
      derOutputStream1.write(DerValue.createTag(-128, true, (byte)1), derOutputStream2);
    } 
    derOutputStream2 = new DerOutputStream();
    derOutputStream2.write((byte)48, derOutputStream1);
    return derOutputStream2.toByteArray();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\MethodData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */