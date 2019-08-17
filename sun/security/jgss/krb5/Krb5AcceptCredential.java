package sun.security.jgss.krb5;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.Provider;
import javax.security.auth.DestroyFailedException;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import sun.security.jgss.GSSCaller;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.krb5.Credentials;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.PrincipalName;

public class Krb5AcceptCredential implements Krb5CredElement {
  private final Krb5NameElement name;
  
  private final ServiceCreds screds;
  
  private Krb5AcceptCredential(Krb5NameElement paramKrb5NameElement, ServiceCreds paramServiceCreds) {
    this.name = paramKrb5NameElement;
    this.screds = paramServiceCreds;
  }
  
  static Krb5AcceptCredential getInstance(final GSSCaller caller, Krb5NameElement paramKrb5NameElement) throws GSSException {
    final String serverPrinc = (paramKrb5NameElement == null) ? null : paramKrb5NameElement.getKrb5PrincipalName().getName();
    final AccessControlContext acc = AccessController.getContext();
    ServiceCreds serviceCreds = null;
    try {
      serviceCreds = (ServiceCreds)AccessController.doPrivileged(new PrivilegedExceptionAction<ServiceCreds>() {
            public ServiceCreds run() throws Exception { return Krb5Util.getServiceCreds((caller == GSSCaller.CALLER_UNKNOWN) ? GSSCaller.CALLER_ACCEPT : caller, serverPrinc, acc); }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      GSSException gSSException = new GSSException(13, -1, "Attempt to obtain new ACCEPT credentials failed!");
      gSSException.initCause(privilegedActionException.getException());
      throw gSSException;
    } 
    if (serviceCreds == null)
      throw new GSSException(13, -1, "Failed to find any Kerberos credentails"); 
    if (paramKrb5NameElement == null) {
      String str1 = serviceCreds.getName();
      if (str1 != null)
        paramKrb5NameElement = Krb5NameElement.getInstance(str1, Krb5MechFactory.NT_GSS_KRB5_PRINCIPAL); 
    } 
    return new Krb5AcceptCredential(paramKrb5NameElement, serviceCreds);
  }
  
  public final GSSNameSpi getName() throws GSSException { return this.name; }
  
  public int getInitLifetime() throws GSSException { return 0; }
  
  public int getAcceptLifetime() throws GSSException { return Integer.MAX_VALUE; }
  
  public boolean isInitiatorCredential() throws GSSException { return false; }
  
  public boolean isAcceptorCredential() throws GSSException { return true; }
  
  public final Oid getMechanism() { return Krb5MechFactory.GSS_KRB5_MECH_OID; }
  
  public final Provider getProvider() { return Krb5MechFactory.PROVIDER; }
  
  public EncryptionKey[] getKrb5EncryptionKeys(PrincipalName paramPrincipalName) { return this.screds.getEKeys(paramPrincipalName); }
  
  public void dispose() throws GSSException {
    try {
      destroy();
    } catch (DestroyFailedException destroyFailedException) {
      GSSException gSSException = new GSSException(11, -1, "Could not destroy credentials - " + destroyFailedException.getMessage());
      gSSException.initCause(destroyFailedException);
    } 
  }
  
  public void destroy() throws GSSException { this.screds.destroy(); }
  
  public GSSCredentialSpi impersonate(GSSNameSpi paramGSSNameSpi) throws GSSException {
    Credentials credentials = this.screds.getInitCred();
    if (credentials != null)
      return Krb5InitCredential.getInstance(this.name, credentials).impersonate(paramGSSNameSpi); 
    throw new GSSException(11, -1, "Only an initiate credentials can impersonate");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\krb5\Krb5AcceptCredential.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */