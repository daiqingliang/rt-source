package sun.security.tools.policytool;

class PropPerm extends Perm {
  public PropPerm() { super("PropertyPermission", "java.util.PropertyPermission", new String[0], new String[] { "read", "write" }); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\PropPerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */