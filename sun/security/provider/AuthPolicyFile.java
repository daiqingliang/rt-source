package sun.security.provider;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.Security;
import java.security.UnresolvedPermission;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.security.auth.AuthPermission;
import javax.security.auth.Policy;
import javax.security.auth.PrivateCredentialPermission;
import javax.security.auth.Subject;
import sun.security.util.Debug;
import sun.security.util.PolicyUtil;
import sun.security.util.PropertyExpander;

@Deprecated
public class AuthPolicyFile extends Policy {
  static final ResourceBundle rb = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction<ResourceBundle>() {
        public ResourceBundle run() { return ResourceBundle.getBundle("sun.security.util.AuthResources"); }
      });
  
  private static final Debug debug = Debug.getInstance("policy", "\t[Auth Policy]");
  
  private static final String AUTH_POLICY = "java.security.auth.policy";
  
  private static final String SECURITY_MANAGER = "java.security.manager";
  
  private static final String AUTH_POLICY_URL = "auth.policy.url.";
  
  private Vector<PolicyEntry> policyEntries;
  
  private Hashtable<Object, Object> aliasMapping;
  
  private boolean initialized = false;
  
  private boolean expandProperties = true;
  
  private boolean ignoreIdentityScope = true;
  
  private static final Class<?>[] PARAMS = { String.class, String.class };
  
  public AuthPolicyFile() {
    String str = System.getProperty("java.security.auth.policy");
    if (str == null)
      str = System.getProperty("java.security.manager"); 
    if (str != null)
      init(); 
  }
  
  private void init() {
    if (this.initialized)
      return; 
    this.policyEntries = new Vector();
    this.aliasMapping = new Hashtable(11);
    initPolicyFile();
    this.initialized = true;
  }
  
  public void refresh() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new AuthPermission("refreshPolicy")); 
    this.initialized = false;
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            AuthPolicyFile.this.init();
            return null;
          }
        });
  }
  
  private KeyStore initKeyStore(URL paramURL, String paramString1, String paramString2) {
    if (paramString1 != null)
      try {
        KeyStore keyStore;
        URL uRL = null;
        try {
          uRL = new URL(paramString1);
        } catch (MalformedURLException malformedURLException) {
          uRL = new URL(paramURL, paramString1);
        } 
        if (debug != null)
          debug.println("reading keystore" + uRL); 
        BufferedInputStream bufferedInputStream = new BufferedInputStream(PolicyUtil.getInputStream(uRL));
        if (paramString2 != null) {
          keyStore = KeyStore.getInstance(paramString2);
        } else {
          keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        } 
        keyStore.load(bufferedInputStream, null);
        bufferedInputStream.close();
        return keyStore;
      } catch (Exception exception) {
        if (debug != null)
          exception.printStackTrace(); 
        return null;
      }  
    return null;
  }
  
  private void initPolicyFile() {
    String str1 = Security.getProperty("policy.expandProperties");
    if (str1 != null)
      this.expandProperties = str1.equalsIgnoreCase("true"); 
    String str2 = Security.getProperty("policy.ignoreIdentityScope");
    if (str2 != null)
      this.ignoreIdentityScope = str2.equalsIgnoreCase("true"); 
    String str3 = Security.getProperty("policy.allowSystemProperty");
    if (str3 != null && str3.equalsIgnoreCase("true")) {
      String str = System.getProperty("java.security.auth.policy");
      if (str != null) {
        boolean bool1 = false;
        if (str.startsWith("=")) {
          bool1 = true;
          str = str.substring(1);
        } 
        try {
          URL uRL;
          str = PropertyExpander.expand(str);
          File file = new File(str);
          if (file.exists()) {
            uRL = new URL("file:" + file.getCanonicalPath());
          } else {
            uRL = new URL(str);
          } 
          if (debug != null)
            debug.println("reading " + uRL); 
          init(uRL);
        } catch (Exception exception) {
          if (debug != null)
            debug.println("caught exception: " + exception); 
        } 
        if (bool1) {
          if (debug != null)
            debug.println("overriding other policies!"); 
          return;
        } 
      } 
    } 
    byte b = 1;
    boolean bool = false;
    String str4;
    while ((str4 = Security.getProperty("auth.policy.url." + b)) != null) {
      try {
        str4 = PropertyExpander.expand(str4).replace(File.separatorChar, '/');
        if (debug != null)
          debug.println("reading " + str4); 
        init(new URL(str4));
        bool = true;
      } catch (Exception exception) {
        if (debug != null) {
          debug.println("error reading policy " + exception);
          exception.printStackTrace();
        } 
      } 
      b++;
    } 
    if (!bool);
  }
  
  private boolean checkForTrustedIdentity(Certificate paramCertificate) { return false; }
  
  private void init(URL paramURL) {
    PolicyParser policyParser = new PolicyParser(this.expandProperties);
    try (InputStreamReader null = new InputStreamReader(PolicyUtil.getInputStream(paramURL))) {
      policyParser.read(inputStreamReader);
      keyStore = initKeyStore(paramURL, policyParser.getKeyStoreUrl(), policyParser.getKeyStoreType());
      Enumeration enumeration = policyParser.grantElements();
      while (enumeration.hasMoreElements()) {
        PolicyParser.GrantEntry grantEntry = (PolicyParser.GrantEntry)enumeration.nextElement();
        addGrantEntry(grantEntry, keyStore);
      } 
    } catch (ParsingException parsingException) {
      System.err.println("java.security.auth.policy" + rb.getString(".error.parsing.") + paramURL);
      System.err.println("java.security.auth.policy" + rb.getString("COLON") + parsingException.getMessage());
      if (debug != null)
        parsingException.printStackTrace(); 
    } catch (Exception exception) {
      if (debug != null) {
        debug.println("error parsing " + paramURL);
        debug.println(exception.toString());
        exception.printStackTrace();
      } 
    } 
  }
  
  CodeSource getCodeSource(PolicyParser.GrantEntry paramGrantEntry, KeyStore paramKeyStore) throws MalformedURLException {
    URL uRL;
    Certificate[] arrayOfCertificate = null;
    if (paramGrantEntry.signedBy != null) {
      arrayOfCertificate = getCertificates(paramKeyStore, paramGrantEntry.signedBy);
      if (arrayOfCertificate == null) {
        if (debug != null)
          debug.println(" no certs for alias " + paramGrantEntry.signedBy + ", ignoring."); 
        return null;
      } 
    } 
    if (paramGrantEntry.codeBase != null) {
      uRL = new URL(paramGrantEntry.codeBase);
    } else {
      uRL = null;
    } 
    return (paramGrantEntry.principals == null || paramGrantEntry.principals.size() == 0) ? canonicalizeCodebase(new CodeSource(uRL, arrayOfCertificate), false) : canonicalizeCodebase(new SubjectCodeSource(null, paramGrantEntry.principals, uRL, arrayOfCertificate), false);
  }
  
  private void addGrantEntry(PolicyParser.GrantEntry paramGrantEntry, KeyStore paramKeyStore) {
    if (debug != null) {
      debug.println("Adding policy entry: ");
      debug.println("  signedBy " + paramGrantEntry.signedBy);
      debug.println("  codeBase " + paramGrantEntry.codeBase);
      if (paramGrantEntry.principals != null)
        for (PolicyParser.PrincipalEntry principalEntry : paramGrantEntry.principals)
          debug.println("  " + principalEntry.getPrincipalClass() + " " + principalEntry.getPrincipalName());  
      debug.println();
    } 
    try {
      CodeSource codeSource = getCodeSource(paramGrantEntry, paramKeyStore);
      if (codeSource == null)
        return; 
      PolicyEntry policyEntry = new PolicyEntry(codeSource);
      Enumeration enumeration = paramGrantEntry.permissionElements();
      while (enumeration.hasMoreElements()) {
        PolicyParser.PermissionEntry permissionEntry = (PolicyParser.PermissionEntry)enumeration.nextElement();
        try {
          Permission permission;
          if (permissionEntry.permission.equals("javax.security.auth.PrivateCredentialPermission") && permissionEntry.name.endsWith(" self")) {
            permission = getInstance(permissionEntry.permission, permissionEntry.name + " \"self\"", permissionEntry.action);
          } else {
            permission = getInstance(permissionEntry.permission, permissionEntry.name, permissionEntry.action);
          } 
          policyEntry.add(permission);
          if (debug != null)
            debug.println("  " + permission); 
        } catch (ClassNotFoundException classNotFoundException) {
          Certificate[] arrayOfCertificate;
          if (permissionEntry.signedBy != null) {
            arrayOfCertificate = getCertificates(paramKeyStore, permissionEntry.signedBy);
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
          System.err.println("java.security.auth.policy" + rb.getString(".error.adding.Permission.") + permissionEntry.permission + rb.getString("SPACE") + invocationTargetException.getTargetException());
        } catch (Exception exception) {
          System.err.println("java.security.auth.policy" + rb.getString(".error.adding.Permission.") + permissionEntry.permission + rb.getString("SPACE") + exception);
        } 
      } 
      this.policyEntries.addElement(policyEntry);
    } catch (Exception exception) {
      System.err.println("java.security.auth.policy" + rb.getString(".error.adding.Entry.") + paramGrantEntry + rb.getString("SPACE") + exception);
    } 
    if (debug != null)
      debug.println(); 
  }
  
  private static final Permission getInstance(String paramString1, String paramString2, String paramString3) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
    Class clazz = Class.forName(paramString1);
    Constructor constructor = clazz.getConstructor(PARAMS);
    return (Permission)constructor.newInstance(new Object[] { paramString2, paramString3 });
  }
  
  Certificate[] getCertificates(KeyStore paramKeyStore, String paramString) {
    Vector vector = null;
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, ",");
    byte b = 0;
    while (stringTokenizer.hasMoreTokens()) {
      String str = stringTokenizer.nextToken().trim();
      b++;
      Certificate certificate = null;
      certificate = (Certificate)this.aliasMapping.get(str);
      if (certificate == null && paramKeyStore != null) {
        try {
          certificate = paramKeyStore.getCertificate(str);
        } catch (KeyStoreException keyStoreException) {}
        if (certificate != null) {
          this.aliasMapping.put(str, certificate);
          this.aliasMapping.put(certificate, str);
        } 
      } 
      if (certificate != null) {
        if (vector == null)
          vector = new Vector(); 
        vector.addElement(certificate);
      } 
    } 
    if (vector != null && b == vector.size()) {
      Certificate[] arrayOfCertificate = new Certificate[vector.size()];
      vector.copyInto(arrayOfCertificate);
      return arrayOfCertificate;
    } 
    return null;
  }
  
  private final Enumeration<PolicyEntry> elements() { return this.policyEntries.elements(); }
  
  public PermissionCollection getPermissions(final Subject subject, final CodeSource codesource) { return (PermissionCollection)AccessController.doPrivileged(new PrivilegedAction<PermissionCollection>() {
          public PermissionCollection run() {
            SubjectCodeSource subjectCodeSource = new SubjectCodeSource(subject, null, (codesource == null) ? null : codesource.getLocation(), (codesource == null) ? null : codesource.getCertificates());
            return AuthPolicyFile.this.initialized ? AuthPolicyFile.this.getPermissions(new Permissions(), subjectCodeSource) : new PolicyPermissions(AuthPolicyFile.this, subjectCodeSource);
          }
        }); }
  
  PermissionCollection getPermissions(CodeSource paramCodeSource) { return this.initialized ? getPermissions(new Permissions(), paramCodeSource) : new PolicyPermissions(this, paramCodeSource); }
  
  Permissions getPermissions(Permissions paramPermissions, CodeSource paramCodeSource) {
    if (!this.initialized)
      init(); 
    CodeSource[] arrayOfCodeSource = { null };
    arrayOfCodeSource[0] = canonicalizeCodebase(paramCodeSource, true);
    if (debug != null)
      debug.println("evaluate(" + arrayOfCodeSource[0] + ")\n"); 
    for (byte b = 0; b < this.policyEntries.size(); b++) {
      PolicyEntry policyEntry = (PolicyEntry)this.policyEntries.elementAt(b);
      if (debug != null)
        debug.println("PolicyFile CodeSource implies: " + policyEntry.codesource.toString() + "\n\n\t" + arrayOfCodeSource[0].toString() + "\n\n"); 
      if (policyEntry.codesource.implies(arrayOfCodeSource[0]))
        for (byte b1 = 0; b1 < policyEntry.permissions.size(); b1++) {
          Permission permission = (Permission)policyEntry.permissions.elementAt(b1);
          if (debug != null)
            debug.println("  granting " + permission); 
          if (!addSelfPermissions(permission, policyEntry.codesource, arrayOfCodeSource[0], paramPermissions))
            paramPermissions.add(permission); 
        }  
    } 
    if (!this.ignoreIdentityScope) {
      Certificate[] arrayOfCertificate = arrayOfCodeSource[0].getCertificates();
      if (arrayOfCertificate != null)
        for (byte b1 = 0; b1 < arrayOfCertificate.length; b1++) {
          if (this.aliasMapping.get(arrayOfCertificate[b1]) == null && checkForTrustedIdentity(arrayOfCertificate[b1]))
            paramPermissions.add(new AllPermission()); 
        }  
    } 
    return paramPermissions;
  }
  
  private boolean addSelfPermissions(Permission paramPermission, CodeSource paramCodeSource1, CodeSource paramCodeSource2, Permissions paramPermissions) {
    if (!(paramPermission instanceof PrivateCredentialPermission))
      return false; 
    if (!(paramCodeSource1 instanceof SubjectCodeSource))
      return false; 
    PrivateCredentialPermission privateCredentialPermission = (PrivateCredentialPermission)paramPermission;
    SubjectCodeSource subjectCodeSource = (SubjectCodeSource)paramCodeSource1;
    String[][] arrayOfString = privateCredentialPermission.getPrincipals();
    if (arrayOfString.length <= 0 || !arrayOfString[0][0].equalsIgnoreCase("self") || !arrayOfString[0][1].equalsIgnoreCase("self"))
      return false; 
    if (subjectCodeSource.getPrincipals() == null)
      return true; 
    for (PolicyParser.PrincipalEntry principalEntry : subjectCodeSource.getPrincipals()) {
      String[][] arrayOfString1 = getPrincipalInfo(principalEntry, paramCodeSource2);
      for (byte b = 0; b < arrayOfString1.length; b++) {
        PrivateCredentialPermission privateCredentialPermission1 = new PrivateCredentialPermission(privateCredentialPermission.getCredentialClass() + " " + arrayOfString1[b][0] + " \"" + arrayOfString1[b][1] + "\"", "read");
        if (debug != null)
          debug.println("adding SELF permission: " + privateCredentialPermission1.toString()); 
        paramPermissions.add(privateCredentialPermission1);
      } 
    } 
    return true;
  }
  
  private String[][] getPrincipalInfo(PolicyParser.PrincipalEntry paramPrincipalEntry, CodeSource paramCodeSource) {
    if (!paramPrincipalEntry.getPrincipalClass().equals("WILDCARD_PRINCIPAL_CLASS") && !paramPrincipalEntry.getPrincipalName().equals("WILDCARD_PRINCIPAL_NAME")) {
      String[][] arrayOfString1 = new String[1][2];
      arrayOfString1[0][0] = paramPrincipalEntry.getPrincipalClass();
      arrayOfString1[0][1] = paramPrincipalEntry.getPrincipalName();
      return arrayOfString1;
    } 
    if (!paramPrincipalEntry.getPrincipalClass().equals("WILDCARD_PRINCIPAL_CLASS") && paramPrincipalEntry.getPrincipalName().equals("WILDCARD_PRINCIPAL_NAME")) {
      SubjectCodeSource subjectCodeSource1 = (SubjectCodeSource)paramCodeSource;
      Set set1 = null;
      try {
        Class clazz = Class.forName(paramPrincipalEntry.getPrincipalClass(), false, ClassLoader.getSystemClassLoader());
        set1 = subjectCodeSource1.getSubject().getPrincipals(clazz);
      } catch (Exception exception) {
        if (debug != null)
          debug.println("problem finding Principal Class when expanding SELF permission: " + exception.toString()); 
      } 
      if (set1 == null)
        return new String[0][0]; 
      String[][] arrayOfString1 = new String[set1.size()][2];
      byte b1 = 0;
      for (Principal principal : set1) {
        arrayOfString1[b1][0] = principal.getClass().getName();
        arrayOfString1[b1][1] = principal.getName();
        b1++;
      } 
      return arrayOfString1;
    } 
    SubjectCodeSource subjectCodeSource = (SubjectCodeSource)paramCodeSource;
    Set set = subjectCodeSource.getSubject().getPrincipals();
    String[][] arrayOfString = new String[set.size()][2];
    byte b = 0;
    for (Principal principal : set) {
      arrayOfString[b][0] = principal.getClass().getName();
      arrayOfString[b][1] = principal.getName();
      b++;
    } 
    return arrayOfString;
  }
  
  Certificate[] getSignerCertificates(CodeSource paramCodeSource) {
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
    CodeSource codeSource = paramCodeSource;
    if (paramCodeSource.getLocation() != null && paramCodeSource.getLocation().getProtocol().equalsIgnoreCase("file")) {
      try {
        String str = paramCodeSource.getLocation().getFile().replace('/', File.separatorChar);
        URL uRL = null;
        if (str.endsWith("*")) {
          str = str.substring(0, str.length() - 1);
          boolean bool = false;
          if (str.endsWith(File.separator))
            bool = true; 
          if (str.equals(""))
            str = System.getProperty("user.dir"); 
          File file = new File(str);
          str = file.getCanonicalPath();
          StringBuffer stringBuffer = new StringBuffer(str);
          if (!str.endsWith(File.separator) && (bool || file.isDirectory()))
            stringBuffer.append(File.separatorChar); 
          stringBuffer.append('*');
          str = stringBuffer.toString();
        } else {
          str = (new File(str)).getCanonicalPath();
        } 
        uRL = (new File(str)).toURL();
        if (paramCodeSource instanceof SubjectCodeSource) {
          SubjectCodeSource subjectCodeSource = (SubjectCodeSource)paramCodeSource;
          if (paramBoolean) {
            codeSource = new SubjectCodeSource(subjectCodeSource.getSubject(), subjectCodeSource.getPrincipals(), uRL, getSignerCertificates(subjectCodeSource));
          } else {
            codeSource = new SubjectCodeSource(subjectCodeSource.getSubject(), subjectCodeSource.getPrincipals(), uRL, subjectCodeSource.getCertificates());
          } 
        } else if (paramBoolean) {
          codeSource = new CodeSource(uRL, getSignerCertificates(paramCodeSource));
        } else {
          codeSource = new CodeSource(uRL, paramCodeSource.getCertificates());
        } 
      } catch (IOException iOException) {
        if (paramBoolean)
          if (!(paramCodeSource instanceof SubjectCodeSource)) {
            codeSource = new CodeSource(paramCodeSource.getLocation(), getSignerCertificates(paramCodeSource));
          } else {
            SubjectCodeSource subjectCodeSource = (SubjectCodeSource)paramCodeSource;
            codeSource = new SubjectCodeSource(subjectCodeSource.getSubject(), subjectCodeSource.getPrincipals(), subjectCodeSource.getLocation(), getSignerCertificates(subjectCodeSource));
          }  
      } 
    } else if (paramBoolean) {
      if (!(paramCodeSource instanceof SubjectCodeSource)) {
        codeSource = new CodeSource(paramCodeSource.getLocation(), getSignerCertificates(paramCodeSource));
      } else {
        SubjectCodeSource subjectCodeSource = (SubjectCodeSource)paramCodeSource;
        codeSource = new SubjectCodeSource(subjectCodeSource.getSubject(), subjectCodeSource.getPrincipals(), subjectCodeSource.getLocation(), getSignerCertificates(subjectCodeSource));
      } 
    } 
    return codeSource;
  }
  
  private static class PolicyEntry {
    CodeSource codesource;
    
    Vector<Permission> permissions;
    
    PolicyEntry(CodeSource param1CodeSource) {
      this.codesource = param1CodeSource;
      this.permissions = new Vector();
    }
    
    void add(Permission param1Permission) { this.permissions.addElement(param1Permission); }
    
    CodeSource getCodeSource() { return this.codesource; }
    
    public String toString() {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append(AuthPolicyFile.rb.getString("LPARAM"));
      stringBuffer.append(getCodeSource());
      stringBuffer.append("\n");
      for (byte b = 0; b < this.permissions.size(); b++) {
        Permission permission = (Permission)this.permissions.elementAt(b);
        stringBuffer.append(AuthPolicyFile.rb.getString("SPACE"));
        stringBuffer.append(AuthPolicyFile.rb.getString("SPACE"));
        stringBuffer.append(permission);
        stringBuffer.append(AuthPolicyFile.rb.getString("NEWLINE"));
      } 
      stringBuffer.append(AuthPolicyFile.rb.getString("RPARAM"));
      stringBuffer.append(AuthPolicyFile.rb.getString("NEWLINE"));
      return stringBuffer.toString();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\AuthPolicyFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */