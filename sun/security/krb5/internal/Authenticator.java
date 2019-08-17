package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Vector;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.Checksum;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.krb5.RealmException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class Authenticator {
  public int authenticator_vno;
  
  public PrincipalName cname;
  
  Checksum cksum;
  
  public int cusec;
  
  public KerberosTime ctime;
  
  EncryptionKey subKey;
  
  Integer seqNumber;
  
  public AuthorizationData authorizationData;
  
  public Authenticator(PrincipalName paramPrincipalName, Checksum paramChecksum, int paramInt, KerberosTime paramKerberosTime, EncryptionKey paramEncryptionKey, Integer paramInteger, AuthorizationData paramAuthorizationData) {
    this.authenticator_vno = 5;
    this.cname = paramPrincipalName;
    this.cksum = paramChecksum;
    this.cusec = paramInt;
    this.ctime = paramKerberosTime;
    this.subKey = paramEncryptionKey;
    this.seqNumber = paramInteger;
    this.authorizationData = paramAuthorizationData;
  }
  
  public Authenticator(byte[] paramArrayOfByte) throws Asn1Exception, IOException, KrbApErrException, RealmException { init(new DerValue(paramArrayOfByte)); }
  
  public Authenticator(DerValue paramDerValue) throws Asn1Exception, IOException, KrbApErrException, RealmException { init(paramDerValue); }
  
  private void init(DerValue paramDerValue) throws Asn1Exception, IOException, KrbApErrException, RealmException {
    if ((paramDerValue.getTag() & 0x1F) != 2 || paramDerValue.isApplication() != true || paramDerValue.isConstructed() != true)
      throw new Asn1Exception(906); 
    DerValue derValue1 = paramDerValue.getData().getDerValue();
    if (derValue1.getTag() != 48)
      throw new Asn1Exception(906); 
    DerValue derValue2 = derValue1.getData().getDerValue();
    if ((derValue2.getTag() & 0x1F) != 0)
      throw new Asn1Exception(906); 
    this.authenticator_vno = derValue2.getData().getBigInteger().intValue();
    if (this.authenticator_vno != 5)
      throw new KrbApErrException(39); 
    Realm realm = Realm.parse(derValue1.getData(), (byte)1, false);
    this.cname = PrincipalName.parse(derValue1.getData(), (byte)2, false, realm);
    this.cksum = Checksum.parse(derValue1.getData(), (byte)3, true);
    derValue2 = derValue1.getData().getDerValue();
    if ((derValue2.getTag() & 0x1F) == 4) {
      this.cusec = derValue2.getData().getBigInteger().intValue();
    } else {
      throw new Asn1Exception(906);
    } 
    this.ctime = KerberosTime.parse(derValue1.getData(), (byte)5, false);
    if (derValue1.getData().available() > 0) {
      this.subKey = EncryptionKey.parse(derValue1.getData(), (byte)6, true);
    } else {
      this.subKey = null;
      this.seqNumber = null;
      this.authorizationData = null;
    } 
    if (derValue1.getData().available() > 0) {
      if ((derValue1.getData().peekByte() & 0x1F) == 7) {
        derValue2 = derValue1.getData().getDerValue();
        if ((derValue2.getTag() & 0x1F) == 7)
          this.seqNumber = new Integer(derValue2.getData().getBigInteger().intValue()); 
      } 
    } else {
      this.seqNumber = null;
      this.authorizationData = null;
    } 
    if (derValue1.getData().available() > 0) {
      this.authorizationData = AuthorizationData.parse(derValue1.getData(), (byte)8, true);
    } else {
      this.authorizationData = null;
    } 
    if (derValue1.getData().available() > 0)
      throw new Asn1Exception(906); 
  }
  
  public byte[] asn1Encode() throws Asn1Exception, IOException {
    Vector vector = new Vector();
    DerOutputStream derOutputStream1 = new DerOutputStream();
    derOutputStream1.putInteger(BigInteger.valueOf(this.authenticator_vno));
    vector.addElement(new DerValue(DerValue.createTag(-128, true, (byte)0), derOutputStream1.toByteArray()));
    vector.addElement(new DerValue(DerValue.createTag(-128, true, (byte)1), this.cname.getRealm().asn1Encode()));
    vector.addElement(new DerValue(DerValue.createTag(-128, true, (byte)2), this.cname.asn1Encode()));
    if (this.cksum != null)
      vector.addElement(new DerValue(DerValue.createTag(-128, true, (byte)3), this.cksum.asn1Encode())); 
    derOutputStream1 = new DerOutputStream();
    derOutputStream1.putInteger(BigInteger.valueOf(this.cusec));
    vector.addElement(new DerValue(DerValue.createTag(-128, true, (byte)4), derOutputStream1.toByteArray()));
    vector.addElement(new DerValue(DerValue.createTag(-128, true, (byte)5), this.ctime.asn1Encode()));
    if (this.subKey != null)
      vector.addElement(new DerValue(DerValue.createTag(-128, true, (byte)6), this.subKey.asn1Encode())); 
    if (this.seqNumber != null) {
      derOutputStream1 = new DerOutputStream();
      derOutputStream1.putInteger(BigInteger.valueOf(this.seqNumber.longValue()));
      vector.addElement(new DerValue(DerValue.createTag(-128, true, (byte)7), derOutputStream1.toByteArray()));
    } 
    if (this.authorizationData != null)
      vector.addElement(new DerValue(DerValue.createTag(-128, true, (byte)8), this.authorizationData.asn1Encode())); 
    DerValue[] arrayOfDerValue = new DerValue[vector.size()];
    vector.copyInto(arrayOfDerValue);
    derOutputStream1 = new DerOutputStream();
    derOutputStream1.putSequence(arrayOfDerValue);
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream2.write(DerValue.createTag((byte)64, true, (byte)2), derOutputStream1);
    return derOutputStream2.toByteArray();
  }
  
  public final Checksum getChecksum() { return this.cksum; }
  
  public final Integer getSeqNumber() { return this.seqNumber; }
  
  public final EncryptionKey getSubKey() { return this.subKey; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\Authenticator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */