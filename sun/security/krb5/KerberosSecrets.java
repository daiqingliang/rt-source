package sun.security.krb5;

import sun.misc.Unsafe;

public class KerberosSecrets {
  private static final Unsafe unsafe = Unsafe.getUnsafe();
  
  private static JavaxSecurityAuthKerberosAccess javaxSecurityAuthKerberosAccess;
  
  public static void setJavaxSecurityAuthKerberosAccess(JavaxSecurityAuthKerberosAccess paramJavaxSecurityAuthKerberosAccess) { javaxSecurityAuthKerberosAccess = paramJavaxSecurityAuthKerberosAccess; }
  
  public static JavaxSecurityAuthKerberosAccess getJavaxSecurityAuthKerberosAccess() {
    if (javaxSecurityAuthKerberosAccess == null)
      unsafe.ensureClassInitialized(javax.security.auth.kerberos.KeyTab.class); 
    return javaxSecurityAuthKerberosAccess;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\KerberosSecrets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */