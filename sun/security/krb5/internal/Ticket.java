package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptedData;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.krb5.RealmException;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class Ticket implements Cloneable {
  public int tkt_vno;
  
  public PrincipalName sname;
  
  public EncryptedData encPart;
  
  private Ticket() {}
  
  public Object clone() {
    Ticket ticket = new Ticket();
    ticket.sname = (PrincipalName)this.sname.clone();
    ticket.encPart = (EncryptedData)this.encPart.clone();
    ticket.tkt_vno = this.tkt_vno;
    return ticket;
  }
  
  public Ticket(PrincipalName paramPrincipalName, EncryptedData paramEncryptedData) {
    this.tkt_vno = 5;
    this.sname = paramPrincipalName;
    this.encPart = paramEncryptedData;
  }
  
  public Ticket(byte[] paramArrayOfByte) throws Asn1Exception, RealmException, KrbApErrException, IOException { init(new DerValue(paramArrayOfByte)); }
  
  public Ticket(DerValue paramDerValue) throws Asn1Exception, RealmException, KrbApErrException, IOException { init(paramDerValue); }
  
  private void init(DerValue paramDerValue) throws Asn1Exception, RealmException, KrbApErrException, IOException {
    if ((paramDerValue.getTag() & 0x1F) != 1 || paramDerValue.isApplication() != true || paramDerValue.isConstructed() != true)
      throw new Asn1Exception(906); 
    DerValue derValue1 = paramDerValue.getData().getDerValue();
    if (derValue1.getTag() != 48)
      throw new Asn1Exception(906); 
    DerValue derValue2 = derValue1.getData().getDerValue();
    if ((derValue2.getTag() & 0x1F) != 0)
      throw new Asn1Exception(906); 
    this.tkt_vno = derValue2.getData().getBigInteger().intValue();
    if (this.tkt_vno != 5)
      throw new KrbApErrException(39); 
    Realm realm = Realm.parse(derValue1.getData(), (byte)1, false);
    this.sname = PrincipalName.parse(derValue1.getData(), (byte)2, false, realm);
    this.encPart = EncryptedData.parse(derValue1.getData(), (byte)3, false);
    if (derValue1.getData().available() > 0)
      throw new Asn1Exception(906); 
  }
  
  public byte[] asn1Encode() throws Asn1Exception, IOException {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    DerValue[] arrayOfDerValue = new DerValue[4];
    derOutputStream2.putInteger(BigInteger.valueOf(this.tkt_vno));
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)0), derOutputStream2);
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)1), this.sname.getRealm().asn1Encode());
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)2), this.sname.asn1Encode());
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)3), this.encPart.asn1Encode());
    derOutputStream2 = new DerOutputStream();
    derOutputStream2.write((byte)48, derOutputStream1);
    DerOutputStream derOutputStream3 = new DerOutputStream();
    derOutputStream3.write(DerValue.createTag((byte)64, true, (byte)1), derOutputStream2);
    return derOutputStream3.toByteArray();
  }
  
  public static Ticket parse(DerInputStream paramDerInputStream, byte paramByte, boolean paramBoolean) throws Asn1Exception, IOException, RealmException, KrbApErrException {
    if (paramBoolean && ((byte)paramDerInputStream.peekByte() & 0x1F) != paramByte)
      return null; 
    DerValue derValue1 = paramDerInputStream.getDerValue();
    if (paramByte != (derValue1.getTag() & 0x1F))
      throw new Asn1Exception(906); 
    DerValue derValue2 = derValue1.getData().getDerValue();
    return new Ticket(derValue2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\Ticket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */