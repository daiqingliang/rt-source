package sun.security.krb5.internal;

import java.io.IOException;
import sun.security.krb5.Credentials;
import sun.security.krb5.KrbException;
import sun.security.krb5.KrbTgsReq;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;

public class CredentialsUtil {
  private static boolean DEBUG = Krb5.DEBUG;
  
  public static Credentials acquireS4U2selfCreds(PrincipalName paramPrincipalName, Credentials paramCredentials) throws KrbException, IOException {
    String str1 = paramPrincipalName.getRealmString();
    String str2 = paramCredentials.getClient().getRealmString();
    if (!str1.equals(str2))
      throw new KrbException("Cross realm impersonation not supported"); 
    if (!paramCredentials.isForwardable())
      throw new KrbException("S4U2self needs a FORWARDABLE ticket"); 
    KrbTgsReq krbTgsReq = new KrbTgsReq(paramCredentials, paramCredentials.getClient(), new PAData(129, (new PAForUserEnc(paramPrincipalName, paramCredentials.getSessionKey())).asn1Encode()));
    Credentials credentials = krbTgsReq.sendAndGetCreds();
    if (!credentials.getClient().equals(paramPrincipalName))
      throw new KrbException("S4U2self request not honored by KDC"); 
    if (!credentials.isForwardable())
      throw new KrbException("S4U2self ticket must be FORWARDABLE"); 
    return credentials;
  }
  
  public static Credentials acquireS4U2proxyCreds(String paramString, Ticket paramTicket, PrincipalName paramPrincipalName, Credentials paramCredentials) throws KrbException, IOException {
    KrbTgsReq krbTgsReq = new KrbTgsReq(paramCredentials, paramTicket, new PrincipalName(paramString));
    Credentials credentials = krbTgsReq.sendAndGetCreds();
    if (!credentials.getClient().equals(paramPrincipalName))
      throw new KrbException("S4U2proxy request not honored by KDC"); 
    return credentials;
  }
  
  public static Credentials acquireServiceCreds(String paramString, Credentials paramCredentials) throws KrbException, IOException {
    PrincipalName principalName = new PrincipalName(paramString);
    String str1 = principalName.getRealmString();
    String str2 = paramCredentials.getClient().getRealmString();
    if (str2.equals(str1)) {
      if (DEBUG)
        System.out.println(">>> Credentials acquireServiceCreds: same realm"); 
      return serviceCreds(principalName, paramCredentials);
    } 
    Credentials credentials1 = null;
    boolean[] arrayOfBoolean = new boolean[1];
    Credentials credentials2 = getTGTforRealm(str2, str1, paramCredentials, arrayOfBoolean);
    if (credentials2 != null) {
      if (DEBUG) {
        System.out.println(">>> Credentials acquireServiceCreds: got right tgt");
        System.out.println(">>> Credentials acquireServiceCreds: obtaining service creds for " + principalName);
      } 
      try {
        credentials1 = serviceCreds(principalName, credentials2);
      } catch (Exception exception) {
        if (DEBUG)
          System.out.println(exception); 
        credentials1 = null;
      } 
    } 
    if (credentials1 != null) {
      if (DEBUG) {
        System.out.println(">>> Credentials acquireServiceCreds: returning creds:");
        Credentials.printDebug(credentials1);
      } 
      if (!arrayOfBoolean[0])
        credentials1.resetDelegate(); 
      return credentials1;
    } 
    throw new KrbApErrException(63, "No service creds");
  }
  
  private static Credentials getTGTforRealm(String paramString1, String paramString2, Credentials paramCredentials, boolean[] paramArrayOfBoolean) throws KrbException {
    String[] arrayOfString = Realm.getRealmsList(paramString1, paramString2);
    byte b1 = 0;
    byte b2 = 0;
    Credentials credentials1 = null;
    Credentials credentials2 = null;
    Credentials credentials3 = null;
    PrincipalName principalName = null;
    String str = null;
    paramArrayOfBoolean[0] = true;
    credentials1 = paramCredentials;
    b1 = 0;
    while (b1 < arrayOfString.length) {
      principalName = PrincipalName.tgsService(paramString2, arrayOfString[b1]);
      if (DEBUG)
        System.out.println(">>> Credentials acquireServiceCreds: main loop: [" + b1 + "] tempService=" + principalName); 
      try {
        credentials2 = serviceCreds(principalName, credentials1);
      } catch (Exception exception) {
        credentials2 = null;
      } 
      if (credentials2 == null) {
        if (DEBUG)
          System.out.println(">>> Credentials acquireServiceCreds: no tgt; searching thru capath"); 
        credentials2 = null;
        for (b2 = b1 + 1; credentials2 == null && b2 < arrayOfString.length; b2++) {
          principalName = PrincipalName.tgsService(arrayOfString[b2], arrayOfString[b1]);
          if (DEBUG)
            System.out.println(">>> Credentials acquireServiceCreds: inner loop: [" + b2 + "] tempService=" + principalName); 
          try {
            credentials2 = serviceCreds(principalName, credentials1);
          } catch (Exception exception) {
            credentials2 = null;
          } 
        } 
      } 
      if (credentials2 == null) {
        if (DEBUG)
          System.out.println(">>> Credentials acquireServiceCreds: no tgt; cannot get creds"); 
        break;
      } 
      str = credentials2.getServer().getInstanceComponent();
      if (paramArrayOfBoolean[0] && !credentials2.checkDelegate()) {
        if (DEBUG)
          System.out.println(">>> Credentials acquireServiceCreds: global OK-AS-DELEGATE turned off at " + credentials2.getServer()); 
        paramArrayOfBoolean[0] = false;
      } 
      if (DEBUG)
        System.out.println(">>> Credentials acquireServiceCreds: got tgt"); 
      if (str.equals(paramString2)) {
        credentials3 = credentials2;
        break;
      } 
      for (b2 = b1 + 1; b2 < arrayOfString.length && !str.equals(arrayOfString[b2]); b2++);
      if (b2 < arrayOfString.length) {
        b1 = b2;
        credentials1 = credentials2;
        if (DEBUG)
          System.out.println(">>> Credentials acquireServiceCreds: continuing with main loop counter reset to " + b1); 
      } 
    } 
    return credentials3;
  }
  
  private static Credentials serviceCreds(PrincipalName paramPrincipalName, Credentials paramCredentials) throws KrbException, IOException { return (new KrbTgsReq(paramCredentials, paramPrincipalName)).sendAndGetCreds(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\CredentialsUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */