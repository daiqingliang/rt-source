package sun.security.tools.policytool;

class FilePerm extends Perm {
  public FilePerm() { super("FilePermission", "java.io.FilePermission", new String[] { "<<ALL FILES>>" }, new String[] { "read", "write", "delete", "execute" }); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\FilePerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */