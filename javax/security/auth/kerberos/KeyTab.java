package javax.security.auth.kerberos;

import java.io.File;
import java.security.AccessControlException;
import java.util.Objects;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KerberosSecrets;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.RealmException;
import sun.security.krb5.internal.ktab.KeyTab;

public final class KeyTab {
  private final File file;
  
  private final KerberosPrincipal princ;
  
  private final boolean bound;
  
  private KeyTab(KerberosPrincipal paramKerberosPrincipal, File paramFile, boolean paramBoolean) {
    this.princ = paramKerberosPrincipal;
    this.file = paramFile;
    this.bound = paramBoolean;
  }
  
  public static KeyTab getInstance(File paramFile) {
    if (paramFile == null)
      throw new NullPointerException("file must be non null"); 
    return new KeyTab(null, paramFile, true);
  }
  
  public static KeyTab getUnboundInstance(File paramFile) {
    if (paramFile == null)
      throw new NullPointerException("file must be non null"); 
    return new KeyTab(null, paramFile, false);
  }
  
  public static KeyTab getInstance(KerberosPrincipal paramKerberosPrincipal, File paramFile) {
    if (paramKerberosPrincipal == null)
      throw new NullPointerException("princ must be non null"); 
    if (paramFile == null)
      throw new NullPointerException("file must be non null"); 
    return new KeyTab(paramKerberosPrincipal, paramFile, true);
  }
  
  public static KeyTab getInstance() { return new KeyTab(null, null, true); }
  
  public static KeyTab getUnboundInstance() { return new KeyTab(null, null, false); }
  
  public static KeyTab getInstance(KerberosPrincipal paramKerberosPrincipal) {
    if (paramKerberosPrincipal == null)
      throw new NullPointerException("princ must be non null"); 
    return new KeyTab(paramKerberosPrincipal, null, true);
  }
  
  KeyTab takeSnapshot() {
    try {
      return KeyTab.getInstance(this.file);
    } catch (AccessControlException accessControlException1) {
      if (this.file != null)
        throw accessControlException1; 
      AccessControlException accessControlException2 = new AccessControlException("Access to default keytab denied (modified exception)");
      accessControlException2.setStackTrace(accessControlException1.getStackTrace());
      throw accessControlException2;
    } 
  }
  
  public KerberosKey[] getKeys(KerberosPrincipal paramKerberosPrincipal) {
    try {
      if (this.princ != null && !paramKerberosPrincipal.equals(this.princ))
        return new KerberosKey[0]; 
      PrincipalName principalName = new PrincipalName(paramKerberosPrincipal.getName());
      EncryptionKey[] arrayOfEncryptionKey = takeSnapshot().readServiceKeys(principalName);
      KerberosKey[] arrayOfKerberosKey = new KerberosKey[arrayOfEncryptionKey.length];
      for (byte b = 0; b < arrayOfKerberosKey.length; b++) {
        Integer integer = arrayOfEncryptionKey[b].getKeyVersionNumber();
        arrayOfKerberosKey[b] = new KerberosKey(paramKerberosPrincipal, arrayOfEncryptionKey[b].getBytes(), arrayOfEncryptionKey[b].getEType(), (integer == null) ? 0 : integer.intValue());
        arrayOfEncryptionKey[b].destroy();
      } 
      return arrayOfKerberosKey;
    } catch (RealmException realmException) {
      return new KerberosKey[0];
    } 
  }
  
  EncryptionKey[] getEncryptionKeys(PrincipalName paramPrincipalName) { return takeSnapshot().readServiceKeys(paramPrincipalName); }
  
  public boolean exists() { return !takeSnapshot().isMissing(); }
  
  public String toString() {
    String str = (this.file == null) ? "Default keytab" : this.file.toString();
    return !this.bound ? str : ((this.princ == null) ? (str + " for someone") : (str + " for " + this.princ));
  }
  
  public int hashCode() { return Objects.hash(new Object[] { this.file, this.princ, Boolean.valueOf(this.bound) }); }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof KeyTab))
      return false; 
    KeyTab keyTab = (KeyTab)paramObject;
    return (Objects.equals(keyTab.princ, this.princ) && Objects.equals(keyTab.file, this.file) && this.bound == keyTab.bound);
  }
  
  public KerberosPrincipal getPrincipal() { return this.princ; }
  
  public boolean isBound() { return this.bound; }
  
  static  {
    KerberosSecrets.setJavaxSecurityAuthKerberosAccess(new JavaxSecurityAuthKerberosAccessImpl());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\security\auth\kerberos\KeyTab.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */