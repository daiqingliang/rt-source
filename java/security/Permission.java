package java.security;

import java.io.Serializable;

public abstract class Permission implements Guard, Serializable {
  private static final long serialVersionUID = -5636570222231596674L;
  
  private String name;
  
  public Permission(String paramString) { this.name = paramString; }
  
  public void checkGuard(Object paramObject) throws SecurityException {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(this); 
  }
  
  public abstract boolean implies(Permission paramPermission);
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
  
  public final String getName() { return this.name; }
  
  public abstract String getActions();
  
  public PermissionCollection newPermissionCollection() { return null; }
  
  public String toString() {
    String str = getActions();
    return (str == null || str.length() == 0) ? ("(\"" + getClass().getName() + "\" \"" + this.name + "\")") : ("(\"" + getClass().getName() + "\" \"" + this.name + "\" \"" + str + "\")");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\Permission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */