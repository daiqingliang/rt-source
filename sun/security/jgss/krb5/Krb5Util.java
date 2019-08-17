package sun.security.jgss.krb5;

import java.io.IOException;
import java.security.AccessControlContext;
import java.security.AccessController;
import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.kerberos.KerberosTicket;
import javax.security.auth.kerberos.KeyTab;
import javax.security.auth.login.LoginException;
import sun.security.action.GetBooleanAction;
import sun.security.jgss.GSSCaller;
import sun.security.jgss.GSSUtil;
import sun.security.krb5.Credentials;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KerberosSecrets;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.internal.ktab.KeyTab;

public class Krb5Util {
  static final boolean DEBUG = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.security.krb5.debug"))).booleanValue();
  
  public static KerberosTicket getTicketFromSubjectAndTgs(GSSCaller paramGSSCaller, String paramString1, String paramString2, String paramString3, AccessControlContext paramAccessControlContext) throws LoginException, KrbException, IOException {
    boolean bool;
    Subject subject1 = Subject.getSubject(paramAccessControlContext);
    KerberosTicket kerberosTicket1 = (KerberosTicket)SubjectComber.find(subject1, paramString2, paramString1, KerberosTicket.class);
    if (kerberosTicket1 != null)
      return kerberosTicket1; 
    Subject subject2 = null;
    if (!GSSUtil.useSubjectCredsOnly(paramGSSCaller))
      try {
        subject2 = GSSUtil.login(paramGSSCaller, GSSUtil.GSS_KRB5_MECH_OID);
        kerberosTicket1 = (KerberosTicket)SubjectComber.find(subject2, paramString2, paramString1, KerberosTicket.class);
        if (kerberosTicket1 != null)
          return kerberosTicket1; 
      } catch (LoginException loginException) {} 
    KerberosTicket kerberosTicket2 = (KerberosTicket)SubjectComber.find(subject1, paramString3, paramString1, KerberosTicket.class);
    if (kerberosTicket2 == null && subject2 != null) {
      kerberosTicket2 = (KerberosTicket)SubjectComber.find(subject2, paramString3, paramString1, KerberosTicket.class);
      bool = false;
    } else {
      bool = true;
    } 
    if (kerberosTicket2 != null) {
      Credentials credentials1;
      Credentials credentials2 = (credentials1 = ticketToCreds(kerberosTicket2)).acquireServiceCreds(paramString2, credentials1);
      if (credentials2 != null) {
        kerberosTicket1 = credsToTicket(credentials2);
        if (bool && subject1 != null && !subject1.isReadOnly())
          subject1.getPrivateCredentials().add(kerberosTicket1); 
      } 
    } 
    return kerberosTicket1;
  }
  
  static KerberosTicket getTicket(GSSCaller paramGSSCaller, String paramString1, String paramString2, AccessControlContext paramAccessControlContext) throws LoginException {
    Subject subject = Subject.getSubject(paramAccessControlContext);
    KerberosTicket kerberosTicket = (KerberosTicket)SubjectComber.find(subject, paramString2, paramString1, KerberosTicket.class);
    if (kerberosTicket == null && !GSSUtil.useSubjectCredsOnly(paramGSSCaller)) {
      Subject subject1 = GSSUtil.login(paramGSSCaller, GSSUtil.GSS_KRB5_MECH_OID);
      kerberosTicket = (KerberosTicket)SubjectComber.find(subject1, paramString2, paramString1, KerberosTicket.class);
    } 
    return kerberosTicket;
  }
  
  public static Subject getSubject(GSSCaller paramGSSCaller, AccessControlContext paramAccessControlContext) throws LoginException {
    Subject subject = Subject.getSubject(paramAccessControlContext);
    if (subject == null && !GSSUtil.useSubjectCredsOnly(paramGSSCaller))
      subject = GSSUtil.login(paramGSSCaller, GSSUtil.GSS_KRB5_MECH_OID); 
    return subject;
  }
  
  public static ServiceCreds getServiceCreds(GSSCaller paramGSSCaller, String paramString, AccessControlContext paramAccessControlContext) throws LoginException {
    Subject subject = Subject.getSubject(paramAccessControlContext);
    ServiceCreds serviceCreds = null;
    if (subject != null)
      serviceCreds = ServiceCreds.getInstance(subject, paramString); 
    if (serviceCreds == null && !GSSUtil.useSubjectCredsOnly(paramGSSCaller)) {
      Subject subject1 = GSSUtil.login(paramGSSCaller, GSSUtil.GSS_KRB5_MECH_OID);
      serviceCreds = ServiceCreds.getInstance(subject1, paramString);
    } 
    return serviceCreds;
  }
  
  public static KerberosTicket credsToTicket(Credentials paramCredentials) {
    EncryptionKey encryptionKey = paramCredentials.getSessionKey();
    return new KerberosTicket(paramCredentials.getEncoded(), new KerberosPrincipal(paramCredentials.getClient().getName()), new KerberosPrincipal(paramCredentials.getServer().getName(), 2), encryptionKey.getBytes(), encryptionKey.getEType(), paramCredentials.getFlags(), paramCredentials.getAuthTime(), paramCredentials.getStartTime(), paramCredentials.getEndTime(), paramCredentials.getRenewTill(), paramCredentials.getClientAddresses());
  }
  
  public static Credentials ticketToCreds(KerberosTicket paramKerberosTicket) throws KrbException, IOException { return new Credentials(paramKerberosTicket.getEncoded(), paramKerberosTicket.getClient().getName(), paramKerberosTicket.getServer().getName(), paramKerberosTicket.getSessionKey().getEncoded(), paramKerberosTicket.getSessionKeyType(), paramKerberosTicket.getFlags(), paramKerberosTicket.getAuthTime(), paramKerberosTicket.getStartTime(), paramKerberosTicket.getEndTime(), paramKerberosTicket.getRenewTill(), paramKerberosTicket.getClientAddresses()); }
  
  public static KeyTab snapshotFromJavaxKeyTab(KeyTab paramKeyTab) { return KerberosSecrets.getJavaxSecurityAuthKerberosAccess().keyTabTakeSnapshot(paramKeyTab); }
  
  public static EncryptionKey[] keysFromJavaxKeyTab(KeyTab paramKeyTab, PrincipalName paramPrincipalName) { return snapshotFromJavaxKeyTab(paramKeyTab).readServiceKeys(paramPrincipalName); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\krb5\Krb5Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */