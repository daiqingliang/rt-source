package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Vector;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptedData;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.krb5.RealmException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class KDCReqBody {
  public KDCOptions kdcOptions;
  
  public PrincipalName cname;
  
  public PrincipalName sname;
  
  public KerberosTime from;
  
  public KerberosTime till;
  
  public KerberosTime rtime;
  
  public HostAddresses addresses;
  
  private int nonce;
  
  private int[] eType = null;
  
  private EncryptedData encAuthorizationData;
  
  private Ticket[] additionalTickets;
  
  public KDCReqBody(KDCOptions paramKDCOptions, PrincipalName paramPrincipalName1, PrincipalName paramPrincipalName2, KerberosTime paramKerberosTime1, KerberosTime paramKerberosTime2, KerberosTime paramKerberosTime3, int paramInt, int[] paramArrayOfInt, HostAddresses paramHostAddresses, EncryptedData paramEncryptedData, Ticket[] paramArrayOfTicket) throws IOException {
    this.kdcOptions = paramKDCOptions;
    this.cname = paramPrincipalName1;
    this.sname = paramPrincipalName2;
    this.from = paramKerberosTime1;
    this.till = paramKerberosTime2;
    this.rtime = paramKerberosTime3;
    this.nonce = paramInt;
    if (paramArrayOfInt != null)
      this.eType = (int[])paramArrayOfInt.clone(); 
    this.addresses = paramHostAddresses;
    this.encAuthorizationData = paramEncryptedData;
    if (paramArrayOfTicket != null) {
      this.additionalTickets = new Ticket[paramArrayOfTicket.length];
      for (byte b = 0; b < paramArrayOfTicket.length; b++) {
        if (paramArrayOfTicket[b] == null)
          throw new IOException("Cannot create a KDCReqBody"); 
        this.additionalTickets[b] = (Ticket)paramArrayOfTicket[b].clone();
      } 
    } 
  }
  
  public KDCReqBody(DerValue paramDerValue, int paramInt) throws Asn1Exception, RealmException, KrbException, IOException {
    this.addresses = null;
    this.encAuthorizationData = null;
    this.additionalTickets = null;
    if (paramDerValue.getTag() != 48)
      throw new Asn1Exception(906); 
    this.kdcOptions = KDCOptions.parse(paramDerValue.getData(), (byte)0, false);
    this.cname = PrincipalName.parse(paramDerValue.getData(), (byte)1, true, new Realm("PLACEHOLDER"));
    if (paramInt != 10 && this.cname != null)
      throw new Asn1Exception(906); 
    Realm realm = Realm.parse(paramDerValue.getData(), (byte)2, false);
    if (this.cname != null)
      this.cname = new PrincipalName(this.cname.getNameType(), this.cname.getNameStrings(), realm); 
    this.sname = PrincipalName.parse(paramDerValue.getData(), (byte)3, true, realm);
    this.rtime = (this.till = (this.from = KerberosTime.parse(paramDerValue.getData(), (byte)4, true)).parse(paramDerValue.getData(), (byte)5, false)).parse(paramDerValue.getData(), (byte)6, true);
    DerValue derValue = paramDerValue.getData().getDerValue();
    if ((derValue.getTag() & 0x1F) == 7) {
      this.nonce = derValue.getData().getBigInteger().intValue();
    } else {
      throw new Asn1Exception(906);
    } 
    derValue = paramDerValue.getData().getDerValue();
    Vector vector = new Vector();
    if ((derValue.getTag() & 0x1F) == 8) {
      DerValue derValue1 = derValue.getData().getDerValue();
      if (derValue1.getTag() == 48) {
        while (derValue1.getData().available() > 0)
          vector.addElement(Integer.valueOf(derValue1.getData().getBigInteger().intValue())); 
        this.eType = new int[vector.size()];
        for (byte b = 0; b < vector.size(); b++)
          this.eType[b] = ((Integer)vector.elementAt(b)).intValue(); 
      } else {
        throw new Asn1Exception(906);
      } 
    } else {
      throw new Asn1Exception(906);
    } 
    if (paramDerValue.getData().available() > 0)
      this.addresses = HostAddresses.parse(paramDerValue.getData(), (byte)9, true); 
    if (paramDerValue.getData().available() > 0)
      this.encAuthorizationData = EncryptedData.parse(paramDerValue.getData(), (byte)10, true); 
    if (paramDerValue.getData().available() > 0) {
      Vector vector1 = new Vector();
      derValue = paramDerValue.getData().getDerValue();
      if ((derValue.getTag() & 0x1F) == 11) {
        DerValue derValue1 = derValue.getData().getDerValue();
        if (derValue1.getTag() == 48) {
          while (derValue1.getData().available() > 0)
            vector1.addElement(new Ticket(derValue1.getData().getDerValue())); 
        } else {
          throw new Asn1Exception(906);
        } 
        if (vector1.size() > 0) {
          this.additionalTickets = new Ticket[vector1.size()];
          vector1.copyInto(this.additionalTickets);
        } 
      } else {
        throw new Asn1Exception(906);
      } 
    } 
    if (paramDerValue.getData().available() > 0)
      throw new Asn1Exception(906); 
  }
  
  public byte[] asn1Encode(int paramInt) throws Asn1Exception, IOException {
    Vector vector = new Vector();
    vector.addElement(new DerValue(DerValue.createTag(-128, true, (byte)0), this.kdcOptions.asn1Encode()));
    if (paramInt == 10 && this.cname != null)
      vector.addElement(new DerValue(DerValue.createTag(-128, true, (byte)1), this.cname.asn1Encode())); 
    if (this.sname != null) {
      vector.addElement(new DerValue(DerValue.createTag(-128, true, (byte)2), this.sname.getRealm().asn1Encode()));
      vector.addElement(new DerValue(DerValue.createTag(-128, true, (byte)3), this.sname.asn1Encode()));
    } else if (this.cname != null) {
      vector.addElement(new DerValue(DerValue.createTag(-128, true, (byte)2), this.cname.getRealm().asn1Encode()));
    } 
    if (this.from != null)
      vector.addElement(new DerValue(DerValue.createTag(-128, true, (byte)4), this.from.asn1Encode())); 
    vector.addElement(new DerValue(DerValue.createTag(-128, true, (byte)5), this.till.asn1Encode()));
    if (this.rtime != null)
      vector.addElement(new DerValue(DerValue.createTag(-128, true, (byte)6), this.rtime.asn1Encode())); 
    DerOutputStream derOutputStream1 = new DerOutputStream();
    derOutputStream1.putInteger(BigInteger.valueOf(this.nonce));
    vector.addElement(new DerValue(DerValue.createTag(-128, true, (byte)7), derOutputStream1.toByteArray()));
    derOutputStream1 = new DerOutputStream();
    for (byte b = 0; b < this.eType.length; b++)
      derOutputStream1.putInteger(BigInteger.valueOf(this.eType[b])); 
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream2.write((byte)48, derOutputStream1);
    vector.addElement(new DerValue(DerValue.createTag(-128, true, (byte)8), derOutputStream2.toByteArray()));
    if (this.addresses != null)
      vector.addElement(new DerValue(DerValue.createTag(-128, true, (byte)9), this.addresses.asn1Encode())); 
    if (this.encAuthorizationData != null)
      vector.addElement(new DerValue(DerValue.createTag(-128, true, (byte)10), this.encAuthorizationData.asn1Encode())); 
    if (this.additionalTickets != null && this.additionalTickets.length > 0) {
      derOutputStream1 = new DerOutputStream();
      for (byte b1 = 0; b1 < this.additionalTickets.length; b1++)
        derOutputStream1.write(this.additionalTickets[b1].asn1Encode()); 
      DerOutputStream derOutputStream = new DerOutputStream();
      derOutputStream.write((byte)48, derOutputStream1);
      vector.addElement(new DerValue(DerValue.createTag(-128, true, (byte)11), derOutputStream.toByteArray()));
    } 
    DerValue[] arrayOfDerValue = new DerValue[vector.size()];
    vector.copyInto(arrayOfDerValue);
    derOutputStream1 = new DerOutputStream();
    derOutputStream1.putSequence(arrayOfDerValue);
    return derOutputStream1.toByteArray();
  }
  
  public int getNonce() { return this.nonce; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\KDCReqBody.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */