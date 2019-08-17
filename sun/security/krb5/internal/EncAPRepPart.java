package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Vector;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptionKey;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class EncAPRepPart {
  public KerberosTime ctime;
  
  public int cusec;
  
  EncryptionKey subKey;
  
  Integer seqNumber;
  
  public EncAPRepPart(KerberosTime paramKerberosTime, int paramInt, EncryptionKey paramEncryptionKey, Integer paramInteger) {
    this.ctime = paramKerberosTime;
    this.cusec = paramInt;
    this.subKey = paramEncryptionKey;
    this.seqNumber = paramInteger;
  }
  
  public EncAPRepPart(byte[] paramArrayOfByte) throws Asn1Exception, IOException { init(new DerValue(paramArrayOfByte)); }
  
  public EncAPRepPart(DerValue paramDerValue) throws Asn1Exception, IOException { init(paramDerValue); }
  
  private void init(DerValue paramDerValue) throws Asn1Exception, IOException {
    if ((paramDerValue.getTag() & 0x1F) != 27 || paramDerValue.isApplication() != true || paramDerValue.isConstructed() != true)
      throw new Asn1Exception(906); 
    DerValue derValue1 = paramDerValue.getData().getDerValue();
    if (derValue1.getTag() != 48)
      throw new Asn1Exception(906); 
    this.ctime = KerberosTime.parse(derValue1.getData(), (byte)0, true);
    DerValue derValue2 = derValue1.getData().getDerValue();
    if ((derValue2.getTag() & 0x1F) == 1) {
      this.cusec = derValue2.getData().getBigInteger().intValue();
    } else {
      throw new Asn1Exception(906);
    } 
    if (derValue1.getData().available() > 0) {
      this.subKey = EncryptionKey.parse(derValue1.getData(), (byte)2, true);
    } else {
      this.subKey = null;
      this.seqNumber = null;
    } 
    if (derValue1.getData().available() > 0) {
      derValue2 = derValue1.getData().getDerValue();
      if ((derValue2.getTag() & 0x1F) != 3)
        throw new Asn1Exception(906); 
      this.seqNumber = new Integer(derValue2.getData().getBigInteger().intValue());
    } else {
      this.seqNumber = null;
    } 
    if (derValue1.getData().available() > 0)
      throw new Asn1Exception(906); 
  }
  
  public byte[] asn1Encode() throws Asn1Exception, IOException {
    Vector vector = new Vector();
    DerOutputStream derOutputStream1 = new DerOutputStream();
    vector.addElement(new DerValue(DerValue.createTag(-128, true, (byte)0), this.ctime.asn1Encode()));
    derOutputStream1.putInteger(BigInteger.valueOf(this.cusec));
    vector.addElement(new DerValue(DerValue.createTag(-128, true, (byte)1), derOutputStream1.toByteArray()));
    if (this.subKey != null)
      vector.addElement(new DerValue(DerValue.createTag(-128, true, (byte)2), this.subKey.asn1Encode())); 
    if (this.seqNumber != null) {
      derOutputStream1 = new DerOutputStream();
      derOutputStream1.putInteger(BigInteger.valueOf(this.seqNumber.longValue()));
      vector.addElement(new DerValue(DerValue.createTag(-128, true, (byte)3), derOutputStream1.toByteArray()));
    } 
    DerValue[] arrayOfDerValue = new DerValue[vector.size()];
    vector.copyInto(arrayOfDerValue);
    derOutputStream1 = new DerOutputStream();
    derOutputStream1.putSequence(arrayOfDerValue);
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream2.write(DerValue.createTag((byte)64, true, (byte)27), derOutputStream1);
    return derOutputStream2.toByteArray();
  }
  
  public final EncryptionKey getSubKey() { return this.subKey; }
  
  public final Integer getSeqNumber() { return this.seqNumber; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\EncAPRepPart.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */