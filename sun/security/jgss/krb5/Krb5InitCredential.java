package sun.security.jgss.krb5;

import java.io.IOException;
import java.net.InetAddress;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.Provider;
import java.util.Date;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.kerberos.KerberosTicket;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import sun.security.jgss.GSSCaller;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.krb5.Credentials;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;

public class Krb5InitCredential extends KerberosTicket implements Krb5CredElement {
  private static final long serialVersionUID = 7723415700837898232L;
  
  private Krb5NameElement name;
  
  private Credentials krb5Credentials;
  
  private Krb5InitCredential(Krb5NameElement paramKrb5NameElement, byte[] paramArrayOfByte1, KerberosPrincipal paramKerberosPrincipal1, KerberosPrincipal paramKerberosPrincipal2, byte[] paramArrayOfByte2, int paramInt, boolean[] paramArrayOfBoolean, Date paramDate1, Date paramDate2, Date paramDate3, Date paramDate4, InetAddress[] paramArrayOfInetAddress) throws GSSException {
    super(paramArrayOfByte1, paramKerberosPrincipal1, paramKerberosPrincipal2, paramArrayOfByte2, paramInt, paramArrayOfBoolean, paramDate1, paramDate2, paramDate3, paramDate4, paramArrayOfInetAddress);
    this.name = paramKrb5NameElement;
    try {
      this.krb5Credentials = new Credentials(paramArrayOfByte1, paramKerberosPrincipal1.getName(), paramKerberosPrincipal2.getName(), paramArrayOfByte2, paramInt, paramArrayOfBoolean, paramDate1, paramDate2, paramDate3, paramDate4, paramArrayOfInetAddress);
    } catch (KrbException krbException) {
      throw new GSSException(13, -1, krbException.getMessage());
    } catch (IOException iOException) {
      throw new GSSException(13, -1, iOException.getMessage());
    } 
  }
  
  private Krb5InitCredential(Krb5NameElement paramKrb5NameElement, Credentials paramCredentials, byte[] paramArrayOfByte1, KerberosPrincipal paramKerberosPrincipal1, KerberosPrincipal paramKerberosPrincipal2, byte[] paramArrayOfByte2, int paramInt, boolean[] paramArrayOfBoolean, Date paramDate1, Date paramDate2, Date paramDate3, Date paramDate4, InetAddress[] paramArrayOfInetAddress) throws GSSException {
    super(paramArrayOfByte1, paramKerberosPrincipal1, paramKerberosPrincipal2, paramArrayOfByte2, paramInt, paramArrayOfBoolean, paramDate1, paramDate2, paramDate3, paramDate4, paramArrayOfInetAddress);
    this.name = paramKrb5NameElement;
    this.krb5Credentials = paramCredentials;
  }
  
  static Krb5InitCredential getInstance(GSSCaller paramGSSCaller, Krb5NameElement paramKrb5NameElement, int paramInt) throws GSSException {
    KerberosTicket kerberosTicket = getTgt(paramGSSCaller, paramKrb5NameElement, paramInt);
    if (kerberosTicket == null)
      throw new GSSException(13, -1, "Failed to find any Kerberos tgt"); 
    if (paramKrb5NameElement == null) {
      String str = kerberosTicket.getClient().getName();
      paramKrb5NameElement = Krb5NameElement.getInstance(str, Krb5MechFactory.NT_GSS_KRB5_PRINCIPAL);
    } 
    return new Krb5InitCredential(paramKrb5NameElement, kerberosTicket.getEncoded(), kerberosTicket.getClient(), kerberosTicket.getServer(), kerberosTicket.getSessionKey().getEncoded(), kerberosTicket.getSessionKeyType(), kerberosTicket.getFlags(), kerberosTicket.getAuthTime(), kerberosTicket.getStartTime(), kerberosTicket.getEndTime(), kerberosTicket.getRenewTill(), kerberosTicket.getClientAddresses());
  }
  
  static Krb5InitCredential getInstance(Krb5NameElement paramKrb5NameElement, Credentials paramCredentials) throws GSSException {
    EncryptionKey encryptionKey = paramCredentials.getSessionKey();
    PrincipalName principalName1 = paramCredentials.getClient();
    PrincipalName principalName2 = paramCredentials.getServer();
    KerberosPrincipal kerberosPrincipal1 = null;
    KerberosPrincipal kerberosPrincipal2 = null;
    Krb5NameElement krb5NameElement = null;
    if (principalName1 != null) {
      String str = principalName1.getName();
      krb5NameElement = Krb5NameElement.getInstance(str, Krb5MechFactory.NT_GSS_KRB5_PRINCIPAL);
      kerberosPrincipal1 = new KerberosPrincipal(str);
    } 
    if (principalName2 != null)
      kerberosPrincipal2 = new KerberosPrincipal(principalName2.getName(), 2); 
    return new Krb5InitCredential(krb5NameElement, paramCredentials, paramCredentials.getEncoded(), kerberosPrincipal1, kerberosPrincipal2, encryptionKey.getBytes(), encryptionKey.getEType(), paramCredentials.getFlags(), paramCredentials.getAuthTime(), paramCredentials.getStartTime(), paramCredentials.getEndTime(), paramCredentials.getRenewTill(), paramCredentials.getClientAddresses());
  }
  
  public final GSSNameSpi getName() throws GSSException { return this.name; }
  
  public int getInitLifetime() throws GSSException {
    int i = 0;
    Date date = getEndTime();
    if (date == null)
      return 0; 
    i = (int)(date.getTime() - (new Date()).getTime());
    return i / 1000;
  }
  
  public int getAcceptLifetime() throws GSSException { return 0; }
  
  public boolean isInitiatorCredential() throws GSSException { return true; }
  
  public boolean isAcceptorCredential() throws GSSException { return false; }
  
  public final Oid getMechanism() { return Krb5MechFactory.GSS_KRB5_MECH_OID; }
  
  public final Provider getProvider() { return Krb5MechFactory.PROVIDER; }
  
  Credentials getKrb5Credentials() { return this.krb5Credentials; }
  
  public void dispose() throws GSSException {
    try {
      destroy();
    } catch (DestroyFailedException destroyFailedException) {
      GSSException gSSException = new GSSException(11, -1, "Could not destroy credentials - " + destroyFailedException.getMessage());
      gSSException.initCause(destroyFailedException);
    } 
  }
  
  private static KerberosTicket getTgt(GSSCaller paramGSSCaller, Krb5NameElement paramKrb5NameElement, int paramInt) throws GSSException {
    final String clientPrincipal;
    if (paramKrb5NameElement != null) {
      str = paramKrb5NameElement.getKrb5PrincipalName().getName();
    } else {
      str = null;
    } 
    final AccessControlContext acc = AccessController.getContext();
    try {
      final GSSCaller realCaller = (paramGSSCaller == GSSCaller.CALLER_UNKNOWN) ? GSSCaller.CALLER_INITIATE : paramGSSCaller;
      return (KerberosTicket)AccessController.doPrivileged(new PrivilegedExceptionAction<KerberosTicket>() {
            public KerberosTicket run() throws Exception { return Krb5Util.getTicket(realCaller, clientPrincipal, null, acc); }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      GSSException gSSException = new GSSException(13, -1, "Attempt to obtain new INITIATE credentials failed! (" + privilegedActionException.getMessage() + ")");
      gSSException.initCause(privilegedActionException.getException());
      throw gSSException;
    } 
  }
  
  public GSSCredentialSpi impersonate(GSSNameSpi paramGSSNameSpi) throws GSSException {
    try {
      Krb5NameElement krb5NameElement = (Krb5NameElement)paramGSSNameSpi;
      Credentials credentials = Credentials.acquireS4U2selfCreds(krb5NameElement.getKrb5PrincipalName(), this.krb5Credentials);
      return new Krb5ProxyCredential(this, krb5NameElement, credentials.getTicket());
    } catch (IOException|KrbException iOException) {
      GSSException gSSException = new GSSException(11, -1, "Attempt to obtain S4U2self credentials failed!");
      gSSException.initCause(iOException);
      throw gSSException;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\krb5\Krb5InitCredential.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */