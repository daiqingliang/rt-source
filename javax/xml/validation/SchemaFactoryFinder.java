package javax.xml.validation;

import com.sun.org.apache.xerces.internal.jaxp.validation.XMLSchemaFactory;
import java.io.File;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

class SchemaFactoryFinder {
  private static boolean debug = false;
  
  private static final SecuritySupport ss = new SecuritySupport();
  
  private static final String DEFAULT_PACKAGE = "com.sun.org.apache.xerces.internal";
  
  private static final Properties cacheProps = new Properties();
  
  private final ClassLoader classLoader;
  
  private static final Class<SchemaFactory> SERVICE_CLASS;
  
  private static void debugPrintln(String paramString) {
    if (debug)
      System.err.println("JAXP: " + paramString); 
  }
  
  public SchemaFactoryFinder(ClassLoader paramClassLoader) {
    this.classLoader = paramClassLoader;
    if (debug)
      debugDisplayClassLoader(); 
  }
  
  private void debugDisplayClassLoader() {
    try {
      if (this.classLoader == ss.getContextClassLoader()) {
        debugPrintln("using thread context class loader (" + this.classLoader + ") for search");
        return;
      } 
    } catch (Throwable throwable) {}
    if (this.classLoader == ClassLoader.getSystemClassLoader()) {
      debugPrintln("using system class loader (" + this.classLoader + ") for search");
      return;
    } 
    debugPrintln("using class loader (" + this.classLoader + ") for search");
  }
  
  public SchemaFactory newFactory(String paramString) {
    if (paramString == null)
      throw new NullPointerException(); 
    SchemaFactory schemaFactory = _newFactory(paramString);
    if (schemaFactory != null) {
      debugPrintln("factory '" + schemaFactory.getClass().getName() + "' was found for " + paramString);
    } else {
      debugPrintln("unable to find a factory for " + paramString);
    } 
    return schemaFactory;
  }
  
  private SchemaFactory _newFactory(String paramString) {
    String str1 = SERVICE_CLASS.getName() + ":" + paramString;
    try {
      debugPrintln("Looking up system property '" + str1 + "'");
      String str = ss.getSystemProperty(str1);
      if (str != null) {
        debugPrintln("The value is '" + str + "'");
        SchemaFactory schemaFactory1 = createInstance(str);
        if (schemaFactory1 != null)
          return schemaFactory1; 
      } else {
        debugPrintln("The property is undefined.");
      } 
    } catch (Throwable throwable) {
      if (debug) {
        debugPrintln("failed to look up system property '" + str1 + "'");
        throwable.printStackTrace();
      } 
    } 
    String str2 = ss.getSystemProperty("java.home");
    String str3 = str2 + File.separator + "lib" + File.separator + "jaxp.properties";
    try {
      if (firstTime)
        synchronized (cacheProps) {
          if (firstTime) {
            File file = new File(str3);
            firstTime = false;
            if (ss.doesFileExist(file)) {
              debugPrintln("Read properties file " + file);
              cacheProps.load(ss.getFileInputStream(file));
            } 
          } 
        }  
      String str = cacheProps.getProperty(str1);
      debugPrintln("found " + str + " in $java.home/jaxp.properties");
      if (str != null) {
        SchemaFactory schemaFactory1 = createInstance(str);
        if (schemaFactory1 != null)
          return schemaFactory1; 
      } 
    } catch (Exception exception) {
      if (debug)
        exception.printStackTrace(); 
    } 
    SchemaFactory schemaFactory = findServiceProvider(paramString);
    if (schemaFactory != null)
      return schemaFactory; 
    if (paramString.equals("http://www.w3.org/2001/XMLSchema")) {
      debugPrintln("attempting to use the platform default XML Schema validator");
      return new XMLSchemaFactory();
    } 
    debugPrintln("all things were tried, but none was found. bailing out.");
    return null;
  }
  
  private Class<?> createClass(String paramString) {
    Class clazz;
    boolean bool = false;
    if (System.getSecurityManager() != null && paramString != null && paramString.startsWith("com.sun.org.apache.xerces.internal"))
      bool = true; 
    try {
      if (this.classLoader != null && !bool) {
        clazz = Class.forName(paramString, false, this.classLoader);
      } else {
        clazz = Class.forName(paramString);
      } 
    } catch (Throwable throwable) {
      if (debug)
        throwable.printStackTrace(); 
      return null;
    } 
    return clazz;
  }
  
  SchemaFactory createInstance(String paramString) {
    SchemaFactory schemaFactory = null;
    debugPrintln("createInstance(" + paramString + ")");
    Class clazz = createClass(paramString);
    if (clazz == null) {
      debugPrintln("failed to getClass(" + paramString + ")");
      return null;
    } 
    debugPrintln("loaded " + paramString + " from " + which(clazz));
    try {
      if (!SchemaFactory.class.isAssignableFrom(clazz))
        throw new ClassCastException(clazz.getName() + " cannot be cast to " + SchemaFactory.class); 
      schemaFactory = (SchemaFactory)clazz.newInstance();
    } catch (ClassCastException classCastException) {
      debugPrintln("could not instantiate " + clazz.getName());
      if (debug)
        classCastException.printStackTrace(); 
      return null;
    } catch (IllegalAccessException illegalAccessException) {
      debugPrintln("could not instantiate " + clazz.getName());
      if (debug)
        illegalAccessException.printStackTrace(); 
      return null;
    } catch (InstantiationException instantiationException) {
      debugPrintln("could not instantiate " + clazz.getName());
      if (debug)
        instantiationException.printStackTrace(); 
      return null;
    } 
    return schemaFactory;
  }
  
  private boolean isSchemaLanguageSupportedBy(final SchemaFactory factory, final String schemaLanguage, AccessControlContext paramAccessControlContext) { return ((Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
          public Boolean run() { return Boolean.valueOf(factory.isSchemaLanguageSupported(schemaLanguage)); }
        },  paramAccessControlContext)).booleanValue(); }
  
  private SchemaFactory findServiceProvider(final String schemaLanguage) {
    assert paramString != null;
    final AccessControlContext acc = AccessController.getContext();
    try {
      return (SchemaFactory)AccessController.doPrivileged(new PrivilegedAction<SchemaFactory>() {
            public SchemaFactory run() {
              ServiceLoader serviceLoader = ServiceLoader.load(SERVICE_CLASS);
              for (SchemaFactory schemaFactory : serviceLoader) {
                if (SchemaFactoryFinder.this.isSchemaLanguageSupportedBy(schemaFactory, schemaLanguage, acc))
                  return schemaFactory; 
              } 
              return null;
            }
          });
    } catch (ServiceConfigurationError serviceConfigurationError) {
      throw new SchemaFactoryConfigurationError("Provider for " + SERVICE_CLASS + " cannot be created", serviceConfigurationError);
    } 
  }
  
  private static String which(Class<?> paramClass) { return which(paramClass.getName(), paramClass.getClassLoader()); }
  
  private static String which(String paramString, ClassLoader paramClassLoader) {
    String str = paramString.replace('.', '/') + ".class";
    if (paramClassLoader == null)
      paramClassLoader = ClassLoader.getSystemClassLoader(); 
    URL uRL = ss.getResourceAsURL(paramClassLoader, str);
    return (uRL != null) ? uRL.toString() : null;
  }
  
  static  {
    try {
      debug = (ss.getSystemProperty("jaxp.debug") != null);
    } catch (Exception exception) {
      debug = false;
    } 
    SERVICE_CLASS = SchemaFactory.class;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\validation\SchemaFactoryFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */