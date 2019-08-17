package sun.security.jgss.krb5;

import java.security.Provider;
import javax.security.auth.DestroyFailedException;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.krb5.internal.Ticket;

public class Krb5ProxyCredential implements Krb5CredElement {
  public final Krb5InitCredential self;
  
  private final Krb5NameElement client;
  
  public final Ticket tkt;
  
  Krb5ProxyCredential(Krb5InitCredential paramKrb5InitCredential, Krb5NameElement paramKrb5NameElement, Ticket paramTicket) {
    this.self = paramKrb5InitCredential;
    this.tkt = paramTicket;
    this.client = paramKrb5NameElement;
  }
  
  public final Krb5NameElement getName() throws GSSException { return this.client; }
  
  public int getInitLifetime() throws GSSException { return this.self.getInitLifetime(); }
  
  public int getAcceptLifetime() throws GSSException { return 0; }
  
  public boolean isInitiatorCredential() throws GSSException { return true; }
  
  public boolean isAcceptorCredential() throws GSSException { return false; }
  
  public final Oid getMechanism() { return Krb5MechFactory.GSS_KRB5_MECH_OID; }
  
  public final Provider getProvider() { return Krb5MechFactory.PROVIDER; }
  
  public void dispose() throws GSSException {
    try {
      this.self.destroy();
    } catch (DestroyFailedException destroyFailedException) {
      GSSException gSSException = new GSSException(11, -1, "Could not destroy credentials - " + destroyFailedException.getMessage());
      gSSException.initCause(destroyFailedException);
    } 
  }
  
  public GSSCredentialSpi impersonate(GSSNameSpi paramGSSNameSpi) throws GSSException { throw new GSSException(11, -1, "Only an initiate credentials can impersonate"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\krb5\Krb5ProxyCredential.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */