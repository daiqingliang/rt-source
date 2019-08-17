package java.security;

import java.util.Enumeration;

@Deprecated
public abstract class IdentityScope extends Identity {
  private static final long serialVersionUID = -2337346281189773310L;
  
  private static IdentityScope scope;
  
  private static void initializeSystemScope() {
    String str = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
          public String run() { return Security.getProperty("system.scope"); }
        });
    if (str == null)
      return; 
    try {
      Class.forName(str);
    } catch (ClassNotFoundException classNotFoundException) {
      classNotFoundException.printStackTrace();
    } 
  }
  
  protected IdentityScope() { this("restoring..."); }
  
  public IdentityScope(String paramString) { super(paramString); }
  
  public IdentityScope(String paramString, IdentityScope paramIdentityScope) throws KeyManagementException { super(paramString, paramIdentityScope); }
  
  public static IdentityScope getSystemScope() {
    if (scope == null)
      initializeSystemScope(); 
    return scope;
  }
  
  protected static void setSystemScope(IdentityScope paramIdentityScope) {
    check("setSystemScope");
    scope = paramIdentityScope;
  }
  
  public abstract int size();
  
  public abstract Identity getIdentity(String paramString);
  
  public Identity getIdentity(Principal paramPrincipal) { return getIdentity(paramPrincipal.getName()); }
  
  public abstract Identity getIdentity(PublicKey paramPublicKey);
  
  public abstract void addIdentity(Identity paramIdentity) throws KeyManagementException;
  
  public abstract void removeIdentity(Identity paramIdentity) throws KeyManagementException;
  
  public abstract Enumeration<Identity> identities();
  
  public String toString() { return super.toString() + "[" + size() + "]"; }
  
  private static void check(String paramString) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkSecurityAccess(paramString); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\IdentityScope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */