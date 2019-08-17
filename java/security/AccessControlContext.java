package java.security;

import java.util.ArrayList;
import sun.security.util.Debug;
import sun.security.util.SecurityConstants;

public final class AccessControlContext {
  private ProtectionDomain[] context;
  
  private boolean isPrivileged;
  
  private boolean isAuthorized = false;
  
  private AccessControlContext privilegedContext;
  
  private DomainCombiner combiner = null;
  
  private Permission[] permissions;
  
  private AccessControlContext parent;
  
  private boolean isWrapped;
  
  private boolean isLimited;
  
  private ProtectionDomain[] limitedContext;
  
  private static boolean debugInit = false;
  
  private static Debug debug = null;
  
  static Debug getDebug() {
    if (debugInit)
      return debug; 
    if (Policy.isSet()) {
      debug = Debug.getInstance("access");
      debugInit = true;
    } 
    return debug;
  }
  
  public AccessControlContext(ProtectionDomain[] paramArrayOfProtectionDomain) {
    if (paramArrayOfProtectionDomain.length == 0) {
      this.context = null;
    } else if (paramArrayOfProtectionDomain.length == 1) {
      if (paramArrayOfProtectionDomain[false] != null) {
        this.context = (ProtectionDomain[])paramArrayOfProtectionDomain.clone();
      } else {
        this.context = null;
      } 
    } else {
      ArrayList arrayList = new ArrayList(paramArrayOfProtectionDomain.length);
      for (byte b = 0; b < paramArrayOfProtectionDomain.length; b++) {
        if (paramArrayOfProtectionDomain[b] != null && !arrayList.contains(paramArrayOfProtectionDomain[b]))
          arrayList.add(paramArrayOfProtectionDomain[b]); 
      } 
      if (!arrayList.isEmpty()) {
        this.context = new ProtectionDomain[arrayList.size()];
        this.context = (ProtectionDomain[])arrayList.toArray(this.context);
      } 
    } 
  }
  
  public AccessControlContext(AccessControlContext paramAccessControlContext, DomainCombiner paramDomainCombiner) { this(paramAccessControlContext, paramDomainCombiner, false); }
  
  AccessControlContext(AccessControlContext paramAccessControlContext, DomainCombiner paramDomainCombiner, boolean paramBoolean) {
    if (!paramBoolean) {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null) {
        securityManager.checkPermission(SecurityConstants.CREATE_ACC_PERMISSION);
        this.isAuthorized = true;
      } 
    } else {
      this.isAuthorized = true;
    } 
    this.context = paramAccessControlContext.context;
    this.combiner = paramDomainCombiner;
  }
  
  AccessControlContext(ProtectionDomain paramProtectionDomain, DomainCombiner paramDomainCombiner, AccessControlContext paramAccessControlContext1, AccessControlContext paramAccessControlContext2, Permission[] paramArrayOfPermission) {
    ProtectionDomain[] arrayOfProtectionDomain = null;
    if (paramProtectionDomain != null)
      arrayOfProtectionDomain = new ProtectionDomain[] { paramProtectionDomain }; 
    if (paramAccessControlContext2 != null) {
      if (paramDomainCombiner != null) {
        this.context = paramDomainCombiner.combine(arrayOfProtectionDomain, paramAccessControlContext2.context);
      } else {
        this.context = combine(arrayOfProtectionDomain, paramAccessControlContext2.context);
      } 
    } else if (paramDomainCombiner != null) {
      this.context = paramDomainCombiner.combine(arrayOfProtectionDomain, null);
    } else {
      this.context = combine(arrayOfProtectionDomain, null);
    } 
    this.combiner = paramDomainCombiner;
    Permission[] arrayOfPermission = null;
    if (paramArrayOfPermission != null) {
      arrayOfPermission = new Permission[paramArrayOfPermission.length];
      for (byte b = 0; b < paramArrayOfPermission.length; b++) {
        if (paramArrayOfPermission[b] == null)
          throw new NullPointerException("permission can't be null"); 
        if (paramArrayOfPermission[b].getClass() == AllPermission.class)
          paramAccessControlContext1 = null; 
        arrayOfPermission[b] = paramArrayOfPermission[b];
      } 
    } 
    if (paramAccessControlContext1 != null) {
      this.limitedContext = combine(paramAccessControlContext1.context, paramAccessControlContext1.limitedContext);
      this.isLimited = true;
      this.isWrapped = true;
      this.permissions = arrayOfPermission;
      this.parent = paramAccessControlContext1;
      this.privilegedContext = paramAccessControlContext2;
    } 
    this.isAuthorized = true;
  }
  
  AccessControlContext(ProtectionDomain[] paramArrayOfProtectionDomain, boolean paramBoolean) {
    this.context = paramArrayOfProtectionDomain;
    this.isPrivileged = paramBoolean;
    this.isAuthorized = true;
  }
  
  AccessControlContext(ProtectionDomain[] paramArrayOfProtectionDomain, AccessControlContext paramAccessControlContext) {
    this.context = paramArrayOfProtectionDomain;
    this.privilegedContext = paramAccessControlContext;
    this.isPrivileged = true;
  }
  
  ProtectionDomain[] getContext() { return this.context; }
  
  boolean isPrivileged() { return this.isPrivileged; }
  
  DomainCombiner getAssignedCombiner() {
    AccessControlContext accessControlContext;
    if (this.isPrivileged) {
      accessControlContext = this.privilegedContext;
    } else {
      accessControlContext = AccessController.getInheritedAccessControlContext();
    } 
    return (accessControlContext != null) ? accessControlContext.combiner : null;
  }
  
  public DomainCombiner getDomainCombiner() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(SecurityConstants.GET_COMBINER_PERMISSION); 
    return getCombiner();
  }
  
  DomainCombiner getCombiner() { return this.combiner; }
  
  boolean isAuthorized() { return this.isAuthorized; }
  
  public void checkPermission(Permission paramPermission) throws AccessControlException {
    boolean bool = false;
    if (paramPermission == null)
      throw new NullPointerException("permission can't be null"); 
    if (getDebug() != null) {
      bool = !Debug.isOn("codebase=") ? 1 : 0;
      if (!bool)
        for (byte b1 = 0; this.context != null && b1 < this.context.length; b1++) {
          if (this.context[b1].getCodeSource() != null && this.context[b1].getCodeSource().getLocation() != null && Debug.isOn("codebase=" + this.context[b1].getCodeSource().getLocation().toString())) {
            bool = true;
            break;
          } 
        }  
      bool &= ((!Debug.isOn("permission=") || Debug.isOn("permission=" + paramPermission.getClass().getCanonicalName())) ? 1 : 0);
      if (bool && Debug.isOn("stack"))
        Thread.dumpStack(); 
      if (bool && Debug.isOn("domain"))
        if (this.context == null) {
          debug.println("domain (context is null)");
        } else {
          for (byte b1 = 0; b1 < this.context.length; b1++)
            debug.println("domain " + b1 + " " + this.context[b1]); 
        }  
    } 
    if (this.context == null) {
      checkPermission2(paramPermission);
      return;
    } 
    for (byte b = 0; b < this.context.length; b++) {
      if (this.context[b] != null && !this.context[b].implies(paramPermission)) {
        if (bool)
          debug.println("access denied " + paramPermission); 
        if (Debug.isOn("failure") && debug != null) {
          if (!bool)
            debug.println("access denied " + paramPermission); 
          Thread.dumpStack();
          final ProtectionDomain pd = this.context[b];
          final Debug db = debug;
          AccessController.doPrivileged(new PrivilegedAction<Void>() {
                public Void run() {
                  db.println("domain that failed " + pd);
                  return null;
                }
              });
        } 
        throw new AccessControlException("access denied " + paramPermission, paramPermission);
      } 
    } 
    if (bool)
      debug.println("access allowed " + paramPermission); 
    checkPermission2(paramPermission);
  }
  
  private void checkPermission2(Permission paramPermission) throws AccessControlException {
    if (!this.isLimited)
      return; 
    if (this.privilegedContext != null)
      this.privilegedContext.checkPermission2(paramPermission); 
    if (this.isWrapped)
      return; 
    if (this.permissions != null) {
      Class clazz = paramPermission.getClass();
      for (byte b = 0; b < this.permissions.length; b++) {
        Permission permission = this.permissions[b];
        if (permission.getClass().equals(clazz) && permission.implies(paramPermission))
          return; 
      } 
    } 
    if (this.parent != null)
      if (this.permissions == null) {
        this.parent.checkPermission2(paramPermission);
      } else {
        this.parent.checkPermission(paramPermission);
      }  
  }
  
  AccessControlContext optimize() {
    ProtectionDomain[] arrayOfProtectionDomain2;
    AccessControlContext accessControlContext1;
    DomainCombiner domainCombiner = null;
    AccessControlContext accessControlContext2 = null;
    Permission[] arrayOfPermission = null;
    if (this.isPrivileged) {
      accessControlContext1 = this.privilegedContext;
      if (accessControlContext1 != null && accessControlContext1.isWrapped) {
        arrayOfPermission = accessControlContext1.permissions;
        accessControlContext2 = accessControlContext1.parent;
      } 
    } else {
      accessControlContext1 = AccessController.getInheritedAccessControlContext();
      if (accessControlContext1 != null && accessControlContext1.isLimited)
        accessControlContext2 = accessControlContext1; 
    } 
    boolean bool1 = (this.context == null) ? 1 : 0;
    boolean bool2 = (accessControlContext1 == null || accessControlContext1.context == null) ? 1 : 0;
    ProtectionDomain[] arrayOfProtectionDomain1 = bool2 ? null : accessControlContext1.context;
    boolean bool3 = ((accessControlContext1 == null || !accessControlContext1.isWrapped) && accessControlContext2 == null) ? 1 : 0;
    if (accessControlContext1 != null && accessControlContext1.combiner != null) {
      if (getDebug() != null)
        debug.println("AccessControlContext invoking the Combiner"); 
      domainCombiner = accessControlContext1.combiner;
      arrayOfProtectionDomain2 = domainCombiner.combine(this.context, arrayOfProtectionDomain1);
    } else {
      if (bool1) {
        if (bool2) {
          calculateFields(accessControlContext1, accessControlContext2, arrayOfPermission);
          return this;
        } 
        if (bool3)
          return accessControlContext1; 
      } else if (arrayOfProtectionDomain1 != null && bool3 && this.context.length == 1 && this.context[false] == arrayOfProtectionDomain1[false]) {
        return accessControlContext1;
      } 
      arrayOfProtectionDomain2 = combine(this.context, arrayOfProtectionDomain1);
      if (bool3 && !bool2 && arrayOfProtectionDomain2 == arrayOfProtectionDomain1)
        return accessControlContext1; 
      if (bool2 && arrayOfProtectionDomain2 == this.context) {
        calculateFields(accessControlContext1, accessControlContext2, arrayOfPermission);
        return this;
      } 
    } 
    this.context = arrayOfProtectionDomain2;
    this.combiner = domainCombiner;
    this.isPrivileged = false;
    calculateFields(accessControlContext1, accessControlContext2, arrayOfPermission);
    return this;
  }
  
  private static ProtectionDomain[] combine(ProtectionDomain[] paramArrayOfProtectionDomain1, ProtectionDomain[] paramArrayOfProtectionDomain2) {
    boolean bool1 = (paramArrayOfProtectionDomain1 == null) ? 1 : 0;
    boolean bool2 = (paramArrayOfProtectionDomain2 == null) ? 1 : 0;
    byte b1 = bool1 ? 0 : paramArrayOfProtectionDomain1.length;
    if (bool2 && b1 <= 2)
      return paramArrayOfProtectionDomain1; 
    byte b2 = bool2 ? 0 : paramArrayOfProtectionDomain2.length;
    ProtectionDomain[] arrayOfProtectionDomain = new ProtectionDomain[b1 + b2];
    if (!bool2)
      System.arraycopy(paramArrayOfProtectionDomain2, 0, arrayOfProtectionDomain, 0, b2); 
    for (byte b3 = 0; b3 < b1; b3++) {
      ProtectionDomain protectionDomain = paramArrayOfProtectionDomain1[b3];
      if (protectionDomain != null) {
        byte b = 0;
        while (true) {
          if (b < b2) {
            if (protectionDomain == arrayOfProtectionDomain[b])
              break; 
            b++;
            continue;
          } 
          arrayOfProtectionDomain[b2++] = protectionDomain;
          break;
        } 
      } 
    } 
    if (b2 != arrayOfProtectionDomain.length) {
      if (!bool2 && b2 == paramArrayOfProtectionDomain2.length)
        return paramArrayOfProtectionDomain2; 
      if (bool2 && b2 == b1)
        return paramArrayOfProtectionDomain1; 
      ProtectionDomain[] arrayOfProtectionDomain1 = new ProtectionDomain[b2];
      System.arraycopy(arrayOfProtectionDomain, 0, arrayOfProtectionDomain1, 0, b2);
      arrayOfProtectionDomain = arrayOfProtectionDomain1;
    } 
    return arrayOfProtectionDomain;
  }
  
  private void calculateFields(AccessControlContext paramAccessControlContext1, AccessControlContext paramAccessControlContext2, Permission[] paramArrayOfPermission) {
    ProtectionDomain[] arrayOfProtectionDomain1 = null;
    ProtectionDomain[] arrayOfProtectionDomain2 = null;
    arrayOfProtectionDomain1 = (paramAccessControlContext2 != null) ? paramAccessControlContext2.limitedContext : null;
    arrayOfProtectionDomain2 = (paramAccessControlContext1 != null) ? paramAccessControlContext1.limitedContext : null;
    ProtectionDomain[] arrayOfProtectionDomain3 = combine(arrayOfProtectionDomain1, arrayOfProtectionDomain2);
    if (arrayOfProtectionDomain3 != null && (this.context == null || !containsAllPDs(arrayOfProtectionDomain3, this.context))) {
      this.limitedContext = arrayOfProtectionDomain3;
      this.permissions = paramArrayOfPermission;
      this.parent = paramAccessControlContext2;
      this.isLimited = true;
    } 
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof AccessControlContext))
      return false; 
    AccessControlContext accessControlContext = (AccessControlContext)paramObject;
    return !equalContext(accessControlContext) ? false : (!!equalLimitedContext(accessControlContext));
  }
  
  private boolean equalContext(AccessControlContext paramAccessControlContext) { return !equalPDs(this.context, paramAccessControlContext.context) ? false : ((this.combiner == null && paramAccessControlContext.combiner != null) ? false : (!(this.combiner != null && !this.combiner.equals(paramAccessControlContext.combiner)))); }
  
  private boolean equalPDs(ProtectionDomain[] paramArrayOfProtectionDomain1, ProtectionDomain[] paramArrayOfProtectionDomain2) { return (paramArrayOfProtectionDomain1 == null) ? ((paramArrayOfProtectionDomain2 == null)) : ((paramArrayOfProtectionDomain2 == null) ? false : (!(!containsAllPDs(paramArrayOfProtectionDomain1, paramArrayOfProtectionDomain2) || !containsAllPDs(paramArrayOfProtectionDomain2, paramArrayOfProtectionDomain1)))); }
  
  private boolean equalLimitedContext(AccessControlContext paramAccessControlContext) {
    if (paramAccessControlContext == null)
      return false; 
    if (!this.isLimited && !paramAccessControlContext.isLimited)
      return true; 
    if (!this.isLimited || !paramAccessControlContext.isLimited)
      return false; 
    if ((this.isWrapped && !paramAccessControlContext.isWrapped) || (!this.isWrapped && paramAccessControlContext.isWrapped))
      return false; 
    if (this.permissions == null && paramAccessControlContext.permissions != null)
      return false; 
    if (this.permissions != null && paramAccessControlContext.permissions == null)
      return false; 
    if (!containsAllLimits(paramAccessControlContext) || !paramAccessControlContext.containsAllLimits(this))
      return false; 
    AccessControlContext accessControlContext1;
    AccessControlContext accessControlContext2 = (accessControlContext1 = getNextPC(this)).getNextPC(paramAccessControlContext);
    return (accessControlContext1 == null && accessControlContext2 != null && accessControlContext2.isLimited) ? false : ((accessControlContext1 != null && !accessControlContext1.equalLimitedContext(accessControlContext2)) ? false : ((this.parent == null && paramAccessControlContext.parent != null) ? false : (!(this.parent != null && !this.parent.equals(paramAccessControlContext.parent)))));
  }
  
  private static AccessControlContext getNextPC(AccessControlContext paramAccessControlContext) {
    while (paramAccessControlContext != null && paramAccessControlContext.privilegedContext != null) {
      paramAccessControlContext = paramAccessControlContext.privilegedContext;
      if (!paramAccessControlContext.isWrapped)
        return paramAccessControlContext; 
    } 
    return null;
  }
  
  private static boolean containsAllPDs(ProtectionDomain[] paramArrayOfProtectionDomain1, ProtectionDomain[] paramArrayOfProtectionDomain2) {
    boolean bool = false;
    for (byte b = 0; b < paramArrayOfProtectionDomain1.length; b++) {
      bool = false;
      ProtectionDomain protectionDomain;
      if ((protectionDomain = paramArrayOfProtectionDomain1[b]) == null) {
        for (byte b1 = 0; b1 < paramArrayOfProtectionDomain2.length && !bool; b1++)
          bool = (paramArrayOfProtectionDomain2[b1] == null); 
      } else {
        Class clazz = protectionDomain.getClass();
        for (byte b1 = 0; b1 < paramArrayOfProtectionDomain2.length && !bool; b1++) {
          ProtectionDomain protectionDomain1 = paramArrayOfProtectionDomain2[b1];
          bool = (protectionDomain1 != null && clazz == protectionDomain1.getClass() && protectionDomain.equals(protectionDomain1));
        } 
      } 
      if (!bool)
        return false; 
    } 
    return bool;
  }
  
  private boolean containsAllLimits(AccessControlContext paramAccessControlContext) {
    boolean bool = false;
    if (this.permissions == null && paramAccessControlContext.permissions == null)
      return true; 
    for (byte b = 0; b < this.permissions.length; b++) {
      Permission permission = this.permissions[b];
      Class clazz = permission.getClass();
      bool = false;
      for (byte b1 = 0; b1 < paramAccessControlContext.permissions.length && !bool; b1++) {
        Permission permission1 = paramAccessControlContext.permissions[b1];
        bool = (clazz.equals(permission1.getClass()) && permission.equals(permission1));
      } 
      if (!bool)
        return false; 
    } 
    return bool;
  }
  
  public int hashCode() {
    int i = 0;
    if (this.context == null)
      return i; 
    for (byte b = 0; b < this.context.length; b++) {
      if (this.context[b] != null)
        i ^= this.context[b].hashCode(); 
    } 
    return i;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\AccessControlContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */