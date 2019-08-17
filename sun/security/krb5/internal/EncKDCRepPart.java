package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.krb5.RealmException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class EncKDCRepPart {
  public EncryptionKey key;
  
  public LastReq lastReq;
  
  public int nonce;
  
  public KerberosTime keyExpiration;
  
  public TicketFlags flags;
  
  public KerberosTime authtime;
  
  public KerberosTime starttime;
  
  public KerberosTime endtime;
  
  public KerberosTime renewTill;
  
  public PrincipalName sname;
  
  public HostAddresses caddr;
  
  public int msgType;
  
  public EncKDCRepPart(EncryptionKey paramEncryptionKey, LastReq paramLastReq, int paramInt1, KerberosTime paramKerberosTime1, TicketFlags paramTicketFlags, KerberosTime paramKerberosTime2, KerberosTime paramKerberosTime3, KerberosTime paramKerberosTime4, KerberosTime paramKerberosTime5, PrincipalName paramPrincipalName, HostAddresses paramHostAddresses, int paramInt2) {
    this.key = paramEncryptionKey;
    this.lastReq = paramLastReq;
    this.nonce = paramInt1;
    this.keyExpiration = paramKerberosTime1;
    this.flags = paramTicketFlags;
    this.authtime = paramKerberosTime2;
    this.starttime = paramKerberosTime3;
    this.endtime = paramKerberosTime4;
    this.renewTill = paramKerberosTime5;
    this.sname = paramPrincipalName;
    this.caddr = paramHostAddresses;
    this.msgType = paramInt2;
  }
  
  public EncKDCRepPart() {}
  
  public EncKDCRepPart(byte[] paramArrayOfByte, int paramInt) throws Asn1Exception, IOException, RealmException { init(new DerValue(paramArrayOfByte), paramInt); }
  
  public EncKDCRepPart(DerValue paramDerValue, int paramInt) throws Asn1Exception, IOException, RealmException { init(paramDerValue, paramInt); }
  
  protected void init(DerValue paramDerValue, int paramInt) throws Asn1Exception, IOException, RealmException {
    this.msgType = paramDerValue.getTag() & 0x1F;
    if (this.msgType != 25 && this.msgType != 26)
      throw new Asn1Exception(906); 
    DerValue derValue1 = paramDerValue.getData().getDerValue();
    if (derValue1.getTag() != 48)
      throw new Asn1Exception(906); 
    this.key = EncryptionKey.parse(derValue1.getData(), (byte)0, false);
    this.lastReq = LastReq.parse(derValue1.getData(), (byte)1, false);
    DerValue derValue2 = derValue1.getData().getDerValue();
    if ((derValue2.getTag() & 0x1F) == 2) {
      this.nonce = derValue2.getData().getBigInteger().intValue();
    } else {
      throw new Asn1Exception(906);
    } 
    this.keyExpiration = KerberosTime.parse(derValue1.getData(), (byte)3, true);
    this.flags = TicketFlags.parse(derValue1.getData(), (byte)4, false);
    this.renewTill = (this.endtime = (this.starttime = (this.authtime = KerberosTime.parse(derValue1.getData(), (byte)5, false)).parse(derValue1.getData(), (byte)6, true)).parse(derValue1.getData(), (byte)7, false)).parse(derValue1.getData(), (byte)8, true);
    Realm realm = Realm.parse(derValue1.getData(), (byte)9, false);
    this.sname = PrincipalName.parse(derValue1.getData(), (byte)10, false, realm);
    if (derValue1.getData().available() > 0)
      this.caddr = HostAddresses.parse(derValue1.getData(), (byte)11, true); 
  }
  
  public byte[] asn1Encode(int paramInt) throws Asn1Exception, IOException {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream2.write(DerValue.createTag(-128, true, (byte)0), this.key.asn1Encode());
    derOutputStream2.write(DerValue.createTag(-128, true, (byte)1), this.lastReq.asn1Encode());
    derOutputStream1.putInteger(BigInteger.valueOf(this.nonce));
    derOutputStream2.write(DerValue.createTag(-128, true, (byte)2), derOutputStream1);
    if (this.keyExpiration != null)
      derOutputStream2.write(DerValue.createTag(-128, true, (byte)3), this.keyExpiration.asn1Encode()); 
    derOutputStream2.write(DerValue.createTag(-128, true, (byte)4), this.flags.asn1Encode());
    derOutputStream2.write(DerValue.createTag(-128, true, (byte)5), this.authtime.asn1Encode());
    if (this.starttime != null)
      derOutputStream2.write(DerValue.createTag(-128, true, (byte)6), this.starttime.asn1Encode()); 
    derOutputStream2.write(DerValue.createTag(-128, true, (byte)7), this.endtime.asn1Encode());
    if (this.renewTill != null)
      derOutputStream2.write(DerValue.createTag(-128, true, (byte)8), this.renewTill.asn1Encode()); 
    derOutputStream2.write(DerValue.createTag(-128, true, (byte)9), this.sname.getRealm().asn1Encode());
    derOutputStream2.write(DerValue.createTag(-128, true, (byte)10), this.sname.asn1Encode());
    if (this.caddr != null)
      derOutputStream2.write(DerValue.createTag(-128, true, (byte)11), this.caddr.asn1Encode()); 
    derOutputStream1 = new DerOutputStream();
    derOutputStream1.write((byte)48, derOutputStream2);
    derOutputStream2 = new DerOutputStream();
    derOutputStream2.write(DerValue.createTag((byte)64, true, (byte)this.msgType), derOutputStream1);
    return derOutputStream2.toByteArray();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\EncKDCRepPart.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */