package java.security;

import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.security.util.Debug;

public final class AccessController {
  @CallerSensitive
  public static native <T> T doPrivileged(PrivilegedAction<T> paramPrivilegedAction);
  
  @CallerSensitive
  public static <T> T doPrivilegedWithCombiner(PrivilegedAction<T> paramPrivilegedAction) {
    AccessControlContext accessControlContext = getStackAccessControlContext();
    if (accessControlContext == null)
      return (T)doPrivileged(paramPrivilegedAction); 
    DomainCombiner domainCombiner = accessControlContext.getAssignedCombiner();
    return (T)doPrivileged(paramPrivilegedAction, preserveCombiner(domainCombiner, Reflection.getCallerClass()));
  }
  
  @CallerSensitive
  public static native <T> T doPrivileged(PrivilegedAction<T> paramPrivilegedAction, AccessControlContext paramAccessControlContext);
  
  @CallerSensitive
  public static <T> T doPrivileged(PrivilegedAction<T> paramPrivilegedAction, AccessControlContext paramAccessControlContext, Permission... paramVarArgs) {
    AccessControlContext accessControlContext = getContext();
    if (paramVarArgs == null)
      throw new NullPointerException("null permissions parameter"); 
    Class clazz = Reflection.getCallerClass();
    return (T)doPrivileged(paramPrivilegedAction, createWrapper(null, clazz, accessControlContext, paramAccessControlContext, paramVarArgs));
  }
  
  @CallerSensitive
  public static <T> T doPrivilegedWithCombiner(PrivilegedAction<T> paramPrivilegedAction, AccessControlContext paramAccessControlContext, Permission... paramVarArgs) {
    AccessControlContext accessControlContext = getContext();
    DomainCombiner domainCombiner = accessControlContext.getCombiner();
    if (domainCombiner == null && paramAccessControlContext != null)
      domainCombiner = paramAccessControlContext.getCombiner(); 
    if (paramVarArgs == null)
      throw new NullPointerException("null permissions parameter"); 
    Class clazz = Reflection.getCallerClass();
    return (T)doPrivileged(paramPrivilegedAction, createWrapper(domainCombiner, clazz, accessControlContext, paramAccessControlContext, paramVarArgs));
  }
  
  @CallerSensitive
  public static native <T> T doPrivileged(PrivilegedExceptionAction<T> paramPrivilegedExceptionAction) throws PrivilegedActionException;
  
  @CallerSensitive
  public static <T> T doPrivilegedWithCombiner(PrivilegedExceptionAction<T> paramPrivilegedExceptionAction) throws PrivilegedActionException {
    AccessControlContext accessControlContext = getStackAccessControlContext();
    if (accessControlContext == null)
      return (T)doPrivileged(paramPrivilegedExceptionAction); 
    DomainCombiner domainCombiner = accessControlContext.getAssignedCombiner();
    return (T)doPrivileged(paramPrivilegedExceptionAction, preserveCombiner(domainCombiner, Reflection.getCallerClass()));
  }
  
  private static AccessControlContext preserveCombiner(DomainCombiner paramDomainCombiner, Class<?> paramClass) { return createWrapper(paramDomainCombiner, paramClass, null, null, null); }
  
  private static AccessControlContext createWrapper(DomainCombiner paramDomainCombiner, Class<?> paramClass, AccessControlContext paramAccessControlContext1, AccessControlContext paramAccessControlContext2, Permission[] paramArrayOfPermission) {
    ProtectionDomain protectionDomain = getCallerPD(paramClass);
    if (paramAccessControlContext2 != null && !paramAccessControlContext2.isAuthorized() && System.getSecurityManager() != null && !protectionDomain.impliesCreateAccessControlContext()) {
      ProtectionDomain protectionDomain1 = new ProtectionDomain(null, null);
      return new AccessControlContext(new ProtectionDomain[] { protectionDomain1 });
    } 
    return new AccessControlContext(protectionDomain, paramDomainCombiner, paramAccessControlContext1, paramAccessControlContext2, paramArrayOfPermission);
  }
  
  private static ProtectionDomain getCallerPD(final Class<?> caller) { return (ProtectionDomain)doPrivileged(new PrivilegedAction<ProtectionDomain>() {
          public ProtectionDomain run() { return caller.getProtectionDomain(); }
        }); }
  
  @CallerSensitive
  public static native <T> T doPrivileged(PrivilegedExceptionAction<T> paramPrivilegedExceptionAction, AccessControlContext paramAccessControlContext) throws PrivilegedActionException;
  
  @CallerSensitive
  public static <T> T doPrivileged(PrivilegedExceptionAction<T> paramPrivilegedExceptionAction, AccessControlContext paramAccessControlContext, Permission... paramVarArgs) throws PrivilegedActionException {
    AccessControlContext accessControlContext = getContext();
    if (paramVarArgs == null)
      throw new NullPointerException("null permissions parameter"); 
    Class clazz = Reflection.getCallerClass();
    return (T)doPrivileged(paramPrivilegedExceptionAction, createWrapper(null, clazz, accessControlContext, paramAccessControlContext, paramVarArgs));
  }
  
  @CallerSensitive
  public static <T> T doPrivilegedWithCombiner(PrivilegedExceptionAction<T> paramPrivilegedExceptionAction, AccessControlContext paramAccessControlContext, Permission... paramVarArgs) throws PrivilegedActionException {
    AccessControlContext accessControlContext = getContext();
    DomainCombiner domainCombiner = accessControlContext.getCombiner();
    if (domainCombiner == null && paramAccessControlContext != null)
      domainCombiner = paramAccessControlContext.getCombiner(); 
    if (paramVarArgs == null)
      throw new NullPointerException("null permissions parameter"); 
    Class clazz = Reflection.getCallerClass();
    return (T)doPrivileged(paramPrivilegedExceptionAction, createWrapper(domainCombiner, clazz, accessControlContext, paramAccessControlContext, paramVarArgs));
  }
  
  private static native AccessControlContext getStackAccessControlContext();
  
  static native AccessControlContext getInheritedAccessControlContext();
  
  public static AccessControlContext getContext() {
    AccessControlContext accessControlContext = getStackAccessControlContext();
    return (accessControlContext == null) ? new AccessControlContext(null, true) : accessControlContext.optimize();
  }
  
  public static void checkPermission(Permission paramPermission) throws AccessControlException {
    if (paramPermission == null)
      throw new NullPointerException("permission can't be null"); 
    AccessControlContext accessControlContext1 = getStackAccessControlContext();
    if (accessControlContext1 == null) {
      Debug debug = AccessControlContext.getDebug();
      boolean bool = false;
      if (debug != null) {
        bool = !Debug.isOn("codebase=") ? 1 : 0;
        bool &= ((!Debug.isOn("permission=") || Debug.isOn("permission=" + paramPermission.getClass().getCanonicalName())) ? 1 : 0);
      } 
      if (bool && Debug.isOn("stack"))
        Thread.dumpStack(); 
      if (bool && Debug.isOn("domain"))
        debug.println("domain (context is null)"); 
      if (bool)
        debug.println("access allowed " + paramPermission); 
      return;
    } 
    AccessControlContext accessControlContext2 = accessControlContext1.optimize();
    accessControlContext2.checkPermission(paramPermission);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\AccessController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */