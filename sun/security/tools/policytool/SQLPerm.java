package sun.security.tools.policytool;

class SQLPerm extends Perm {
  public SQLPerm() { super("SQLPermission", "java.sql.SQLPermission", new String[] { "setLog", "callAbort", "setSyncFactory", "setNetworkTimeout" }, null); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\SQLPerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */