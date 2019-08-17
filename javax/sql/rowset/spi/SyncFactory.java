package javax.sql.rowset.spi;

import com.sun.rowset.providers.RIOptimisticProvider;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.sql.SQLPermission;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.PropertyPermission;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NotContextException;
import sun.reflect.misc.ReflectUtil;

public class SyncFactory {
  public static final String ROWSET_SYNC_PROVIDER = "rowset.provider.classname";
  
  public static final String ROWSET_SYNC_VENDOR = "rowset.provider.vendor";
  
  public static final String ROWSET_SYNC_PROVIDER_VERSION = "rowset.provider.version";
  
  private static String ROWSET_PROPERTIES = "rowset.properties";
  
  private static final SQLPermission SET_SYNCFACTORY_PERMISSION = new SQLPermission("setSyncFactory");
  
  private static Context ic;
  
  private static Hashtable<String, SyncProvider> implementations;
  
  private static String colon = ":";
  
  private static String strFileSep = "/";
  
  private static boolean debug = false;
  
  private static int providerImplIndex = 0;
  
  private static boolean lazyJNDICtxRefresh = false;
  
  private SyncFactory() {}
  
  public static void registerProvider(String paramString) throws SyncFactoryException {
    ProviderImpl providerImpl = new ProviderImpl();
    providerImpl.setClassname(paramString);
    initMapIfNecessary();
    implementations.put(paramString, providerImpl);
  }
  
  public static SyncFactory getSyncFactory() { return SyncFactoryHolder.factory; }
  
  public static void unregisterProvider(String paramString) throws SyncFactoryException {
    initMapIfNecessary();
    if (implementations.containsKey(paramString))
      implementations.remove(paramString); 
  }
  
  private static void initMapIfNecessary() {
    Properties properties = new Properties();
    if (implementations == null) {
      String str;
      implementations = new Hashtable();
      try {
        try {
          str = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
                public String run() { return System.getProperty("rowset.properties"); }
              },  null, new Permission[] { new PropertyPermission("rowset.properties", "read") });
        } catch (Exception exception) {
          System.out.println("errorget rowset.properties: " + exception);
          str = null;
        } 
        if (str != null) {
          ROWSET_PROPERTIES = str;
          try (FileInputStream null = new FileInputStream(ROWSET_PROPERTIES)) {
            properties.load(fileInputStream);
          } 
          parseProperties(properties);
        } 
        ROWSET_PROPERTIES = "javax" + strFileSep + "sql" + strFileSep + "rowset" + strFileSep + "rowset.properties";
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
          AccessController.doPrivileged(() -> {
                try (InputStream null = (paramClassLoader == null) ? ClassLoader.getSystemResourceAsStream(ROWSET_PROPERTIES) : paramClassLoader.getResourceAsStream(ROWSET_PROPERTIES)) {
                  if (inputStream == null)
                    throw new SyncFactoryException("Resource " + ROWSET_PROPERTIES + " not found"); 
                  paramProperties.load(inputStream);
                } 
                return null;
              });
        } catch (PrivilegedActionException privilegedActionException) {
          Exception exception = privilegedActionException.getException();
          if (exception instanceof SyncFactoryException)
            throw (SyncFactoryException)exception; 
          SyncFactoryException syncFactoryException = new SyncFactoryException();
          syncFactoryException.initCause(privilegedActionException.getException());
          throw syncFactoryException;
        } 
        parseProperties(properties);
      } catch (FileNotFoundException null) {
        throw new SyncFactoryException("Cannot locate properties file: " + str);
      } catch (IOException null) {
        throw new SyncFactoryException("IOException: " + str);
      } 
      properties.clear();
      try {
        str = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
              public String run() { return System.getProperty("rowset.provider.classname"); }
            },  null, new Permission[] { new PropertyPermission("rowset.provider.classname", "read") });
      } catch (Exception exception) {
        str = null;
      } 
      if (str != null) {
        byte b = 0;
        if (str.indexOf(colon) > 0) {
          StringTokenizer stringTokenizer = new StringTokenizer(str, colon);
          while (stringTokenizer.hasMoreElements()) {
            properties.put("rowset.provider.classname." + b, stringTokenizer.nextToken());
            b++;
          } 
        } else {
          properties.put("rowset.provider.classname", str);
        } 
        parseProperties(properties);
      } 
    } 
  }
  
  private static void parseProperties(Properties paramProperties) {
    ProviderImpl providerImpl = null;
    String str = null;
    String[] arrayOfString = null;
    Enumeration enumeration = paramProperties.propertyNames();
    while (enumeration.hasMoreElements()) {
      String str1 = (String)enumeration.nextElement();
      int i = str1.length();
      if (str1.startsWith("rowset.provider.classname")) {
        providerImpl = new ProviderImpl();
        providerImpl.setIndex(providerImplIndex++);
        if (i == "rowset.provider.classname".length()) {
          arrayOfString = getPropertyNames(false);
        } else {
          arrayOfString = getPropertyNames(true, str1.substring(i - 1));
        } 
        str = paramProperties.getProperty(arrayOfString[0]);
        providerImpl.setClassname(str);
        providerImpl.setVendor(paramProperties.getProperty(arrayOfString[1]));
        providerImpl.setVersion(paramProperties.getProperty(arrayOfString[2]));
        implementations.put(str, providerImpl);
      } 
    } 
  }
  
  private static String[] getPropertyNames(boolean paramBoolean) { return getPropertyNames(paramBoolean, null); }
  
  private static String[] getPropertyNames(boolean paramBoolean, String paramString) {
    String str = ".";
    String[] arrayOfString = { "rowset.provider.classname", "rowset.provider.vendor", "rowset.provider.version" };
    if (paramBoolean) {
      for (byte b = 0; b < arrayOfString.length; b++)
        arrayOfString[b] = arrayOfString[b] + str + paramString; 
      return arrayOfString;
    } 
    return arrayOfString;
  }
  
  private static void showImpl(ProviderImpl paramProviderImpl) {
    System.out.println("Provider implementation:");
    System.out.println("Classname: " + paramProviderImpl.getClassname());
    System.out.println("Vendor: " + paramProviderImpl.getVendor());
    System.out.println("Version: " + paramProviderImpl.getVersion());
    System.out.println("Impl index: " + paramProviderImpl.getIndex());
  }
  
  public static SyncProvider getInstance(String paramString) throws SyncFactoryException {
    if (paramString == null)
      throw new SyncFactoryException("The providerID cannot be null"); 
    initMapIfNecessary();
    initJNDIContext();
    ProviderImpl providerImpl = (ProviderImpl)implementations.get(paramString);
    if (providerImpl == null)
      return new RIOptimisticProvider(); 
    try {
      ReflectUtil.checkPackageAccess(paramString);
    } catch (AccessControlException accessControlException) {
      SyncFactoryException syncFactoryException = new SyncFactoryException();
      syncFactoryException.initCause(accessControlException);
      throw syncFactoryException;
    } 
    Class clazz = null;
    try {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      clazz = Class.forName(paramString, true, classLoader);
      return (clazz != null) ? (SyncProvider)clazz.newInstance() : new RIOptimisticProvider();
    } catch (IllegalAccessException illegalAccessException) {
      throw new SyncFactoryException("IllegalAccessException: " + illegalAccessException.getMessage());
    } catch (InstantiationException instantiationException) {
      throw new SyncFactoryException("InstantiationException: " + instantiationException.getMessage());
    } catch (ClassNotFoundException classNotFoundException) {
      throw new SyncFactoryException("ClassNotFoundException: " + classNotFoundException.getMessage());
    } 
  }
  
  public static Enumeration<SyncProvider> getRegisteredProviders() throws SyncFactoryException {
    initMapIfNecessary();
    return implementations.elements();
  }
  
  public static void setLogger(Logger paramLogger) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(SET_SYNCFACTORY_PERMISSION); 
    if (paramLogger == null)
      throw new NullPointerException("You must provide a Logger"); 
    rsLogger = paramLogger;
  }
  
  public static void setLogger(Logger paramLogger, Level paramLevel) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(SET_SYNCFACTORY_PERMISSION); 
    if (paramLogger == null)
      throw new NullPointerException("You must provide a Logger"); 
    paramLogger.setLevel(paramLevel);
    rsLogger = paramLogger;
  }
  
  public static Logger getLogger() throws SyncFactoryException {
    Logger logger = rsLogger;
    if (logger == null)
      throw new SyncFactoryException("(SyncFactory) : No logger has been set"); 
    return logger;
  }
  
  public static void setJNDIContext(Context paramContext) throws SyncFactoryException {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(SET_SYNCFACTORY_PERMISSION); 
    if (paramContext == null)
      throw new SyncFactoryException("Invalid JNDI context supplied"); 
    ic = paramContext;
  }
  
  private static void initJNDIContext() {
    if (ic != null && !lazyJNDICtxRefresh)
      try {
        parseProperties(parseJNDIContext());
        lazyJNDICtxRefresh = true;
      } catch (NamingException namingException) {
        namingException.printStackTrace();
        throw new SyncFactoryException("SPI: NamingException: " + namingException.getExplanation());
      } catch (Exception exception) {
        exception.printStackTrace();
        throw new SyncFactoryException("SPI: Exception: " + exception.getMessage());
      }  
  }
  
  private static Properties parseJNDIContext() throws NamingException {
    NamingEnumeration namingEnumeration = ic.listBindings("");
    Properties properties = new Properties();
    enumerateBindings(namingEnumeration, properties);
    return properties;
  }
  
  private static void enumerateBindings(NamingEnumeration<?> paramNamingEnumeration, Properties paramProperties) throws NamingException {
    boolean bool = false;
    try {
      Binding binding = null;
      Object object = null;
      String str = null;
      while (paramNamingEnumeration.hasMore()) {
        binding = (Binding)paramNamingEnumeration.next();
        str = binding.getName();
        object = binding.getObject();
        if (!(ic.lookup(str) instanceof Context) && ic.lookup(str) instanceof SyncProvider)
          bool = true; 
        if (bool) {
          SyncProvider syncProvider = (SyncProvider)object;
          paramProperties.put("rowset.provider.classname", syncProvider.getProviderID());
          bool = false;
        } 
      } 
    } catch (NotContextException notContextException) {
      paramNamingEnumeration.next();
      enumerateBindings(paramNamingEnumeration, paramProperties);
    } 
  }
  
  private static class SyncFactoryHolder {
    static final SyncFactory factory = new SyncFactory(null);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sql\rowset\spi\SyncFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */