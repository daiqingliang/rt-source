package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.Checksum;
import sun.security.krb5.RealmException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class KRBSafe {
  public int pvno;
  
  public int msgType;
  
  public KRBSafeBody safeBody;
  
  public Checksum cksum;
  
  public KRBSafe(KRBSafeBody paramKRBSafeBody, Checksum paramChecksum) {
    this.pvno = 5;
    this.msgType = 20;
    this.safeBody = paramKRBSafeBody;
    this.cksum = paramChecksum;
  }
  
  public KRBSafe(byte[] paramArrayOfByte) throws Asn1Exception, RealmException, KrbApErrException, IOException { init(new DerValue(paramArrayOfByte)); }
  
  public KRBSafe(DerValue paramDerValue) throws Asn1Exception, RealmException, KrbApErrException, IOException { init(paramDerValue); }
  
  private void init(DerValue paramDerValue) throws Asn1Exception, RealmException, KrbApErrException, IOException {
    if ((paramDerValue.getTag() & 0x1F) != 20 || paramDerValue.isApplication() != true || paramDerValue.isConstructed() != true)
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
      if (this.msgType != 20)
        throw new KrbApErrException(40); 
    } else {
      throw new Asn1Exception(906);
    } 
    this.safeBody = KRBSafeBody.parse(derValue1.getData(), (byte)2, false);
    this.cksum = Checksum.parse(derValue1.getData(), (byte)3, false);
    if (derValue1.getData().available() > 0)
      throw new Asn1Exception(906); 
  }
  
  public byte[] asn1Encode() throws Asn1Exception, IOException {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream1.putInteger(BigInteger.valueOf(this.pvno));
    derOutputStream2.write(DerValue.createTag(-128, true, (byte)0), derOutputStream1);
    derOutputStream1 = new DerOutputStream();
    derOutputStream1.putInteger(BigInteger.valueOf(this.msgType));
    derOutputStream2.write(DerValue.createTag(-128, true, (byte)1), derOutputStream1);
    derOutputStream2.write(DerValue.createTag(-128, true, (byte)2), this.safeBody.asn1Encode());
    derOutputStream2.write(DerValue.createTag(-128, true, (byte)3), this.cksum.asn1Encode());
    derOutputStream1 = new DerOutputStream();
    derOutputStream1.write((byte)48, derOutputStream2);
    derOutputStream2 = new DerOutputStream();
    derOutputStream2.write(DerValue.createTag((byte)64, true, (byte)20), derOutputStream1);
    return derOutputStream2.toByteArray();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\KRBSafe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */