package sun.misc;

import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.PropertyPermission;
import sun.security.util.SecurityConstants;

class PathPermissions extends PermissionCollection {
  private static final long serialVersionUID = 8133287259134945693L;
  
  private File[] path;
  
  private Permissions perms;
  
  URL codeBase;
  
  PathPermissions(File[] paramArrayOfFile) {
    this.path = paramArrayOfFile;
    this.perms = null;
    this.codeBase = null;
  }
  
  URL getCodeBase() { return this.codeBase; }
  
  public void add(Permission paramPermission) { throw new SecurityException("attempt to add a permission"); }
  
  private void init() {
    if (this.perms != null)
      return; 
    this.perms = new Permissions();
    this.perms.add(SecurityConstants.CREATE_CLASSLOADER_PERMISSION);
    this.perms.add(new PropertyPermission("java.*", "read"));
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            for (byte b = 0; b < PathPermissions.this.path.length; b++) {
              String str;
              File file = PathPermissions.this.path[b];
              try {
                str = file.getCanonicalPath();
              } catch (IOException iOException) {
                str = file.getAbsolutePath();
              } 
              if (!b)
                PathPermissions.this.codeBase = Launcher.getFileURL(new File(str)); 
              if (file.isDirectory()) {
                if (str.endsWith(File.separator)) {
                  PathPermissions.this.perms.add(new FilePermission(str + "-", "read"));
                } else {
                  PathPermissions.this.perms.add(new FilePermission(str + File.separator + "-", "read"));
                } 
              } else {
                int i = str.lastIndexOf(File.separatorChar);
                if (i != -1) {
                  str = str.substring(0, i + 1) + "-";
                  PathPermissions.this.perms.add(new FilePermission(str, "read"));
                } 
              } 
            } 
            return null;
          }
        });
  }
  
  public boolean implies(Permission paramPermission) {
    if (this.perms == null)
      init(); 
    return this.perms.implies(paramPermission);
  }
  
  public Enumeration<Permission> elements() {
    if (this.perms == null)
      init(); 
    synchronized (this.perms) {
      return this.perms.elements();
    } 
  }
  
  public String toString() {
    if (this.perms == null)
      init(); 
    return this.perms.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\PathPermissions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */