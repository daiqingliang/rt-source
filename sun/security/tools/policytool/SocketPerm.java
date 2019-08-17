package sun.security.tools.policytool;

class SocketPerm extends Perm {
  public SocketPerm() { super("SocketPermission", "java.net.SocketPermission", new String[0], new String[] { "accept", "connect", "listen", "resolve" }); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\SocketPerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */