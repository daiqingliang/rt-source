package javax.security.auth;

import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.DomainCombiner;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.security.Security;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;
import sun.misc.JavaSecurityProtectionDomainAccess;
import sun.misc.SharedSecrets;
import sun.security.util.Debug;

public class SubjectDomainCombiner implements DomainCombiner {
  private Subject subject;
  
  private WeakKeyValueMap<ProtectionDomain, ProtectionDomain> cachedPDs = new WeakKeyValueMap(null);
  
  private Set<Principal> principalSet;
  
  private Principal[] principals;
  
  private static final Debug debug = Debug.getInstance("combiner", "\t[SubjectDomainCombiner]");
  
  private static final boolean useJavaxPolicy = Policy.isCustomPolicySet(debug);
  
  private static final boolean allowCaching = (useJavaxPolicy && cachePolicy());
  
  private static final JavaSecurityProtectionDomainAccess pdAccess = SharedSecrets.getJavaSecurityProtectionDomainAccess();
  
  public SubjectDomainCombiner(Subject paramSubject) {
    this.subject = paramSubject;
    if (paramSubject.isReadOnly()) {
      this.principalSet = paramSubject.getPrincipals();
      this.principals = (Principal[])this.principalSet.toArray(new Principal[this.principalSet.size()]);
    } 
  }
  
  public Subject getSubject() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new AuthPermission("getSubjectFromDomainCombiner")); 
    return this.subject;
  }
  
  public ProtectionDomain[] combine(ProtectionDomain[] paramArrayOfProtectionDomain1, ProtectionDomain[] paramArrayOfProtectionDomain2) {
    if (debug != null) {
      if (this.subject == null) {
        debug.println("null subject");
      } else {
        final Subject s = this.subject;
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
              public Void run() {
                debug.println(s.toString());
                return null;
              }
            });
      } 
      printInputDomains(paramArrayOfProtectionDomain1, paramArrayOfProtectionDomain2);
    } 
    if (paramArrayOfProtectionDomain1 == null || paramArrayOfProtectionDomain1.length == 0)
      return paramArrayOfProtectionDomain2; 
    paramArrayOfProtectionDomain1 = optimize(paramArrayOfProtectionDomain1);
    if (debug != null) {
      debug.println("after optimize");
      printInputDomains(paramArrayOfProtectionDomain1, paramArrayOfProtectionDomain2);
    } 
    if (paramArrayOfProtectionDomain1 == null && paramArrayOfProtectionDomain2 == null)
      return null; 
    if (useJavaxPolicy)
      return combineJavaxPolicy(paramArrayOfProtectionDomain1, paramArrayOfProtectionDomain2); 
    byte b1 = (paramArrayOfProtectionDomain1 == null) ? 0 : paramArrayOfProtectionDomain1.length;
    byte b2 = (paramArrayOfProtectionDomain2 == null) ? 0 : paramArrayOfProtectionDomain2.length;
    ProtectionDomain[] arrayOfProtectionDomain = new ProtectionDomain[b1 + b2];
    boolean bool = true;
    synchronized (this.cachedPDs) {
      if (!this.subject.isReadOnly() && !this.subject.getPrincipals().equals(this.principalSet)) {
        Set set = this.subject.getPrincipals();
        synchronized (set) {
          this.principalSet = new HashSet(set);
        } 
        this.principals = (Principal[])this.principalSet.toArray(new Principal[this.principalSet.size()]);
        this.cachedPDs.clear();
        if (debug != null)
          debug.println("Subject mutated - clearing cache"); 
      } 
      for (byte b = 0; b < b1; b++) {
        ProtectionDomain protectionDomain2 = paramArrayOfProtectionDomain1[b];
        ProtectionDomain protectionDomain1 = (ProtectionDomain)this.cachedPDs.getValue(protectionDomain2);
        if (protectionDomain1 == null) {
          if (pdAccess.getStaticPermissionsField(protectionDomain2)) {
            protectionDomain1 = new ProtectionDomain(protectionDomain2.getCodeSource(), protectionDomain2.getPermissions());
          } else {
            protectionDomain1 = new ProtectionDomain(protectionDomain2.getCodeSource(), protectionDomain2.getPermissions(), protectionDomain2.getClassLoader(), this.principals);
          } 
          this.cachedPDs.putValue(protectionDomain2, protectionDomain1);
        } else {
          bool = false;
        } 
        arrayOfProtectionDomain[b] = protectionDomain1;
      } 
    } 
    if (debug != null) {
      debug.println("updated current: ");
      for (byte b = 0; b < b1; b++)
        debug.println("\tupdated[" + b + "] = " + printDomain(arrayOfProtectionDomain[b])); 
    } 
    if (b2 > 0) {
      System.arraycopy(paramArrayOfProtectionDomain2, 0, arrayOfProtectionDomain, b1, b2);
      if (!bool)
        arrayOfProtectionDomain = optimize(arrayOfProtectionDomain); 
    } 
    if (debug != null)
      if (arrayOfProtectionDomain == null || arrayOfProtectionDomain.length == 0) {
        debug.println("returning null");
      } else {
        debug.println("combinedDomains: ");
        for (byte b = 0; b < arrayOfProtectionDomain.length; b++)
          debug.println("newDomain " + b + ": " + printDomain(arrayOfProtectionDomain[b])); 
      }  
    return (arrayOfProtectionDomain == null || arrayOfProtectionDomain.length == 0) ? null : arrayOfProtectionDomain;
  }
  
  private ProtectionDomain[] combineJavaxPolicy(ProtectionDomain[] paramArrayOfProtectionDomain1, ProtectionDomain[] paramArrayOfProtectionDomain2) {
    if (!allowCaching)
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
              Policy.getPolicy().refresh();
              return null;
            }
          }); 
    byte b1 = (paramArrayOfProtectionDomain1 == null) ? 0 : paramArrayOfProtectionDomain1.length;
    byte b2 = (paramArrayOfProtectionDomain2 == null) ? 0 : paramArrayOfProtectionDomain2.length;
    ProtectionDomain[] arrayOfProtectionDomain = new ProtectionDomain[b1 + b2];
    synchronized (this.cachedPDs) {
      if (!this.subject.isReadOnly() && !this.subject.getPrincipals().equals(this.principalSet)) {
        Set set = this.subject.getPrincipals();
        synchronized (set) {
          this.principalSet = new HashSet(set);
        } 
        this.principals = (Principal[])this.principalSet.toArray(new Principal[this.principalSet.size()]);
        this.cachedPDs.clear();
        if (debug != null)
          debug.println("Subject mutated - clearing cache"); 
      } 
      for (byte b = 0; b < b1; b++) {
        ProtectionDomain protectionDomain1 = paramArrayOfProtectionDomain1[b];
        ProtectionDomain protectionDomain2 = (ProtectionDomain)this.cachedPDs.getValue(protectionDomain1);
        if (protectionDomain2 == null) {
          if (pdAccess.getStaticPermissionsField(protectionDomain1)) {
            protectionDomain2 = new ProtectionDomain(protectionDomain1.getCodeSource(), protectionDomain1.getPermissions());
          } else {
            Permissions permissions = new Permissions();
            PermissionCollection permissionCollection1 = protectionDomain1.getPermissions();
            if (permissionCollection1 != null)
              synchronized (permissionCollection1) {
                Enumeration enumeration = permissionCollection1.elements();
                while (enumeration.hasMoreElements()) {
                  Permission permission = (Permission)enumeration.nextElement();
                  permissions.add(permission);
                } 
              }  
            final CodeSource finalCs = protectionDomain1.getCodeSource();
            final Subject finalS = this.subject;
            PermissionCollection permissionCollection2 = (PermissionCollection)AccessController.doPrivileged(new PrivilegedAction<PermissionCollection>() {
                  public PermissionCollection run() { return Policy.getPolicy().getPermissions(finalS, finalCs); }
                });
            synchronized (permissionCollection2) {
              Enumeration enumeration = permissionCollection2.elements();
              while (enumeration.hasMoreElements()) {
                Permission permission = (Permission)enumeration.nextElement();
                if (!permissions.implies(permission)) {
                  permissions.add(permission);
                  if (debug != null)
                    debug.println("Adding perm " + permission + "\n"); 
                } 
              } 
            } 
            protectionDomain2 = new ProtectionDomain(codeSource, permissions, protectionDomain1.getClassLoader(), this.principals);
          } 
          if (allowCaching)
            this.cachedPDs.putValue(protectionDomain1, protectionDomain2); 
        } 
        arrayOfProtectionDomain[b] = protectionDomain2;
      } 
    } 
    if (debug != null) {
      debug.println("updated current: ");
      for (byte b = 0; b < b1; b++)
        debug.println("\tupdated[" + b + "] = " + arrayOfProtectionDomain[b]); 
    } 
    if (b2 > 0)
      System.arraycopy(paramArrayOfProtectionDomain2, 0, arrayOfProtectionDomain, b1, b2); 
    if (debug != null)
      if (arrayOfProtectionDomain == null || arrayOfProtectionDomain.length == 0) {
        debug.println("returning null");
      } else {
        debug.println("combinedDomains: ");
        for (byte b = 0; b < arrayOfProtectionDomain.length; b++)
          debug.println("newDomain " + b + ": " + arrayOfProtectionDomain[b].toString()); 
      }  
    return (arrayOfProtectionDomain == null || arrayOfProtectionDomain.length == 0) ? null : arrayOfProtectionDomain;
  }
  
  private static ProtectionDomain[] optimize(ProtectionDomain[] paramArrayOfProtectionDomain) {
    if (paramArrayOfProtectionDomain == null || paramArrayOfProtectionDomain.length == 0)
      return null; 
    ProtectionDomain[] arrayOfProtectionDomain = new ProtectionDomain[paramArrayOfProtectionDomain.length];
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramArrayOfProtectionDomain.length; b2++) {
      ProtectionDomain protectionDomain;
      if ((protectionDomain = paramArrayOfProtectionDomain[b2]) != null) {
        boolean bool = false;
        for (byte b = 0; b < b1 && !bool; b++)
          bool = (arrayOfProtectionDomain[b] == protectionDomain) ? 1 : 0; 
        if (!bool)
          arrayOfProtectionDomain[b1++] = protectionDomain; 
      } 
    } 
    if (b1 > 0 && b1 < paramArrayOfProtectionDomain.length) {
      ProtectionDomain[] arrayOfProtectionDomain1 = new ProtectionDomain[b1];
      System.arraycopy(arrayOfProtectionDomain, 0, arrayOfProtectionDomain1, 0, arrayOfProtectionDomain1.length);
      arrayOfProtectionDomain = arrayOfProtectionDomain1;
    } 
    return (b1 == 0 || arrayOfProtectionDomain.length == 0) ? null : arrayOfProtectionDomain;
  }
  
  private static boolean cachePolicy() {
    String str = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
          public String run() { return Security.getProperty("cache.auth.policy"); }
        });
    return (str != null) ? Boolean.parseBoolean(str) : 1;
  }
  
  private static void printInputDomains(ProtectionDomain[] paramArrayOfProtectionDomain1, ProtectionDomain[] paramArrayOfProtectionDomain2) {
    if (paramArrayOfProtectionDomain1 == null || paramArrayOfProtectionDomain1.length == 0) {
      debug.println("currentDomains null or 0 length");
    } else {
      for (byte b = 0; paramArrayOfProtectionDomain1 != null && b < paramArrayOfProtectionDomain1.length; b++) {
        if (paramArrayOfProtectionDomain1[b] == null) {
          debug.println("currentDomain " + b + ": SystemDomain");
        } else {
          debug.println("currentDomain " + b + ": " + printDomain(paramArrayOfProtectionDomain1[b]));
        } 
      } 
    } 
    if (paramArrayOfProtectionDomain2 == null || paramArrayOfProtectionDomain2.length == 0) {
      debug.println("assignedDomains null or 0 length");
    } else {
      debug.println("assignedDomains = ");
      for (byte b = 0; paramArrayOfProtectionDomain2 != null && b < paramArrayOfProtectionDomain2.length; b++) {
        if (paramArrayOfProtectionDomain2[b] == null) {
          debug.println("assignedDomain " + b + ": SystemDomain");
        } else {
          debug.println("assignedDomain " + b + ": " + printDomain(paramArrayOfProtectionDomain2[b]));
        } 
      } 
    } 
  }
  
  private static String printDomain(final ProtectionDomain pd) { return (paramProtectionDomain == null) ? "null" : (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
          public String run() { return pd.toString(); }
        }); }
  
  private static class WeakKeyValueMap<K, V> extends WeakHashMap<K, WeakReference<V>> {
    private WeakKeyValueMap() {}
    
    public V getValue(K param1K) {
      WeakReference weakReference = (WeakReference)get(param1K);
      return (weakReference != null) ? (V)weakReference.get() : null;
    }
    
    public V putValue(K param1K, V param1V) {
      WeakReference weakReference = (WeakReference)put(param1K, new WeakReference(param1V));
      return (weakReference != null) ? (V)weakReference.get() : null;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\security\auth\SubjectDomainCombiner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */