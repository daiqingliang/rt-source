package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class KRBSafeBody {
  public byte[] userData = null;
  
  public KerberosTime timestamp;
  
  public Integer usec;
  
  public Integer seqNumber;
  
  public HostAddress sAddress;
  
  public HostAddress rAddress;
  
  public KRBSafeBody(byte[] paramArrayOfByte, KerberosTime paramKerberosTime, Integer paramInteger1, Integer paramInteger2, HostAddress paramHostAddress1, HostAddress paramHostAddress2) {
    if (paramArrayOfByte != null)
      this.userData = (byte[])paramArrayOfByte.clone(); 
    this.timestamp = paramKerberosTime;
    this.usec = paramInteger1;
    this.seqNumber = paramInteger2;
    this.sAddress = paramHostAddress1;
    this.rAddress = paramHostAddress2;
  }
  
  public KRBSafeBody(DerValue paramDerValue) throws Asn1Exception, IOException {
    if (paramDerValue.getTag() != 48)
      throw new Asn1Exception(906); 
    DerValue derValue = paramDerValue.getData().getDerValue();
    if ((derValue.getTag() & 0x1F) == 0) {
      this.userData = derValue.getData().getOctetString();
    } else {
      throw new Asn1Exception(906);
    } 
    this.timestamp = KerberosTime.parse(paramDerValue.getData(), (byte)1, true);
    if ((paramDerValue.getData().peekByte() & 0x1F) == 2) {
      derValue = paramDerValue.getData().getDerValue();
      this.usec = new Integer(derValue.getData().getBigInteger().intValue());
    } 
    if ((paramDerValue.getData().peekByte() & 0x1F) == 3) {
      derValue = paramDerValue.getData().getDerValue();
      this.seqNumber = new Integer(derValue.getData().getBigInteger().intValue());
    } 
    this.sAddress = HostAddress.parse(paramDerValue.getData(), (byte)4, false);
    if (paramDerValue.getData().available() > 0)
      this.rAddress = HostAddress.parse(paramDerValue.getData(), (byte)5, true); 
    if (paramDerValue.getData().available() > 0)
      throw new Asn1Exception(906); 
  }
  
  public byte[] asn1Encode() throws Asn1Exception, IOException {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream2.putOctetString(this.userData);
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)0), derOutputStream2);
    if (this.timestamp != null)
      derOutputStream1.write(DerValue.createTag(-128, true, (byte)1), this.timestamp.asn1Encode()); 
    if (this.usec != null) {
      derOutputStream2 = new DerOutputStream();
      derOutputStream2.putInteger(BigInteger.valueOf(this.usec.intValue()));
      derOutputStream1.write(DerValue.createTag(-128, true, (byte)2), derOutputStream2);
    } 
    if (this.seqNumber != null) {
      derOutputStream2 = new DerOutputStream();
      derOutputStream2.putInteger(BigInteger.valueOf(this.seqNumber.longValue()));
      derOutputStream1.write(DerValue.createTag(-128, true, (byte)3), derOutputStream2);
    } 
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)4), this.sAddress.asn1Encode());
    if (this.rAddress != null)
      derOutputStream2 = new DerOutputStream(); 
    derOutputStream2.write((byte)48, derOutputStream1);
    return derOutputStream2.toByteArray();
  }
  
  public static KRBSafeBody parse(DerInputStream paramDerInputStream, byte paramByte, boolean paramBoolean) throws Asn1Exception, IOException {
    if (paramBoolean && ((byte)paramDerInputStream.peekByte() & 0x1F) != paramByte)
      return null; 
    DerValue derValue1 = paramDerInputStream.getDerValue();
    if (paramByte != (derValue1.getTag() & 0x1F))
      throw new Asn1Exception(906); 
    DerValue derValue2 = derValue1.getData().getDerValue();
    return new KRBSafeBody(derValue2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\KRBSafeBody.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */