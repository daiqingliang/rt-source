package java.security;

import java.util.Enumeration;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicReference;
import sun.security.jca.GetInstance;
import sun.security.provider.PolicyFile;
import sun.security.util.Debug;
import sun.security.util.SecurityConstants;

public abstract class Policy {
  public static final PermissionCollection UNSUPPORTED_EMPTY_COLLECTION = new UnsupportedEmptyCollection();
  
  private static AtomicReference<PolicyInfo> policy = new AtomicReference(new PolicyInfo(null, false));
  
  private static final Debug debug = Debug.getInstance("policy");
  
  private WeakHashMap<ProtectionDomain.Key, PermissionCollection> pdMapping;
  
  static boolean isSet() {
    PolicyInfo policyInfo = (PolicyInfo)policy.get();
    return (policyInfo.policy != null && policyInfo.initialized == true);
  }
  
  private static void checkPermission(String paramString) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new SecurityPermission("createPolicy." + paramString)); 
  }
  
  public static Policy getPolicy() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(SecurityConstants.GET_POLICY_PERMISSION); 
    return getPolicyNoCheck();
  }
  
  static Policy getPolicyNoCheck() {
    PolicyInfo policyInfo = (PolicyInfo)policy.get();
    if (!policyInfo.initialized || policyInfo.policy == null)
      synchronized (Policy.class) {
        PolicyInfo policyInfo1 = (PolicyInfo)policy.get();
        if (policyInfo1.policy == null) {
          String str = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
                public String run() { return Security.getProperty("policy.provider"); }
              });
          if (str == null)
            str = "sun.security.provider.PolicyFile"; 
          try {
            policyInfo1 = new PolicyInfo((Policy)Class.forName(str).newInstance(), true);
          } catch (Exception exception) {
            PolicyFile policyFile = new PolicyFile();
            policyInfo1 = new PolicyInfo(policyFile, false);
            policy.set(policyInfo1);
            final String pc = str;
            Policy policy1 = (Policy)AccessController.doPrivileged(new PrivilegedAction<Policy>() {
                  public Policy run() {
                    try {
                      ClassLoader classLoader1 = ClassLoader.getSystemClassLoader();
                      ClassLoader classLoader2 = null;
                      while (classLoader1 != null) {
                        classLoader2 = classLoader1;
                        classLoader1 = classLoader1.getParent();
                      } 
                      return (classLoader2 != null) ? (Policy)Class.forName(pc, true, classLoader2).newInstance() : null;
                    } catch (Exception exception) {
                      if (debug != null) {
                        debug.println("policy provider " + pc + " not available");
                        exception.printStackTrace();
                      } 
                      return null;
                    } 
                  }
                });
            if (policy1 != null) {
              policyInfo1 = new PolicyInfo(policy1, true);
            } else {
              if (debug != null)
                debug.println("using sun.security.provider.PolicyFile"); 
              policyInfo1 = new PolicyInfo(policyFile, true);
            } 
          } 
          policy.set(policyInfo1);
        } 
        return policyInfo1.policy;
      }  
    return policyInfo.policy;
  }
  
  public static void setPolicy(Policy paramPolicy) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new SecurityPermission("setPolicy")); 
    if (paramPolicy != null)
      initPolicy(paramPolicy); 
    synchronized (Policy.class) {
      policy.set(new PolicyInfo(paramPolicy, (paramPolicy != null)));
    } 
  }
  
  private static void initPolicy(final Policy p) {
    ProtectionDomain protectionDomain = (ProtectionDomain)AccessController.doPrivileged(new PrivilegedAction<ProtectionDomain>() {
          public ProtectionDomain run() { return p.getClass().getProtectionDomain(); }
        });
    PermissionCollection permissionCollection = null;
    synchronized (paramPolicy) {
      if (paramPolicy.pdMapping == null)
        paramPolicy.pdMapping = new WeakHashMap(); 
    } 
    if (protectionDomain.getCodeSource() != null) {
      Policy policy1 = ((PolicyInfo)policy.get()).policy;
      if (policy1 != null)
        permissionCollection = policy1.getPermissions(protectionDomain); 
      if (permissionCollection == null) {
        permissionCollection = new Permissions();
        permissionCollection.add(SecurityConstants.ALL_PERMISSION);
      } 
      synchronized (paramPolicy.pdMapping) {
        paramPolicy.pdMapping.put(protectionDomain.key, permissionCollection);
      } 
    } 
  }
  
  public static Policy getInstance(String paramString, Parameters paramParameters) throws NoSuchAlgorithmException {
    checkPermission(paramString);
    try {
      GetInstance.Instance instance = GetInstance.getInstance("Policy", PolicySpi.class, paramString, paramParameters);
      return new PolicyDelegate((PolicySpi)instance.impl, instance.provider, paramString, paramParameters, null);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      return handleException(noSuchAlgorithmException);
    } 
  }
  
  public static Policy getInstance(String paramString1, Parameters paramParameters, String paramString2) throws NoSuchProviderException, NoSuchAlgorithmException {
    if (paramString2 == null || paramString2.length() == 0)
      throw new IllegalArgumentException("missing provider"); 
    checkPermission(paramString1);
    try {
      GetInstance.Instance instance = GetInstance.getInstance("Policy", PolicySpi.class, paramString1, paramParameters, paramString2);
      return new PolicyDelegate((PolicySpi)instance.impl, instance.provider, paramString1, paramParameters, null);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      return handleException(noSuchAlgorithmException);
    } 
  }
  
  public static Policy getInstance(String paramString, Parameters paramParameters, Provider paramProvider) throws NoSuchAlgorithmException {
    if (paramProvider == null)
      throw new IllegalArgumentException("missing provider"); 
    checkPermission(paramString);
    try {
      GetInstance.Instance instance = GetInstance.getInstance("Policy", PolicySpi.class, paramString, paramParameters, paramProvider);
      return new PolicyDelegate((PolicySpi)instance.impl, instance.provider, paramString, paramParameters, null);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      return handleException(noSuchAlgorithmException);
    } 
  }
  
  private static Policy handleException(NoSuchAlgorithmException paramNoSuchAlgorithmException) throws NoSuchAlgorithmException {
    Throwable throwable = paramNoSuchAlgorithmException.getCause();
    if (throwable instanceof IllegalArgumentException)
      throw (IllegalArgumentException)throwable; 
    throw paramNoSuchAlgorithmException;
  }
  
  public Provider getProvider() { return null; }
  
  public String getType() { return null; }
  
  public Parameters getParameters() { return null; }
  
  public PermissionCollection getPermissions(CodeSource paramCodeSource) { return UNSUPPORTED_EMPTY_COLLECTION; }
  
  public PermissionCollection getPermissions(ProtectionDomain paramProtectionDomain) {
    PermissionCollection permissionCollection = null;
    if (paramProtectionDomain == null)
      return new Permissions(); 
    if (this.pdMapping == null)
      initPolicy(this); 
    synchronized (this.pdMapping) {
      permissionCollection = (PermissionCollection)this.pdMapping.get(paramProtectionDomain.key);
    } 
    if (permissionCollection != null) {
      Permissions permissions = new Permissions();
      synchronized (permissionCollection) {
        Enumeration enumeration = permissionCollection.elements();
        while (enumeration.hasMoreElements())
          permissions.add((Permission)enumeration.nextElement()); 
      } 
      return permissions;
    } 
    permissionCollection = getPermissions(paramProtectionDomain.getCodeSource());
    if (permissionCollection == null || permissionCollection == UNSUPPORTED_EMPTY_COLLECTION)
      permissionCollection = new Permissions(); 
    addStaticPerms(permissionCollection, paramProtectionDomain.getPermissions());
    return permissionCollection;
  }
  
  private void addStaticPerms(PermissionCollection paramPermissionCollection1, PermissionCollection paramPermissionCollection2) {
    if (paramPermissionCollection2 != null)
      synchronized (paramPermissionCollection2) {
        Enumeration enumeration = paramPermissionCollection2.elements();
        while (enumeration.hasMoreElements())
          paramPermissionCollection1.add((Permission)enumeration.nextElement()); 
      }  
  }
  
  public boolean implies(ProtectionDomain paramProtectionDomain, Permission paramPermission) {
    if (this.pdMapping == null)
      initPolicy(this); 
    synchronized (this.pdMapping) {
      permissionCollection = (PermissionCollection)this.pdMapping.get(paramProtectionDomain.key);
    } 
    if (permissionCollection != null)
      return permissionCollection.implies(paramPermission); 
    PermissionCollection permissionCollection = getPermissions(paramProtectionDomain);
    if (permissionCollection == null)
      return false; 
    synchronized (this.pdMapping) {
      this.pdMapping.put(paramProtectionDomain.key, permissionCollection);
    } 
    return permissionCollection.implies(paramPermission);
  }
  
  public void refresh() {}
  
  public static interface Parameters {}
  
  private static class PolicyDelegate extends Policy {
    private PolicySpi spi;
    
    private Provider p;
    
    private String type;
    
    private Policy.Parameters params;
    
    private PolicyDelegate(PolicySpi param1PolicySpi, Provider param1Provider, String param1String, Policy.Parameters param1Parameters) {
      this.spi = param1PolicySpi;
      this.p = param1Provider;
      this.type = param1String;
      this.params = param1Parameters;
    }
    
    public String getType() { return this.type; }
    
    public Policy.Parameters getParameters() { return this.params; }
    
    public Provider getProvider() { return this.p; }
    
    public PermissionCollection getPermissions(CodeSource param1CodeSource) { return this.spi.engineGetPermissions(param1CodeSource); }
    
    public PermissionCollection getPermissions(ProtectionDomain param1ProtectionDomain) { return this.spi.engineGetPermissions(param1ProtectionDomain); }
    
    public boolean implies(ProtectionDomain param1ProtectionDomain, Permission param1Permission) { return this.spi.engineImplies(param1ProtectionDomain, param1Permission); }
    
    public void refresh() { this.spi.engineRefresh(); }
  }
  
  private static class PolicyInfo {
    final Policy policy;
    
    final boolean initialized;
    
    PolicyInfo(Policy param1Policy, boolean param1Boolean) {
      this.policy = param1Policy;
      this.initialized = param1Boolean;
    }
  }
  
  private static class UnsupportedEmptyCollection extends PermissionCollection {
    private static final long serialVersionUID = -8492269157353014774L;
    
    private Permissions perms = new Permissions();
    
    public UnsupportedEmptyCollection() { this.perms.setReadOnly(); }
    
    public void add(Permission param1Permission) { this.perms.add(param1Permission); }
    
    public boolean implies(Permission param1Permission) { return this.perms.implies(param1Permission); }
    
    public Enumeration<Permission> elements() { return this.perms.elements(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\Policy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */