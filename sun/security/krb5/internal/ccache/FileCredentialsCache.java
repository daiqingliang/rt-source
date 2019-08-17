package sun.security.krb5.internal.ccache;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.StringTokenizer;
import java.util.Vector;
import sun.security.action.GetPropertyAction;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.LoginOptions;

public class FileCredentialsCache extends CredentialsCache implements FileCCacheConstants {
  public int version;
  
  public Tag tag;
  
  public PrincipalName primaryPrincipal;
  
  private Vector<Credentials> credentialsList;
  
  private static String dir;
  
  private static boolean DEBUG = Krb5.DEBUG;
  
  public static FileCredentialsCache acquireInstance(PrincipalName paramPrincipalName, String paramString) {
    try {
      FileCredentialsCache fileCredentialsCache = new FileCredentialsCache();
      if (paramString == null) {
        cacheName = getDefaultCacheName();
      } else {
        cacheName = checkValidation(paramString);
      } 
      if (cacheName == null || !(new File(cacheName)).exists())
        return null; 
      if (paramPrincipalName != null)
        fileCredentialsCache.primaryPrincipal = paramPrincipalName; 
      fileCredentialsCache.load(cacheName);
      return fileCredentialsCache;
    } catch (IOException iOException) {
      if (DEBUG)
        iOException.printStackTrace(); 
    } catch (KrbException krbException) {
      if (DEBUG)
        krbException.printStackTrace(); 
    } 
    return null;
  }
  
  public static FileCredentialsCache acquireInstance() { return acquireInstance(null, null); }
  
  static FileCredentialsCache New(PrincipalName paramPrincipalName, String paramString) {
    try {
      FileCredentialsCache fileCredentialsCache;
      cacheName = (fileCredentialsCache = new FileCredentialsCache()).checkValidation(paramString);
      if (cacheName == null)
        return null; 
      fileCredentialsCache.init(paramPrincipalName, cacheName);
      return fileCredentialsCache;
    } catch (IOException iOException) {
    
    } catch (KrbException krbException) {}
    return null;
  }
  
  static FileCredentialsCache New(PrincipalName paramPrincipalName) {
    try {
      FileCredentialsCache fileCredentialsCache;
      cacheName = (fileCredentialsCache = new FileCredentialsCache()).getDefaultCacheName();
      fileCredentialsCache.init(paramPrincipalName, cacheName);
      return fileCredentialsCache;
    } catch (IOException iOException) {
      if (DEBUG)
        iOException.printStackTrace(); 
    } catch (KrbException krbException) {
      if (DEBUG)
        krbException.printStackTrace(); 
    } 
    return null;
  }
  
  boolean exists(String paramString) {
    File file = new File(paramString);
    return file.exists();
  }
  
  void init(PrincipalName paramPrincipalName, String paramString) throws IOException, KrbException {
    this.primaryPrincipal = paramPrincipalName;
    try(FileOutputStream null = new FileOutputStream(paramString); CCacheOutputStream null = new CCacheOutputStream(fileOutputStream)) {
      this.version = 1283;
      cCacheOutputStream.writeHeader(this.primaryPrincipal, this.version);
    } 
    load(paramString);
  }
  
  void load(String paramString) throws IOException, KrbException {
    try(FileInputStream null = new FileInputStream(paramString); CCacheInputStream null = new CCacheInputStream(fileInputStream)) {
      this.version = cCacheInputStream.readVersion();
      if (this.version == 1284) {
        this.tag = cCacheInputStream.readTag();
      } else {
        this.tag = null;
        if (this.version == 1281 || this.version == 1282)
          cCacheInputStream.setNativeByteOrder(); 
      } 
      PrincipalName principalName = cCacheInputStream.readPrincipal(this.version);
      if (this.primaryPrincipal != null) {
        if (!this.primaryPrincipal.match(principalName))
          throw new IOException("Primary principals don't match."); 
      } else {
        this.primaryPrincipal = principalName;
      } 
      this.credentialsList = new Vector();
      while (cCacheInputStream.available() > 0) {
        Credentials credentials = cCacheInputStream.readCred(this.version);
        if (credentials != null)
          this.credentialsList.addElement(credentials); 
      } 
    } 
  }
  
  public void update(Credentials paramCredentials) {
    if (this.credentialsList != null)
      if (this.credentialsList.isEmpty()) {
        this.credentialsList.addElement(paramCredentials);
      } else {
        Credentials credentials = null;
        boolean bool = false;
        for (byte b = 0; b < this.credentialsList.size(); b++) {
          credentials = (Credentials)this.credentialsList.elementAt(b);
          if (match(paramCredentials.sname.getNameStrings(), credentials.sname.getNameStrings()) && paramCredentials.sname.getRealmString().equalsIgnoreCase(credentials.sname.getRealmString())) {
            bool = true;
            if (paramCredentials.endtime.getTime() >= credentials.endtime.getTime()) {
              if (DEBUG)
                System.out.println(" >>> FileCredentialsCache Ticket matched, overwrite the old one."); 
              this.credentialsList.removeElementAt(b);
              this.credentialsList.addElement(paramCredentials);
            } 
          } 
        } 
        if (!bool) {
          if (DEBUG)
            System.out.println(" >>> FileCredentialsCache Ticket not exactly matched, add new one into cache."); 
          this.credentialsList.addElement(paramCredentials);
        } 
      }  
  }
  
  public PrincipalName getPrimaryPrincipal() { return this.primaryPrincipal; }
  
  public void save() {
    try(FileOutputStream null = new FileOutputStream(cacheName); CCacheOutputStream null = new CCacheOutputStream(fileOutputStream)) {
      cCacheOutputStream.writeHeader(this.primaryPrincipal, this.version);
      arrayOfCredentials = null;
      if ((arrayOfCredentials = getCredsList()) != null)
        for (byte b = 0; b < arrayOfCredentials.length; b++)
          cCacheOutputStream.addCreds(arrayOfCredentials[b]);  
    } 
  }
  
  boolean match(String[] paramArrayOfString1, String[] paramArrayOfString2) {
    if (paramArrayOfString1.length != paramArrayOfString2.length)
      return false; 
    for (byte b = 0; b < paramArrayOfString1.length; b++) {
      if (!paramArrayOfString1[b].equalsIgnoreCase(paramArrayOfString2[b]))
        return false; 
    } 
    return true;
  }
  
  public Credentials[] getCredsList() {
    if (this.credentialsList == null || this.credentialsList.isEmpty())
      return null; 
    Credentials[] arrayOfCredentials = new Credentials[this.credentialsList.size()];
    for (byte b = 0; b < this.credentialsList.size(); b++)
      arrayOfCredentials[b] = (Credentials)this.credentialsList.elementAt(b); 
    return arrayOfCredentials;
  }
  
  public Credentials getCreds(LoginOptions paramLoginOptions, PrincipalName paramPrincipalName) {
    if (paramLoginOptions == null)
      return getCreds(paramPrincipalName); 
    Credentials[] arrayOfCredentials = getCredsList();
    if (arrayOfCredentials == null)
      return null; 
    for (byte b = 0; b < arrayOfCredentials.length; b++) {
      if (paramPrincipalName.match((arrayOfCredentials[b]).sname) && (arrayOfCredentials[b]).flags.match(paramLoginOptions))
        return arrayOfCredentials[b]; 
    } 
    return null;
  }
  
  public Credentials getCreds(PrincipalName paramPrincipalName) {
    Credentials[] arrayOfCredentials = getCredsList();
    if (arrayOfCredentials == null)
      return null; 
    for (byte b = 0; b < arrayOfCredentials.length; b++) {
      if (paramPrincipalName.match((arrayOfCredentials[b]).sname))
        return arrayOfCredentials[b]; 
    } 
    return null;
  }
  
  public Credentials getDefaultCreds() {
    Credentials[] arrayOfCredentials = getCredsList();
    if (arrayOfCredentials == null)
      return null; 
    for (int i = arrayOfCredentials.length - 1; i >= 0; i--) {
      if ((arrayOfCredentials[i]).sname.toString().startsWith("krbtgt")) {
        String[] arrayOfString = (arrayOfCredentials[i]).sname.getNameStrings();
        if (arrayOfString[1].equals((arrayOfCredentials[i]).sname.getRealm().toString()))
          return arrayOfCredentials[i]; 
      } 
    } 
    return null;
  }
  
  public static String getDefaultCacheName() {
    String str1 = "krb5cc";
    String str2 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
          public String run() {
            String str = System.getenv("KRB5CCNAME");
            if (str != null && str.length() >= 5 && str.regionMatches(true, 0, "FILE:", 0, 5))
              str = str.substring(5); 
            return str;
          }
        });
    if (str2 != null) {
      if (DEBUG)
        System.out.println(">>>KinitOptions cache name is " + str2); 
      return str2;
    } 
    String str3 = (String)AccessController.doPrivileged(new GetPropertyAction("os.name"));
    if (str3 != null) {
      Object object1 = null;
      Object object2 = null;
      long l = 0L;
      if (!str3.startsWith("Windows"))
        try {
          Class clazz = Class.forName("com.sun.security.auth.module.UnixSystem");
          Constructor constructor = clazz.getConstructor(new Class[0]);
          Object object = constructor.newInstance(new Object[0]);
          Method method = clazz.getMethod("getUid", new Class[0]);
          l = ((Long)method.invoke(object, new Object[0])).longValue();
          str2 = File.separator + "tmp" + File.separator + str1 + "_" + l;
          if (DEBUG)
            System.out.println(">>>KinitOptions cache name is " + str2); 
          return str2;
        } catch (Exception exception) {
          if (DEBUG) {
            System.out.println("Exception in obtaining uid for Unix platforms Using user's home directory");
            exception.printStackTrace();
          } 
        }  
    } 
    String str4 = (String)AccessController.doPrivileged(new GetPropertyAction("user.name"));
    String str5 = (String)AccessController.doPrivileged(new GetPropertyAction("user.home"));
    if (str5 == null)
      str5 = (String)AccessController.doPrivileged(new GetPropertyAction("user.dir")); 
    if (str4 != null) {
      str2 = str5 + File.separator + str1 + "_" + str4;
    } else {
      str2 = str5 + File.separator + str1;
    } 
    if (DEBUG)
      System.out.println(">>>KinitOptions cache name is " + str2); 
    return str2;
  }
  
  public static String checkValidation(String paramString) {
    String str = null;
    if (paramString == null)
      return null; 
    try {
      str = (new File(paramString)).getCanonicalPath();
      File file = new File(str);
      if (!file.exists()) {
        File file1 = new File(file.getParent());
        if (!file1.isDirectory())
          str = null; 
        file1 = null;
      } 
      file = null;
    } catch (IOException iOException) {
      str = null;
    } 
    return str;
  }
  
  private static String exec(String paramString) {
    StringTokenizer stringTokenizer = new StringTokenizer(paramString);
    Vector vector = new Vector();
    while (stringTokenizer.hasMoreTokens())
      vector.addElement(stringTokenizer.nextToken()); 
    final String[] command = new String[vector.size()];
    vector.copyInto(arrayOfString);
    try {
      Process process = (Process)AccessController.doPrivileged(new PrivilegedAction<Process>() {
            public Process run() {
              try {
                return Runtime.getRuntime().exec(command);
              } catch (IOException iOException) {
                if (DEBUG)
                  iOException.printStackTrace(); 
                return null;
              } 
            }
          });
      if (process == null)
        return null; 
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "8859_1"));
      String str = null;
      if (arrayOfString.length == 1 && arrayOfString[0].equals("/usr/bin/env")) {
        while ((str = bufferedReader.readLine()) != null) {
          if (str.length() >= 11 && str.substring(0, 11).equalsIgnoreCase("KRB5CCNAME=")) {
            str = str.substring(11);
            break;
          } 
        } 
      } else {
        str = bufferedReader.readLine();
      } 
      bufferedReader.close();
      return str;
    } catch (Exception exception) {
      if (DEBUG)
        exception.printStackTrace(); 
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\ccache\FileCredentialsCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */