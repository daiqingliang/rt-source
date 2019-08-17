package sun.security.jgss.krb5;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosKey;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.kerberos.KerberosTicket;
import javax.security.auth.kerberos.KeyTab;
import sun.security.krb5.Credentials;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;

public final class ServiceCreds {
  private KerberosPrincipal kp;
  
  private Set<KerberosPrincipal> allPrincs;
  
  private List<KeyTab> ktabs;
  
  private List<KerberosKey> kk;
  
  private KerberosTicket tgt;
  
  private boolean destroyed;
  
  public static ServiceCreds getInstance(Subject paramSubject, String paramString) {
    ServiceCreds serviceCreds = new ServiceCreds();
    serviceCreds.allPrincs = paramSubject.getPrincipals(KerberosPrincipal.class);
    for (KerberosKey kerberosKey : SubjectComber.findMany(paramSubject, paramString, null, KerberosKey.class))
      serviceCreds.allPrincs.add(kerberosKey.getPrincipal()); 
    if (paramString != null) {
      serviceCreds.kp = new KerberosPrincipal(paramString);
    } else if (serviceCreds.allPrincs.size() == 1) {
      boolean bool = false;
      for (KeyTab keyTab : SubjectComber.findMany(paramSubject, null, null, KeyTab.class)) {
        if (!keyTab.isBound()) {
          bool = true;
          break;
        } 
      } 
      if (!bool) {
        serviceCreds.kp = (KerberosPrincipal)serviceCreds.allPrincs.iterator().next();
        paramString = serviceCreds.kp.getName();
      } 
    } 
    serviceCreds.ktabs = SubjectComber.findMany(paramSubject, paramString, null, KeyTab.class);
    serviceCreds.kk = SubjectComber.findMany(paramSubject, paramString, null, KerberosKey.class);
    serviceCreds.tgt = (KerberosTicket)SubjectComber.find(paramSubject, null, paramString, KerberosTicket.class);
    if (serviceCreds.ktabs.isEmpty() && serviceCreds.kk.isEmpty() && serviceCreds.tgt == null)
      return null; 
    serviceCreds.destroyed = false;
    return serviceCreds;
  }
  
  public String getName() {
    if (this.destroyed)
      throw new IllegalStateException("This object is destroyed"); 
    return (this.kp == null) ? null : this.kp.getName();
  }
  
  public KerberosKey[] getKKeys() {
    if (this.destroyed)
      throw new IllegalStateException("This object is destroyed"); 
    KerberosPrincipal kerberosPrincipal = this.kp;
    if (kerberosPrincipal == null && !this.allPrincs.isEmpty())
      kerberosPrincipal = (KerberosPrincipal)this.allPrincs.iterator().next(); 
    if (kerberosPrincipal == null)
      for (KeyTab keyTab : this.ktabs) {
        PrincipalName principalName = Krb5Util.snapshotFromJavaxKeyTab(keyTab).getOneName();
        if (principalName != null) {
          kerberosPrincipal = new KerberosPrincipal(principalName.getName());
          break;
        } 
      }  
    return (kerberosPrincipal != null) ? getKKeys(kerberosPrincipal) : new KerberosKey[0];
  }
  
  public KerberosKey[] getKKeys(KerberosPrincipal paramKerberosPrincipal) {
    if (this.destroyed)
      throw new IllegalStateException("This object is destroyed"); 
    ArrayList arrayList = new ArrayList();
    if (this.kp != null && !paramKerberosPrincipal.equals(this.kp))
      return new KerberosKey[0]; 
    for (KerberosKey kerberosKey : this.kk) {
      if (kerberosKey.getPrincipal().equals(paramKerberosPrincipal))
        arrayList.add(kerberosKey); 
    } 
    for (KeyTab keyTab : this.ktabs) {
      if (keyTab.getPrincipal() == null && keyTab.isBound() && !this.allPrincs.contains(paramKerberosPrincipal))
        continue; 
      for (KerberosKey kerberosKey : keyTab.getKeys(paramKerberosPrincipal))
        arrayList.add(kerberosKey); 
    } 
    return (KerberosKey[])arrayList.toArray(new KerberosKey[arrayList.size()]);
  }
  
  public EncryptionKey[] getEKeys(PrincipalName paramPrincipalName) {
    if (this.destroyed)
      throw new IllegalStateException("This object is destroyed"); 
    KerberosKey[] arrayOfKerberosKey = getKKeys(new KerberosPrincipal(paramPrincipalName.getName()));
    if (arrayOfKerberosKey.length == 0)
      arrayOfKerberosKey = getKKeys(); 
    EncryptionKey[] arrayOfEncryptionKey = new EncryptionKey[arrayOfKerberosKey.length];
    for (byte b = 0; b < arrayOfEncryptionKey.length; b++)
      arrayOfEncryptionKey[b] = new EncryptionKey(arrayOfKerberosKey[b].getEncoded(), arrayOfKerberosKey[b].getKeyType(), new Integer(arrayOfKerberosKey[b].getVersionNumber())); 
    return arrayOfEncryptionKey;
  }
  
  public Credentials getInitCred() {
    if (this.destroyed)
      throw new IllegalStateException("This object is destroyed"); 
    if (this.tgt == null)
      return null; 
    try {
      return Krb5Util.ticketToCreds(this.tgt);
    } catch (KrbException|java.io.IOException krbException) {
      return null;
    } 
  }
  
  public void destroy() {
    this.destroyed = true;
    this.kp = null;
    this.ktabs.clear();
    this.kk.clear();
    this.tgt = null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\krb5\ServiceCreds.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */