package sun.security.provider;

import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.NetPermission;
import java.net.SocketPermission;
import java.net.URI;
import java.net.URL;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.security.Security;
import java.security.UnresolvedPermission;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PropertyPermission;
import java.util.Random;
import java.util.StringTokenizer;
import javax.security.auth.Subject;
import javax.security.auth.x500.X500Principal;
import sun.misc.JavaSecurityProtectionDomainAccess;
import sun.misc.SharedSecrets;
import sun.net.www.ParseUtil;
import sun.security.util.Debug;
import sun.security.util.PolicyUtil;
import sun.security.util.PropertyExpander;
import sun.security.util.ResourcesMgr;
import sun.security.util.SecurityConstants;

public class PolicyFile extends Policy {
  private static final Debug debug = Debug.getInstance("policy");
  
  private static final String NONE = "NONE";
  
  private static final String P11KEYSTORE = "PKCS11";
  
  private static final String SELF = "${{self}}";
  
  private static final String X500PRINCIPAL = "javax.security.auth.x500.X500Principal";
  
  private static final String POLICY = "java.security.policy";
  
  private static final String SECURITY_MANAGER = "java.security.manager";
  
  private static final String POLICY_URL = "policy.url.";
  
  private static final String AUTH_POLICY = "java.security.auth.policy";
  
  private static final String AUTH_POLICY_URL = "auth.policy.url.";
  
  private static final int DEFAULT_CACHE_SIZE = 1;
  
  private boolean constructed = false;
  
  private boolean expandProperties = true;
  
  private boolean ignoreIdentityScope = true;
  
  private boolean allowSystemProperties = true;
  
  private boolean notUtf8 = false;
  
  private URL url;
  
  private static final Class[] PARAMS0 = new Class[0];
  
  private static final Class[] PARAMS1 = { String.class };
  
  private static final Class[] PARAMS2 = { String.class, String.class };
  
  public PolicyFile() { init((URL)null); }
  
  public PolicyFile(URL paramURL) {
    this.url = paramURL;
    init(paramURL);
  }
  
  private void init(URL paramURL) {
    byte b;
    String str = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
          public String run() {
            PolicyFile.this.expandProperties = "true".equalsIgnoreCase(Security.getProperty("policy.expandProperties"));
            PolicyFile.this.ignoreIdentityScope = "true".equalsIgnoreCase(Security.getProperty("policy.ignoreIdentityScope"));
            PolicyFile.this.allowSystemProperties = "true".equalsIgnoreCase(Security.getProperty("policy.allowSystemProperty"));
            PolicyFile.this.notUtf8 = "false".equalsIgnoreCase(System.getProperty("sun.security.policy.utf8"));
            return System.getProperty("sun.security.policy.numcaches");
          }
        });
    if (str != null) {
      try {
        b = Integer.parseInt(str);
      } catch (NumberFormatException numberFormatException) {
        b = 1;
      } 
    } else {
      b = 1;
    } 
    PolicyInfo policyInfo1 = new PolicyInfo(b);
    initPolicyFile(policyInfo1, paramURL);
    this.policyInfo = policyInfo1;
  }
  
  private void initPolicyFile(final PolicyInfo newInfo, final URL url) {
    if (paramURL != null) {
      if (debug != null)
        debug.println("reading " + paramURL); 
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
              if (!PolicyFile.this.init(url, newInfo))
                PolicyFile.this.initStaticPolicy(newInfo); 
              return null;
            }
          });
    } else {
      boolean bool = initPolicyFile("java.security.policy", "policy.url.", paramPolicyInfo);
      if (!bool)
        initStaticPolicy(paramPolicyInfo); 
      initPolicyFile("java.security.auth.policy", "auth.policy.url.", paramPolicyInfo);
    } 
  }
  
  private boolean initPolicyFile(final String propname, final String urlname, final PolicyInfo newInfo) {
    Boolean bool = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
          public Boolean run() {
            boolean bool = false;
            if (PolicyFile.this.allowSystemProperties) {
              String str1 = System.getProperty(propname);
              if (str1 != null) {
                boolean bool1 = false;
                if (str1.startsWith("=")) {
                  bool1 = true;
                  str1 = str1.substring(1);
                } 
                try {
                  URL uRL;
                  str1 = PropertyExpander.expand(str1);
                  File file = new File(str1);
                  if (file.exists()) {
                    uRL = ParseUtil.fileToEncodedURL(new File(file.getCanonicalPath()));
                  } else {
                    uRL = new URL(str1);
                  } 
                  if (debug != null)
                    debug.println("reading " + uRL); 
                  if (PolicyFile.this.init(uRL, newInfo))
                    bool = true; 
                } catch (Exception exception) {
                  if (debug != null)
                    debug.println("caught exception: " + exception); 
                } 
                if (bool1) {
                  if (debug != null)
                    debug.println("overriding other policies!"); 
                  return Boolean.valueOf(bool);
                } 
              } 
            } 
            String str;
            for (byte b = 1; (str = Security.getProperty(urlname + b)) != null; b++) {
              try {
                URL uRL = null;
                String str1 = PropertyExpander.expand(str).replace(File.separatorChar, '/');
                if (str.startsWith("file:${java.home}/") || str.startsWith("file:${user.home}/")) {
                  uRL = (new File(str1.substring(5))).toURI().toURL();
                } else {
                  uRL = (new URI(str1)).toURL();
                } 
                if (debug != null)
                  debug.println("reading " + uRL); 
                if (PolicyFile.this.init(uRL, newInfo))
                  bool = true; 
              } catch (Exception exception) {
                if (debug != null) {
                  debug.println("error reading policy " + exception);
                  exception.printStackTrace();
                } 
              } 
            } 
            return Boolean.valueOf(bool);
          }
        });
    return bool.booleanValue();
  }
  
  private boolean init(URL paramURL, PolicyInfo paramPolicyInfo) {
    bool = false;
    PolicyParser policyParser = new PolicyParser(this.expandProperties);
    inputStreamReader = null;
    try {
      if (this.notUtf8) {
        inputStreamReader = new InputStreamReader(PolicyUtil.getInputStream(paramURL));
      } else {
        inputStreamReader = new InputStreamReader(PolicyUtil.getInputStream(paramURL), "UTF-8");
      } 
      policyParser.read(inputStreamReader);
      keyStore = null;
      try {
        keyStore = PolicyUtil.getKeyStore(paramURL, policyParser.getKeyStoreUrl(), policyParser.getKeyStoreType(), policyParser.getKeyStoreProvider(), policyParser.getStorePassURL(), debug);
      } catch (Exception exception) {
        if (debug != null)
          exception.printStackTrace(); 
      } 
      Enumeration enumeration = policyParser.grantElements();
      while (enumeration.hasMoreElements()) {
        PolicyParser.GrantEntry grantEntry = (PolicyParser.GrantEntry)enumeration.nextElement();
        addGrantEntry(grantEntry, keyStore, paramPolicyInfo);
      } 
    } catch (ParsingException parsingException) {
      MessageFormat messageFormat = new MessageFormat(ResourcesMgr.getString("java.security.policy.error.parsing.policy.message"));
      Object[] arrayOfObject = { paramURL, parsingException.getLocalizedMessage() };
      System.err.println(messageFormat.format(arrayOfObject));
      if (debug != null)
        parsingException.printStackTrace(); 
    } catch (Exception exception) {
      if (debug != null) {
        debug.println("error parsing " + paramURL);
        debug.println(exception.toString());
        exception.printStackTrace();
      } 
    } finally {
      if (inputStreamReader != null) {
        try {
          inputStreamReader.close();
          bool = true;
        } catch (IOException iOException) {}
      } else {
        bool = true;
      } 
    } 
    return bool;
  }
  
  private void initStaticPolicy(final PolicyInfo newInfo) { AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            PolicyFile.PolicyEntry policyEntry = new PolicyFile.PolicyEntry(new CodeSource(null, (Certificate[])null));
            policyEntry.add(SecurityConstants.LOCAL_LISTEN_PERMISSION);
            policyEntry.add(new PropertyPermission("java.version", "read"));
            policyEntry.add(new PropertyPermission("java.vendor", "read"));
            policyEntry.add(new PropertyPermission("java.vendor.url", "read"));
            policyEntry.add(new PropertyPermission("java.class.version", "read"));
            policyEntry.add(new PropertyPermission("os.name", "read"));
            policyEntry.add(new PropertyPermission("os.version", "read"));
            policyEntry.add(new PropertyPermission("os.arch", "read"));
            policyEntry.add(new PropertyPermission("file.separator", "read"));
            policyEntry.add(new PropertyPermission("path.separator", "read"));
            policyEntry.add(new PropertyPermission("line.separator", "read"));
            policyEntry.add(new PropertyPermission("java.specification.version", "read"));
            policyEntry.add(new PropertyPermission("java.specification.vendor", "read"));
            policyEntry.add(new PropertyPermission("java.specification.name", "read"));
            policyEntry.add(new PropertyPermission("java.vm.specification.version", "read"));
            policyEntry.add(new PropertyPermission("java.vm.specification.vendor", "read"));
            policyEntry.add(new PropertyPermission("java.vm.specification.name", "read"));
            policyEntry.add(new PropertyPermission("java.vm.version", "read"));
            policyEntry.add(new PropertyPermission("java.vm.vendor", "read"));
            policyEntry.add(new PropertyPermission("java.vm.name", "read"));
            this.val$newInfo.policyEntries.add(policyEntry);
            String[] arrayOfString = PolicyParser.parseExtDirs("${{java.ext.dirs}}", 0);
            if (arrayOfString != null && arrayOfString.length > 0)
              for (byte b = 0; b < arrayOfString.length; b++) {
                try {
                  policyEntry = new PolicyFile.PolicyEntry(PolicyFile.this.canonicalizeCodebase(new CodeSource(new URL(arrayOfString[b]), (Certificate[])null), false));
                  policyEntry.add(SecurityConstants.ALL_PERMISSION);
                  this.val$newInfo.policyEntries.add(policyEntry);
                } catch (Exception exception) {}
              }  
            return null;
          }
        }); }
  
  private CodeSource getCodeSource(PolicyParser.GrantEntry paramGrantEntry, KeyStore paramKeyStore, PolicyInfo paramPolicyInfo) throws MalformedURLException {
    URL uRL;
    Certificate[] arrayOfCertificate = null;
    if (paramGrantEntry.signedBy != null) {
      arrayOfCertificate = getCertificates(paramKeyStore, paramGrantEntry.signedBy, paramPolicyInfo);
      if (arrayOfCertificate == null) {
        if (debug != null)
          debug.println("  -- No certs for alias '" + paramGrantEntry.signedBy + "' - ignoring entry"); 
        return null;
      } 
    } 
    if (paramGrantEntry.codeBase != null) {
      uRL = new URL(paramGrantEntry.codeBase);
    } else {
      uRL = null;
    } 
    return canonicalizeCodebase(new CodeSource(uRL, arrayOfCertificate), false);
  }
  
  private void addGrantEntry(PolicyParser.GrantEntry paramGrantEntry, KeyStore paramKeyStore, PolicyInfo paramPolicyInfo) {
    if (debug != null) {
      debug.println("Adding policy entry: ");
      debug.println("  signedBy " + paramGrantEntry.signedBy);
      debug.println("  codeBase " + paramGrantEntry.codeBase);
      if (paramGrantEntry.principals != null)
        for (PolicyParser.PrincipalEntry principalEntry : paramGrantEntry.principals)
          debug.println("  " + principalEntry.toString());  
    } 
    try {
      CodeSource codeSource = getCodeSource(paramGrantEntry, paramKeyStore, paramPolicyInfo);
      if (codeSource == null)
        return; 
      if (!replacePrincipals(paramGrantEntry.principals, paramKeyStore))
        return; 
      PolicyEntry policyEntry = new PolicyEntry(codeSource, paramGrantEntry.principals);
      Enumeration enumeration = paramGrantEntry.permissionElements();
      while (enumeration.hasMoreElements()) {
        PolicyParser.PermissionEntry permissionEntry = (PolicyParser.PermissionEntry)enumeration.nextElement();
        try {
          Permission permission;
          expandPermissionName(permissionEntry, paramKeyStore);
          if (permissionEntry.permission.equals("javax.security.auth.PrivateCredentialPermission") && permissionEntry.name.endsWith(" self"))
            permissionEntry.name = permissionEntry.name.substring(0, permissionEntry.name.indexOf("self")) + "${{self}}"; 
          if (permissionEntry.name != null && permissionEntry.name.indexOf("${{self}}") != -1) {
            Certificate[] arrayOfCertificate;
            if (permissionEntry.signedBy != null) {
              arrayOfCertificate = getCertificates(paramKeyStore, permissionEntry.signedBy, paramPolicyInfo);
            } else {
              arrayOfCertificate = null;
            } 
            permission = new SelfPermission(permissionEntry.permission, permissionEntry.name, permissionEntry.action, arrayOfCertificate);
          } else {
            permission = getInstance(permissionEntry.permission, permissionEntry.name, permissionEntry.action);
          } 
          policyEntry.add(permission);
          if (debug != null)
            debug.println("  " + permission); 
        } catch (ClassNotFoundException classNotFoundException) {
          Certificate[] arrayOfCertificate;
          if (permissionEntry.signedBy != null) {
            arrayOfCertificate = getCertificates(paramKeyStore, permissionEntry.signedBy, paramPolicyInfo);
          } else {
            arrayOfCertificate = null;
          } 
          if (arrayOfCertificate != null || permissionEntry.signedBy == null) {
            UnresolvedPermission unresolvedPermission = new UnresolvedPermission(permissionEntry.permission, permissionEntry.name, permissionEntry.action, arrayOfCertificate);
            policyEntry.add(unresolvedPermission);
            if (debug != null)
              debug.println("  " + unresolvedPermission); 
          } 
        } catch (InvocationTargetException invocationTargetException) {
          MessageFormat messageFormat = new MessageFormat(ResourcesMgr.getString("java.security.policy.error.adding.Permission.perm.message"));
          Object[] arrayOfObject = { permissionEntry.permission, invocationTargetException.getTargetException().toString() };
          System.err.println(messageFormat.format(arrayOfObject));
        } catch (Exception exception) {
          MessageFormat messageFormat = new MessageFormat(ResourcesMgr.getString("java.security.policy.error.adding.Permission.perm.message"));
          Object[] arrayOfObject = { permissionEntry.permission, exception.toString() };
          System.err.println(messageFormat.format(arrayOfObject));
        } 
      } 
      paramPolicyInfo.policyEntries.add(policyEntry);
    } catch (Exception exception) {
      MessageFormat messageFormat = new MessageFormat(ResourcesMgr.getString("java.security.policy.error.adding.Entry.message"));
      Object[] arrayOfObject = { exception.toString() };
      System.err.println(messageFormat.format(arrayOfObject));
    } 
    if (debug != null)
      debug.println(); 
  }
  
  private static final Permission getInstance(String paramString1, String paramString2, String paramString3) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
    Class clazz = Class.forName(paramString1, false, null);
    Permission permission = getKnownInstance(clazz, paramString2, paramString3);
    if (permission != null)
      return permission; 
    if (!Permission.class.isAssignableFrom(clazz))
      throw new ClassCastException(paramString1 + " is not a Permission"); 
    if (paramString2 == null && paramString3 == null)
      try {
        Constructor constructor1 = clazz.getConstructor(PARAMS0);
        return (Permission)constructor1.newInstance(new Object[0]);
      } catch (NoSuchMethodException noSuchMethodException) {
        try {
          Constructor constructor1 = clazz.getConstructor(PARAMS1);
          return (Permission)constructor1.newInstance(new Object[] { paramString2 });
        } catch (NoSuchMethodException noSuchMethodException1) {
          Constructor constructor1 = clazz.getConstructor(PARAMS2);
          return (Permission)constructor1.newInstance(new Object[] { paramString2, paramString3 });
        } 
      }  
    if (paramString2 != null && paramString3 == null)
      try {
        Constructor constructor1 = clazz.getConstructor(PARAMS1);
        return (Permission)constructor1.newInstance(new Object[] { paramString2 });
      } catch (NoSuchMethodException noSuchMethodException) {
        Constructor constructor1 = clazz.getConstructor(PARAMS2);
        return (Permission)constructor1.newInstance(new Object[] { paramString2, paramString3 });
      }  
    Constructor constructor = clazz.getConstructor(PARAMS2);
    return (Permission)constructor.newInstance(new Object[] { paramString2, paramString3 });
  }
  
  private static final Permission getKnownInstance(Class<?> paramClass, String paramString1, String paramString2) { return paramClass.equals(FilePermission.class) ? new FilePermission(paramString1, paramString2) : (paramClass.equals(SocketPermission.class) ? new SocketPermission(paramString1, paramString2) : (paramClass.equals(RuntimePermission.class) ? new RuntimePermission(paramString1, paramString2) : (paramClass.equals(PropertyPermission.class) ? new PropertyPermission(paramString1, paramString2) : (paramClass.equals(NetPermission.class) ? new NetPermission(paramString1, paramString2) : (paramClass.equals(java.security.AllPermission.class) ? SecurityConstants.ALL_PERMISSION : null))))); }
  
  private Certificate[] getCertificates(KeyStore paramKeyStore, String paramString, PolicyInfo paramPolicyInfo) {
    ArrayList arrayList = null;
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, ",");
    byte b = 0;
    while (stringTokenizer.hasMoreTokens()) {
      String str = stringTokenizer.nextToken().trim();
      b++;
      Certificate certificate = null;
      synchronized (paramPolicyInfo.aliasMapping) {
        certificate = (Certificate)paramPolicyInfo.aliasMapping.get(str);
        if (certificate == null && paramKeyStore != null) {
          try {
            certificate = paramKeyStore.getCertificate(str);
          } catch (KeyStoreException keyStoreException) {}
          if (certificate != null) {
            paramPolicyInfo.aliasMapping.put(str, certificate);
            paramPolicyInfo.aliasMapping.put(certificate, str);
          } 
        } 
      } 
      if (certificate != null) {
        if (arrayList == null)
          arrayList = new ArrayList(); 
        arrayList.add(certificate);
      } 
    } 
    if (arrayList != null && b == arrayList.size()) {
      Certificate[] arrayOfCertificate = new Certificate[arrayList.size()];
      arrayList.toArray(arrayOfCertificate);
      return arrayOfCertificate;
    } 
    return null;
  }
  
  public void refresh() { init(this.url); }
  
  public boolean implies(ProtectionDomain paramProtectionDomain, Permission paramPermission) {
    JavaSecurityProtectionDomainAccess.ProtectionDomainCache protectionDomainCache = this.policyInfo.getPdMapping();
    PermissionCollection permissionCollection = protectionDomainCache.get(paramProtectionDomain);
    if (permissionCollection != null)
      return permissionCollection.implies(paramPermission); 
    permissionCollection = getPermissions(paramProtectionDomain);
    if (permissionCollection == null)
      return false; 
    protectionDomainCache.put(paramProtectionDomain, permissionCollection);
    return permissionCollection.implies(paramPermission);
  }
  
  public PermissionCollection getPermissions(ProtectionDomain paramProtectionDomain) {
    Permissions permissions = new Permissions();
    if (paramProtectionDomain == null)
      return permissions; 
    getPermissions(permissions, paramProtectionDomain);
    PermissionCollection permissionCollection = paramProtectionDomain.getPermissions();
    if (permissionCollection != null)
      synchronized (permissionCollection) {
        Enumeration enumeration = permissionCollection.elements();
        while (enumeration.hasMoreElements())
          permissions.add((Permission)enumeration.nextElement()); 
      }  
    return permissions;
  }
  
  public PermissionCollection getPermissions(CodeSource paramCodeSource) { return getPermissions(new Permissions(), paramCodeSource); }
  
  private PermissionCollection getPermissions(Permissions paramPermissions, ProtectionDomain paramProtectionDomain) {
    if (debug != null)
      debug.println("getPermissions:\n\t" + printPD(paramProtectionDomain)); 
    final CodeSource cs = paramProtectionDomain.getCodeSource();
    if (codeSource1 == null)
      return paramPermissions; 
    CodeSource codeSource2 = (CodeSource)AccessController.doPrivileged(new PrivilegedAction<CodeSource>() {
          public CodeSource run() { return PolicyFile.this.canonicalizeCodebase(cs, true); }
        });
    return getPermissions(paramPermissions, codeSource2, paramProtectionDomain.getPrincipals());
  }
  
  private PermissionCollection getPermissions(Permissions paramPermissions, final CodeSource cs) {
    if (paramCodeSource == null)
      return paramPermissions; 
    CodeSource codeSource = (CodeSource)AccessController.doPrivileged(new PrivilegedAction<CodeSource>() {
          public CodeSource run() { return PolicyFile.this.canonicalizeCodebase(cs, true); }
        });
    return getPermissions(paramPermissions, codeSource, null);
  }
  
  private Permissions getPermissions(Permissions paramPermissions, CodeSource paramCodeSource, Principal[] paramArrayOfPrincipal) {
    PolicyInfo policyInfo1 = this.policyInfo;
    for (PolicyEntry policyEntry : policyInfo1.policyEntries)
      addPermissions(paramPermissions, paramCodeSource, paramArrayOfPrincipal, policyEntry); 
    synchronized (policyInfo1.identityPolicyEntries) {
      for (PolicyEntry policyEntry : policyInfo1.identityPolicyEntries)
        addPermissions(paramPermissions, paramCodeSource, paramArrayOfPrincipal, policyEntry); 
    } 
    if (!this.ignoreIdentityScope) {
      Certificate[] arrayOfCertificate = paramCodeSource.getCertificates();
      if (arrayOfCertificate != null)
        for (byte b = 0; b < arrayOfCertificate.length; b++) {
          Object object = policyInfo1.aliasMapping.get(arrayOfCertificate[b]);
          if (object == null && checkForTrustedIdentity(arrayOfCertificate[b], policyInfo1))
            paramPermissions.add(SecurityConstants.ALL_PERMISSION); 
        }  
    } 
    return paramPermissions;
  }
  
  private void addPermissions(Permissions paramPermissions, final CodeSource cs, Principal[] paramArrayOfPrincipal, final PolicyEntry entry) {
    if (debug != null)
      debug.println("evaluate codesources:\n\tPolicy CodeSource: " + paramPolicyEntry.getCodeSource() + "\n\tActive CodeSource: " + paramCodeSource); 
    Boolean bool = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
          public Boolean run() { return new Boolean(entry.getCodeSource().implies(cs)); }
        });
    if (!bool.booleanValue()) {
      if (debug != null)
        debug.println("evaluation (codesource) failed"); 
      return;
    } 
    List list = paramPolicyEntry.getPrincipals();
    if (debug != null) {
      ArrayList arrayList = new ArrayList();
      if (paramArrayOfPrincipal != null)
        for (byte b = 0; b < paramArrayOfPrincipal.length; b++)
          arrayList.add(new PolicyParser.PrincipalEntry(paramArrayOfPrincipal[b].getClass().getName(), paramArrayOfPrincipal[b].getName()));  
      debug.println("evaluate principals:\n\tPolicy Principals: " + list + "\n\tActive Principals: " + arrayList);
    } 
    if (list == null || list.isEmpty()) {
      addPerms(paramPermissions, paramArrayOfPrincipal, paramPolicyEntry);
      if (debug != null)
        debug.println("evaluation (codesource/principals) passed"); 
      return;
    } 
    if (paramArrayOfPrincipal == null || paramArrayOfPrincipal.length == 0) {
      if (debug != null)
        debug.println("evaluation (principals) failed"); 
      return;
    } 
    for (PolicyParser.PrincipalEntry principalEntry : list) {
      if (principalEntry.isWildcardClass())
        continue; 
      if (principalEntry.isWildcardName()) {
        if (wildcardPrincipalNameImplies(principalEntry.principalClass, paramArrayOfPrincipal))
          continue; 
        if (debug != null)
          debug.println("evaluation (principal name wildcard) failed"); 
        return;
      } 
      HashSet hashSet = new HashSet(Arrays.asList(paramArrayOfPrincipal));
      Subject subject = new Subject(true, hashSet, Collections.EMPTY_SET, Collections.EMPTY_SET);
      try {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class clazz = Class.forName(principalEntry.principalClass, false, classLoader);
        if (!Principal.class.isAssignableFrom(clazz))
          throw new ClassCastException(principalEntry.principalClass + " is not a Principal"); 
        Constructor constructor = clazz.getConstructor(PARAMS1);
        Principal principal = (Principal)constructor.newInstance(new Object[] { principalEntry.principalName });
        if (debug != null)
          debug.println("found Principal " + principal.getClass().getName()); 
        if (!principal.implies(subject)) {
          if (debug != null)
            debug.println("evaluation (principal implies) failed"); 
          return;
        } 
      } catch (Exception exception) {
        if (debug != null)
          exception.printStackTrace(); 
        if (!principalEntry.implies(subject)) {
          if (debug != null)
            debug.println("evaluation (default principal implies) failed"); 
          return;
        } 
      } 
    } 
    if (debug != null)
      debug.println("evaluation (codesource/principals) passed"); 
    addPerms(paramPermissions, paramArrayOfPrincipal, paramPolicyEntry);
  }
  
  private static boolean wildcardPrincipalNameImplies(String paramString, Principal[] paramArrayOfPrincipal) {
    for (Principal principal : paramArrayOfPrincipal) {
      if (paramString.equals(principal.getClass().getName()))
        return true; 
    } 
    return false;
  }
  
  private void addPerms(Permissions paramPermissions, Principal[] paramArrayOfPrincipal, PolicyEntry paramPolicyEntry) {
    for (byte b = 0; b < paramPolicyEntry.permissions.size(); b++) {
      Permission permission = (Permission)paramPolicyEntry.permissions.get(b);
      if (debug != null)
        debug.println("  granting " + permission); 
      if (permission instanceof SelfPermission) {
        expandSelf((SelfPermission)permission, paramPolicyEntry.getPrincipals(), paramArrayOfPrincipal, paramPermissions);
      } else {
        paramPermissions.add(permission);
      } 
    } 
  }
  
  private void expandSelf(SelfPermission paramSelfPermission, List<PolicyParser.PrincipalEntry> paramList, Principal[] paramArrayOfPrincipal, Permissions paramPermissions) {
    if (paramList == null || paramList.isEmpty()) {
      if (debug != null)
        debug.println("Ignoring permission " + paramSelfPermission.getSelfType() + " with target name (" + paramSelfPermission.getSelfName() + ").  No Principal(s) specified in the grant clause.  SELF-based target names are only valid in the context of a Principal-based grant entry."); 
      return;
    } 
    int i = 0;
    StringBuilder stringBuilder = new StringBuilder();
    int j;
    while ((j = paramSelfPermission.getSelfName().indexOf("${{self}}", i)) != -1) {
      stringBuilder.append(paramSelfPermission.getSelfName().substring(i, j));
      Iterator iterator = paramList.iterator();
      while (iterator.hasNext()) {
        PolicyParser.PrincipalEntry principalEntry = (PolicyParser.PrincipalEntry)iterator.next();
        String[][] arrayOfString = getPrincipalInfo(principalEntry, paramArrayOfPrincipal);
        for (byte b = 0; b < arrayOfString.length; b++) {
          if (b)
            stringBuilder.append(", "); 
          stringBuilder.append(arrayOfString[b][0] + " \"" + arrayOfString[b][1] + "\"");
        } 
        if (iterator.hasNext())
          stringBuilder.append(", "); 
      } 
      i = j + "${{self}}".length();
    } 
    stringBuilder.append(paramSelfPermission.getSelfName().substring(i));
    if (debug != null)
      debug.println("  expanded:\n\t" + paramSelfPermission.getSelfName() + "\n  into:\n\t" + stringBuilder.toString()); 
    try {
      paramPermissions.add(getInstance(paramSelfPermission.getSelfType(), stringBuilder.toString(), paramSelfPermission.getSelfActions()));
    } catch (ClassNotFoundException classNotFoundException) {
      Class clazz = null;
      synchronized (paramPermissions) {
        Enumeration enumeration = paramPermissions.elements();
        while (enumeration.hasMoreElements()) {
          Permission permission = (Permission)enumeration.nextElement();
          if (permission.getClass().getName().equals(paramSelfPermission.getSelfType())) {
            clazz = permission.getClass();
            break;
          } 
        } 
      } 
      if (clazz == null) {
        paramPermissions.add(new UnresolvedPermission(paramSelfPermission.getSelfType(), stringBuilder.toString(), paramSelfPermission.getSelfActions(), paramSelfPermission.getCerts()));
      } else {
        try {
          if (paramSelfPermission.getSelfActions() == null) {
            try {
              Constructor constructor = clazz.getConstructor(PARAMS1);
              paramPermissions.add((Permission)constructor.newInstance(new Object[] { stringBuilder.toString() }));
            } catch (NoSuchMethodException noSuchMethodException) {
              Constructor constructor = clazz.getConstructor(PARAMS2);
              paramPermissions.add((Permission)constructor.newInstance(new Object[] { stringBuilder.toString(), paramSelfPermission.getSelfActions() }));
            } 
          } else {
            Constructor constructor = clazz.getConstructor(PARAMS2);
            paramPermissions.add((Permission)constructor.newInstance(new Object[] { stringBuilder.toString(), paramSelfPermission.getSelfActions() }));
          } 
        } catch (Exception exception) {
          if (debug != null)
            debug.println("self entry expansion  instantiation failed: " + exception.toString()); 
        } 
      } 
    } catch (Exception exception) {
      if (debug != null)
        debug.println(exception.toString()); 
    } 
  }
  
  private String[][] getPrincipalInfo(PolicyParser.PrincipalEntry paramPrincipalEntry, Principal[] paramArrayOfPrincipal) {
    if (!paramPrincipalEntry.isWildcardClass() && !paramPrincipalEntry.isWildcardName()) {
      String[][] arrayOfString1 = new String[1][2];
      arrayOfString1[0][0] = paramPrincipalEntry.principalClass;
      arrayOfString1[0][1] = paramPrincipalEntry.principalName;
      return arrayOfString1;
    } 
    if (!paramPrincipalEntry.isWildcardClass() && paramPrincipalEntry.isWildcardName()) {
      ArrayList arrayList = new ArrayList();
      for (byte b1 = 0; b1 < paramArrayOfPrincipal.length; b1++) {
        if (paramPrincipalEntry.principalClass.equals(paramArrayOfPrincipal[b1].getClass().getName()))
          arrayList.add(paramArrayOfPrincipal[b1]); 
      } 
      String[][] arrayOfString1 = new String[arrayList.size()][2];
      byte b2 = 0;
      for (Principal principal : arrayList) {
        arrayOfString1[b2][0] = principal.getClass().getName();
        arrayOfString1[b2][1] = principal.getName();
        b2++;
      } 
      return arrayOfString1;
    } 
    String[][] arrayOfString = new String[paramArrayOfPrincipal.length][2];
    for (byte b = 0; b < paramArrayOfPrincipal.length; b++) {
      arrayOfString[b][0] = paramArrayOfPrincipal[b].getClass().getName();
      arrayOfString[b][1] = paramArrayOfPrincipal[b].getName();
    } 
    return arrayOfString;
  }
  
  protected Certificate[] getSignerCertificates(CodeSource paramCodeSource) {
    Certificate[] arrayOfCertificate1 = null;
    if ((arrayOfCertificate1 = paramCodeSource.getCertificates()) == null)
      return null; 
    byte b1;
    for (b1 = 0; b1 < arrayOfCertificate1.length; b1++) {
      if (!(arrayOfCertificate1[b1] instanceof X509Certificate))
        return paramCodeSource.getCertificates(); 
    } 
    b1 = 0;
    byte b2 = 0;
    while (b1 < arrayOfCertificate1.length) {
      b2++;
      while (b1 + 1 < arrayOfCertificate1.length && ((X509Certificate)arrayOfCertificate1[b1]).getIssuerDN().equals(((X509Certificate)arrayOfCertificate1[b1 + 1]).getSubjectDN()))
        b1++; 
      b1++;
    } 
    if (b2 == arrayOfCertificate1.length)
      return arrayOfCertificate1; 
    ArrayList arrayList = new ArrayList();
    for (b1 = 0; b1 < arrayOfCertificate1.length; b1++) {
      arrayList.add(arrayOfCertificate1[b1]);
      while (b1 + 1 < arrayOfCertificate1.length && ((X509Certificate)arrayOfCertificate1[b1]).getIssuerDN().equals(((X509Certificate)arrayOfCertificate1[b1 + 1]).getSubjectDN()))
        b1++; 
    } 
    Certificate[] arrayOfCertificate2 = new Certificate[arrayList.size()];
    arrayList.toArray(arrayOfCertificate2);
    return arrayOfCertificate2;
  }
  
  private CodeSource canonicalizeCodebase(CodeSource paramCodeSource, boolean paramBoolean) {
    String str = null;
    CodeSource codeSource = paramCodeSource;
    URL uRL = paramCodeSource.getLocation();
    if (uRL != null) {
      if (uRL.getProtocol().equals("jar")) {
        String str1 = uRL.getFile();
        int i = str1.indexOf("!/");
        if (i != -1)
          try {
            uRL = new URL(str1.substring(0, i));
          } catch (MalformedURLException malformedURLException) {} 
      } 
      if (uRL.getProtocol().equals("file")) {
        boolean bool = false;
        String str1 = uRL.getHost();
        bool = (str1 == null || str1.equals("") || str1.equals("~") || str1.equalsIgnoreCase("localhost")) ? 1 : 0;
        if (bool) {
          str = uRL.getFile().replace('/', File.separatorChar);
          str = ParseUtil.decode(str);
        } 
      } 
    } 
    if (str != null) {
      try {
        URL uRL1 = null;
        str = canonPath(str);
        uRL1 = ParseUtil.fileToEncodedURL(new File(str));
        if (paramBoolean) {
          codeSource = new CodeSource(uRL1, getSignerCertificates(paramCodeSource));
        } else {
          codeSource = new CodeSource(uRL1, paramCodeSource.getCertificates());
        } 
      } catch (IOException iOException) {
        if (paramBoolean)
          codeSource = new CodeSource(paramCodeSource.getLocation(), getSignerCertificates(paramCodeSource)); 
      } 
    } else if (paramBoolean) {
      codeSource = new CodeSource(paramCodeSource.getLocation(), getSignerCertificates(paramCodeSource));
    } 
    return codeSource;
  }
  
  private static String canonPath(String paramString) throws IOException {
    if (paramString.endsWith("*")) {
      paramString = paramString.substring(0, paramString.length() - 1) + "-";
      paramString = (new File(paramString)).getCanonicalPath();
      return paramString.substring(0, paramString.length() - 1) + "*";
    } 
    return (new File(paramString)).getCanonicalPath();
  }
  
  private String printPD(ProtectionDomain paramProtectionDomain) {
    Principal[] arrayOfPrincipal = paramProtectionDomain.getPrincipals();
    String str = "<no principals>";
    if (arrayOfPrincipal != null && arrayOfPrincipal.length > 0) {
      StringBuilder stringBuilder = new StringBuilder("(principals ");
      for (byte b = 0; b < arrayOfPrincipal.length; b++) {
        stringBuilder.append(arrayOfPrincipal[b].getClass().getName() + " \"" + arrayOfPrincipal[b].getName() + "\"");
        if (b < arrayOfPrincipal.length - 1) {
          stringBuilder.append(", ");
        } else {
          stringBuilder.append(")");
        } 
      } 
      str = stringBuilder.toString();
    } 
    return "PD CodeSource: " + paramProtectionDomain.getCodeSource() + "\n\tPD ClassLoader: " + paramProtectionDomain.getClassLoader() + "\n\tPD Principals: " + str;
  }
  
  private boolean replacePrincipals(List<PolicyParser.PrincipalEntry> paramList, KeyStore paramKeyStore) {
    if (paramList == null || paramList.isEmpty() || paramKeyStore == null)
      return true; 
    for (PolicyParser.PrincipalEntry principalEntry : paramList) {
      if (principalEntry.isReplaceName()) {
        String str;
        if ((str = getDN(principalEntry.principalName, paramKeyStore)) == null)
          return false; 
        if (debug != null)
          debug.println("  Replacing \"" + principalEntry.principalName + "\" with " + "javax.security.auth.x500.X500Principal" + "/\"" + str + "\""); 
        principalEntry.principalClass = "javax.security.auth.x500.X500Principal";
        principalEntry.principalName = str;
      } 
    } 
    return true;
  }
  
  private void expandPermissionName(PolicyParser.PermissionEntry paramPermissionEntry, KeyStore paramKeyStore) throws Exception {
    if (paramPermissionEntry.name == null || paramPermissionEntry.name.indexOf("${{", 0) == -1)
      return; 
    int i = 0;
    StringBuilder stringBuilder = new StringBuilder();
    int j;
    while ((j = paramPermissionEntry.name.indexOf("${{", i)) != -1) {
      int k = paramPermissionEntry.name.indexOf("}}", j);
      if (k < 1)
        break; 
      stringBuilder.append(paramPermissionEntry.name.substring(i, j));
      String str1 = paramPermissionEntry.name.substring(j + 3, k);
      String str2 = str1;
      int m;
      if ((m = str1.indexOf(":")) != -1)
        str2 = str1.substring(0, m); 
      if (str2.equalsIgnoreCase("self")) {
        stringBuilder.append(paramPermissionEntry.name.substring(j, k + 2));
        i = k + 2;
        continue;
      } 
      if (str2.equalsIgnoreCase("alias")) {
        if (m == -1) {
          MessageFormat messageFormat1 = new MessageFormat(ResourcesMgr.getString("alias.name.not.provided.pe.name."));
          Object[] arrayOfObject1 = { paramPermissionEntry.name };
          throw new Exception(messageFormat1.format(arrayOfObject1));
        } 
        String str = str1.substring(m + 1);
        if ((str = getDN(str, paramKeyStore)) == null) {
          MessageFormat messageFormat1 = new MessageFormat(ResourcesMgr.getString("unable.to.perform.substitution.on.alias.suffix"));
          Object[] arrayOfObject1 = { str1.substring(m + 1) };
          throw new Exception(messageFormat1.format(arrayOfObject1));
        } 
        stringBuilder.append("javax.security.auth.x500.X500Principal \"" + str + "\"");
        i = k + 2;
        continue;
      } 
      MessageFormat messageFormat = new MessageFormat(ResourcesMgr.getString("substitution.value.prefix.unsupported"));
      Object[] arrayOfObject = { str2 };
      throw new Exception(messageFormat.format(arrayOfObject));
    } 
    stringBuilder.append(paramPermissionEntry.name.substring(i));
    if (debug != null)
      debug.println("  Permission name expanded from:\n\t" + paramPermissionEntry.name + "\nto\n\t" + stringBuilder.toString()); 
    paramPermissionEntry.name = stringBuilder.toString();
  }
  
  private String getDN(String paramString, KeyStore paramKeyStore) {
    Certificate certificate = null;
    try {
      certificate = paramKeyStore.getCertificate(paramString);
    } catch (Exception exception) {
      if (debug != null)
        debug.println("  Error retrieving certificate for '" + paramString + "': " + exception.toString()); 
      return null;
    } 
    if (certificate == null || !(certificate instanceof X509Certificate)) {
      if (debug != null)
        debug.println("  -- No certificate for '" + paramString + "' - ignoring entry"); 
      return null;
    } 
    X509Certificate x509Certificate = (X509Certificate)certificate;
    X500Principal x500Principal = new X500Principal(x509Certificate.getSubjectX500Principal().toString());
    return x500Principal.getName();
  }
  
  private boolean checkForTrustedIdentity(Certificate paramCertificate, PolicyInfo paramPolicyInfo) { return false; }
  
  private static class PolicyEntry {
    private final CodeSource codesource;
    
    final List<Permission> permissions;
    
    private final List<PolicyParser.PrincipalEntry> principals;
    
    PolicyEntry(CodeSource param1CodeSource, List<PolicyParser.PrincipalEntry> param1List) {
      this.codesource = param1CodeSource;
      this.permissions = new ArrayList();
      this.principals = param1List;
    }
    
    PolicyEntry(CodeSource param1CodeSource) { this(param1CodeSource, null); }
    
    List<PolicyParser.PrincipalEntry> getPrincipals() { return this.principals; }
    
    void add(Permission param1Permission) { this.permissions.add(param1Permission); }
    
    CodeSource getCodeSource() { return this.codesource; }
    
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(ResourcesMgr.getString("LPARAM"));
      stringBuilder.append(getCodeSource());
      stringBuilder.append("\n");
      for (byte b = 0; b < this.permissions.size(); b++) {
        Permission permission = (Permission)this.permissions.get(b);
        stringBuilder.append(ResourcesMgr.getString("SPACE"));
        stringBuilder.append(ResourcesMgr.getString("SPACE"));
        stringBuilder.append(permission);
        stringBuilder.append(ResourcesMgr.getString("NEWLINE"));
      } 
      stringBuilder.append(ResourcesMgr.getString("RPARAM"));
      stringBuilder.append(ResourcesMgr.getString("NEWLINE"));
      return stringBuilder.toString();
    }
  }
  
  private static class PolicyInfo {
    private static final boolean verbose = false;
    
    final List<PolicyFile.PolicyEntry> policyEntries = new ArrayList();
    
    final List<PolicyFile.PolicyEntry> identityPolicyEntries = Collections.synchronizedList(new ArrayList(2));
    
    final Map<Object, Object> aliasMapping = Collections.synchronizedMap(new HashMap(11));
    
    private final JavaSecurityProtectionDomainAccess.ProtectionDomainCache[] pdMapping;
    
    private Random random;
    
    PolicyInfo(int param1Int) {
      this.pdMapping = new JavaSecurityProtectionDomainAccess.ProtectionDomainCache[param1Int];
      JavaSecurityProtectionDomainAccess javaSecurityProtectionDomainAccess = SharedSecrets.getJavaSecurityProtectionDomainAccess();
      for (byte b = 0; b < param1Int; b++)
        this.pdMapping[b] = javaSecurityProtectionDomainAccess.getProtectionDomainCache(); 
      if (param1Int > 1)
        this.random = new Random(); 
    }
    
    JavaSecurityProtectionDomainAccess.ProtectionDomainCache getPdMapping() {
      if (this.pdMapping.length == 1)
        return this.pdMapping[0]; 
      int i = Math.abs(this.random.nextInt() % this.pdMapping.length);
      return this.pdMapping[i];
    }
  }
  
  private static class SelfPermission extends Permission {
    private static final long serialVersionUID = -8315562579967246806L;
    
    private String type;
    
    private String name;
    
    private String actions;
    
    private Certificate[] certs;
    
    public SelfPermission(String param1String1, String param1String2, String param1String3, Certificate[] param1ArrayOfCertificate) {
      super(param1String1);
      if (param1String1 == null)
        throw new NullPointerException(ResourcesMgr.getString("type.can.t.be.null")); 
      this.type = param1String1;
      this.name = param1String2;
      this.actions = param1String3;
      if (param1ArrayOfCertificate != null) {
        byte b;
        for (b = 0; b < param1ArrayOfCertificate.length; b++) {
          if (!(param1ArrayOfCertificate[b] instanceof X509Certificate)) {
            this.certs = (Certificate[])param1ArrayOfCertificate.clone();
            break;
          } 
        } 
        if (this.certs == null) {
          b = 0;
          byte b1 = 0;
          while (b < param1ArrayOfCertificate.length) {
            b1++;
            while (b + 1 < param1ArrayOfCertificate.length && ((X509Certificate)param1ArrayOfCertificate[b]).getIssuerDN().equals(((X509Certificate)param1ArrayOfCertificate[b + 1]).getSubjectDN()))
              b++; 
            b++;
          } 
          if (b1 == param1ArrayOfCertificate.length)
            this.certs = (Certificate[])param1ArrayOfCertificate.clone(); 
          if (this.certs == null) {
            ArrayList arrayList = new ArrayList();
            for (b = 0; b < param1ArrayOfCertificate.length; b++) {
              arrayList.add(param1ArrayOfCertificate[b]);
              while (b + 1 < param1ArrayOfCertificate.length && ((X509Certificate)param1ArrayOfCertificate[b]).getIssuerDN().equals(((X509Certificate)param1ArrayOfCertificate[b + 1]).getSubjectDN()))
                b++; 
            } 
            this.certs = new Certificate[arrayList.size()];
            arrayList.toArray(this.certs);
          } 
        } 
      } 
    }
    
    public boolean implies(Permission param1Permission) { return false; }
    
    public boolean equals(Object param1Object) {
      if (param1Object == this)
        return true; 
      if (!(param1Object instanceof SelfPermission))
        return false; 
      SelfPermission selfPermission = (SelfPermission)param1Object;
      if (!this.type.equals(selfPermission.type) || !this.name.equals(selfPermission.name) || !this.actions.equals(selfPermission.actions))
        return false; 
      if (this.certs.length != selfPermission.certs.length)
        return false; 
      byte b;
      for (b = 0; b < this.certs.length; b++) {
        boolean bool = false;
        for (byte b1 = 0; b1 < selfPermission.certs.length; b1++) {
          if (this.certs[b].equals(selfPermission.certs[b1])) {
            bool = true;
            break;
          } 
        } 
        if (!bool)
          return false; 
      } 
      for (b = 0; b < selfPermission.certs.length; b++) {
        boolean bool = false;
        for (byte b1 = 0; b1 < this.certs.length; b1++) {
          if (selfPermission.certs[b].equals(this.certs[b1])) {
            bool = true;
            break;
          } 
        } 
        if (!bool)
          return false; 
      } 
      return true;
    }
    
    public int hashCode() {
      int i = this.type.hashCode();
      if (this.name != null)
        i ^= this.name.hashCode(); 
      if (this.actions != null)
        i ^= this.actions.hashCode(); 
      return i;
    }
    
    public String getActions() { return ""; }
    
    public String getSelfType() { return this.type; }
    
    public String getSelfName() { return this.name; }
    
    public String getSelfActions() { return this.actions; }
    
    public Certificate[] getCerts() { return this.certs; }
    
    public String toString() { return "(SelfPermission " + this.type + " " + this.name + " " + this.actions + ")"; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\PolicyFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */