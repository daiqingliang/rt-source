package sun.security.tools.policytool;

class ServicePerm extends Perm {
  public ServicePerm() { super("ServicePermission", "javax.security.auth.kerberos.ServicePermission", new String[0], new String[] { "initiate", "accept" }); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\ServicePerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */