package sun.security.tools.policytool;

class URLPerm extends Perm {
  public URLPerm() { super("URLPermission", "java.net.URLPermission", new String[] { "<" + PolicyTool.getMessage("url") + ">" }, new String[] { "<" + PolicyTool.getMessage("method.list") + ">:<" + PolicyTool.getMessage("request.headers.list") + ">" }); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\URLPerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */