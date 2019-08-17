package sun.security.tools.policytool;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Permission;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.text.Collator;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.security.auth.x500.X500Principal;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import sun.security.provider.PolicyParser;
import sun.security.util.PolicyUtil;
import sun.security.util.PropertyExpander;

public class PolicyTool {
  static final ResourceBundle rb = ResourceBundle.getBundle("sun.security.tools.policytool.Resources");
  
  static final Collator collator = Collator.getInstance();
  
  Vector<String> warnings;
  
  boolean newWarning = false;
  
  boolean modified = false;
  
  private static final boolean testing = false;
  
  private static final Class<?>[] TWOPARAMS;
  
  private static final Class<?>[] ONEPARAMS;
  
  private static final Class<?>[] NOPARAMS;
  
  private static String policyFileName;
  
  private Vector<PolicyEntry> policyEntries = null;
  
  private PolicyParser parser = null;
  
  private KeyStore keyStore = null;
  
  private String keyStoreName = " ";
  
  private String keyStoreType = " ";
  
  private String keyStoreProvider = " ";
  
  private String keyStorePwdURL = " ";
  
  private static final String P11KEYSTORE = "PKCS11";
  
  private static final String NONE = "NONE";
  
  private PolicyTool() {
    this.policyEntries = new Vector();
    this.parser = new PolicyParser();
    this.warnings = new Vector();
  }
  
  String getPolicyFileName() { return policyFileName; }
  
  void setPolicyFileName(String paramString) { policyFileName = paramString; }
  
  void clearKeyStoreInfo() {
    this.keyStoreName = null;
    this.keyStoreType = null;
    this.keyStoreProvider = null;
    this.keyStorePwdURL = null;
    this.keyStore = null;
  }
  
  String getKeyStoreName() { return this.keyStoreName; }
  
  String getKeyStoreType() { return this.keyStoreType; }
  
  String getKeyStoreProvider() { return this.keyStoreProvider; }
  
  String getKeyStorePwdURL() { return this.keyStorePwdURL; }
  
  void openPolicy(String paramString) {
    this.newWarning = false;
    this.policyEntries = new Vector();
    this.parser = new PolicyParser();
    this.warnings = new Vector();
    setPolicyFileName(null);
    clearKeyStoreInfo();
    if (paramString == null) {
      this.modified = false;
      return;
    } 
    setPolicyFileName(paramString);
    this.parser.read(new FileReader(paramString));
    openKeyStore(this.parser.getKeyStoreUrl(), this.parser.getKeyStoreType(), this.parser.getKeyStoreProvider(), this.parser.getStorePassURL());
    Enumeration enumeration = this.parser.grantElements();
    while (enumeration.hasMoreElements()) {
      PolicyParser.GrantEntry grantEntry = (PolicyParser.GrantEntry)enumeration.nextElement();
      if (grantEntry.signedBy != null) {
        String[] arrayOfString = parseSigners(grantEntry.signedBy);
        for (byte b = 0; b < arrayOfString.length; b++) {
          PublicKey publicKey = getPublicKeyAlias(arrayOfString[b]);
          if (publicKey == null) {
            this.newWarning = true;
            MessageFormat messageFormat = new MessageFormat(getMessage("Warning.A.public.key.for.alias.signers.i.does.not.exist.Make.sure.a.KeyStore.is.properly.configured."));
            Object[] arrayOfObject = { arrayOfString[b] };
            this.warnings.addElement(messageFormat.format(arrayOfObject));
          } 
        } 
      } 
      ListIterator listIterator = grantEntry.principals.listIterator(0);
      while (listIterator.hasNext()) {
        PolicyParser.PrincipalEntry principalEntry = (PolicyParser.PrincipalEntry)listIterator.next();
        try {
          verifyPrincipal(principalEntry.getPrincipalClass(), principalEntry.getPrincipalName());
        } catch (ClassNotFoundException classNotFoundException) {
          this.newWarning = true;
          MessageFormat messageFormat = new MessageFormat(getMessage("Warning.Class.not.found.class"));
          Object[] arrayOfObject = { principalEntry.getPrincipalClass() };
          this.warnings.addElement(messageFormat.format(arrayOfObject));
        } 
      } 
      Enumeration enumeration1 = grantEntry.permissionElements();
      while (enumeration1.hasMoreElements()) {
        PolicyParser.PermissionEntry permissionEntry = (PolicyParser.PermissionEntry)enumeration1.nextElement();
        try {
          verifyPermission(permissionEntry.permission, permissionEntry.name, permissionEntry.action);
        } catch (ClassNotFoundException classNotFoundException) {
          this.newWarning = true;
          MessageFormat messageFormat = new MessageFormat(getMessage("Warning.Class.not.found.class"));
          Object[] arrayOfObject = { permissionEntry.permission };
          this.warnings.addElement(messageFormat.format(arrayOfObject));
        } catch (InvocationTargetException invocationTargetException) {
          this.newWarning = true;
          MessageFormat messageFormat = new MessageFormat(getMessage("Warning.Invalid.argument.s.for.constructor.arg"));
          Object[] arrayOfObject = { permissionEntry.permission };
          this.warnings.addElement(messageFormat.format(arrayOfObject));
        } 
        if (permissionEntry.signedBy != null) {
          String[] arrayOfString = parseSigners(permissionEntry.signedBy);
          for (byte b = 0; b < arrayOfString.length; b++) {
            PublicKey publicKey = getPublicKeyAlias(arrayOfString[b]);
            if (publicKey == null) {
              this.newWarning = true;
              MessageFormat messageFormat = new MessageFormat(getMessage("Warning.A.public.key.for.alias.signers.i.does.not.exist.Make.sure.a.KeyStore.is.properly.configured."));
              Object[] arrayOfObject = { arrayOfString[b] };
              this.warnings.addElement(messageFormat.format(arrayOfObject));
            } 
          } 
        } 
      } 
      PolicyEntry policyEntry = new PolicyEntry(this, grantEntry);
      this.policyEntries.addElement(policyEntry);
    } 
    this.modified = false;
  }
  
  void savePolicy(String paramString) {
    this.parser.setKeyStoreUrl(this.keyStoreName);
    this.parser.setKeyStoreType(this.keyStoreType);
    this.parser.setKeyStoreProvider(this.keyStoreProvider);
    this.parser.setStorePassURL(this.keyStorePwdURL);
    this.parser.write(new FileWriter(paramString));
    this.modified = false;
  }
  
  void openKeyStore(String paramString1, String paramString2, String paramString3, String paramString4) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, IOException, CertificateException, NoSuchProviderException, PropertyExpander.ExpandException {
    if (paramString1 == null && paramString2 == null && paramString3 == null && paramString4 == null) {
      this.keyStoreName = null;
      this.keyStoreType = null;
      this.keyStoreProvider = null;
      this.keyStorePwdURL = null;
      return;
    } 
    URL uRL = null;
    if (policyFileName != null) {
      File file = new File(policyFileName);
      uRL = new URL("file:" + file.getCanonicalPath());
    } 
    if (paramString1 != null && paramString1.length() > 0)
      paramString1 = PropertyExpander.expand(paramString1).replace(File.separatorChar, '/'); 
    if (paramString2 == null || paramString2.length() == 0)
      paramString2 = KeyStore.getDefaultType(); 
    if (paramString4 != null && paramString4.length() > 0)
      paramString4 = PropertyExpander.expand(paramString4).replace(File.separatorChar, '/'); 
    try {
      this.keyStore = PolicyUtil.getKeyStore(uRL, paramString1, paramString2, paramString3, paramString4, null);
    } catch (IOException iOException) {
      String str = "no password provided, and no callback handler available for retrieving password";
      Throwable throwable = iOException.getCause();
      if (throwable != null && throwable instanceof javax.security.auth.login.LoginException && str.equals(throwable.getMessage()))
        throw new IOException(str); 
      throw iOException;
    } 
    this.keyStoreName = paramString1;
    this.keyStoreType = paramString2;
    this.keyStoreProvider = paramString3;
    this.keyStorePwdURL = paramString4;
  }
  
  boolean addEntry(PolicyEntry paramPolicyEntry, int paramInt) {
    if (paramInt < 0) {
      this.policyEntries.addElement(paramPolicyEntry);
      this.parser.add(paramPolicyEntry.getGrantEntry());
    } else {
      PolicyEntry policyEntry = (PolicyEntry)this.policyEntries.elementAt(paramInt);
      this.parser.replace(policyEntry.getGrantEntry(), paramPolicyEntry.getGrantEntry());
      this.policyEntries.setElementAt(paramPolicyEntry, paramInt);
    } 
    return true;
  }
  
  boolean addPrinEntry(PolicyEntry paramPolicyEntry, PolicyParser.PrincipalEntry paramPrincipalEntry, int paramInt) {
    PolicyParser.GrantEntry grantEntry = paramPolicyEntry.getGrantEntry();
    if (grantEntry.contains(paramPrincipalEntry) == true)
      return false; 
    LinkedList linkedList = grantEntry.principals;
    if (paramInt != -1) {
      linkedList.set(paramInt, paramPrincipalEntry);
    } else {
      linkedList.add(paramPrincipalEntry);
    } 
    this.modified = true;
    return true;
  }
  
  boolean addPermEntry(PolicyEntry paramPolicyEntry, PolicyParser.PermissionEntry paramPermissionEntry, int paramInt) {
    PolicyParser.GrantEntry grantEntry = paramPolicyEntry.getGrantEntry();
    if (grantEntry.contains(paramPermissionEntry) == true)
      return false; 
    Vector vector = grantEntry.permissionEntries;
    if (paramInt != -1) {
      vector.setElementAt(paramPermissionEntry, paramInt);
    } else {
      vector.addElement(paramPermissionEntry);
    } 
    this.modified = true;
    return true;
  }
  
  boolean removePermEntry(PolicyEntry paramPolicyEntry, PolicyParser.PermissionEntry paramPermissionEntry) {
    PolicyParser.GrantEntry grantEntry = paramPolicyEntry.getGrantEntry();
    this.modified = grantEntry.remove(paramPermissionEntry);
    return this.modified;
  }
  
  boolean removeEntry(PolicyEntry paramPolicyEntry) {
    this.parser.remove(paramPolicyEntry.getGrantEntry());
    this.modified = true;
    return this.policyEntries.removeElement(paramPolicyEntry);
  }
  
  PolicyEntry[] getEntry() {
    if (this.policyEntries.size() > 0) {
      PolicyEntry[] arrayOfPolicyEntry = new PolicyEntry[this.policyEntries.size()];
      for (byte b = 0; b < this.policyEntries.size(); b++)
        arrayOfPolicyEntry[b] = (PolicyEntry)this.policyEntries.elementAt(b); 
      return arrayOfPolicyEntry;
    } 
    return null;
  }
  
  PublicKey getPublicKeyAlias(String paramString) throws KeyStoreException {
    if (this.keyStore == null)
      return null; 
    Certificate certificate = this.keyStore.getCertificate(paramString);
    return (certificate == null) ? null : certificate.getPublicKey();
  }
  
  String[] getPublicKeyAlias() throws KeyStoreException {
    byte b = 0;
    String[] arrayOfString = null;
    if (this.keyStore == null)
      return null; 
    Enumeration enumeration = this.keyStore.aliases();
    while (enumeration.hasMoreElements()) {
      enumeration.nextElement();
      b++;
    } 
    if (b > 0) {
      arrayOfString = new String[b];
      b = 0;
      enumeration = this.keyStore.aliases();
      while (enumeration.hasMoreElements()) {
        arrayOfString[b] = new String((String)enumeration.nextElement());
        b++;
      } 
    } 
    return arrayOfString;
  }
  
  String[] parseSigners(String paramString) {
    String[] arrayOfString = null;
    byte b1 = 1;
    int i = 0;
    int j = 0;
    byte b2 = 0;
    while (j) {
      j = paramString.indexOf(',', i);
      if (j >= 0) {
        b1++;
        i = j + 1;
      } 
    } 
    arrayOfString = new String[b1];
    j = 0;
    i = 0;
    while (j >= 0) {
      if ((j = paramString.indexOf(',', i)) >= 0) {
        arrayOfString[b2] = paramString.substring(i, j).trim();
        b2++;
        i = j + 1;
        continue;
      } 
      arrayOfString[b2] = paramString.substring(i).trim();
    } 
    return arrayOfString;
  }
  
  void verifyPrincipal(String paramString1, String paramString2) throws ClassNotFoundException, InstantiationException {
    if (paramString1.equals("WILDCARD_PRINCIPAL_CLASS") || paramString1.equals("PolicyParser.REPLACE_NAME"))
      return; 
    Class clazz1;
    Class clazz2 = (clazz1 = Class.forName("java.security.Principal")).forName(paramString1, true, Thread.currentThread().getContextClassLoader());
    if (!clazz1.isAssignableFrom(clazz2)) {
      MessageFormat messageFormat = new MessageFormat(getMessage("Illegal.Principal.Type.type"));
      Object[] arrayOfObject = { paramString1 };
      throw new InstantiationException(messageFormat.format(arrayOfObject));
    } 
    if ("javax.security.auth.x500.X500Principal".equals(clazz2.getName()))
      X500Principal x500Principal = new X500Principal(paramString2); 
  }
  
  void verifyPermission(String paramString1, String paramString2, String paramString3) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
    Class clazz = Class.forName(paramString1, true, Thread.currentThread().getContextClassLoader());
    Constructor constructor = null;
    Vector vector = new Vector(2);
    if (paramString2 != null)
      vector.add(paramString2); 
    if (paramString3 != null)
      vector.add(paramString3); 
    switch (vector.size()) {
      case 0:
        try {
          constructor = clazz.getConstructor(NOPARAMS);
        } catch (NoSuchMethodException noSuchMethodException) {
          vector.add(null);
        } 
        break;
      case 1:
        try {
          constructor = clazz.getConstructor(ONEPARAMS);
        } catch (NoSuchMethodException noSuchMethodException) {
          vector.add(null);
        } 
        break;
      case 2:
        constructor = clazz.getConstructor(TWOPARAMS);
        break;
    } 
    Object[] arrayOfObject = vector.toArray();
    Permission permission = (Permission)constructor.newInstance(arrayOfObject);
  }
  
  static void parseArgs(String[] paramArrayOfString) {
    byte b = 0;
    for (b = 0; b < paramArrayOfString.length && paramArrayOfString[b].startsWith("-"); b++) {
      String str = paramArrayOfString[b];
      if (collator.compare(str, "-file") == 0) {
        if (++b == paramArrayOfString.length)
          usage(); 
        policyFileName = paramArrayOfString[b];
      } else {
        MessageFormat messageFormat = new MessageFormat(getMessage("Illegal.option.option"));
        Object[] arrayOfObject = { str };
        System.err.println(messageFormat.format(arrayOfObject));
        usage();
      } 
    } 
  }
  
  static void usage() {
    System.out.println(getMessage("Usage.policytool.options."));
    System.out.println();
    System.out.println(getMessage(".file.file.policy.file.location"));
    System.out.println();
    System.exit(1);
  }
  
  public static void main(final String[] args) {
    parseArgs(paramArrayOfString);
    SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            ToolWindow toolWindow = new ToolWindow(new PolicyTool(null));
            toolWindow.displayToolWindow(args);
          }
        });
  }
  
  static String splitToWords(String paramString) { return paramString.replaceAll("([A-Z])", " $1"); }
  
  static String getMessage(String paramString) { return removeMnemonicAmpersand(rb.getString(paramString)); }
  
  static int getMnemonicInt(String paramString) {
    String str = rb.getString(paramString);
    return findMnemonicInt(str);
  }
  
  static int getDisplayedMnemonicIndex(String paramString) {
    String str = rb.getString(paramString);
    return findMnemonicIndex(str);
  }
  
  private static int findMnemonicInt(String paramString) {
    for (byte b = 0; b < paramString.length() - 1; b++) {
      if (paramString.charAt(b) == '&') {
        if (paramString.charAt(b + 1) != '&')
          return KeyEvent.getExtendedKeyCodeForChar(paramString.charAt(b + 1)); 
        b++;
      } 
    } 
    return 0;
  }
  
  private static int findMnemonicIndex(String paramString) {
    for (byte b = 0; b < paramString.length() - 1; b++) {
      if (paramString.charAt(b) == '&') {
        if (paramString.charAt(b + 1) != '&')
          return b; 
        b++;
      } 
    } 
    return -1;
  }
  
  private static String removeMnemonicAmpersand(String paramString) {
    StringBuilder stringBuilder = new StringBuilder();
    for (byte b = 0; b < paramString.length(); b++) {
      char c = paramString.charAt(b);
      if (c != '&' || b == paramString.length() - 1 || paramString.charAt(b + 1) == '&')
        stringBuilder.append(c); 
    } 
    return stringBuilder.toString();
  }
  
  static  {
    collator.setStrength(0);
    if (System.getProperty("apple.laf.useScreenMenuBar") == null)
      System.setProperty("apple.laf.useScreenMenuBar", "true"); 
    System.setProperty("apple.awt.application.name", getMessage("Policy.Tool"));
    if (System.getProperty("swing.defaultlaf") == null)
      try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception exception) {} 
    TWOPARAMS = new Class[] { String.class, String.class };
    ONEPARAMS = new Class[] { String.class };
    NOPARAMS = new Class[0];
    policyFileName = null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\PolicyTool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */