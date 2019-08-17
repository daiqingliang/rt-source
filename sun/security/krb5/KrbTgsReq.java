package sun.security.krb5;

import java.io.IOException;
import java.net.UnknownHostException;
import sun.security.krb5.internal.APOptions;
import sun.security.krb5.internal.AuthorizationData;
import sun.security.krb5.internal.HostAddresses;
import sun.security.krb5.internal.KDCOptions;
import sun.security.krb5.internal.KDCReqBody;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.PAData;
import sun.security.krb5.internal.TGSReq;
import sun.security.krb5.internal.Ticket;
import sun.security.krb5.internal.crypto.EType;
import sun.security.krb5.internal.crypto.Nonce;

public class KrbTgsReq {
  private PrincipalName princName;
  
  private PrincipalName servName;
  
  private TGSReq tgsReqMessg;
  
  private KerberosTime ctime;
  
  private Ticket secondTicket = null;
  
  private boolean useSubkey = false;
  
  EncryptionKey tgsReqKey;
  
  private static final boolean DEBUG = Krb5.DEBUG;
  
  private byte[] obuf;
  
  private byte[] ibuf;
  
  public KrbTgsReq(Credentials paramCredentials, PrincipalName paramPrincipalName) throws KrbException, IOException { this(new KDCOptions(), paramCredentials, paramPrincipalName, null, null, null, null, null, null, null, null); }
  
  public KrbTgsReq(Credentials paramCredentials, Ticket paramTicket, PrincipalName paramPrincipalName) throws KrbException, IOException { this(KDCOptions.with(new int[] { 14, 1 }, ), paramCredentials, paramPrincipalName, null, null, null, null, null, null, new Ticket[] { paramTicket }, null); }
  
  public KrbTgsReq(Credentials paramCredentials, PrincipalName paramPrincipalName, PAData paramPAData) throws KrbException, IOException { this(KDCOptions.with(new int[] { 1 }, ), paramCredentials, paramCredentials.getClient(), paramPrincipalName, null, null, null, null, null, null, null, null, paramPAData); }
  
  KrbTgsReq(KDCOptions paramKDCOptions, Credentials paramCredentials, PrincipalName paramPrincipalName, KerberosTime paramKerberosTime1, KerberosTime paramKerberosTime2, KerberosTime paramKerberosTime3, int[] paramArrayOfInt, HostAddresses paramHostAddresses, AuthorizationData paramAuthorizationData, Ticket[] paramArrayOfTicket, EncryptionKey paramEncryptionKey) throws KrbException, IOException { this(paramKDCOptions, paramCredentials, paramCredentials.getClient(), paramPrincipalName, paramKerberosTime1, paramKerberosTime2, paramKerberosTime3, paramArrayOfInt, paramHostAddresses, paramAuthorizationData, paramArrayOfTicket, paramEncryptionKey, null); }
  
  private KrbTgsReq(KDCOptions paramKDCOptions, Credentials paramCredentials, PrincipalName paramPrincipalName1, PrincipalName paramPrincipalName2, KerberosTime paramKerberosTime1, KerberosTime paramKerberosTime2, KerberosTime paramKerberosTime3, int[] paramArrayOfInt, HostAddresses paramHostAddresses, AuthorizationData paramAuthorizationData, Ticket[] paramArrayOfTicket, EncryptionKey paramEncryptionKey, PAData paramPAData) throws KrbException, IOException {
    this.princName = paramPrincipalName1;
    this.servName = paramPrincipalName2;
    this.ctime = KerberosTime.now();
    if (paramKDCOptions.get(1) && !paramCredentials.flags.get(1))
      paramKDCOptions.set(1, false); 
    if (paramKDCOptions.get(2) && !paramCredentials.flags.get(1))
      throw new KrbException(101); 
    if (paramKDCOptions.get(3) && !paramCredentials.flags.get(3))
      throw new KrbException(101); 
    if (paramKDCOptions.get(4) && !paramCredentials.flags.get(3))
      throw new KrbException(101); 
    if (paramKDCOptions.get(5) && !paramCredentials.flags.get(5))
      throw new KrbException(101); 
    if (paramKDCOptions.get(8) && !paramCredentials.flags.get(8))
      throw new KrbException(101); 
    if (paramKDCOptions.get(6)) {
      if (!paramCredentials.flags.get(6))
        throw new KrbException(101); 
    } else if (paramKerberosTime1 != null) {
      paramKerberosTime1 = null;
    } 
    if (paramKDCOptions.get(8)) {
      if (!paramCredentials.flags.get(8))
        throw new KrbException(101); 
    } else if (paramKerberosTime3 != null) {
      paramKerberosTime3 = null;
    } 
    if (paramKDCOptions.get(28) || paramKDCOptions.get(14)) {
      if (paramArrayOfTicket == null)
        throw new KrbException(101); 
      this.secondTicket = paramArrayOfTicket[0];
    } else if (paramArrayOfTicket != null) {
      paramArrayOfTicket = null;
    } 
    this.tgsReqMessg = createRequest(paramKDCOptions, paramCredentials.ticket, paramCredentials.key, this.ctime, this.princName, this.servName, paramKerberosTime1, paramKerberosTime2, paramKerberosTime3, paramArrayOfInt, paramHostAddresses, paramAuthorizationData, paramArrayOfTicket, paramEncryptionKey, paramPAData);
    this.obuf = this.tgsReqMessg.asn1Encode();
    if (paramCredentials.flags.get(2))
      paramKDCOptions.set(2, true); 
  }
  
  public void send() throws IOException, KrbException {
    String str = null;
    if (this.servName != null)
      str = this.servName.getRealmString(); 
    KdcComm kdcComm = new KdcComm(str);
    this.ibuf = kdcComm.send(this.obuf);
  }
  
  public KrbTgsRep getReply() throws KrbException, IOException { return new KrbTgsRep(this.ibuf, this); }
  
  public Credentials sendAndGetCreds() throws IOException, KrbException {
    KrbTgsRep krbTgsRep = null;
    Object object = null;
    send();
    krbTgsRep = getReply();
    return krbTgsRep.getCreds();
  }
  
  KerberosTime getCtime() { return this.ctime; }
  
  private TGSReq createRequest(KDCOptions paramKDCOptions, Ticket paramTicket, EncryptionKey paramEncryptionKey1, KerberosTime paramKerberosTime1, PrincipalName paramPrincipalName1, PrincipalName paramPrincipalName2, KerberosTime paramKerberosTime2, KerberosTime paramKerberosTime3, KerberosTime paramKerberosTime4, int[] paramArrayOfInt, HostAddresses paramHostAddresses, AuthorizationData paramAuthorizationData, Ticket[] paramArrayOfTicket, EncryptionKey paramEncryptionKey2, PAData paramPAData) throws IOException, KrbException, UnknownHostException {
    Checksum checksum;
    KerberosTime kerberosTime = null;
    if (paramKerberosTime3 == null) {
      kerberosTime = new KerberosTime(0L);
    } else {
      kerberosTime = paramKerberosTime3;
    } 
    this.tgsReqKey = paramEncryptionKey1;
    int[] arrayOfInt = null;
    if (paramArrayOfInt == null) {
      arrayOfInt = EType.getDefaults("default_tgs_enctypes");
    } else {
      arrayOfInt = paramArrayOfInt;
    } 
    EncryptionKey encryptionKey = null;
    EncryptedData encryptedData = null;
    if (paramAuthorizationData != null) {
      byte[] arrayOfByte = paramAuthorizationData.asn1Encode();
      if (paramEncryptionKey2 != null) {
        encryptionKey = paramEncryptionKey2;
        this.tgsReqKey = paramEncryptionKey2;
        this.useSubkey = true;
        encryptedData = new EncryptedData(encryptionKey, arrayOfByte, 5);
      } else {
        encryptedData = new EncryptedData(paramEncryptionKey1, arrayOfByte, 4);
      } 
    } 
    KDCReqBody kDCReqBody = new KDCReqBody(paramKDCOptions, paramPrincipalName1, paramPrincipalName2, paramKerberosTime2, kerberosTime, paramKerberosTime4, Nonce.value(), arrayOfInt, paramHostAddresses, encryptedData, paramArrayOfTicket);
    byte[] arrayOfByte1 = kDCReqBody.asn1Encode(12);
    switch (Checksum.CKSUMTYPE_DEFAULT) {
      case -138:
      case 3:
      case 4:
      case 5:
      case 6:
      case 8:
      case 12:
      case 15:
      case 16:
        checksum = new Checksum(Checksum.CKSUMTYPE_DEFAULT, arrayOfByte1, paramEncryptionKey1, 6);
        break;
      default:
        checksum = new Checksum(Checksum.CKSUMTYPE_DEFAULT, arrayOfByte1);
        break;
    } 
    byte[] arrayOfByte2 = (new KrbApReq(new APOptions(), paramTicket, paramEncryptionKey1, paramPrincipalName1, checksum, paramKerberosTime1, encryptionKey, null, null)).getMessage();
    PAData pAData = new PAData(1, arrayOfByte2);
    new PAData[2][0] = paramPAData;
    new PAData[2][1] = pAData;
    new PAData[1][0] = pAData;
    return new TGSReq((paramPAData != null) ? new PAData[2] : new PAData[1], kDCReqBody);
  }
  
  TGSReq getMessage() { return this.tgsReqMessg; }
  
  Ticket getSecondTicket() { return this.secondTicket; }
  
  private static void debug(String paramString) {}
  
  boolean usedSubkey() { return this.useSubkey; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\KrbTgsReq.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */