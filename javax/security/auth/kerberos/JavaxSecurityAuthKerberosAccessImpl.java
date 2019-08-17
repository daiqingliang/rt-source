package javax.security.auth.kerberos;

import sun.security.krb5.JavaxSecurityAuthKerberosAccess;
import sun.security.krb5.internal.ktab.KeyTab;

class JavaxSecurityAuthKerberosAccessImpl implements JavaxSecurityAuthKerberosAccess {
  public KeyTab keyTabTakeSnapshot(KeyTab paramKeyTab) { return paramKeyTab.takeSnapshot(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\security\auth\kerberos\JavaxSecurityAuthKerberosAccessImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */