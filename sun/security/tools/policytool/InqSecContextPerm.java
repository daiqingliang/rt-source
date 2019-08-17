package sun.security.tools.policytool;

class InqSecContextPerm extends Perm {
  public InqSecContextPerm() { super("InquireSecContextPermission", "com.sun.security.jgss.InquireSecContextPermission", new String[] { "KRB5_GET_SESSION_KEY", "KRB5_GET_TKT_FLAGS", "KRB5_GET_AUTHZ_DATA", "KRB5_GET_AUTHTIME" }, null); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\InqSecContextPerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */