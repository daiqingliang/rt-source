package java.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.WeakHashMap;
import sun.misc.JavaSecurityAccess;
import sun.misc.JavaSecurityProtectionDomainAccess;
import sun.misc.SharedSecrets;
import sun.security.util.Debug;
import sun.security.util.SecurityConstants;

public class ProtectionDomain {
  private CodeSource codesource;
  
  private ClassLoader classloader;
  
  private Principal[] principals;
  
  private PermissionCollection permissions;
  
  private boolean hasAllPerm = false;
  
  private boolean staticPermissions;
  
  final Key key = new Key();
  
  private static final Debug debug;
  
  public ProtectionDomain(CodeSource paramCodeSource, PermissionCollection paramPermissionCollection) {
    this.codesource = paramCodeSource;
    if (paramPermissionCollection != null) {
      this.permissions = paramPermissionCollection;
      this.permissions.setReadOnly();
      if (paramPermissionCollection instanceof Permissions && ((Permissions)paramPermissionCollection).allPermission != null)
        this.hasAllPerm = true; 
    } 
    this.classloader = null;
    this.principals = new Principal[0];
    this.staticPermissions = true;
  }
  
  public ProtectionDomain(CodeSource paramCodeSource, PermissionCollection paramPermissionCollection, ClassLoader paramClassLoader, Principal[] paramArrayOfPrincipal) {
    this.codesource = paramCodeSource;
    if (paramPermissionCollection != null) {
      this.permissions = paramPermissionCollection;
      this.permissions.setReadOnly();
      if (paramPermissionCollection instanceof Permissions && ((Permissions)paramPermissionCollection).allPermission != null)
        this.hasAllPerm = true; 
    } 
    this.classloader = paramClassLoader;
    this.principals = (paramArrayOfPrincipal != null) ? (Principal[])paramArrayOfPrincipal.clone() : new Principal[0];
    this.staticPermissions = false;
  }
  
  public final CodeSource getCodeSource() { return this.codesource; }
  
  public final ClassLoader getClassLoader() { return this.classloader; }
  
  public final Principal[] getPrincipals() { return (Principal[])this.principals.clone(); }
  
  public final PermissionCollection getPermissions() { return this.permissions; }
  
  public boolean implies(Permission paramPermission) { return this.hasAllPerm ? true : ((!this.staticPermissions && Policy.getPolicyNoCheck().implies(this, paramPermission)) ? true : ((this.permissions != null) ? this.permissions.implies(paramPermission) : 0)); }
  
  boolean impliesCreateAccessControlContext() { return implies(SecurityConstants.CREATE_ACC_PERMISSION); }
  
  public String toString() {
    String str = "<no principals>";
    if (this.principals != null && this.principals.length > 0) {
      StringBuilder stringBuilder = new StringBuilder("(principals ");
      for (byte b = 0; b < this.principals.length; b++) {
        stringBuilder.append(this.principals[b].getClass().getName() + " \"" + this.principals[b].getName() + "\"");
        if (b < this.principals.length - 1) {
          stringBuilder.append(",\n");
        } else {
          stringBuilder.append(")\n");
        } 
      } 
      str = stringBuilder.toString();
    } 
    PermissionCollection permissionCollection = (Policy.isSet() && seeAllp()) ? mergePermissions() : getPermissions();
    return "ProtectionDomain  " + this.codesource + "\n " + this.classloader + "\n " + str + "\n " + permissionCollection + "\n";
  }
  
  private static boolean seeAllp() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager == null)
      return true; 
    if (debug != null) {
      if (securityManager.getClass().getClassLoader() == null && Policy.getPolicyNoCheck().getClass().getClassLoader() == null)
        return true; 
    } else {
      try {
        securityManager.checkPermission(SecurityConstants.GET_POLICY_PERMISSION);
        return true;
      } catch (SecurityException securityException) {}
    } 
    return false;
  }
  
  private PermissionCollection mergePermissions() {
    if (this.staticPermissions)
      return this.permissions; 
    PermissionCollection permissionCollection = (PermissionCollection)AccessController.doPrivileged(new PrivilegedAction<PermissionCollection>() {
          public PermissionCollection run() {
            Policy policy = Policy.getPolicyNoCheck();
            return policy.getPermissions(ProtectionDomain.this);
          }
        });
    Permissions permissions1 = new Permissions();
    byte b1 = 32;
    byte b2 = 8;
    ArrayList arrayList1 = new ArrayList(b2);
    ArrayList arrayList2 = new ArrayList(b1);
    if (this.permissions != null)
      synchronized (this.permissions) {
        Enumeration enumeration = this.permissions.elements();
        while (enumeration.hasMoreElements())
          arrayList1.add(enumeration.nextElement()); 
      }  
    if (permissionCollection != null)
      synchronized (permissionCollection) {
        Enumeration enumeration = permissionCollection.elements();
        while (enumeration.hasMoreElements()) {
          arrayList2.add(enumeration.nextElement());
          b2++;
        } 
      }  
    if (permissionCollection != null && this.permissions != null)
      synchronized (this.permissions) {
        Enumeration enumeration = this.permissions.elements();
        while (enumeration.hasMoreElements()) {
          Permission permission = (Permission)enumeration.nextElement();
          Class clazz = permission.getClass();
          String str1 = permission.getActions();
          String str2 = permission.getName();
          for (byte b = 0; b < arrayList2.size(); b++) {
            Permission permission1 = (Permission)arrayList2.get(b);
            if (clazz.isInstance(permission1) && str2.equals(permission1.getName()) && str1.equals(permission1.getActions())) {
              arrayList2.remove(b);
              break;
            } 
          } 
        } 
      }  
    if (permissionCollection != null)
      for (int i = arrayList2.size() - 1; i >= 0; i--)
        permissions1.add((Permission)arrayList2.get(i));  
    if (this.permissions != null)
      for (int i = arrayList1.size() - 1; i >= 0; i--)
        permissions1.add((Permission)arrayList1.get(i));  
    return permissions1;
  }
  
  static  {
    SharedSecrets.setJavaSecurityAccess(new JavaSecurityAccessImpl(null));
    debug = Debug.getInstance("domain");
    SharedSecrets.setJavaSecurityProtectionDomainAccess(new JavaSecurityProtectionDomainAccess() {
          public ProtectionDomainCache getProtectionDomainCache() { return new ProtectionDomainCache() {
                private final Map<ProtectionDomain.Key, PermissionCollection> map = Collections.synchronizedMap(new WeakHashMap());
                
                public void put(ProtectionDomain param2ProtectionDomain, PermissionCollection param2PermissionCollection) { this.map.put((param2ProtectionDomain == null) ? null : param2ProtectionDomain.key, param2PermissionCollection); }
                
                public PermissionCollection get(ProtectionDomain param2ProtectionDomain) { return (param2ProtectionDomain == null) ? (PermissionCollection)this.map.get(null) : (PermissionCollection)this.map.get(param2ProtectionDomain.key); }
              }; }
          
          public boolean getStaticPermissionsField(ProtectionDomain param1ProtectionDomain) { return param1ProtectionDomain.staticPermissions; }
        });
  }
  
  private static class JavaSecurityAccessImpl implements JavaSecurityAccess {
    private JavaSecurityAccessImpl() {}
    
    public <T> T doIntersectionPrivilege(PrivilegedAction<T> param1PrivilegedAction, AccessControlContext param1AccessControlContext1, AccessControlContext param1AccessControlContext2) {
      if (param1PrivilegedAction == null)
        throw new NullPointerException(); 
      return (T)AccessController.doPrivileged(param1PrivilegedAction, getCombinedACC(param1AccessControlContext2, param1AccessControlContext1));
    }
    
    public <T> T doIntersectionPrivilege(PrivilegedAction<T> param1PrivilegedAction, AccessControlContext param1AccessControlContext) { return (T)doIntersectionPrivilege(param1PrivilegedAction, AccessController.getContext(), param1AccessControlContext); }
    
    private static AccessControlContext getCombinedACC(AccessControlContext param1AccessControlContext1, AccessControlContext param1AccessControlContext2) {
      AccessControlContext accessControlContext = new AccessControlContext(param1AccessControlContext1, param1AccessControlContext2.getCombiner(), true);
      return (new AccessControlContext(param1AccessControlContext2.getContext(), accessControlContext)).optimize();
    }
  }
  
  final class Key {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\ProtectionDomain.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */