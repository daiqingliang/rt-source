package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.RealmException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class EncKrbCredPart {
  public KrbCredInfo[] ticketInfo = null;
  
  public KerberosTime timeStamp;
  
  private Integer nonce;
  
  private Integer usec;
  
  private HostAddress sAddress;
  
  private HostAddresses rAddress;
  
  public EncKrbCredPart(KrbCredInfo[] paramArrayOfKrbCredInfo, KerberosTime paramKerberosTime, Integer paramInteger1, Integer paramInteger2, HostAddress paramHostAddress, HostAddresses paramHostAddresses) throws IOException {
    if (paramArrayOfKrbCredInfo != null) {
      this.ticketInfo = new KrbCredInfo[paramArrayOfKrbCredInfo.length];
      for (byte b = 0; b < paramArrayOfKrbCredInfo.length; b++) {
        if (paramArrayOfKrbCredInfo[b] == null)
          throw new IOException("Cannot create a EncKrbCredPart"); 
        this.ticketInfo[b] = (KrbCredInfo)paramArrayOfKrbCredInfo[b].clone();
      } 
    } 
    this.timeStamp = paramKerberosTime;
    this.usec = paramInteger1;
    this.nonce = paramInteger2;
    this.sAddress = paramHostAddress;
    this.rAddress = paramHostAddresses;
  }
  
  public EncKrbCredPart(byte[] paramArrayOfByte) throws Asn1Exception, IOException, RealmException { init(new DerValue(paramArrayOfByte)); }
  
  public EncKrbCredPart(DerValue paramDerValue) throws Asn1Exception, IOException, RealmException { init(paramDerValue); }
  
  private void init(DerValue paramDerValue) throws Asn1Exception, IOException, RealmException {
    this.nonce = null;
    this.timeStamp = null;
    this.usec = null;
    this.sAddress = null;
    this.rAddress = null;
    if ((paramDerValue.getTag() & 0x1F) != 29 || paramDerValue.isApplication() != true || paramDerValue.isConstructed() != true)
      throw new Asn1Exception(906); 
    DerValue derValue1 = paramDerValue.getData().getDerValue();
    if (derValue1.getTag() != 48)
      throw new Asn1Exception(906); 
    DerValue derValue2 = derValue1.getData().getDerValue();
    if ((derValue2.getTag() & 0x1F) == 0) {
      DerValue[] arrayOfDerValue = derValue2.getData().getSequence(1);
      this.ticketInfo = new KrbCredInfo[arrayOfDerValue.length];
      for (byte b = 0; b < arrayOfDerValue.length; b++)
        this.ticketInfo[b] = new KrbCredInfo(arrayOfDerValue[b]); 
    } else {
      throw new Asn1Exception(906);
    } 
    if (derValue1.getData().available() > 0 && ((byte)derValue1.getData().peekByte() & 0x1F) == 1) {
      derValue2 = derValue1.getData().getDerValue();
      this.nonce = new Integer(derValue2.getData().getBigInteger().intValue());
    } 
    if (derValue1.getData().available() > 0)
      this.timeStamp = KerberosTime.parse(derValue1.getData(), (byte)2, true); 
    if (derValue1.getData().available() > 0 && ((byte)derValue1.getData().peekByte() & 0x1F) == 3) {
      derValue2 = derValue1.getData().getDerValue();
      this.usec = new Integer(derValue2.getData().getBigInteger().intValue());
    } 
    if (derValue1.getData().available() > 0)
      this.sAddress = HostAddress.parse(derValue1.getData(), (byte)4, true); 
    if (derValue1.getData().available() > 0)
      this.rAddress = HostAddresses.parse(derValue1.getData(), (byte)5, true); 
    if (derValue1.getData().available() > 0)
      throw new Asn1Exception(906); 
  }
  
  public byte[] asn1Encode() throws Asn1Exception, IOException {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    DerValue[] arrayOfDerValue = new DerValue[this.ticketInfo.length];
    for (byte b = 0; b < this.ticketInfo.length; b++)
      arrayOfDerValue[b] = new DerValue(this.ticketInfo[b].asn1Encode()); 
    derOutputStream2.putSequence(arrayOfDerValue);
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)0), derOutputStream2);
    if (this.nonce != null) {
      derOutputStream2 = new DerOutputStream();
      derOutputStream2.putInteger(BigInteger.valueOf(this.nonce.intValue()));
      derOutputStream1.write(DerValue.createTag(-128, true, (byte)1), derOutputStream2);
    } 
    if (this.timeStamp != null)
      derOutputStream1.write(DerValue.createTag(-128, true, (byte)2), this.timeStamp.asn1Encode()); 
    if (this.usec != null) {
      derOutputStream2 = new DerOutputStream();
      derOutputStream2.putInteger(BigInteger.valueOf(this.usec.intValue()));
      derOutputStream1.write(DerValue.createTag(-128, true, (byte)3), derOutputStream2);
    } 
    if (this.sAddress != null)
      derOutputStream1.write(DerValue.createTag(-128, true, (byte)4), this.sAddress.asn1Encode()); 
    if (this.rAddress != null)
      derOutputStream1.write(DerValue.createTag(-128, true, (byte)5), this.rAddress.asn1Encode()); 
    derOutputStream2 = new DerOutputStream();
    derOutputStream2.write((byte)48, derOutputStream1);
    derOutputStream1 = new DerOutputStream();
    derOutputStream1.write(DerValue.createTag((byte)64, true, (byte)29), derOutputStream2);
    return derOutputStream1.toByteArray();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\EncKrbCredPart.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */