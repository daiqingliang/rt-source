package sun.security.krb5;

import java.io.IOException;
import sun.security.krb5.internal.EncKrbCredPart;
import sun.security.krb5.internal.HostAddresses;
import sun.security.krb5.internal.KDCOptions;
import sun.security.krb5.internal.KRBCred;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.KrbCredInfo;
import sun.security.krb5.internal.Ticket;
import sun.security.krb5.internal.TicketFlags;
import sun.security.util.DerValue;

public class KrbCred {
  private static boolean DEBUG = Krb5.DEBUG;
  
  private byte[] obuf = null;
  
  private KRBCred credMessg = null;
  
  private Ticket ticket = null;
  
  private EncKrbCredPart encPart = null;
  
  private Credentials creds = null;
  
  private KerberosTime timeStamp = null;
  
  public KrbCred(Credentials paramCredentials1, Credentials paramCredentials2, EncryptionKey paramEncryptionKey) throws KrbException, IOException {
    PrincipalName principalName1 = paramCredentials1.getClient();
    PrincipalName principalName2 = paramCredentials1.getServer();
    PrincipalName principalName3 = paramCredentials2.getServer();
    if (!paramCredentials2.getClient().equals(principalName1))
      throw new KrbException(60, "Client principal does not match"); 
    KDCOptions kDCOptions = new KDCOptions();
    kDCOptions.set(2, true);
    kDCOptions.set(1, true);
    HostAddresses hostAddresses = null;
    if (principalName3.getNameType() == 3)
      hostAddresses = new HostAddresses(principalName3); 
    KrbTgsReq krbTgsReq = new KrbTgsReq(kDCOptions, paramCredentials1, principalName2, null, null, null, null, hostAddresses, null, null, null);
    this.credMessg = createMessage(krbTgsReq.sendAndGetCreds(), paramEncryptionKey);
    this.obuf = this.credMessg.asn1Encode();
  }
  
  KRBCred createMessage(Credentials paramCredentials, EncryptionKey paramEncryptionKey) throws KrbException, IOException {
    EncryptionKey encryptionKey = paramCredentials.getSessionKey();
    PrincipalName principalName1 = paramCredentials.getClient();
    Realm realm = principalName1.getRealm();
    PrincipalName principalName2 = paramCredentials.getServer();
    KrbCredInfo krbCredInfo = new KrbCredInfo(encryptionKey, principalName1, paramCredentials.flags, paramCredentials.authTime, paramCredentials.startTime, paramCredentials.endTime, paramCredentials.renewTill, principalName2, paramCredentials.cAddr);
    this.timeStamp = KerberosTime.now();
    KrbCredInfo[] arrayOfKrbCredInfo = { krbCredInfo };
    EncKrbCredPart encKrbCredPart = new EncKrbCredPart(arrayOfKrbCredInfo, this.timeStamp, null, null, null, null);
    EncryptedData encryptedData = new EncryptedData(paramEncryptionKey, encKrbCredPart.asn1Encode(), 14);
    Ticket[] arrayOfTicket = { paramCredentials.ticket };
    this.credMessg = new KRBCred(arrayOfTicket, encryptedData);
    return this.credMessg;
  }
  
  public KrbCred(byte[] paramArrayOfByte, EncryptionKey paramEncryptionKey) throws KrbException, IOException {
    this.credMessg = new KRBCred(paramArrayOfByte);
    this.ticket = this.credMessg.tickets[0];
    if (this.credMessg.encPart.getEType() == 0)
      paramEncryptionKey = EncryptionKey.NULL_KEY; 
    byte[] arrayOfByte1 = this.credMessg.encPart.decrypt(paramEncryptionKey, 14);
    byte[] arrayOfByte2 = this.credMessg.encPart.reset(arrayOfByte1);
    DerValue derValue = new DerValue(arrayOfByte2);
    EncKrbCredPart encKrbCredPart = new EncKrbCredPart(derValue);
    this.timeStamp = encKrbCredPart.timeStamp;
    KrbCredInfo krbCredInfo = encKrbCredPart.ticketInfo[0];
    EncryptionKey encryptionKey = krbCredInfo.key;
    PrincipalName principalName1 = krbCredInfo.pname;
    TicketFlags ticketFlags = krbCredInfo.flags;
    KerberosTime kerberosTime1 = krbCredInfo.authtime;
    KerberosTime kerberosTime2 = krbCredInfo.starttime;
    KerberosTime kerberosTime3 = krbCredInfo.endtime;
    KerberosTime kerberosTime4 = krbCredInfo.renewTill;
    PrincipalName principalName2 = krbCredInfo.sname;
    HostAddresses hostAddresses = krbCredInfo.caddr;
    if (DEBUG)
      System.out.println(">>>Delegated Creds have pname=" + principalName1 + " sname=" + principalName2 + " authtime=" + kerberosTime1 + " starttime=" + kerberosTime2 + " endtime=" + kerberosTime3 + "renewTill=" + kerberosTime4); 
    this.creds = new Credentials(this.ticket, principalName1, principalName2, encryptionKey, ticketFlags, kerberosTime1, kerberosTime2, kerberosTime3, kerberosTime4, hostAddresses);
  }
  
  public Credentials[] getDelegatedCreds() { return new Credentials[] { this.creds }; }
  
  public byte[] getMessage() { return this.obuf; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\KrbCred.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */