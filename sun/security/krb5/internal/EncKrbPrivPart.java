package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class EncKrbPrivPart {
  public byte[] userData = null;
  
  public KerberosTime timestamp;
  
  public Integer usec;
  
  public Integer seqNumber;
  
  public HostAddress sAddress;
  
  public HostAddress rAddress;
  
  public EncKrbPrivPart(byte[] paramArrayOfByte, KerberosTime paramKerberosTime, Integer paramInteger1, Integer paramInteger2, HostAddress paramHostAddress1, HostAddress paramHostAddress2) {
    if (paramArrayOfByte != null)
      this.userData = (byte[])paramArrayOfByte.clone(); 
    this.timestamp = paramKerberosTime;
    this.usec = paramInteger1;
    this.seqNumber = paramInteger2;
    this.sAddress = paramHostAddress1;
    this.rAddress = paramHostAddress2;
  }
  
  public EncKrbPrivPart(byte[] paramArrayOfByte) throws Asn1Exception, IOException { init(new DerValue(paramArrayOfByte)); }
  
  public EncKrbPrivPart(DerValue paramDerValue) throws Asn1Exception, IOException { init(paramDerValue); }
  
  private void init(DerValue paramDerValue) throws Asn1Exception, IOException {
    if ((paramDerValue.getTag() & 0x1F) != 28 || paramDerValue.isApplication() != true || paramDerValue.isConstructed() != true)
      throw new Asn1Exception(906); 
    DerValue derValue1 = paramDerValue.getData().getDerValue();
    if (derValue1.getTag() != 48)
      throw new Asn1Exception(906); 
    DerValue derValue2 = derValue1.getData().getDerValue();
    if ((derValue2.getTag() & 0x1F) == 0) {
      this.userData = derValue2.getData().getOctetString();
    } else {
      throw new Asn1Exception(906);
    } 
    this.timestamp = KerberosTime.parse(derValue1.getData(), (byte)1, true);
    if ((derValue1.getData().peekByte() & 0x1F) == 2) {
      derValue2 = derValue1.getData().getDerValue();
      this.usec = new Integer(derValue2.getData().getBigInteger().intValue());
    } else {
      this.usec = null;
    } 
    if ((derValue1.getData().peekByte() & 0x1F) == 3) {
      derValue2 = derValue1.getData().getDerValue();
      this.seqNumber = new Integer(derValue2.getData().getBigInteger().intValue());
    } else {
      this.seqNumber = null;
    } 
    this.sAddress = HostAddress.parse(derValue1.getData(), (byte)4, false);
    if (derValue1.getData().available() > 0)
      this.rAddress = HostAddress.parse(derValue1.getData(), (byte)5, true); 
    if (derValue1.getData().available() > 0)
      throw new Asn1Exception(906); 
  }
  
  public byte[] asn1Encode() throws Asn1Exception, IOException {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream1.putOctetString(this.userData);
    derOutputStream2.write(DerValue.createTag(-128, true, (byte)0), derOutputStream1);
    if (this.timestamp != null)
      derOutputStream2.write(DerValue.createTag(-128, true, (byte)1), this.timestamp.asn1Encode()); 
    if (this.usec != null) {
      derOutputStream1 = new DerOutputStream();
      derOutputStream1.putInteger(BigInteger.valueOf(this.usec.intValue()));
      derOutputStream2.write(DerValue.createTag(-128, true, (byte)2), derOutputStream1);
    } 
    if (this.seqNumber != null) {
      derOutputStream1 = new DerOutputStream();
      derOutputStream1.putInteger(BigInteger.valueOf(this.seqNumber.longValue()));
      derOutputStream2.write(DerValue.createTag(-128, true, (byte)3), derOutputStream1);
    } 
    derOutputStream2.write(DerValue.createTag(-128, true, (byte)4), this.sAddress.asn1Encode());
    if (this.rAddress != null)
      derOutputStream2.write(DerValue.createTag(-128, true, (byte)5), this.rAddress.asn1Encode()); 
    derOutputStream1 = new DerOutputStream();
    derOutputStream1.write((byte)48, derOutputStream2);
    derOutputStream2 = new DerOutputStream();
    derOutputStream2.write(DerValue.createTag((byte)64, true, (byte)28), derOutputStream1);
    return derOutputStream2.toByteArray();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\EncKrbPrivPart.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */