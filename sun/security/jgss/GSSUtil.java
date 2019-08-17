package sun.security.jgss;

import com.sun.security.auth.callback.TextCallbackHandler;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.Security;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosKey;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.kerberos.KerberosTicket;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import sun.net.www.protocol.http.spnego.NegotiateCallbackHandler;
import sun.security.action.GetBooleanAction;
import sun.security.action.GetPropertyAction;
import sun.security.jgss.krb5.Krb5NameElement;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.jgss.spnego.SpNegoCredElement;

public class GSSUtil {
  public static final Oid GSS_KRB5_MECH_OID = createOid("1.2.840.113554.1.2.2");
  
  public static final Oid GSS_KRB5_MECH_OID2 = createOid("1.3.5.1.5.2");
  
  public static final Oid GSS_KRB5_MECH_OID_MS = createOid("1.2.840.48018.1.2.2");
  
  public static final Oid GSS_SPNEGO_MECH_OID = createOid("1.3.6.1.5.5.2");
  
  public static final Oid NT_GSS_KRB5_PRINCIPAL = createOid("1.2.840.113554.1.2.2.1");
  
  private static final String DEFAULT_HANDLER = "auth.login.defaultCallbackHandler";
  
  static final boolean DEBUG = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.security.jgss.debug"))).booleanValue();
  
  static void debug(String paramString) {
    if (DEBUG) {
      assert paramString != null;
      System.out.println(paramString);
    } 
  }
  
  public static Oid createOid(String paramString) {
    try {
      return new Oid(paramString);
    } catch (GSSException gSSException) {
      debug("Ignored invalid OID: " + paramString);
      return null;
    } 
  }
  
  public static boolean isSpNegoMech(Oid paramOid) { return GSS_SPNEGO_MECH_OID.equals(paramOid); }
  
  public static boolean isKerberosMech(Oid paramOid) { return (GSS_KRB5_MECH_OID.equals(paramOid) || GSS_KRB5_MECH_OID2.equals(paramOid) || GSS_KRB5_MECH_OID_MS.equals(paramOid)); }
  
  public static String getMechStr(Oid paramOid) { return isSpNegoMech(paramOid) ? "SPNEGO" : (isKerberosMech(paramOid) ? "Kerberos V5" : paramOid.toString()); }
  
  public static Subject getSubject(GSSName paramGSSName, GSSCredential paramGSSCredential) {
    HashSet hashSet1 = null;
    HashSet hashSet2 = new HashSet();
    Set set = null;
    HashSet hashSet3 = new HashSet();
    if (paramGSSName instanceof GSSNameImpl)
      try {
        GSSNameSpi gSSNameSpi = ((GSSNameImpl)paramGSSName).getElement(GSS_KRB5_MECH_OID);
        String str = gSSNameSpi.toString();
        if (gSSNameSpi instanceof Krb5NameElement)
          str = ((Krb5NameElement)gSSNameSpi).getKrb5PrincipalName().getName(); 
        KerberosPrincipal kerberosPrincipal = new KerberosPrincipal(str);
        hashSet3.add(kerberosPrincipal);
      } catch (GSSException gSSException) {
        debug("Skipped name " + paramGSSName + " due to " + gSSException);
      }  
    if (paramGSSCredential instanceof GSSCredentialImpl) {
      set = ((GSSCredentialImpl)paramGSSCredential).getElements();
      hashSet1 = new HashSet(set.size());
      populateCredentials(hashSet1, set);
    } else {
      hashSet1 = new HashSet();
    } 
    debug("Created Subject with the following");
    debug("principals=" + hashSet3);
    debug("public creds=" + hashSet2);
    debug("private creds=" + hashSet1);
    return new Subject(false, hashSet3, hashSet2, hashSet1);
  }
  
  private static void populateCredentials(Set<Object> paramSet1, Set<?> paramSet2) {
    for (Object object : paramSet2) {
      if (object instanceof SpNegoCredElement)
        object = ((SpNegoCredElement)object).getInternalCred(); 
      if (object instanceof KerberosTicket) {
        if (!object.getClass().getName().equals("javax.security.auth.kerberos.KerberosTicket")) {
          KerberosTicket kerberosTicket = (KerberosTicket)object;
          object = new KerberosTicket(kerberosTicket.getEncoded(), kerberosTicket.getClient(), kerberosTicket.getServer(), kerberosTicket.getSessionKey().getEncoded(), kerberosTicket.getSessionKeyType(), kerberosTicket.getFlags(), kerberosTicket.getAuthTime(), kerberosTicket.getStartTime(), kerberosTicket.getEndTime(), kerberosTicket.getRenewTill(), kerberosTicket.getClientAddresses());
        } 
        paramSet1.add(object);
        continue;
      } 
      if (object instanceof KerberosKey) {
        if (!object.getClass().getName().equals("javax.security.auth.kerberos.KerberosKey")) {
          KerberosKey kerberosKey = (KerberosKey)object;
          object = new KerberosKey(kerberosKey.getPrincipal(), kerberosKey.getEncoded(), kerberosKey.getKeyType(), kerberosKey.getVersionNumber());
        } 
        paramSet1.add(object);
        continue;
      } 
      debug("Skipped cred element: " + object);
    } 
  }
  
  public static Subject login(GSSCaller paramGSSCaller, Oid paramOid) throws LoginException {
    TextCallbackHandler textCallbackHandler = null;
    if (paramGSSCaller instanceof HttpCaller) {
      textCallbackHandler = new NegotiateCallbackHandler(((HttpCaller)paramGSSCaller).info());
    } else {
      String str = Security.getProperty("auth.login.defaultCallbackHandler");
      if (str != null && str.length() != 0) {
        textCallbackHandler = null;
      } else {
        textCallbackHandler = new TextCallbackHandler();
      } 
    } 
    LoginContext loginContext = new LoginContext("", null, textCallbackHandler, new LoginConfigImpl(paramGSSCaller, paramOid));
    loginContext.login();
    return loginContext.getSubject();
  }
  
  public static boolean useSubjectCredsOnly(GSSCaller paramGSSCaller) {
    String str = GetPropertyAction.privilegedGetProperty("javax.security.auth.useSubjectCredsOnly");
    return (paramGSSCaller instanceof HttpCaller) ? "true".equalsIgnoreCase(str) : (!"false".equalsIgnoreCase(str) ? 1 : 0);
  }
  
  public static boolean useMSInterop() {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.security.spnego.msinterop", "true"));
    return !str.equalsIgnoreCase("false");
  }
  
  public static <T extends GSSCredentialSpi> Vector<T> searchSubject(final GSSNameSpi name, final Oid mech, final boolean initiate, final Class<? extends T> credCls) {
    debug("Search Subject for " + getMechStr(paramOid) + (paramBoolean ? " INIT" : " ACCEPT") + " cred (" + ((paramGSSNameSpi == null) ? "<<DEF>>" : paramGSSNameSpi.toString()) + ", " + paramClass.getName() + ")");
    final AccessControlContext acc = AccessController.getContext();
    try {
      return (Vector)AccessController.doPrivileged(new PrivilegedExceptionAction<Vector<T>>() {
            public Vector<T> run() throws Exception {
              Subject subject = Subject.getSubject(acc);
              Vector vector = null;
              if (subject != null) {
                vector = new Vector();
                for (GSSCredentialImpl gSSCredentialImpl : subject.getPrivateCredentials(GSSCredentialImpl.class)) {
                  GSSUtil.debug("...Found cred" + gSSCredentialImpl);
                  try {
                    GSSCredentialSpi gSSCredentialSpi = gSSCredentialImpl.getElement(mech, initiate);
                    GSSUtil.debug("......Found element: " + gSSCredentialSpi);
                    if (gSSCredentialSpi.getClass().equals(credCls) && (name == null || name.equals(gSSCredentialSpi.getName()))) {
                      vector.add(credCls.cast(gSSCredentialSpi));
                      continue;
                    } 
                    GSSUtil.debug("......Discard element");
                  } catch (GSSException gSSException) {
                    GSSUtil.debug("...Discard cred (" + gSSException + ")");
                  } 
                } 
              } else {
                GSSUtil.debug("No Subject");
              } 
              return vector;
            }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      debug("Unexpected exception when searching Subject:");
      if (DEBUG)
        privilegedActionException.printStackTrace(); 
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\GSSUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */