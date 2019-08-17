package sun.security.tools.policytool;

class NetPerm extends Perm {
  public NetPerm() { super("NetPermission", "java.net.NetPermission", new String[] { "setDefaultAuthenticator", "requestPasswordAuthentication", "specifyStreamHandler", "setProxySelector", "getProxySelector", "setCookieHandler", "getCookieHandler", "setResponseCache", "getResponseCache" }, null); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\NetPerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */