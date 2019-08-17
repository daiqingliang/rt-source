package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptedData;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class KRBPriv {
  public int pvno;
  
  public int msgType;
  
  public EncryptedData encPart;
  
  public KRBPriv(EncryptedData paramEncryptedData) {
    this.pvno = 5;
    this.msgType = 21;
    this.encPart = paramEncryptedData;
  }
  
  public KRBPriv(byte[] paramArrayOfByte) throws Asn1Exception, KrbApErrException, IOException { init(new DerValue(paramArrayOfByte)); }
  
  public KRBPriv(DerValue paramDerValue) throws Asn1Exception, KrbApErrException, IOException { init(paramDerValue); }
  
  private void init(DerValue paramDerValue) throws Asn1Exception, KrbApErrException, IOException {
    if ((paramDerValue.getTag() & 0x1F) != 21 || paramDerValue.isApplication() != true || paramDerValue.isConstructed() != true)
      throw new Asn1Exception(906); 
    DerValue derValue1 = paramDerValue.getData().getDerValue();
    if (derValue1.getTag() != 48)
      throw new Asn1Exception(906); 
    DerValue derValue2 = derValue1.getData().getDerValue();
    if ((derValue2.getTag() & 0x1F) == 0) {
      this.pvno = derValue2.getData().getBigInteger().intValue();
      if (this.pvno != 5)
        throw new KrbApErrException(39); 
    } else {
      throw new Asn1Exception(906);
    } 
    derValue2 = derValue1.getData().getDerValue();
    if ((derValue2.getTag() & 0x1F) == 1) {
      this.msgType = derValue2.getData().getBigInteger().intValue();
      if (this.msgType != 21)
        throw new KrbApErrException(40); 
    } else {
      throw new Asn1Exception(906);
    } 
    this.encPart = EncryptedData.parse(derValue1.getData(), (byte)3, false);
    if (derValue1.getData().available() > 0)
      throw new Asn1Exception(906); 
  }
  
  public byte[] asn1Encode() throws Asn1Exception, IOException {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    derOutputStream1.putInteger(BigInteger.valueOf(this.pvno));
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream2.write(DerValue.createTag(-128, true, (byte)0), derOutputStream1);
    derOutputStream1 = new DerOutputStream();
    derOutputStream1.putInteger(BigInteger.valueOf(this.msgType));
    derOutputStream2.write(DerValue.createTag(-128, true, (byte)1), derOutputStream1);
    derOutputStream2.write(DerValue.createTag(-128, true, (byte)3), this.encPart.asn1Encode());
    derOutputStream1 = new DerOutputStream();
    derOutputStream1.write((byte)48, derOutputStream2);
    derOutputStream2 = new DerOutputStream();
    derOutputStream2.write(DerValue.createTag((byte)64, true, (byte)21), derOutputStream1);
    return derOutputStream2.toByteArray();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\KRBPriv.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */