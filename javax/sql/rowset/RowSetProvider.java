package javax.sql.rowset;

import java.security.AccessControlException;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.PropertyPermission;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import sun.reflect.misc.ReflectUtil;

public class RowSetProvider {
  private static final String ROWSET_DEBUG_PROPERTY = "javax.sql.rowset.RowSetProvider.debug";
  
  private static final String ROWSET_FACTORY_IMPL = "com.sun.rowset.RowSetFactoryImpl";
  
  private static final String ROWSET_FACTORY_NAME = "javax.sql.rowset.RowSetFactory";
  
  private static boolean debug = true;
  
  public static RowSetFactory newFactory() throws SQLException {
    RowSetFactory rowSetFactory = null;
    String str = null;
    try {
      trace("Checking for Rowset System Property...");
      str = getSystemProperty("javax.sql.rowset.RowSetFactory");
      if (str != null) {
        trace("Found system property, value=" + str);
        rowSetFactory = (RowSetFactory)ReflectUtil.newInstance(getFactoryClass(str, null, true));
      } 
    } catch (Exception exception) {
      throw new SQLException("RowSetFactory: " + str + " could not be instantiated: ", exception);
    } 
    if (rowSetFactory == null) {
      rowSetFactory = loadViaServiceLoader();
      rowSetFactory = (rowSetFactory == null) ? newFactory("com.sun.rowset.RowSetFactoryImpl", null) : rowSetFactory;
    } 
    return rowSetFactory;
  }
  
  public static RowSetFactory newFactory(String paramString, ClassLoader paramClassLoader) throws SQLException {
    trace("***In newInstance()");
    if (paramString == null)
      throw new SQLException("Error: factoryClassName cannot be null"); 
    try {
      ReflectUtil.checkPackageAccess(paramString);
    } catch (AccessControlException accessControlException) {
      throw new SQLException("Access Exception", accessControlException);
    } 
    try {
      Class clazz = getFactoryClass(paramString, paramClassLoader, false);
      RowSetFactory rowSetFactory = (RowSetFactory)clazz.newInstance();
      if (debug)
        trace("Created new instance of " + clazz + " using ClassLoader: " + paramClassLoader); 
      return rowSetFactory;
    } catch (ClassNotFoundException classNotFoundException) {
      throw new SQLException("Provider " + paramString + " not found", classNotFoundException);
    } catch (Exception exception) {
      throw new SQLException("Provider " + paramString + " could not be instantiated: " + exception, exception);
    } 
  }
  
  private static ClassLoader getContextClassLoader() throws SecurityException { return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
          public ClassLoader run() throws SecurityException {
            ClassLoader classLoader = null;
            classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader == null)
              classLoader = ClassLoader.getSystemClassLoader(); 
            return classLoader;
          }
        }); }
  
  private static Class<?> getFactoryClass(String paramString, ClassLoader paramClassLoader, boolean paramBoolean) throws ClassNotFoundException {
    try {
      if (paramClassLoader == null) {
        paramClassLoader = getContextClassLoader();
        if (paramClassLoader == null)
          throw new ClassNotFoundException(); 
        return paramClassLoader.loadClass(paramString);
      } 
      return paramClassLoader.loadClass(paramString);
    } catch (ClassNotFoundException classNotFoundException) {
      if (paramBoolean)
        return Class.forName(paramString, true, RowSetFactory.class.getClassLoader()); 
      throw classNotFoundException;
    } 
  }
  
  private static RowSetFactory loadViaServiceLoader() throws SQLException {
    RowSetFactory rowSetFactory = null;
    try {
      trace("***in loadViaServiceLoader():");
      Iterator iterator = ServiceLoader.load(RowSetFactory.class).iterator();
      if (iterator.hasNext()) {
        RowSetFactory rowSetFactory1 = (RowSetFactory)iterator.next();
        trace(" Loading done by the java.util.ServiceLoader :" + rowSetFactory1.getClass().getName());
        rowSetFactory = rowSetFactory1;
      } 
    } catch (ServiceConfigurationError serviceConfigurationError) {
      throw new SQLException("RowSetFactory: Error locating RowSetFactory using Service Loader API: " + serviceConfigurationError, serviceConfigurationError);
    } 
    return rowSetFactory;
  }
  
  private static String getSystemProperty(final String propName) {
    String str = null;
    try {
      str = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() { return System.getProperty(propName); }
          },  null, new Permission[] { new PropertyPermission(paramString, "read") });
    } catch (SecurityException securityException) {
      trace("error getting " + paramString + ":  " + securityException);
      if (debug)
        securityException.printStackTrace(); 
    } 
    return str;
  }
  
  private static void trace(String paramString) {
    if (debug)
      System.err.println("###RowSets: " + paramString); 
  }
  
  static  {
    String str = getSystemProperty("javax.sql.rowset.RowSetProvider.debug");
    debug = (str != null && !"false".equals(str));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sql\rowset\RowSetProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */