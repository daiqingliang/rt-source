package sun.security.krb5.internal;

import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class EncTicketPart {
  public TicketFlags flags;
  
  public EncryptionKey key;
  
  public PrincipalName cname;
  
  public TransitedEncoding transited;
  
  public KerberosTime authtime;
  
  public KerberosTime starttime;
  
  public KerberosTime endtime;
  
  public KerberosTime renewTill;
  
  public HostAddresses caddr;
  
  public AuthorizationData authorizationData;
  
  public EncTicketPart(TicketFlags paramTicketFlags, EncryptionKey paramEncryptionKey, PrincipalName paramPrincipalName, TransitedEncoding paramTransitedEncoding, KerberosTime paramKerberosTime1, KerberosTime paramKerberosTime2, KerberosTime paramKerberosTime3, KerberosTime paramKerberosTime4, HostAddresses paramHostAddresses, AuthorizationData paramAuthorizationData) {
    this.flags = paramTicketFlags;
    this.key = paramEncryptionKey;
    this.cname = paramPrincipalName;
    this.transited = paramTransitedEncoding;
    this.authtime = paramKerberosTime1;
    this.starttime = paramKerberosTime2;
    this.endtime = paramKerberosTime3;
    this.renewTill = paramKerberosTime4;
    this.caddr = paramHostAddresses;
    this.authorizationData = paramAuthorizationData;
  }
  
  public EncTicketPart(byte[] paramArrayOfByte) throws Asn1Exception, KrbException, IOException { init(new DerValue(paramArrayOfByte)); }
  
  public EncTicketPart(DerValue paramDerValue) throws Asn1Exception, KrbException, IOException { init(paramDerValue); }
  
  private static String getHexBytes(byte[] paramArrayOfByte, int paramInt) throws IOException {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < paramInt; b++) {
      byte b1 = paramArrayOfByte[b] >> 4 & 0xF;
      byte b2 = paramArrayOfByte[b] & 0xF;
      stringBuffer.append(Integer.toHexString(b1));
      stringBuffer.append(Integer.toHexString(b2));
      stringBuffer.append(' ');
    } 
    return stringBuffer.toString();
  }
  
  private void init(DerValue paramDerValue) throws Asn1Exception, KrbException, IOException {
    this.renewTill = null;
    this.caddr = null;
    this.authorizationData = null;
    if ((paramDerValue.getTag() & 0x1F) != 3 || paramDerValue.isApplication() != true || paramDerValue.isConstructed() != true)
      throw new Asn1Exception(906); 
    DerValue derValue = paramDerValue.getData().getDerValue();
    if (derValue.getTag() != 48)
      throw new Asn1Exception(906); 
    this.flags = TicketFlags.parse(derValue.getData(), (byte)0, false);
    this.key = EncryptionKey.parse(derValue.getData(), (byte)1, false);
    Realm realm = Realm.parse(derValue.getData(), (byte)2, false);
    this.cname = PrincipalName.parse(derValue.getData(), (byte)3, false, realm);
    this.transited = TransitedEncoding.parse(derValue.getData(), (byte)4, false);
    this.endtime = (this.starttime = (this.authtime = KerberosTime.parse(derValue.getData(), (byte)5, false)).parse(derValue.getData(), (byte)6, true)).parse(derValue.getData(), (byte)7, false);
    if (derValue.getData().available() > 0)
      this.renewTill = KerberosTime.parse(derValue.getData(), (byte)8, true); 
    if (derValue.getData().available() > 0)
      this.caddr = HostAddresses.parse(derValue.getData(), (byte)9, true); 
    if (derValue.getData().available() > 0)
      this.authorizationData = AuthorizationData.parse(derValue.getData(), (byte)10, true); 
    if (derValue.getData().available() > 0)
      throw new Asn1Exception(906); 
  }
  
  public byte[] asn1Encode() throws Asn1Exception, IOException {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)0), this.flags.asn1Encode());
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)1), this.key.asn1Encode());
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)2), this.cname.getRealm().asn1Encode());
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)3), this.cname.asn1Encode());
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)4), this.transited.asn1Encode());
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)5), this.authtime.asn1Encode());
    if (this.starttime != null)
      derOutputStream1.write(DerValue.createTag(-128, true, (byte)6), this.starttime.asn1Encode()); 
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)7), this.endtime.asn1Encode());
    if (this.renewTill != null)
      derOutputStream1.write(DerValue.createTag(-128, true, (byte)8), this.renewTill.asn1Encode()); 
    if (this.caddr != null)
      derOutputStream1.write(DerValue.createTag(-128, true, (byte)9), this.caddr.asn1Encode()); 
    if (this.authorizationData != null)
      derOutputStream1.write(DerValue.createTag(-128, true, (byte)10), this.authorizationData.asn1Encode()); 
    derOutputStream2.write((byte)48, derOutputStream1);
    derOutputStream1 = new DerOutputStream();
    derOutputStream1.write(DerValue.createTag((byte)64, true, (byte)3), derOutputStream2);
    return derOutputStream1.toByteArray();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\EncTicketPart.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */