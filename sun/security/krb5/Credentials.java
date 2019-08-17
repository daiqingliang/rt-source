package sun.security.krb5;

import java.io.IOException;
import java.net.InetAddress;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Date;
import java.util.Locale;
import sun.security.action.GetPropertyAction;
import sun.security.krb5.internal.AuthorizationData;
import sun.security.krb5.internal.CredentialsUtil;
import sun.security.krb5.internal.HostAddresses;
import sun.security.krb5.internal.KDCOptions;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.Ticket;
import sun.security.krb5.internal.TicketFlags;
import sun.security.krb5.internal.ccache.Credentials;
import sun.security.krb5.internal.ccache.CredentialsCache;
import sun.security.krb5.internal.crypto.EType;

public class Credentials {
  Ticket ticket;
  
  PrincipalName client;
  
  PrincipalName server;
  
  EncryptionKey key;
  
  TicketFlags flags;
  
  KerberosTime authTime;
  
  KerberosTime startTime;
  
  KerberosTime endTime;
  
  KerberosTime renewTill;
  
  HostAddresses cAddr;
  
  EncryptionKey serviceKey;
  
  AuthorizationData authzData;
  
  private static boolean DEBUG = Krb5.DEBUG;
  
  private static CredentialsCache cache;
  
  static boolean alreadyLoaded = false;
  
  private static boolean alreadyTried = false;
  
  private static native Credentials acquireDefaultNativeCreds(int[] paramArrayOfInt);
  
  public Credentials(Ticket paramTicket, PrincipalName paramPrincipalName1, PrincipalName paramPrincipalName2, EncryptionKey paramEncryptionKey, TicketFlags paramTicketFlags, KerberosTime paramKerberosTime1, KerberosTime paramKerberosTime2, KerberosTime paramKerberosTime3, KerberosTime paramKerberosTime4, HostAddresses paramHostAddresses, AuthorizationData paramAuthorizationData) {
    this(paramTicket, paramPrincipalName1, paramPrincipalName2, paramEncryptionKey, paramTicketFlags, paramKerberosTime1, paramKerberosTime2, paramKerberosTime3, paramKerberosTime4, paramHostAddresses);
    this.authzData = paramAuthorizationData;
  }
  
  public Credentials(Ticket paramTicket, PrincipalName paramPrincipalName1, PrincipalName paramPrincipalName2, EncryptionKey paramEncryptionKey, TicketFlags paramTicketFlags, KerberosTime paramKerberosTime1, KerberosTime paramKerberosTime2, KerberosTime paramKerberosTime3, KerberosTime paramKerberosTime4, HostAddresses paramHostAddresses) {
    this.ticket = paramTicket;
    this.client = paramPrincipalName1;
    this.server = paramPrincipalName2;
    this.key = paramEncryptionKey;
    this.flags = paramTicketFlags;
    this.authTime = paramKerberosTime1;
    this.startTime = paramKerberosTime2;
    this.endTime = paramKerberosTime3;
    this.renewTill = paramKerberosTime4;
    this.cAddr = paramHostAddresses;
  }
  
  public Credentials(byte[] paramArrayOfByte1, String paramString1, String paramString2, byte[] paramArrayOfByte2, int paramInt, boolean[] paramArrayOfBoolean, Date paramDate1, Date paramDate2, Date paramDate3, Date paramDate4, InetAddress[] paramArrayOfInetAddress) throws KrbException, IOException { this(new Ticket(paramArrayOfByte1), new PrincipalName(paramString1, 1), new PrincipalName(paramString2, 2), new EncryptionKey(paramInt, paramArrayOfByte2), (paramArrayOfBoolean == null) ? null : new TicketFlags(paramArrayOfBoolean), (paramDate1 == null) ? null : new KerberosTime(paramDate1), (paramDate2 == null) ? null : new KerberosTime(paramDate2), (paramDate3 == null) ? null : new KerberosTime(paramDate3), (paramDate4 == null) ? null : new KerberosTime(paramDate4), null); }
  
  public final PrincipalName getClient() { return this.client; }
  
  public final PrincipalName getServer() { return this.server; }
  
  public final EncryptionKey getSessionKey() { return this.key; }
  
  public final Date getAuthTime() { return (this.authTime != null) ? this.authTime.toDate() : null; }
  
  public final Date getStartTime() { return (this.startTime != null) ? this.startTime.toDate() : null; }
  
  public final Date getEndTime() { return (this.endTime != null) ? this.endTime.toDate() : null; }
  
  public final Date getRenewTill() { return (this.renewTill != null) ? this.renewTill.toDate() : null; }
  
  public final boolean[] getFlags() { return (this.flags == null) ? null : this.flags.toBooleanArray(); }
  
  public final InetAddress[] getClientAddresses() { return (this.cAddr == null) ? null : this.cAddr.getInetAddresses(); }
  
  public final byte[] getEncoded() {
    byte[] arrayOfByte = null;
    try {
      arrayOfByte = this.ticket.asn1Encode();
    } catch (Asn1Exception asn1Exception) {
      if (DEBUG)
        System.out.println(asn1Exception); 
    } catch (IOException iOException) {
      if (DEBUG)
        System.out.println(iOException); 
    } 
    return arrayOfByte;
  }
  
  public boolean isForwardable() { return this.flags.get(1); }
  
  public boolean isRenewable() { return this.flags.get(8); }
  
  public Ticket getTicket() { return this.ticket; }
  
  public TicketFlags getTicketFlags() { return this.flags; }
  
  public AuthorizationData getAuthzData() { return this.authzData; }
  
  public boolean checkDelegate() { return this.flags.get(13); }
  
  public void resetDelegate() { this.flags.set(13, false); }
  
  public Credentials renew() throws KrbException, IOException {
    KDCOptions kDCOptions = new KDCOptions();
    kDCOptions.set(30, true);
    kDCOptions.set(8, true);
    return (new KrbTgsReq(kDCOptions, this, this.server, null, null, null, null, this.cAddr, null, null, null)).sendAndGetCreds();
  }
  
  public static Credentials acquireTGTFromCache(PrincipalName paramPrincipalName, String paramString) throws KrbException, IOException {
    if (paramString == null) {
      String str = (String)AccessController.doPrivileged(new GetPropertyAction("os.name"));
      if (str.toUpperCase(Locale.ENGLISH).startsWith("WINDOWS") || str.toUpperCase(Locale.ENGLISH).contains("OS X")) {
        Credentials credentials1 = acquireDefaultCreds();
        if (credentials1 == null) {
          if (DEBUG)
            System.out.println(">>> Found no TGT's in LSA"); 
          return null;
        } 
        if (paramPrincipalName != null) {
          if (credentials1.getClient().equals(paramPrincipalName)) {
            if (DEBUG)
              System.out.println(">>> Obtained TGT from LSA: " + credentials1); 
            return credentials1;
          } 
          if (DEBUG)
            System.out.println(">>> LSA contains TGT for " + credentials1.getClient() + " not " + paramPrincipalName); 
          return null;
        } 
        if (DEBUG)
          System.out.println(">>> Obtained TGT from LSA: " + credentials1); 
        return credentials1;
      } 
    } 
    CredentialsCache credentialsCache = CredentialsCache.getInstance(paramPrincipalName, paramString);
    if (credentialsCache == null)
      return null; 
    Credentials credentials = credentialsCache.getDefaultCreds();
    if (credentials == null)
      return null; 
    if (EType.isSupported(credentials.getEType()))
      return credentials.setKrbCreds(); 
    if (DEBUG)
      System.out.println(">>> unsupported key type found the default TGT: " + credentials.getEType()); 
    return null;
  }
  
  public static Credentials acquireDefaultCreds() throws KrbException, IOException {
    Credentials credentials = null;
    if (cache == null)
      cache = CredentialsCache.getInstance(); 
    if (cache != null) {
      Credentials credentials1 = cache.getDefaultCreds();
      if (credentials1 != null) {
        if (DEBUG)
          System.out.println(">>> KrbCreds found the default ticket granting ticket in credential cache."); 
        if (EType.isSupported(credentials1.getEType())) {
          credentials = credentials1.setKrbCreds();
        } else if (DEBUG) {
          System.out.println(">>> unsupported key type found the default TGT: " + credentials1.getEType());
        } 
      } 
    } 
    if (credentials == null) {
      if (!alreadyTried)
        try {
          ensureLoaded();
        } catch (Exception exception) {
          if (DEBUG) {
            System.out.println("Can not load credentials cache");
            exception.printStackTrace();
          } 
          alreadyTried = true;
        }  
      if (alreadyLoaded) {
        if (DEBUG)
          System.out.println(">> Acquire default native Credentials"); 
        try {
          credentials = acquireDefaultNativeCreds(EType.getDefaults("default_tkt_enctypes"));
        } catch (KrbException krbException) {}
      } 
    } 
    return credentials;
  }
  
  public static Credentials acquireServiceCreds(String paramString, Credentials paramCredentials) throws KrbException, IOException { return CredentialsUtil.acquireServiceCreds(paramString, paramCredentials); }
  
  public static Credentials acquireS4U2selfCreds(PrincipalName paramPrincipalName, Credentials paramCredentials) throws KrbException, IOException { return CredentialsUtil.acquireS4U2selfCreds(paramPrincipalName, paramCredentials); }
  
  public static Credentials acquireS4U2proxyCreds(String paramString, Ticket paramTicket, PrincipalName paramPrincipalName, Credentials paramCredentials) throws KrbException, IOException { return CredentialsUtil.acquireS4U2proxyCreds(paramString, paramTicket, paramPrincipalName, paramCredentials); }
  
  public CredentialsCache getCache() { return cache; }
  
  public EncryptionKey getServiceKey() { return this.serviceKey; }
  
  public static void printDebug(Credentials paramCredentials) {
    System.out.println(">>> DEBUG: ----Credentials----");
    System.out.println("\tclient: " + paramCredentials.client.toString());
    System.out.println("\tserver: " + paramCredentials.server.toString());
    System.out.println("\tticket: sname: " + paramCredentials.ticket.sname.toString());
    if (paramCredentials.startTime != null)
      System.out.println("\tstartTime: " + paramCredentials.startTime.getTime()); 
    System.out.println("\tendTime: " + paramCredentials.endTime.getTime());
    System.out.println("        ----Credentials end----");
  }
  
  static void ensureLoaded() {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            if (System.getProperty("os.name").contains("OS X")) {
              System.loadLibrary("osxkrb5");
            } else {
              System.loadLibrary("w2k_lsa_auth");
            } 
            return null;
          }
        });
    alreadyLoaded = true;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer("Credentials:");
    stringBuffer.append("\n      client=").append(this.client);
    stringBuffer.append("\n      server=").append(this.server);
    if (this.authTime != null)
      stringBuffer.append("\n    authTime=").append(this.authTime); 
    if (this.startTime != null)
      stringBuffer.append("\n   startTime=").append(this.startTime); 
    stringBuffer.append("\n     endTime=").append(this.endTime);
    stringBuffer.append("\n   renewTill=").append(this.renewTill);
    stringBuffer.append("\n       flags=").append(this.flags);
    stringBuffer.append("\nEType (skey)=").append(this.key.getEType());
    stringBuffer.append("\n   (tkt key)=").append(this.ticket.encPart.eType);
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\Credentials.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */