package javax.xml.xpath;

import com.sun.org.apache.xpath.internal.jaxp.XPathFactoryImpl;
import java.io.File;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

class XPathFactoryFinder {
  private static final String DEFAULT_PACKAGE = "com.sun.org.apache.xpath.internal";
  
  private static final SecuritySupport ss = new SecuritySupport();
  
  private static boolean debug = false;
  
  private static final Properties cacheProps;
  
  private final ClassLoader classLoader;
  
  private static final Class<XPathFactory> SERVICE_CLASS;
  
  private static void debugPrintln(String paramString) {
    if (debug)
      System.err.println("JAXP: " + paramString); 
  }
  
  public XPathFactoryFinder(ClassLoader paramClassLoader) {
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
  
  public XPathFactory newFactory(String paramString) throws XPathFactoryConfigurationException {
    if (paramString == null)
      throw new NullPointerException(); 
    XPathFactory xPathFactory = _newFactory(paramString);
    if (xPathFactory != null) {
      debugPrintln("factory '" + xPathFactory.getClass().getName() + "' was found for " + paramString);
    } else {
      debugPrintln("unable to find a factory for " + paramString);
    } 
    return xPathFactory;
  }
  
  private XPathFactory _newFactory(String paramString) throws XPathFactoryConfigurationException {
    XPathFactory xPathFactory = null;
    String str1 = SERVICE_CLASS.getName() + ":" + paramString;
    try {
      debugPrintln("Looking up system property '" + str1 + "'");
      String str = ss.getSystemProperty(str1);
      if (str != null) {
        debugPrintln("The value is '" + str + "'");
        xPathFactory = createInstance(str);
        if (xPathFactory != null)
          return xPathFactory; 
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
        xPathFactory = createInstance(str);
        if (xPathFactory != null)
          return xPathFactory; 
      } 
    } catch (Exception exception) {
      if (debug)
        exception.printStackTrace(); 
    } 
    assert xPathFactory == null;
    xPathFactory = findServiceProvider(paramString);
    if (xPathFactory != null)
      return xPathFactory; 
    if (paramString.equals("http://java.sun.com/jaxp/xpath/dom")) {
      debugPrintln("attempting to use the platform default W3C DOM XPath lib");
      return new XPathFactoryImpl();
    } 
    debugPrintln("all things were tried, but none was found. bailing out.");
    return null;
  }
  
  private Class<?> createClass(String paramString) {
    Class clazz;
    boolean bool = false;
    if (System.getSecurityManager() != null && paramString != null && paramString.startsWith("com.sun.org.apache.xpath.internal"))
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
  
  XPathFactory createInstance(String paramString) throws XPathFactoryConfigurationException {
    XPathFactory xPathFactory = null;
    debugPrintln("createInstance(" + paramString + ")");
    Class clazz = createClass(paramString);
    if (clazz == null) {
      debugPrintln("failed to getClass(" + paramString + ")");
      return null;
    } 
    debugPrintln("loaded " + paramString + " from " + which(clazz));
    try {
      xPathFactory = (XPathFactory)clazz.newInstance();
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
    return xPathFactory;
  }
  
  private boolean isObjectModelSupportedBy(final XPathFactory factory, final String objectModel, AccessControlContext paramAccessControlContext) { return ((Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
          public Boolean run() { return Boolean.valueOf(factory.isObjectModelSupported(objectModel)); }
        },  paramAccessControlContext)).booleanValue(); }
  
  private XPathFactory findServiceProvider(final String objectModel) throws XPathFactoryConfigurationException {
    assert paramString != null;
    final AccessControlContext acc = AccessController.getContext();
    try {
      return (XPathFactory)AccessController.doPrivileged(new PrivilegedAction<XPathFactory>() {
            public XPathFactory run() {
              ServiceLoader serviceLoader = ServiceLoader.load(SERVICE_CLASS);
              for (XPathFactory xPathFactory : serviceLoader) {
                if (XPathFactoryFinder.this.isObjectModelSupportedBy(xPathFactory, objectModel, acc))
                  return xPathFactory; 
              } 
              return null;
            }
          });
    } catch (ServiceConfigurationError serviceConfigurationError) {
      throw new XPathFactoryConfigurationException(serviceConfigurationError);
    } 
  }
  
  private static String which(Class paramClass) { return which(paramClass.getName(), paramClass.getClassLoader()); }
  
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
    cacheProps = new Properties();
    firstTime = true;
    SERVICE_CLASS = XPathFactory.class;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\xpath\XPathFactoryFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */