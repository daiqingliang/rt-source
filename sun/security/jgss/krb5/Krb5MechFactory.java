package sun.security.jgss.krb5;

import java.security.Provider;
import java.util.Vector;
import javax.security.auth.kerberos.ServicePermission;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import sun.security.jgss.GSSCaller;
import sun.security.jgss.GSSUtil;
import sun.security.jgss.SunProvider;
import sun.security.jgss.spi.GSSContextSpi;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.jgss.spi.MechanismFactory;

public final class Krb5MechFactory implements MechanismFactory {
  private static final boolean DEBUG = Krb5Util.DEBUG;
  
  static final Provider PROVIDER = new SunProvider();
  
  static final Oid GSS_KRB5_MECH_OID = createOid("1.2.840.113554.1.2.2");
  
  static final Oid NT_GSS_KRB5_PRINCIPAL = createOid("1.2.840.113554.1.2.2.1");
  
  private static Oid[] nameTypes = { GSSName.NT_USER_NAME, GSSName.NT_HOSTBASED_SERVICE, GSSName.NT_EXPORT_NAME, NT_GSS_KRB5_PRINCIPAL };
  
  private final GSSCaller caller;
  
  private static Krb5CredElement getCredFromSubject(GSSNameSpi paramGSSNameSpi, boolean paramBoolean) throws GSSException {
    Vector vector = GSSUtil.searchSubject(paramGSSNameSpi, GSS_KRB5_MECH_OID, paramBoolean, paramBoolean ? Krb5InitCredential.class : Krb5AcceptCredential.class);
    Krb5CredElement krb5CredElement = (vector == null || vector.isEmpty()) ? null : (Krb5CredElement)vector.firstElement();
    if (krb5CredElement != null)
      if (paramBoolean) {
        checkInitCredPermission((Krb5NameElement)krb5CredElement.getName());
      } else {
        checkAcceptCredPermission((Krb5NameElement)krb5CredElement.getName(), paramGSSNameSpi);
      }  
    return krb5CredElement;
  }
  
  public Krb5MechFactory(GSSCaller paramGSSCaller) { this.caller = paramGSSCaller; }
  
  public GSSNameSpi getNameElement(String paramString, Oid paramOid) throws GSSException { return Krb5NameElement.getInstance(paramString, paramOid); }
  
  public GSSNameSpi getNameElement(byte[] paramArrayOfByte, Oid paramOid) throws GSSException { return Krb5NameElement.getInstance(new String(paramArrayOfByte), paramOid); }
  
  public GSSCredentialSpi getCredentialElement(GSSNameSpi paramGSSNameSpi, int paramInt1, int paramInt2, int paramInt3) throws GSSException {
    if (paramGSSNameSpi != null && !(paramGSSNameSpi instanceof Krb5NameElement))
      paramGSSNameSpi = Krb5NameElement.getInstance(paramGSSNameSpi.toString(), paramGSSNameSpi.getStringNameType()); 
    Krb5CredElement krb5CredElement = getCredFromSubject(paramGSSNameSpi, (paramInt3 != 2));
    if (krb5CredElement == null)
      if (paramInt3 == 1 || paramInt3 == 0) {
        krb5CredElement = Krb5InitCredential.getInstance(this.caller, (Krb5NameElement)paramGSSNameSpi, paramInt1);
        checkInitCredPermission((Krb5NameElement)krb5CredElement.getName());
      } else if (paramInt3 == 2) {
        krb5CredElement = Krb5AcceptCredential.getInstance(this.caller, (Krb5NameElement)paramGSSNameSpi);
        checkAcceptCredPermission((Krb5NameElement)krb5CredElement.getName(), paramGSSNameSpi);
      } else {
        throw new GSSException(11, -1, "Unknown usage mode requested");
      }  
    return krb5CredElement;
  }
  
  public static void checkInitCredPermission(Krb5NameElement paramKrb5NameElement) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      String str1 = paramKrb5NameElement.getKrb5PrincipalName().getRealmAsString();
      String str2 = new String("krbtgt/" + str1 + '@' + str1);
      ServicePermission servicePermission = new ServicePermission(str2, "initiate");
      try {
        securityManager.checkPermission(servicePermission);
      } catch (SecurityException securityException) {
        if (DEBUG)
          System.out.println("Permission to initiatekerberos init credential" + securityException.getMessage()); 
        throw securityException;
      } 
    } 
  }
  
  public static void checkAcceptCredPermission(Krb5NameElement paramKrb5NameElement, GSSNameSpi paramGSSNameSpi) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null && paramKrb5NameElement != null) {
      ServicePermission servicePermission = new ServicePermission(paramKrb5NameElement.getKrb5PrincipalName().getName(), "accept");
      try {
        securityManager.checkPermission(servicePermission);
      } catch (SecurityException securityException) {
        if (paramGSSNameSpi == null)
          securityException = new SecurityException("No permission to acquire Kerberos accept credential"); 
        throw securityException;
      } 
    } 
  }
  
  public GSSContextSpi getMechanismContext(GSSNameSpi paramGSSNameSpi, GSSCredentialSpi paramGSSCredentialSpi, int paramInt) throws GSSException {
    if (paramGSSNameSpi != null && !(paramGSSNameSpi instanceof Krb5NameElement))
      paramGSSNameSpi = Krb5NameElement.getInstance(paramGSSNameSpi.toString(), paramGSSNameSpi.getStringNameType()); 
    if (paramGSSCredentialSpi == null)
      paramGSSCredentialSpi = getCredentialElement(null, paramInt, 0, 1); 
    return new Krb5Context(this.caller, (Krb5NameElement)paramGSSNameSpi, (Krb5CredElement)paramGSSCredentialSpi, paramInt);
  }
  
  public GSSContextSpi getMechanismContext(GSSCredentialSpi paramGSSCredentialSpi) throws GSSException {
    if (paramGSSCredentialSpi == null)
      paramGSSCredentialSpi = getCredentialElement(null, 0, 2147483647, 2); 
    return new Krb5Context(this.caller, (Krb5CredElement)paramGSSCredentialSpi);
  }
  
  public GSSContextSpi getMechanismContext(byte[] paramArrayOfByte) throws GSSException { return new Krb5Context(this.caller, paramArrayOfByte); }
  
  public final Oid getMechanismOid() { return GSS_KRB5_MECH_OID; }
  
  public Provider getProvider() { return PROVIDER; }
  
  public Oid[] getNameTypes() { return nameTypes; }
  
  private static Oid createOid(String paramString) {
    Oid oid = null;
    try {
      oid = new Oid(paramString);
    } catch (GSSException gSSException) {}
    return oid;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\krb5\Krb5MechFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */