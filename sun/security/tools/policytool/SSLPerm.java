package sun.security.tools.policytool;

class SSLPerm extends Perm {
  public SSLPerm() { super("SSLPermission", "javax.net.ssl.SSLPermission", new String[] { "setHostnameVerifier", "getSSLSessionContext" }, null); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\SSLPerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */