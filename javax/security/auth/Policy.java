package javax.security.auth;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.security.Security;
import java.util.Objects;
import sun.security.util.Debug;
import sun.security.util.ResourcesMgr;

@Deprecated
public abstract class Policy {
  private static Policy policy;
  
  private static final String AUTH_POLICY = "sun.security.provider.AuthPolicyFile";
  
  private final AccessControlContext acc = AccessController.getContext();
  
  private static boolean isCustomPolicy;
  
  public static Policy getPolicy() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new AuthPermission("getPolicy")); 
    return getPolicyNoCheck();
  }
  
  static Policy getPolicyNoCheck() {
    if (policy == null)
      synchronized (Policy.class) {
        if (policy == null) {
          String str = null;
          str = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
                public String run() { return Security.getProperty("auth.policy.provider"); }
              });
          if (str == null)
            str = "sun.security.provider.AuthPolicyFile"; 
          try {
            final String finalClass = str;
            final Policy untrustedImpl = (Policy)AccessController.doPrivileged(new PrivilegedExceptionAction<Policy>() {
                  public Policy run() {
                    Class clazz = Class.forName(finalClass, false, Thread.currentThread().getContextClassLoader()).asSubclass(Policy.class);
                    return (Policy)clazz.newInstance();
                  }
                });
            AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                  public Void run() {
                    Policy.setPolicy(untrustedImpl);
                    isCustomPolicy = !finalClass.equals("sun.security.provider.AuthPolicyFile");
                    return null;
                  }
                },  (AccessControlContext)Objects.requireNonNull(policy1.acc));
          } catch (Exception exception) {
            throw new SecurityException(ResourcesMgr.getString("unable.to.instantiate.Subject.based.policy"));
          } 
        } 
      }  
    return policy;
  }
  
  public static void setPolicy(Policy paramPolicy) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new AuthPermission("setPolicy")); 
    policy = paramPolicy;
    isCustomPolicy = (paramPolicy != null);
  }
  
  static boolean isCustomPolicySet(Debug paramDebug) {
    if (policy != null) {
      if (paramDebug != null && isCustomPolicy)
        paramDebug.println("Providing backwards compatibility for javax.security.auth.policy implementation: " + policy.toString()); 
      return isCustomPolicy;
    } 
    String str = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
          public String run() { return Security.getProperty("auth.policy.provider"); }
        });
    if (str != null && !str.equals("sun.security.provider.AuthPolicyFile")) {
      if (paramDebug != null)
        paramDebug.println("Providing backwards compatibility for javax.security.auth.policy implementation: " + str); 
      return true;
    } 
    return false;
  }
  
  public abstract PermissionCollection getPermissions(Subject paramSubject, CodeSource paramCodeSource);
  
  public abstract void refresh();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\security\auth\Policy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */