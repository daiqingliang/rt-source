package sun.security.krb5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;
import sun.net.dns.ResolverConfiguration;
import sun.security.action.GetPropertyAction;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.crypto.EType;

public class Config {
  private static Config singleton = null;
  
  private Hashtable<String, Object> stanzaTable = new Hashtable();
  
  private static boolean DEBUG = Krb5.DEBUG;
  
  private static final int BASE16_0 = 1;
  
  private static final int BASE16_1 = 16;
  
  private static final int BASE16_2 = 256;
  
  private static final int BASE16_3 = 4096;
  
  private final String defaultRealm;
  
  private final String defaultKDC;
  
  private static native String getWindowsDirectory(boolean paramBoolean);
  
  public static Config getInstance() throws KrbException {
    if (singleton == null)
      singleton = new Config(); 
    return singleton;
  }
  
  public static void refresh() throws KrbException {
    singleton = new Config();
    KdcComm.initStatic();
    EType.initStatic();
    Checksum.initStatic();
  }
  
  private static boolean isMacosLionOrBetter() {
    String str1 = getProperty("os.name");
    if (!str1.contains("OS X"))
      return false; 
    String str2 = getProperty("os.version");
    String[] arrayOfString = str2.split("\\.");
    if (!arrayOfString[0].equals("10"))
      return false; 
    if (arrayOfString.length < 2)
      return false; 
    try {
      int i = Integer.parseInt(arrayOfString[1]);
      if (i >= 7)
        return true; 
    } catch (NumberFormatException numberFormatException) {}
    return false;
  }
  
  private Config() throws KrbException {
    String str = getProperty("java.security.krb5.kdc");
    if (str != null) {
      this.defaultKDC = str.replace(':', ' ');
    } else {
      this.defaultKDC = null;
    } 
    this.defaultRealm = getProperty("java.security.krb5.realm");
    if ((this.defaultKDC == null && this.defaultRealm != null) || (this.defaultRealm == null && this.defaultKDC != null))
      throw new KrbException("System property java.security.krb5.kdc and java.security.krb5.realm both must be set or neither must be set."); 
    try {
      String str1 = getJavaFileName();
      if (str1 != null) {
        List list = loadConfigFile(str1);
        this.stanzaTable = parseStanzaTable(list);
        if (DEBUG)
          System.out.println("Loaded from Java config"); 
      } else {
        boolean bool = false;
        if (isMacosLionOrBetter())
          try {
            this.stanzaTable = SCDynamicStoreConfig.getConfig();
            if (DEBUG)
              System.out.println("Loaded from SCDynamicStoreConfig"); 
            bool = true;
          } catch (IOException iOException) {} 
        if (!bool) {
          str1 = getNativeFileName();
          List list = loadConfigFile(str1);
          this.stanzaTable = parseStanzaTable(list);
          if (DEBUG)
            System.out.println("Loaded from native config"); 
        } 
      } 
    } catch (IOException iOException) {}
  }
  
  public String get(String... paramVarArgs) {
    Vector vector = getString0(paramVarArgs);
    return (vector == null) ? null : (String)vector.lastElement();
  }
  
  private Boolean getBooleanObject(String... paramVarArgs) {
    String str = get(paramVarArgs);
    if (str == null)
      return null; 
    switch (str.toLowerCase(Locale.US)) {
      case "yes":
      case "true":
        return Boolean.TRUE;
      case "no":
      case "false":
        return Boolean.FALSE;
    } 
    return null;
  }
  
  public String getAll(String... paramVarArgs) {
    Vector vector = getString0(paramVarArgs);
    if (vector == null)
      return null; 
    StringBuilder stringBuilder = new StringBuilder();
    boolean bool = true;
    for (String str : vector) {
      if (bool) {
        stringBuilder.append(str);
        bool = false;
        continue;
      } 
      stringBuilder.append(' ').append(str);
    } 
    return stringBuilder.toString();
  }
  
  public boolean exists(String... paramVarArgs) { return (get0(paramVarArgs) != null); }
  
  private Vector<String> getString0(String... paramVarArgs) {
    try {
      return (Vector)get0(paramVarArgs);
    } catch (ClassCastException classCastException) {
      throw new IllegalArgumentException(classCastException);
    } 
  }
  
  private Object get0(String... paramVarArgs) {
    Hashtable hashtable = this.stanzaTable;
    try {
      Object object;
      for (String str : paramVarArgs) {
        object = ((Hashtable)hashtable).get(str);
        if (object == null)
          return null; 
      } 
      return object;
    } catch (ClassCastException classCastException) {
      throw new IllegalArgumentException(classCastException);
    } 
  }
  
  public int getIntValue(String... paramVarArgs) {
    String str = get(paramVarArgs);
    int i = Integer.MIN_VALUE;
    if (str != null)
      try {
        i = parseIntValue(str);
      } catch (NumberFormatException numberFormatException) {
        if (DEBUG) {
          System.out.println("Exception in getting value of " + Arrays.toString(paramVarArgs) + " " + numberFormatException.getMessage());
          System.out.println("Setting " + Arrays.toString(paramVarArgs) + " to minimum value");
        } 
        i = Integer.MIN_VALUE;
      }  
    return i;
  }
  
  public boolean getBooleanValue(String... paramVarArgs) {
    String str = get(paramVarArgs);
    return (str != null && str.equalsIgnoreCase("true"));
  }
  
  private int parseIntValue(String paramString) throws NumberFormatException {
    int i = 0;
    if (paramString.startsWith("+")) {
      String str = paramString.substring(1);
      return Integer.parseInt(str);
    } 
    if (paramString.startsWith("0x")) {
      String str = paramString.substring(2);
      char[] arrayOfChar = str.toCharArray();
      if (arrayOfChar.length > 8)
        throw new NumberFormatException(); 
      for (int j = 0; j < arrayOfChar.length; j++) {
        int k = arrayOfChar.length - j - 1;
        switch (arrayOfChar[j]) {
          case '0':
            i += false;
            break;
          case '1':
            i += 1 * getBase(k);
            break;
          case '2':
            i += 2 * getBase(k);
            break;
          case '3':
            i += 3 * getBase(k);
            break;
          case '4':
            i += 4 * getBase(k);
            break;
          case '5':
            i += 5 * getBase(k);
            break;
          case '6':
            i += 6 * getBase(k);
            break;
          case '7':
            i += 7 * getBase(k);
            break;
          case '8':
            i += 8 * getBase(k);
            break;
          case '9':
            i += 9 * getBase(k);
            break;
          case 'A':
          case 'a':
            i += 10 * getBase(k);
            break;
          case 'B':
          case 'b':
            i += 11 * getBase(k);
            break;
          case 'C':
          case 'c':
            i += 12 * getBase(k);
            break;
          case 'D':
          case 'd':
            i += 13 * getBase(k);
            break;
          case 'E':
          case 'e':
            i += 14 * getBase(k);
            break;
          case 'F':
          case 'f':
            i += 15 * getBase(k);
            break;
          default:
            throw new NumberFormatException("Invalid numerical format");
        } 
      } 
      if (i < 0)
        throw new NumberFormatException("Data overflow."); 
    } else {
      i = Integer.parseInt(paramString);
    } 
    return i;
  }
  
  private int getBase(int paramInt) { // Byte code:
    //   0: bipush #16
    //   2: istore_2
    //   3: iload_1
    //   4: tableswitch default -> 61, 0 -> 36, 1 -> 41, 2 -> 47, 3 -> 54
    //   36: iconst_1
    //   37: istore_2
    //   38: goto -> 79
    //   41: bipush #16
    //   43: istore_2
    //   44: goto -> 79
    //   47: sipush #256
    //   50: istore_2
    //   51: goto -> 79
    //   54: sipush #4096
    //   57: istore_2
    //   58: goto -> 79
    //   61: iconst_1
    //   62: istore_3
    //   63: iload_3
    //   64: iload_1
    //   65: if_icmpge -> 79
    //   68: iload_2
    //   69: bipush #16
    //   71: imul
    //   72: istore_2
    //   73: iinc #3, 1
    //   76: goto -> 63
    //   79: iload_2
    //   80: ireturn }
  
  private List<String> loadConfigFile(String paramString) throws IOException, KrbException {
    try {
      ArrayList arrayList = new ArrayList();
      try (BufferedReader null = new BufferedReader(new InputStreamReader((InputStream)AccessController.doPrivileged(new PrivilegedExceptionAction<FileInputStream>(this, paramString) {
                  public FileInputStream run() throws IOException { return new FileInputStream(fileName); }
                })))) {
        String str2 = null;
        while ((str1 = bufferedReader.readLine()) != null) {
          str1 = str1.trim();
          if (str1.isEmpty() || str1.startsWith("#") || str1.startsWith(";"))
            continue; 
          if (str1.startsWith("[")) {
            if (!str1.endsWith("]"))
              throw new KrbException("Illegal config content:" + str1); 
            if (str2 != null) {
              arrayList.add(str2);
              arrayList.add("}");
            } 
            String str = str1.substring(1, str1.length() - 1).trim();
            if (str.isEmpty())
              throw new KrbException("Illegal config content:" + str1); 
            str2 = str + " = {";
            continue;
          } 
          if (str1.startsWith("{")) {
            if (str2 == null)
              throw new KrbException("Config file should not start with \"{\""); 
            str2 = str2 + " {";
            if (str1.length() > 1) {
              arrayList.add(str2);
              str2 = str1.substring(1).trim();
            } 
            continue;
          } 
          if (str2 != null) {
            arrayList.add(str2);
            str2 = str1;
          } 
        } 
        if (str2 != null) {
          arrayList.add(str2);
          arrayList.add("}");
        } 
      } 
      return arrayList;
    } catch (PrivilegedActionException privilegedActionException) {
      throw (IOException)privilegedActionException.getException();
    } 
  }
  
  private Hashtable<String, Object> parseStanzaTable(List<String> paramList) throws KrbException {
    Hashtable hashtable = this.stanzaTable;
    for (String str1 : paramList) {
      Vector vector;
      if (str1.equals("}")) {
        hashtable = (Hashtable)hashtable.remove(" PARENT ");
        if (hashtable == null)
          throw new KrbException("Unmatched close brace"); 
        continue;
      } 
      int i = str1.indexOf('=');
      if (i < 0)
        throw new KrbException("Illegal config content:" + str1); 
      String str2 = str1.substring(0, i).trim();
      String str3 = trimmed(str1.substring(i + 1));
      if (str3.equals("{")) {
        if (hashtable == this.stanzaTable)
          str2 = str2.toLowerCase(Locale.US); 
        vector = new Hashtable();
        hashtable.put(str2, vector);
        vector.put(" PARENT ", hashtable);
        hashtable = vector;
        continue;
      } 
      if (hashtable.containsKey(str2)) {
        Object object = hashtable.get(str2);
        if (!(object instanceof Vector))
          throw new KrbException("Key " + str2 + "used for both value and section"); 
        vector = (Vector)hashtable.get(str2);
      } else {
        vector = new Vector();
        hashtable.put(str2, vector);
      } 
      vector.add(str3);
    } 
    if (hashtable != this.stanzaTable)
      throw new KrbException("Not closed"); 
    return hashtable;
  }
  
  private String getJavaFileName() {
    String str = getProperty("java.security.krb5.conf");
    if (str == null) {
      str = getProperty("java.home") + File.separator + "lib" + File.separator + "security" + File.separator + "krb5.conf";
      if (!fileExists(str))
        str = null; 
    } 
    if (DEBUG)
      System.out.println("Java config name: " + str); 
    return str;
  }
  
  private String getNativeFileName() {
    String str1 = null;
    String str2 = getProperty("os.name");
    if (str2.startsWith("Windows")) {
      try {
        Credentials.ensureLoaded();
      } catch (Exception exception) {}
      if (Credentials.alreadyLoaded) {
        String str = getWindowsDirectory(false);
        if (str != null) {
          if (str.endsWith("\\")) {
            str = str + "krb5.ini";
          } else {
            str = str + "\\krb5.ini";
          } 
          if (fileExists(str))
            str1 = str; 
        } 
        if (str1 == null) {
          str = getWindowsDirectory(true);
          if (str != null) {
            if (str.endsWith("\\")) {
              str = str + "krb5.ini";
            } else {
              str = str + "\\krb5.ini";
            } 
            str1 = str;
          } 
        } 
      } 
      if (str1 == null)
        str1 = "c:\\winnt\\krb5.ini"; 
    } else if (str2.startsWith("SunOS")) {
      str1 = "/etc/krb5/krb5.conf";
    } else if (str2.contains("OS X")) {
      str1 = findMacosConfigFile();
    } else {
      str1 = "/etc/krb5.conf";
    } 
    if (DEBUG)
      System.out.println("Native config name: " + str1); 
    return str1;
  }
  
  private static String getProperty(String paramString) { return (String)AccessController.doPrivileged(new GetPropertyAction(paramString)); }
  
  private String findMacosConfigFile() {
    String str1 = getProperty("user.home");
    String str2 = str1 + "/Library/Preferences/edu.mit.Kerberos";
    return fileExists(str2) ? str2 : (fileExists("/Library/Preferences/edu.mit.Kerberos") ? "/Library/Preferences/edu.mit.Kerberos" : "/etc/krb5.conf");
  }
  
  private static String trimmed(String paramString) {
    paramString = paramString.trim();
    if (paramString.length() >= 2 && ((paramString.charAt(0) == '"' && paramString.charAt(paramString.length() - 1) == '"') || (paramString.charAt(0) == '\'' && paramString.charAt(paramString.length() - 1) == '\'')))
      paramString = paramString.substring(1, paramString.length() - 1).trim(); 
    return paramString;
  }
  
  public void listTable() throws KrbException { System.out.println(this); }
  
  public int[] defaultEtype(String paramString) throws KrbException {
    int[] arrayOfInt;
    String str = get(new String[] { "libdefaults", paramString });
    if (str == null) {
      if (DEBUG)
        System.out.println("Using builtin default etypes for " + paramString); 
      arrayOfInt = EType.getBuiltInDefaults();
    } else {
      String str1 = " ";
      int i;
      for (i = 0; i < str.length(); i++) {
        if (str.substring(i, i + true).equals(",")) {
          str1 = ",";
          break;
        } 
      } 
      StringTokenizer stringTokenizer = new StringTokenizer(str, str1);
      i = stringTokenizer.countTokens();
      ArrayList arrayList = new ArrayList(i);
      byte b;
      for (b = 0; b < i; b++) {
        int j = getType(stringTokenizer.nextToken());
        if (j != -1 && EType.isSupported(j))
          arrayList.add(Integer.valueOf(j)); 
      } 
      if (arrayList.isEmpty())
        throw new KrbException("no supported default etypes for " + paramString); 
      arrayOfInt = new int[arrayList.size()];
      for (b = 0; b < arrayOfInt.length; b++)
        arrayOfInt[b] = ((Integer)arrayList.get(b)).intValue(); 
    } 
    if (DEBUG) {
      System.out.print("default etypes for " + paramString + ":");
      for (byte b = 0; b < arrayOfInt.length; b++)
        System.out.print(" " + arrayOfInt[b]); 
      System.out.println(".");
    } 
    return arrayOfInt;
  }
  
  public static int getType(String paramString) throws NumberFormatException {
    short s = -1;
    if (paramString == null)
      return s; 
    if (paramString.startsWith("d") || paramString.startsWith("D")) {
      if (paramString.equalsIgnoreCase("des-cbc-crc")) {
        s = 1;
      } else if (paramString.equalsIgnoreCase("des-cbc-md5")) {
        s = 3;
      } else if (paramString.equalsIgnoreCase("des-mac")) {
        s = 4;
      } else if (paramString.equalsIgnoreCase("des-mac-k")) {
        s = 5;
      } else if (paramString.equalsIgnoreCase("des-cbc-md4")) {
        s = 2;
      } else if (paramString.equalsIgnoreCase("des3-cbc-sha1") || paramString.equalsIgnoreCase("des3-hmac-sha1") || paramString.equalsIgnoreCase("des3-cbc-sha1-kd") || paramString.equalsIgnoreCase("des3-cbc-hmac-sha1-kd")) {
        s = 16;
      } 
    } else if (paramString.startsWith("a") || paramString.startsWith("A")) {
      if (paramString.equalsIgnoreCase("aes128-cts") || paramString.equalsIgnoreCase("aes128-cts-hmac-sha1-96")) {
        s = 17;
      } else if (paramString.equalsIgnoreCase("aes256-cts") || paramString.equalsIgnoreCase("aes256-cts-hmac-sha1-96")) {
        s = 18;
      } else if (paramString.equalsIgnoreCase("arcfour-hmac") || paramString.equalsIgnoreCase("arcfour-hmac-md5")) {
        s = 23;
      } 
    } else if (paramString.equalsIgnoreCase("rc4-hmac")) {
      s = 23;
    } else if (paramString.equalsIgnoreCase("CRC32")) {
      s = 1;
    } else if (paramString.startsWith("r") || paramString.startsWith("R")) {
      if (paramString.equalsIgnoreCase("rsa-md5")) {
        s = 7;
      } else if (paramString.equalsIgnoreCase("rsa-md5-des")) {
        s = 8;
      } 
    } else if (paramString.equalsIgnoreCase("hmac-sha1-des3-kd")) {
      s = 12;
    } else if (paramString.equalsIgnoreCase("hmac-sha1-96-aes128")) {
      s = 15;
    } else if (paramString.equalsIgnoreCase("hmac-sha1-96-aes256")) {
      s = 16;
    } else if (paramString.equalsIgnoreCase("hmac-md5-rc4") || paramString.equalsIgnoreCase("hmac-md5-arcfour") || paramString.equalsIgnoreCase("hmac-md5-enc")) {
      s = -138;
    } else if (paramString.equalsIgnoreCase("NULL")) {
      s = 0;
    } 
    return s;
  }
  
  public void resetDefaultRealm(String paramString) {
    if (DEBUG)
      System.out.println(">>> Config try resetting default kdc " + paramString); 
  }
  
  public boolean useAddresses() {
    boolean bool = false;
    String str = get(new String[] { "libdefaults", "no_addresses" });
    bool = (str != null && str.equalsIgnoreCase("false"));
    if (!bool) {
      str = get(new String[] { "libdefaults", "noaddresses" });
      bool = (str != null && str.equalsIgnoreCase("false"));
    } 
    return bool;
  }
  
  private boolean useDNS(String paramString, boolean paramBoolean) {
    Boolean bool = getBooleanObject(new String[] { "libdefaults", paramString });
    if (bool != null)
      return bool.booleanValue(); 
    bool = getBooleanObject(new String[] { "libdefaults", "dns_fallback" });
    return (bool != null) ? bool.booleanValue() : paramBoolean;
  }
  
  private boolean useDNS_KDC() { return useDNS("dns_lookup_kdc", true); }
  
  private boolean useDNS_Realm() { return useDNS("dns_lookup_realm", false); }
  
  public String getDefaultRealm() {
    if (this.defaultRealm != null)
      return this.defaultRealm; 
    KrbException krbException = null;
    String str = get(new String[] { "libdefaults", "default_realm" });
    if (str == null && useDNS_Realm())
      try {
        str = getRealmFromDNS();
      } catch (KrbException krbException1) {
        krbException = krbException1;
      }  
    if (str == null)
      str = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() {
              String str = System.getProperty("os.name");
              return str.startsWith("Windows") ? System.getenv("USERDNSDOMAIN") : null;
            }
          }); 
    if (str == null) {
      KrbException krbException1 = new KrbException("Cannot locate default realm");
      if (krbException != null)
        krbException1.initCause(krbException); 
      throw krbException1;
    } 
    return str;
  }
  
  public String getKDCList(String paramString) {
    if (paramString == null)
      paramString = getDefaultRealm(); 
    if (paramString.equalsIgnoreCase(this.defaultRealm))
      return this.defaultKDC; 
    KrbException krbException = null;
    String str = getAll(new String[] { "realms", paramString, "kdc" });
    if (str == null && useDNS_KDC())
      try {
        str = getKDCFromDNS(paramString);
      } catch (KrbException krbException1) {
        krbException = krbException1;
      }  
    if (str == null)
      str = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() {
              String str = System.getProperty("os.name");
              if (str.startsWith("Windows")) {
                String str1 = System.getenv("LOGONSERVER");
                if (str1 != null && str1.startsWith("\\\\"))
                  str1 = str1.substring(2); 
                return str1;
              } 
              return null;
            }
          }); 
    if (str == null) {
      if (this.defaultKDC != null)
        return this.defaultKDC; 
      KrbException krbException1 = new KrbException("Cannot locate KDC");
      if (krbException != null)
        krbException1.initCause(krbException); 
      throw krbException1;
    } 
    return str;
  }
  
  private String getRealmFromDNS() {
    String str1 = null;
    String str2 = null;
    try {
      str2 = InetAddress.getLocalHost().getCanonicalHostName();
    } catch (UnknownHostException unknownHostException) {
      KrbException krbException = new KrbException(60, "Unable to locate Kerberos realm: " + unknownHostException.getMessage());
      krbException.initCause(unknownHostException);
      throw krbException;
    } 
    String str3 = PrincipalName.mapHostToRealm(str2);
    if (str3 == null) {
      List list = ResolverConfiguration.open().searchlist();
      for (String str : list) {
        str1 = checkRealm(str);
        if (str1 != null)
          break; 
      } 
    } else {
      str1 = checkRealm(str3);
    } 
    if (str1 == null)
      throw new KrbException(60, "Unable to locate Kerberos realm"); 
    return str1;
  }
  
  private static String checkRealm(String paramString) {
    if (DEBUG)
      System.out.println("getRealmFromDNS: trying " + paramString); 
    String[] arrayOfString = null;
    for (String str = paramString; arrayOfString == null && str != null; str = Realm.parseRealmComponent(str))
      arrayOfString = KrbServiceLocator.getKerberosService(str); 
    if (arrayOfString != null)
      for (byte b = 0; b < arrayOfString.length; b++) {
        if (arrayOfString[b].equalsIgnoreCase(paramString))
          return arrayOfString[b]; 
      }  
    return null;
  }
  
  private String getKDCFromDNS(String paramString) {
    String str = "";
    String[] arrayOfString = null;
    if (DEBUG)
      System.out.println("getKDCFromDNS using UDP"); 
    arrayOfString = KrbServiceLocator.getKerberosService(paramString, "_udp");
    if (arrayOfString == null) {
      if (DEBUG)
        System.out.println("getKDCFromDNS using TCP"); 
      arrayOfString = KrbServiceLocator.getKerberosService(paramString, "_tcp");
    } 
    if (arrayOfString == null)
      throw new KrbException(60, "Unable to locate KDC for realm " + paramString); 
    if (arrayOfString.length == 0)
      return null; 
    for (byte b = 0; b < arrayOfString.length; b++)
      str = str + arrayOfString[b].trim() + " "; 
    str = str.trim();
    return str.equals("") ? null : str;
  }
  
  private boolean fileExists(String paramString) { return ((Boolean)AccessController.doPrivileged(new FileExistsAction(paramString))).booleanValue(); }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    toStringInternal("", this.stanzaTable, stringBuffer);
    return stringBuffer.toString();
  }
  
  private static void toStringInternal(String paramString, Object paramObject, StringBuffer paramStringBuffer) {
    if (paramObject instanceof String) {
      paramStringBuffer.append(paramObject).append('\n');
    } else if (paramObject instanceof Hashtable) {
      Hashtable hashtable = (Hashtable)paramObject;
      paramStringBuffer.append("{\n");
      for (Object object : hashtable.keySet()) {
        paramStringBuffer.append(paramString).append("    ").append(object).append(" = ");
        toStringInternal(paramString + "    ", hashtable.get(object), paramStringBuffer);
      } 
      paramStringBuffer.append(paramString).append("}\n");
    } else if (paramObject instanceof Vector) {
      Vector vector = (Vector)paramObject;
      paramStringBuffer.append("[");
      boolean bool = true;
      for (Object object : vector.toArray()) {
        if (!bool)
          paramStringBuffer.append(","); 
        paramStringBuffer.append(object);
        bool = false;
      } 
      paramStringBuffer.append("]\n");
    } 
  }
  
  static class FileExistsAction extends Object implements PrivilegedAction<Boolean> {
    private String fileName;
    
    public FileExistsAction(String param1String) { this.fileName = param1String; }
    
    public Boolean run() { return Boolean.valueOf((new File(this.fileName)).exists()); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\Config.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */