package java.security;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import sun.security.jca.GetInstance;
import sun.security.jca.ProviderList;
import sun.security.jca.Providers;
import sun.security.util.Debug;
import sun.security.util.PropertyExpander;

public final class Security {
  private static final Debug sdebug = Debug.getInstance("properties");
  
  private static Properties props;
  
  private static final Map<String, Class<?>> spiMap;
  
  private static void initialize() {
    props = new Properties();
    boolean bool1 = false;
    boolean bool2 = false;
    File file = securityPropFile("java.security");
    if (file.exists()) {
      bufferedInputStream = null;
      try {
        fileInputStream = new FileInputStream(file);
        bufferedInputStream = new BufferedInputStream(fileInputStream);
        props.load(bufferedInputStream);
        bool1 = true;
        if (sdebug != null)
          sdebug.println("reading security properties file: " + file); 
      } catch (IOException iOException) {
        if (sdebug != null) {
          sdebug.println("unable to load security properties from " + file);
          iOException.printStackTrace();
        } 
      } finally {
        if (bufferedInputStream != null)
          try {
            bufferedInputStream.close();
          } catch (IOException iOException) {
            if (sdebug != null)
              sdebug.println("unable to close input stream"); 
          }  
      } 
    } 
    if ("true".equalsIgnoreCase(props.getProperty("security.overridePropertiesFile"))) {
      String str = System.getProperty("java.security.properties");
      if (str != null && str.startsWith("=")) {
        bool2 = true;
        str = str.substring(1);
      } 
      if (bool2) {
        props = new Properties();
        if (sdebug != null)
          sdebug.println("overriding other security properties files!"); 
      } 
      if (str != null) {
        bufferedInputStream = null;
        try {
          str = PropertyExpander.expand(str);
          file = new File(str);
          if (file.exists()) {
            uRL = new URL("file:" + file.getCanonicalPath());
          } else {
            uRL = new URL(str);
          } 
          bufferedInputStream = new BufferedInputStream(uRL.openStream());
          props.load(bufferedInputStream);
          bool1 = true;
          if (sdebug != null) {
            sdebug.println("reading security properties file: " + uRL);
            if (bool2)
              sdebug.println("overriding other security properties files!"); 
          } 
        } catch (Exception exception) {
          if (sdebug != null) {
            sdebug.println("unable to load security properties from " + str);
            exception.printStackTrace();
          } 
        } finally {
          if (bufferedInputStream != null)
            try {
              bufferedInputStream.close();
            } catch (IOException iOException) {
              if (sdebug != null)
                sdebug.println("unable to close input stream"); 
            }  
        } 
      } 
    } 
    if (!bool1) {
      initializeStatic();
      if (sdebug != null)
        sdebug.println("unable to load security properties -- using defaults"); 
    } 
  }
  
  private static void initializeStatic() {
    props.put("security.provider.1", "sun.security.provider.Sun");
    props.put("security.provider.2", "sun.security.rsa.SunRsaSign");
    props.put("security.provider.3", "com.sun.net.ssl.internal.ssl.Provider");
    props.put("security.provider.4", "com.sun.crypto.provider.SunJCE");
    props.put("security.provider.5", "sun.security.jgss.SunProvider");
    props.put("security.provider.6", "com.sun.security.sasl.Provider");
  }
  
  private static File securityPropFile(String paramString) {
    String str = File.separator;
    return new File(System.getProperty("java.home") + str + "lib" + str + "security" + str + paramString);
  }
  
  private static ProviderProperty getProviderProperty(String paramString) {
    ProviderProperty providerProperty = null;
    List list = Providers.getProviderList().providers();
    for (byte b = 0; b < list.size(); b++) {
      String str1 = null;
      Provider provider = (Provider)list.get(b);
      String str2 = provider.getProperty(paramString);
      if (str2 == null) {
        Enumeration enumeration = provider.keys();
        while (enumeration.hasMoreElements() && str2 == null) {
          str1 = (String)enumeration.nextElement();
          if (paramString.equalsIgnoreCase(str1)) {
            str2 = provider.getProperty(str1);
            break;
          } 
        } 
      } 
      if (str2 != null) {
        ProviderProperty providerProperty1 = new ProviderProperty(null);
        providerProperty1.className = str2;
        providerProperty1.provider = provider;
        return providerProperty1;
      } 
    } 
    return providerProperty;
  }
  
  private static String getProviderProperty(String paramString, Provider paramProvider) {
    String str = paramProvider.getProperty(paramString);
    if (str == null) {
      Enumeration enumeration = paramProvider.keys();
      while (enumeration.hasMoreElements() && str == null) {
        String str1 = (String)enumeration.nextElement();
        if (paramString.equalsIgnoreCase(str1)) {
          str = paramProvider.getProperty(str1);
          break;
        } 
      } 
    } 
    return str;
  }
  
  @Deprecated
  public static String getAlgorithmProperty(String paramString1, String paramString2) {
    ProviderProperty providerProperty = getProviderProperty("Alg." + paramString2 + "." + paramString1);
    return (providerProperty != null) ? providerProperty.className : null;
  }
  
  public static int insertProviderAt(Provider paramProvider, int paramInt) {
    String str = paramProvider.getName();
    checkInsertProvider(str);
    ProviderList providerList1;
    ProviderList providerList2 = (providerList1 = Providers.getFullProviderList()).insertAt(providerList1, paramProvider, paramInt - 1);
    if (providerList1 == providerList2)
      return -1; 
    Providers.setProviderList(providerList2);
    return providerList2.getIndex(str) + 1;
  }
  
  public static int addProvider(Provider paramProvider) { return insertProviderAt(paramProvider, 0); }
  
  public static void removeProvider(String paramString) {
    check("removeProvider." + paramString);
    ProviderList providerList1;
    ProviderList providerList2 = (providerList1 = Providers.getFullProviderList()).remove(providerList1, paramString);
    Providers.setProviderList(providerList2);
  }
  
  public static Provider[] getProviders() { return Providers.getFullProviderList().toArray(); }
  
  public static Provider getProvider(String paramString) { return Providers.getProviderList().getProvider(paramString); }
  
  public static Provider[] getProviders(String paramString) {
    String str1 = null;
    String str2 = null;
    int i = paramString.indexOf(':');
    if (i == -1) {
      str1 = paramString;
      str2 = "";
    } else {
      str1 = paramString.substring(0, i);
      str2 = paramString.substring(i + 1);
    } 
    Hashtable hashtable = new Hashtable(1);
    hashtable.put(str1, str2);
    return getProviders(hashtable);
  }
  
  public static Provider[] getProviders(Map<String, String> paramMap) {
    Provider[] arrayOfProvider1 = getProviders();
    Set set = paramMap.keySet();
    LinkedHashSet linkedHashSet = new LinkedHashSet(5);
    if (set == null || arrayOfProvider1 == null)
      return arrayOfProvider1; 
    boolean bool = true;
    for (String str1 : set) {
      String str2 = (String)paramMap.get(str1);
      LinkedHashSet linkedHashSet1 = getAllQualifyingCandidates(str1, str2, arrayOfProvider1);
      if (bool) {
        linkedHashSet = linkedHashSet1;
        bool = false;
      } 
      if (linkedHashSet1 != null && !linkedHashSet1.isEmpty()) {
        Iterator iterator = linkedHashSet.iterator();
        while (iterator.hasNext()) {
          Provider provider = (Provider)iterator.next();
          if (!linkedHashSet1.contains(provider))
            iterator.remove(); 
        } 
        continue;
      } 
      linkedHashSet = null;
    } 
    if (linkedHashSet == null || linkedHashSet.isEmpty())
      return null; 
    Object[] arrayOfObject = linkedHashSet.toArray();
    Provider[] arrayOfProvider2 = new Provider[arrayOfObject.length];
    for (byte b = 0; b < arrayOfProvider2.length; b++)
      arrayOfProvider2[b] = (Provider)arrayOfObject[b]; 
    return arrayOfProvider2;
  }
  
  private static Class<?> getSpiClass(String paramString) {
    Class clazz = (Class)spiMap.get(paramString);
    if (clazz != null)
      return clazz; 
    try {
      clazz = Class.forName("java.security." + paramString + "Spi");
      spiMap.put(paramString, clazz);
      return clazz;
    } catch (ClassNotFoundException classNotFoundException) {
      throw new AssertionError("Spi class not found", classNotFoundException);
    } 
  }
  
  static Object[] getImpl(String paramString1, String paramString2, String paramString3) throws NoSuchAlgorithmException, NoSuchProviderException { return (paramString3 == null) ? GetInstance.getInstance(paramString2, getSpiClass(paramString2), paramString1).toArray() : GetInstance.getInstance(paramString2, getSpiClass(paramString2), paramString1, paramString3).toArray(); }
  
  static Object[] getImpl(String paramString1, String paramString2, String paramString3, Object paramObject) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException { return (paramString3 == null) ? GetInstance.getInstance(paramString2, getSpiClass(paramString2), paramString1, paramObject).toArray() : GetInstance.getInstance(paramString2, getSpiClass(paramString2), paramString1, paramObject, paramString3).toArray(); }
  
  static Object[] getImpl(String paramString1, String paramString2, Provider paramProvider) throws NoSuchAlgorithmException { return GetInstance.getInstance(paramString2, getSpiClass(paramString2), paramString1, paramProvider).toArray(); }
  
  static Object[] getImpl(String paramString1, String paramString2, Provider paramProvider, Object paramObject) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException { return GetInstance.getInstance(paramString2, getSpiClass(paramString2), paramString1, paramObject, paramProvider).toArray(); }
  
  public static String getProperty(String paramString) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new SecurityPermission("getProperty." + paramString)); 
    String str = props.getProperty(paramString);
    if (str != null)
      str = str.trim(); 
    return str;
  }
  
  public static void setProperty(String paramString1, String paramString2) {
    check("setProperty." + paramString1);
    props.put(paramString1, paramString2);
    invalidateSMCache(paramString1);
  }
  
  private static void invalidateSMCache(String paramString) {
    final boolean pa = paramString.equals("package.access");
    boolean bool2 = paramString.equals("package.definition");
    if (bool1 || bool2)
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
              try {
                Class clazz = Class.forName("java.lang.SecurityManager", false, null);
                Field field = null;
                boolean bool = false;
                if (pa) {
                  field = clazz.getDeclaredField("packageAccessValid");
                  bool = field.isAccessible();
                  field.setAccessible(true);
                } else {
                  field = clazz.getDeclaredField("packageDefinitionValid");
                  bool = field.isAccessible();
                  field.setAccessible(true);
                } 
                field.setBoolean(field, false);
                field.setAccessible(bool);
              } catch (Exception exception) {}
              return null;
            }
          }); 
  }
  
  private static void check(String paramString) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkSecurityAccess(paramString); 
  }
  
  private static void checkInsertProvider(String paramString) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      try {
        securityManager.checkSecurityAccess("insertProvider");
      } catch (SecurityException securityException) {
        try {
          securityManager.checkSecurityAccess("insertProvider." + paramString);
        } catch (SecurityException securityException1) {
          securityException.addSuppressed(securityException1);
          throw securityException;
        } 
      }  
  }
  
  private static LinkedHashSet<Provider> getAllQualifyingCandidates(String paramString1, String paramString2, Provider[] paramArrayOfProvider) {
    String[] arrayOfString = getFilterComponents(paramString1, paramString2);
    String str1 = arrayOfString[0];
    String str2 = arrayOfString[1];
    String str3 = arrayOfString[2];
    return getProvidersNotUsingCache(str1, str2, str3, paramString2, paramArrayOfProvider);
  }
  
  private static LinkedHashSet<Provider> getProvidersNotUsingCache(String paramString1, String paramString2, String paramString3, String paramString4, Provider[] paramArrayOfProvider) {
    LinkedHashSet linkedHashSet = new LinkedHashSet(5);
    for (byte b = 0; b < paramArrayOfProvider.length; b++) {
      if (isCriterionSatisfied(paramArrayOfProvider[b], paramString1, paramString2, paramString3, paramString4))
        linkedHashSet.add(paramArrayOfProvider[b]); 
    } 
    return linkedHashSet;
  }
  
  private static boolean isCriterionSatisfied(Provider paramProvider, String paramString1, String paramString2, String paramString3, String paramString4) {
    String str1 = paramString1 + '.' + paramString2;
    if (paramString3 != null)
      str1 = str1 + ' ' + paramString3; 
    String str2 = getProviderProperty(str1, paramProvider);
    if (str2 == null) {
      String str = getProviderProperty("Alg.Alias." + paramString1 + "." + paramString2, paramProvider);
      if (str != null) {
        str1 = paramString1 + "." + str;
        if (paramString3 != null)
          str1 = str1 + ' ' + paramString3; 
        str2 = getProviderProperty(str1, paramProvider);
      } 
      if (str2 == null)
        return false; 
    } 
    return (paramString3 == null) ? true : (isStandardAttr(paramString3) ? isConstraintSatisfied(paramString3, paramString4, str2) : paramString4.equalsIgnoreCase(str2));
  }
  
  private static boolean isStandardAttr(String paramString) { return paramString.equalsIgnoreCase("KeySize") ? true : (paramString.equalsIgnoreCase("ImplementedIn")); }
  
  private static boolean isConstraintSatisfied(String paramString1, String paramString2, String paramString3) {
    if (paramString1.equalsIgnoreCase("KeySize")) {
      int i = Integer.parseInt(paramString2);
      int j = Integer.parseInt(paramString3);
      return (i <= j);
    } 
    return paramString1.equalsIgnoreCase("ImplementedIn") ? paramString2.equalsIgnoreCase(paramString3) : 0;
  }
  
  static String[] getFilterComponents(String paramString1, String paramString2) {
    int i = paramString1.indexOf('.');
    if (i < 0)
      throw new InvalidParameterException("Invalid filter"); 
    String str1 = paramString1.substring(0, i);
    String str2 = null;
    String str3 = null;
    if (paramString2.length() == 0) {
      str2 = paramString1.substring(i + 1).trim();
      if (str2.length() == 0)
        throw new InvalidParameterException("Invalid filter"); 
    } else {
      int j = paramString1.indexOf(' ');
      if (j == -1)
        throw new InvalidParameterException("Invalid filter"); 
      str3 = paramString1.substring(j + 1).trim();
      if (str3.length() == 0)
        throw new InvalidParameterException("Invalid filter"); 
      if (j < i || i == j - 1)
        throw new InvalidParameterException("Invalid filter"); 
      str2 = paramString1.substring(i + 1, j);
    } 
    String[] arrayOfString = new String[3];
    arrayOfString[0] = str1;
    arrayOfString[1] = str2;
    arrayOfString[2] = str3;
    return arrayOfString;
  }
  
  public static Set<String> getAlgorithms(String paramString) {
    if (paramString == null || paramString.length() == 0 || paramString.endsWith("."))
      return Collections.emptySet(); 
    HashSet hashSet = new HashSet();
    Provider[] arrayOfProvider = getProviders();
    for (byte b = 0; b < arrayOfProvider.length; b++) {
      Enumeration enumeration = arrayOfProvider[b].keys();
      while (enumeration.hasMoreElements()) {
        String str = ((String)enumeration.nextElement()).toUpperCase(Locale.ENGLISH);
        if (str.startsWith(paramString.toUpperCase(Locale.ENGLISH)) && str.indexOf(" ") < 0)
          hashSet.add(str.substring(paramString.length() + 1)); 
      } 
    } 
    return Collections.unmodifiableSet(hashSet);
  }
  
  static  {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            Security.initialize();
            return null;
          }
        });
    spiMap = new ConcurrentHashMap();
  }
  
  private static class ProviderProperty {
    String className;
    
    Provider provider;
    
    private ProviderProperty() {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\Security.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */