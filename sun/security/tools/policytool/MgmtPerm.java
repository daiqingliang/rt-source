package sun.security.tools.policytool;

class MgmtPerm extends Perm {
  public MgmtPerm() { super("ManagementPermission", "java.lang.management.ManagementPermission", new String[] { "control", "monitor" }, null); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\MgmtPerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */