package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Vector;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptedData;
import sun.security.krb5.RealmException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class KRBCred {
  public Ticket[] tickets = null;
  
  public EncryptedData encPart;
  
  private int pvno;
  
  private int msgType;
  
  public KRBCred(Ticket[] paramArrayOfTicket, EncryptedData paramEncryptedData) throws IOException {
    this.pvno = 5;
    this.msgType = 22;
    if (paramArrayOfTicket != null) {
      this.tickets = new Ticket[paramArrayOfTicket.length];
      for (byte b = 0; b < paramArrayOfTicket.length; b++) {
        if (paramArrayOfTicket[b] == null)
          throw new IOException("Cannot create a KRBCred"); 
        this.tickets[b] = (Ticket)paramArrayOfTicket[b].clone();
      } 
    } 
    this.encPart = paramEncryptedData;
  }
  
  public KRBCred(byte[] paramArrayOfByte) throws Asn1Exception, RealmException, KrbApErrException, IOException { init(new DerValue(paramArrayOfByte)); }
  
  public KRBCred(DerValue paramDerValue) throws Asn1Exception, RealmException, KrbApErrException, IOException { init(paramDerValue); }
  
  private void init(DerValue paramDerValue) throws Asn1Exception, RealmException, KrbApErrException, IOException {
    if ((paramDerValue.getTag() & 0x1F) != 22 || paramDerValue.isApplication() != true || paramDerValue.isConstructed() != true)
      throw new Asn1Exception(906); 
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
      if (this.msgType != 22)
        throw new KrbApErrException(40); 
    } else {
      throw new Asn1Exception(906);
    } 
    derValue2 = derValue1.getData().getDerValue();
    if ((derValue2.getTag() & 0x1F) == 2) {
      DerValue derValue = derValue2.getData().getDerValue();
      if (derValue.getTag() != 48)
        throw new Asn1Exception(906); 
      Vector vector = new Vector();
      while (derValue.getData().available() > 0)
        vector.addElement(new Ticket(derValue.getData().getDerValue())); 
      if (vector.size() > 0) {
        this.tickets = new Ticket[vector.size()];
        vector.copyInto(this.tickets);
      } 
    } else {
      throw new Asn1Exception(906);
    } 
    this.encPart = EncryptedData.parse(derValue1.getData(), (byte)3, false);
    if (derValue1.getData().available() > 0)
      throw new Asn1Exception(906); 
  }
  
  public byte[] asn1Encode() throws Asn1Exception, IOException {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    derOutputStream1.putInteger(BigInteger.valueOf(this.pvno));
    DerOutputStream derOutputStream3 = new DerOutputStream();
    derOutputStream3.write(DerValue.createTag(-128, true, (byte)0), derOutputStream1);
    derOutputStream1 = new DerOutputStream();
    derOutputStream1.putInteger(BigInteger.valueOf(this.msgType));
    derOutputStream3.write(DerValue.createTag(-128, true, (byte)1), derOutputStream1);
    derOutputStream1 = new DerOutputStream();
    for (byte b = 0; b < this.tickets.length; b++)
      derOutputStream1.write(this.tickets[b].asn1Encode()); 
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream2.write((byte)48, derOutputStream1);
    derOutputStream3.write(DerValue.createTag(-128, true, (byte)2), derOutputStream2);
    derOutputStream3.write(DerValue.createTag(-128, true, (byte)3), this.encPart.asn1Encode());
    derOutputStream2 = new DerOutputStream();
    derOutputStream2.write((byte)48, derOutputStream3);
    derOutputStream3 = new DerOutputStream();
    derOutputStream3.write(DerValue.createTag((byte)64, true, (byte)22), derOutputStream2);
    return derOutputStream3.toByteArray();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\KRBCred.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */