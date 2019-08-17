package sun.security.krb5.internal;

import java.io.IOException;
import java.util.Vector;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.krb5.RealmException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class KrbCredInfo {
  public EncryptionKey key;
  
  public PrincipalName pname;
  
  public TicketFlags flags;
  
  public KerberosTime authtime;
  
  public KerberosTime starttime;
  
  public KerberosTime endtime;
  
  public KerberosTime renewTill;
  
  public PrincipalName sname;
  
  public HostAddresses caddr;
  
  private KrbCredInfo() {}
  
  public KrbCredInfo(EncryptionKey paramEncryptionKey, PrincipalName paramPrincipalName1, TicketFlags paramTicketFlags, KerberosTime paramKerberosTime1, KerberosTime paramKerberosTime2, KerberosTime paramKerberosTime3, KerberosTime paramKerberosTime4, PrincipalName paramPrincipalName2, HostAddresses paramHostAddresses) {
    this.key = paramEncryptionKey;
    this.pname = paramPrincipalName1;
    this.flags = paramTicketFlags;
    this.authtime = paramKerberosTime1;
    this.starttime = paramKerberosTime2;
    this.endtime = paramKerberosTime3;
    this.renewTill = paramKerberosTime4;
    this.sname = paramPrincipalName2;
    this.caddr = paramHostAddresses;
  }
  
  public KrbCredInfo(DerValue paramDerValue) throws Asn1Exception, IOException, RealmException {
    if (paramDerValue.getTag() != 48)
      throw new Asn1Exception(906); 
    this.pname = null;
    this.flags = null;
    this.authtime = null;
    this.starttime = null;
    this.endtime = null;
    this.renewTill = null;
    this.sname = null;
    this.caddr = null;
    this.key = EncryptionKey.parse(paramDerValue.getData(), (byte)0, false);
    Realm realm1 = null;
    Realm realm2 = null;
    if (paramDerValue.getData().available() > 0)
      realm1 = Realm.parse(paramDerValue.getData(), (byte)1, true); 
    if (paramDerValue.getData().available() > 0)
      this.pname = PrincipalName.parse(paramDerValue.getData(), (byte)2, true, realm1); 
    if (paramDerValue.getData().available() > 0)
      this.flags = TicketFlags.parse(paramDerValue.getData(), (byte)3, true); 
    if (paramDerValue.getData().available() > 0)
      this.authtime = KerberosTime.parse(paramDerValue.getData(), (byte)4, true); 
    if (paramDerValue.getData().available() > 0)
      this.starttime = KerberosTime.parse(paramDerValue.getData(), (byte)5, true); 
    if (paramDerValue.getData().available() > 0)
      this.endtime = KerberosTime.parse(paramDerValue.getData(), (byte)6, true); 
    if (paramDerValue.getData().available() > 0)
      this.renewTill = KerberosTime.parse(paramDerValue.getData(), (byte)7, true); 
    if (paramDerValue.getData().available() > 0)
      realm2 = Realm.parse(paramDerValue.getData(), (byte)8, true); 
    if (paramDerValue.getData().available() > 0)
      this.sname = PrincipalName.parse(paramDerValue.getData(), (byte)9, true, realm2); 
    if (paramDerValue.getData().available() > 0)
      this.caddr = HostAddresses.parse(paramDerValue.getData(), (byte)10, true); 
    if (paramDerValue.getData().available() > 0)
      throw new Asn1Exception(906); 
  }
  
  public byte[] asn1Encode() throws Asn1Exception, IOException {
    Vector vector = new Vector();
    vector.addElement(new DerValue(DerValue.createTag(-128, true, (byte)0), this.key.asn1Encode()));
    if (this.pname != null) {
      vector.addElement(new DerValue(DerValue.createTag(-128, true, (byte)1), this.pname.getRealm().asn1Encode()));
      vector.addElement(new DerValue(DerValue.createTag(-128, true, (byte)2), this.pname.asn1Encode()));
    } 
    if (this.flags != null)
      vector.addElement(new DerValue(DerValue.createTag(-128, true, (byte)3), this.flags.asn1Encode())); 
    if (this.authtime != null)
      vector.addElement(new DerValue(DerValue.createTag(-128, true, (byte)4), this.authtime.asn1Encode())); 
    if (this.starttime != null)
      vector.addElement(new DerValue(DerValue.createTag(-128, true, (byte)5), this.starttime.asn1Encode())); 
    if (this.endtime != null)
      vector.addElement(new DerValue(DerValue.createTag(-128, true, (byte)6), this.endtime.asn1Encode())); 
    if (this.renewTill != null)
      vector.addElement(new DerValue(DerValue.createTag(-128, true, (byte)7), this.renewTill.asn1Encode())); 
    if (this.sname != null) {
      vector.addElement(new DerValue(DerValue.createTag(-128, true, (byte)8), this.sname.getRealm().asn1Encode()));
      vector.addElement(new DerValue(DerValue.createTag(-128, true, (byte)9), this.sname.asn1Encode()));
    } 
    if (this.caddr != null)
      vector.addElement(new DerValue(DerValue.createTag(-128, true, (byte)10), this.caddr.asn1Encode())); 
    DerValue[] arrayOfDerValue = new DerValue[vector.size()];
    vector.copyInto(arrayOfDerValue);
    DerOutputStream derOutputStream = new DerOutputStream();
    derOutputStream.putSequence(arrayOfDerValue);
    return derOutputStream.toByteArray();
  }
  
  public Object clone() {
    KrbCredInfo krbCredInfo = new KrbCredInfo();
    krbCredInfo.key = (EncryptionKey)this.key.clone();
    if (this.pname != null)
      krbCredInfo.pname = (PrincipalName)this.pname.clone(); 
    if (this.flags != null)
      krbCredInfo.flags = (TicketFlags)this.flags.clone(); 
    krbCredInfo.authtime = this.authtime;
    krbCredInfo.starttime = this.starttime;
    krbCredInfo.endtime = this.endtime;
    krbCredInfo.renewTill = this.renewTill;
    if (this.sname != null)
      krbCredInfo.sname = (PrincipalName)this.sname.clone(); 
    if (this.caddr != null)
      krbCredInfo.caddr = (HostAddresses)this.caddr.clone(); 
    return krbCredInfo;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\KrbCredInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */