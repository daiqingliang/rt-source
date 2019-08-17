package sun.security.jgss.spnego;

import java.security.Provider;
import java.util.Vector;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import sun.security.jgss.GSSCaller;
import sun.security.jgss.GSSManagerImpl;
import sun.security.jgss.GSSUtil;
import sun.security.jgss.ProviderList;
import sun.security.jgss.SunProvider;
import sun.security.jgss.krb5.Krb5AcceptCredential;
import sun.security.jgss.krb5.Krb5InitCredential;
import sun.security.jgss.krb5.Krb5MechFactory;
import sun.security.jgss.krb5.Krb5NameElement;
import sun.security.jgss.spi.GSSContextSpi;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.jgss.spi.MechanismFactory;

public final class SpNegoMechFactory implements MechanismFactory {
  static final Provider PROVIDER = new SunProvider();
  
  static final Oid GSS_SPNEGO_MECH_OID = GSSUtil.createOid("1.3.6.1.5.5.2");
  
  private static Oid[] nameTypes = { GSSName.NT_USER_NAME, GSSName.NT_HOSTBASED_SERVICE, GSSName.NT_EXPORT_NAME };
  
  private static final Oid DEFAULT_SPNEGO_MECH_OID = ProviderList.DEFAULT_MECH_OID.equals(GSS_SPNEGO_MECH_OID) ? GSSUtil.GSS_KRB5_MECH_OID : ProviderList.DEFAULT_MECH_OID;
  
  final GSSManagerImpl manager;
  
  final Oid[] availableMechs;
  
  private static SpNegoCredElement getCredFromSubject(GSSNameSpi paramGSSNameSpi, boolean paramBoolean) throws GSSException {
    Vector vector = GSSUtil.searchSubject(paramGSSNameSpi, GSS_SPNEGO_MECH_OID, paramBoolean, SpNegoCredElement.class);
    SpNegoCredElement spNegoCredElement = (vector == null || vector.isEmpty()) ? null : (SpNegoCredElement)vector.firstElement();
    if (spNegoCredElement != null) {
      GSSCredentialSpi gSSCredentialSpi = spNegoCredElement.getInternalCred();
      if (GSSUtil.isKerberosMech(gSSCredentialSpi.getMechanism()))
        if (paramBoolean) {
          Krb5InitCredential krb5InitCredential = (Krb5InitCredential)gSSCredentialSpi;
          Krb5MechFactory.checkInitCredPermission((Krb5NameElement)krb5InitCredential.getName());
        } else {
          Krb5AcceptCredential krb5AcceptCredential = (Krb5AcceptCredential)gSSCredentialSpi;
          Krb5MechFactory.checkAcceptCredPermission((Krb5NameElement)krb5AcceptCredential.getName(), paramGSSNameSpi);
        }  
    } 
    return spNegoCredElement;
  }
  
  public SpNegoMechFactory(GSSCaller paramGSSCaller) {
    this.manager = new GSSManagerImpl(paramGSSCaller, false);
    Oid[] arrayOfOid = this.manager.getMechs();
    this.availableMechs = new Oid[arrayOfOid.length - 1];
    byte b1 = 0;
    byte b2 = 0;
    while (b1 < arrayOfOid.length) {
      if (!arrayOfOid[b1].equals(GSS_SPNEGO_MECH_OID))
        this.availableMechs[b2++] = arrayOfOid[b1]; 
      b1++;
    } 
    for (b1 = 0; b1 < this.availableMechs.length; b1++) {
      if (this.availableMechs[b1].equals(DEFAULT_SPNEGO_MECH_OID)) {
        if (b1 != 0) {
          this.availableMechs[b1] = this.availableMechs[0];
          this.availableMechs[0] = DEFAULT_SPNEGO_MECH_OID;
        } 
        break;
      } 
    } 
  }
  
  public GSSNameSpi getNameElement(String paramString, Oid paramOid) throws GSSException { return this.manager.getNameElement(paramString, paramOid, DEFAULT_SPNEGO_MECH_OID); }
  
  public GSSNameSpi getNameElement(byte[] paramArrayOfByte, Oid paramOid) throws GSSException { return this.manager.getNameElement(paramArrayOfByte, paramOid, DEFAULT_SPNEGO_MECH_OID); }
  
  public GSSCredentialSpi getCredentialElement(GSSNameSpi paramGSSNameSpi, int paramInt1, int paramInt2, int paramInt3) throws GSSException {
    SpNegoCredElement spNegoCredElement = getCredFromSubject(paramGSSNameSpi, (paramInt3 != 2));
    if (spNegoCredElement == null)
      spNegoCredElement = new SpNegoCredElement(this.manager.getCredentialElement(paramGSSNameSpi, paramInt1, paramInt2, null, paramInt3)); 
    return spNegoCredElement;
  }
  
  public GSSContextSpi getMechanismContext(GSSNameSpi paramGSSNameSpi, GSSCredentialSpi paramGSSCredentialSpi, int paramInt) throws GSSException {
    if (paramGSSCredentialSpi == null) {
      paramGSSCredentialSpi = getCredFromSubject(null, true);
    } else if (!(paramGSSCredentialSpi instanceof SpNegoCredElement)) {
      SpNegoCredElement spNegoCredElement = new SpNegoCredElement(paramGSSCredentialSpi);
      return new SpNegoContext(this, paramGSSNameSpi, spNegoCredElement, paramInt);
    } 
    return new SpNegoContext(this, paramGSSNameSpi, paramGSSCredentialSpi, paramInt);
  }
  
  public GSSContextSpi getMechanismContext(GSSCredentialSpi paramGSSCredentialSpi) throws GSSException {
    if (paramGSSCredentialSpi == null) {
      paramGSSCredentialSpi = getCredFromSubject(null, false);
    } else if (!(paramGSSCredentialSpi instanceof SpNegoCredElement)) {
      SpNegoCredElement spNegoCredElement = new SpNegoCredElement(paramGSSCredentialSpi);
      return new SpNegoContext(this, spNegoCredElement);
    } 
    return new SpNegoContext(this, paramGSSCredentialSpi);
  }
  
  public GSSContextSpi getMechanismContext(byte[] paramArrayOfByte) throws GSSException { return new SpNegoContext(this, paramArrayOfByte); }
  
  public final Oid getMechanismOid() { return GSS_SPNEGO_MECH_OID; }
  
  public Provider getProvider() { return PROVIDER; }
  
  public Oid[] getNameTypes() { return nameTypes; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\spnego\SpNegoMechFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */