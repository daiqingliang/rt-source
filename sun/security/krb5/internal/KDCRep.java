package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptedData;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.krb5.RealmException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class KDCRep {
  public PrincipalName cname;
  
  public Ticket ticket;
  
  public EncryptedData encPart;
  
  public EncKDCRepPart encKDCRepPart;
  
  private int pvno;
  
  private int msgType;
  
  public PAData[] pAData = null;
  
  private boolean DEBUG = Krb5.DEBUG;
  
  public KDCRep(PAData[] paramArrayOfPAData, PrincipalName paramPrincipalName, Ticket paramTicket, EncryptedData paramEncryptedData, int paramInt) throws IOException {
    this.pvno = 5;
    this.msgType = paramInt;
    if (paramArrayOfPAData != null) {
      this.pAData = new PAData[paramArrayOfPAData.length];
      for (byte b = 0; b < paramArrayOfPAData.length; b++) {
        if (paramArrayOfPAData[b] == null)
          throw new IOException("Cannot create a KDCRep"); 
        this.pAData[b] = (PAData)paramArrayOfPAData[b].clone();
      } 
    } 
    this.cname = paramPrincipalName;
    this.ticket = paramTicket;
    this.encPart = paramEncryptedData;
  }
  
  public KDCRep() {}
  
  public KDCRep(byte[] paramArrayOfByte, int paramInt) throws Asn1Exception, KrbApErrException, RealmException, IOException { init(new DerValue(paramArrayOfByte), paramInt); }
  
  public KDCRep(DerValue paramDerValue, int paramInt) throws Asn1Exception, RealmException, KrbApErrException, IOException { init(paramDerValue, paramInt); }
  
  protected void init(DerValue paramDerValue, int paramInt) throws Asn1Exception, RealmException, KrbApErrException, IOException {
    if ((paramDerValue.getTag() & 0x1F) != paramInt) {
      if (this.DEBUG)
        System.out.println(">>> KDCRep: init() encoding tag is " + paramDerValue.getTag() + " req type is " + paramInt); 
      throw new Asn1Exception(906);
    } 
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
      if (this.msgType != paramInt)
        throw new KrbApErrException(40); 
    } else {
      throw new Asn1Exception(906);
    } 
    if ((derValue1.getData().peekByte() & 0x1F) == 2) {
      derValue2 = derValue1.getData().getDerValue();
      DerValue[] arrayOfDerValue = derValue2.getData().getSequence(1);
      this.pAData = new PAData[arrayOfDerValue.length];
      for (byte b = 0; b < arrayOfDerValue.length; b++)
        this.pAData[b] = new PAData(arrayOfDerValue[b]); 
    } else {
      this.pAData = null;
    } 
    Realm realm = Realm.parse(derValue1.getData(), (byte)3, false);
    this.cname = PrincipalName.parse(derValue1.getData(), (byte)4, false, realm);
    this.ticket = Ticket.parse(derValue1.getData(), (byte)5, false);
    this.encPart = EncryptedData.parse(derValue1.getData(), (byte)6, false);
    if (derValue1.getData().available() > 0)
      throw new Asn1Exception(906); 
  }
  
  public byte[] asn1Encode() throws Asn1Exception, IOException {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream2.putInteger(BigInteger.valueOf(this.pvno));
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)0), derOutputStream2);
    derOutputStream2 = new DerOutputStream();
    derOutputStream2.putInteger(BigInteger.valueOf(this.msgType));
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)1), derOutputStream2);
    if (this.pAData != null && this.pAData.length > 0) {
      DerOutputStream derOutputStream = new DerOutputStream();
      for (byte b = 0; b < this.pAData.length; b++)
        derOutputStream.write(this.pAData[b].asn1Encode()); 
      derOutputStream2 = new DerOutputStream();
      derOutputStream2.write((byte)48, derOutputStream);
      derOutputStream1.write(DerValue.createTag(-128, true, (byte)2), derOutputStream2);
    } 
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)3), this.cname.getRealm().asn1Encode());
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)4), this.cname.asn1Encode());
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)5), this.ticket.asn1Encode());
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)6), this.encPart.asn1Encode());
    derOutputStream2 = new DerOutputStream();
    derOutputStream2.write((byte)48, derOutputStream1);
    return derOutputStream2.toByteArray();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\KDCRep.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */