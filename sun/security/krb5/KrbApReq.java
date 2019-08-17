package sun.security.krb5;

import java.io.IOException;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import sun.security.jgss.krb5.Krb5AcceptCredential;
import sun.security.krb5.internal.APOptions;
import sun.security.krb5.internal.APReq;
import sun.security.krb5.internal.Authenticator;
import sun.security.krb5.internal.AuthorizationData;
import sun.security.krb5.internal.EncTicketPart;
import sun.security.krb5.internal.HostAddress;
import sun.security.krb5.internal.KRBError;
import sun.security.krb5.internal.KdcErrException;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.LocalSeqNumber;
import sun.security.krb5.internal.ReplayCache;
import sun.security.krb5.internal.SeqNumber;
import sun.security.krb5.internal.Ticket;
import sun.security.krb5.internal.crypto.EType;
import sun.security.krb5.internal.rcache.AuthTimeWithHash;
import sun.security.util.DerValue;

public class KrbApReq {
  private byte[] obuf;
  
  private KerberosTime ctime;
  
  private int cusec;
  
  private Authenticator authenticator;
  
  private Credentials creds;
  
  private APReq apReqMessg;
  
  private static ReplayCache rcache = ReplayCache.getInstance();
  
  private static boolean DEBUG = Krb5.DEBUG;
  
  private static final char[] hexConst = "0123456789ABCDEF".toCharArray();
  
  public KrbApReq(Credentials paramCredentials, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, Checksum paramChecksum) throws Asn1Exception, KrbCryptoException, KrbException, IOException {
    APOptions aPOptions = paramBoolean1 ? new APOptions(2) : new APOptions();
    if (DEBUG)
      System.out.println(">>> KrbApReq: APOptions are " + aPOptions); 
    EncryptionKey encryptionKey = paramBoolean2 ? new EncryptionKey(paramCredentials.getSessionKey()) : null;
    LocalSeqNumber localSeqNumber = new LocalSeqNumber();
    init(aPOptions, paramCredentials, paramChecksum, encryptionKey, localSeqNumber, null, 11);
  }
  
  public KrbApReq(byte[] paramArrayOfByte, Krb5AcceptCredential paramKrb5AcceptCredential, InetAddress paramInetAddress) throws KrbException, IOException {
    this.obuf = paramArrayOfByte;
    if (this.apReqMessg == null)
      decode(); 
    authenticate(paramKrb5AcceptCredential, paramInetAddress);
  }
  
  KrbApReq(APOptions paramAPOptions, Ticket paramTicket, EncryptionKey paramEncryptionKey1, PrincipalName paramPrincipalName, Checksum paramChecksum, KerberosTime paramKerberosTime, EncryptionKey paramEncryptionKey2, SeqNumber paramSeqNumber, AuthorizationData paramAuthorizationData) throws Asn1Exception, IOException, KdcErrException, KrbCryptoException { init(paramAPOptions, paramTicket, paramEncryptionKey1, paramPrincipalName, paramChecksum, paramKerberosTime, paramEncryptionKey2, paramSeqNumber, paramAuthorizationData, 7); }
  
  private void init(APOptions paramAPOptions, Credentials paramCredentials, Checksum paramChecksum, EncryptionKey paramEncryptionKey, SeqNumber paramSeqNumber, AuthorizationData paramAuthorizationData, int paramInt) throws KrbException, IOException {
    this.ctime = KerberosTime.now();
    init(paramAPOptions, paramCredentials.ticket, paramCredentials.key, paramCredentials.client, paramChecksum, this.ctime, paramEncryptionKey, paramSeqNumber, paramAuthorizationData, paramInt);
  }
  
  private void init(APOptions paramAPOptions, Ticket paramTicket, EncryptionKey paramEncryptionKey1, PrincipalName paramPrincipalName, Checksum paramChecksum, KerberosTime paramKerberosTime, EncryptionKey paramEncryptionKey2, SeqNumber paramSeqNumber, AuthorizationData paramAuthorizationData, int paramInt) throws Asn1Exception, IOException, KdcErrException, KrbCryptoException {
    createMessage(paramAPOptions, paramTicket, paramEncryptionKey1, paramPrincipalName, paramChecksum, paramKerberosTime, paramEncryptionKey2, paramSeqNumber, paramAuthorizationData, paramInt);
    this.obuf = this.apReqMessg.asn1Encode();
  }
  
  void decode() throws KrbException, IOException {
    DerValue derValue = new DerValue(this.obuf);
    decode(derValue);
  }
  
  void decode(DerValue paramDerValue) throws KrbException, IOException {
    this.apReqMessg = null;
    try {
      this.apReqMessg = new APReq(paramDerValue);
    } catch (Asn1Exception asn1Exception) {
      String str2;
      this.apReqMessg = null;
      KRBError kRBError = new KRBError(paramDerValue);
      String str1 = kRBError.getErrorString();
      if (str1.charAt(str1.length() - 1) == '\000') {
        str2 = str1.substring(0, str1.length() - 1);
      } else {
        str2 = str1;
      } 
      KrbException krbException = new KrbException(kRBError.getErrorCode(), str2);
      krbException.initCause(asn1Exception);
      throw krbException;
    } 
  }
  
  private void authenticate(Krb5AcceptCredential paramKrb5AcceptCredential, InetAddress paramInetAddress) throws KrbException, IOException {
    byte[] arrayOfByte5;
    int i = this.apReqMessg.ticket.encPart.getEType();
    Integer integer = this.apReqMessg.ticket.encPart.getKeyVersionNumber();
    EncryptionKey[] arrayOfEncryptionKey = paramKrb5AcceptCredential.getKrb5EncryptionKeys(this.apReqMessg.ticket.sname);
    EncryptionKey encryptionKey = EncryptionKey.findKey(i, integer, arrayOfEncryptionKey);
    if (encryptionKey == null)
      throw new KrbException(400, "Cannot find key of appropriate type to decrypt AP REP - " + EType.toString(i)); 
    byte[] arrayOfByte1 = this.apReqMessg.ticket.encPart.decrypt(encryptionKey, 2);
    byte[] arrayOfByte2 = this.apReqMessg.ticket.encPart.reset(arrayOfByte1);
    EncTicketPart encTicketPart = new EncTicketPart(arrayOfByte2);
    checkPermittedEType(encTicketPart.key.getEType());
    byte[] arrayOfByte3 = this.apReqMessg.authenticator.decrypt(encTicketPart.key, 11);
    byte[] arrayOfByte4 = this.apReqMessg.authenticator.reset(arrayOfByte3);
    this.authenticator = new Authenticator(arrayOfByte4);
    this.ctime = this.authenticator.ctime;
    this.cusec = this.authenticator.cusec;
    this.authenticator.ctime = this.authenticator.ctime.withMicroSeconds(this.authenticator.cusec);
    if (!this.authenticator.cname.equals(encTicketPart.cname))
      throw new KrbApErrException(36); 
    if (!this.authenticator.ctime.inClockSkew())
      throw new KrbApErrException(37); 
    try {
      arrayOfByte5 = MessageDigest.getInstance("MD5").digest(this.apReqMessg.authenticator.cipher);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new AssertionError("Impossible");
    } 
    char[] arrayOfChar = new char[arrayOfByte5.length * 2];
    for (byte b = 0; b < arrayOfByte5.length; b++) {
      arrayOfChar[2 * b] = hexConst[(arrayOfByte5[b] & 0xFF) >> 4];
      arrayOfChar[2 * b + 1] = hexConst[arrayOfByte5[b] & 0xF];
    } 
    AuthTimeWithHash authTimeWithHash = new AuthTimeWithHash(this.authenticator.cname.toString(), this.apReqMessg.ticket.sname.toString(), this.authenticator.ctime.getSeconds(), this.authenticator.cusec, new String(arrayOfChar));
    rcache.checkAndStore(KerberosTime.now(), authTimeWithHash);
    if (paramInetAddress != null) {
      HostAddress hostAddress = new HostAddress(paramInetAddress);
      if (encTicketPart.caddr != null && !encTicketPart.caddr.inList(hostAddress)) {
        if (DEBUG)
          System.out.println(">>> KrbApReq: initiator is " + hostAddress.getInetAddress() + ", but caddr is " + Arrays.toString(encTicketPart.caddr.getInetAddresses())); 
        throw new KrbApErrException(38);
      } 
    } 
    KerberosTime kerberosTime = KerberosTime.now();
    if ((encTicketPart.starttime != null && encTicketPart.starttime.greaterThanWRTClockSkew(kerberosTime)) || encTicketPart.flags.get(7))
      throw new KrbApErrException(33); 
    if (encTicketPart.endtime != null && kerberosTime.greaterThanWRTClockSkew(encTicketPart.endtime))
      throw new KrbApErrException(32); 
    this.creds = new Credentials(this.apReqMessg.ticket, this.authenticator.cname, this.apReqMessg.ticket.sname, encTicketPart.key, encTicketPart.flags, encTicketPart.authtime, encTicketPart.starttime, encTicketPart.endtime, encTicketPart.renewTill, encTicketPart.caddr, encTicketPart.authorizationData);
    if (DEBUG)
      System.out.println(">>> KrbApReq: authenticate succeed."); 
  }
  
  public Credentials getCreds() { return this.creds; }
  
  KerberosTime getCtime() { return (this.ctime != null) ? this.ctime : this.authenticator.ctime; }
  
  int cusec() { return this.cusec; }
  
  APOptions getAPOptions() throws KrbException, IOException {
    if (this.apReqMessg == null)
      decode(); 
    return (this.apReqMessg != null) ? this.apReqMessg.apOptions : null;
  }
  
  public boolean getMutualAuthRequired() throws KrbException, IOException {
    if (this.apReqMessg == null)
      decode(); 
    return (this.apReqMessg != null) ? this.apReqMessg.apOptions.get(2) : 0;
  }
  
  boolean useSessionKey() throws KrbException, IOException {
    if (this.apReqMessg == null)
      decode(); 
    return (this.apReqMessg != null) ? this.apReqMessg.apOptions.get(1) : 0;
  }
  
  public EncryptionKey getSubKey() { return this.authenticator.getSubKey(); }
  
  public Integer getSeqNumber() { return this.authenticator.getSeqNumber(); }
  
  public Checksum getChecksum() { return this.authenticator.getChecksum(); }
  
  public byte[] getMessage() { return this.obuf; }
  
  public PrincipalName getClient() { return this.creds.getClient(); }
  
  private void createMessage(APOptions paramAPOptions, Ticket paramTicket, EncryptionKey paramEncryptionKey1, PrincipalName paramPrincipalName, Checksum paramChecksum, KerberosTime paramKerberosTime, EncryptionKey paramEncryptionKey2, SeqNumber paramSeqNumber, AuthorizationData paramAuthorizationData, int paramInt) throws Asn1Exception, IOException, KdcErrException, KrbCryptoException {
    Integer integer = null;
    if (paramSeqNumber != null)
      integer = new Integer(paramSeqNumber.current()); 
    this.authenticator = new Authenticator(paramPrincipalName, paramChecksum, paramKerberosTime.getMicroSeconds(), paramKerberosTime, paramEncryptionKey2, integer, paramAuthorizationData);
    byte[] arrayOfByte = this.authenticator.asn1Encode();
    EncryptedData encryptedData = new EncryptedData(paramEncryptionKey1, arrayOfByte, paramInt);
    this.apReqMessg = new APReq(paramAPOptions, paramTicket, encryptedData);
  }
  
  private static void checkPermittedEType(int paramInt) throws KrbException {
    int[] arrayOfInt = EType.getDefaults("permitted_enctypes");
    if (!EType.isSupported(paramInt, arrayOfInt))
      throw new KrbException(EType.toString(paramInt) + " encryption type not in permitted_enctypes list"); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\KrbApReq.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */