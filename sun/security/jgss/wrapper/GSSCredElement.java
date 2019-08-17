package sun.security.jgss.wrapper;

import java.security.Provider;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import sun.security.jgss.GSSUtil;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;

public class GSSCredElement implements GSSCredentialSpi {
  private int usage;
  
  long pCred;
  
  private GSSNameElement name = null;
  
  private GSSLibStub cStub;
  
  void doServicePermCheck() throws GSSException {
    if (GSSUtil.isKerberosMech(this.cStub.getMech()) && System.getSecurityManager() != null) {
      if (isInitiatorCredential()) {
        String str = Krb5Util.getTGSName(this.name);
        Krb5Util.checkServicePermission(str, "initiate");
      } 
      if (isAcceptorCredential() && this.name != GSSNameElement.DEF_ACCEPTOR) {
        String str = this.name.getKrbName();
        Krb5Util.checkServicePermission(str, "accept");
      } 
    } 
  }
  
  GSSCredElement(long paramLong, GSSNameElement paramGSSNameElement, Oid paramOid) throws GSSException {
    this.pCred = paramLong;
    this.cStub = GSSLibStub.getInstance(paramOid);
    this.usage = 1;
    this.name = paramGSSNameElement;
  }
  
  GSSCredElement(GSSNameElement paramGSSNameElement, int paramInt1, int paramInt2, GSSLibStub paramGSSLibStub) throws GSSException {
    this.cStub = paramGSSLibStub;
    this.usage = paramInt2;
    if (paramGSSNameElement != null) {
      this.name = paramGSSNameElement;
      doServicePermCheck();
      this.pCred = this.cStub.acquireCred(this.name.pName, paramInt1, paramInt2);
    } else {
      this.pCred = this.cStub.acquireCred(0L, paramInt1, paramInt2);
      this.name = new GSSNameElement(this.cStub.getCredName(this.pCred), this.cStub);
      doServicePermCheck();
    } 
  }
  
  public Provider getProvider() { return SunNativeProvider.INSTANCE; }
  
  public void dispose() throws GSSException {
    this.name = null;
    if (this.pCred != 0L)
      this.pCred = this.cStub.releaseCred(this.pCred); 
  }
  
  public GSSNameElement getName() throws GSSException { return (this.name == GSSNameElement.DEF_ACCEPTOR) ? null : this.name; }
  
  public int getInitLifetime() throws GSSException { return isInitiatorCredential() ? this.cStub.getCredTime(this.pCred) : 0; }
  
  public int getAcceptLifetime() throws GSSException { return isAcceptorCredential() ? this.cStub.getCredTime(this.pCred) : 0; }
  
  public boolean isInitiatorCredential() { return (this.usage != 2); }
  
  public boolean isAcceptorCredential() { return (this.usage != 1); }
  
  public Oid getMechanism() { return this.cStub.getMech(); }
  
  public String toString() { return "N/A"; }
  
  protected void finalize() throws GSSException { dispose(); }
  
  public GSSCredentialSpi impersonate(GSSNameSpi paramGSSNameSpi) throws GSSException { throw new GSSException(11, -1, "Not supported yet"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\wrapper\GSSCredElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */