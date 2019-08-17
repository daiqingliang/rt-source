package java.sql;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;

public class DriverManager {
  private static final CopyOnWriteArrayList<DriverInfo> registeredDrivers = new CopyOnWriteArrayList();
  
  private static final Object logSync = new Object();
  
  static final SQLPermission SET_LOG_PERMISSION;
  
  static final SQLPermission DEREGISTER_DRIVER_PERMISSION;
  
  public static PrintWriter getLogWriter() { return logWriter; }
  
  public static void setLogWriter(PrintWriter paramPrintWriter) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(SET_LOG_PERMISSION); 
    logStream = null;
    logWriter = paramPrintWriter;
  }
  
  @CallerSensitive
  public static Connection getConnection(String paramString, Properties paramProperties) throws SQLException { return getConnection(paramString, paramProperties, Reflection.getCallerClass()); }
  
  @CallerSensitive
  public static Connection getConnection(String paramString1, String paramString2, String paramString3) throws SQLException {
    Properties properties = new Properties();
    if (paramString2 != null)
      properties.put("user", paramString2); 
    if (paramString3 != null)
      properties.put("password", paramString3); 
    return getConnection(paramString1, properties, Reflection.getCallerClass());
  }
  
  @CallerSensitive
  public static Connection getConnection(String paramString) throws SQLException {
    Properties properties = new Properties();
    return getConnection(paramString, properties, Reflection.getCallerClass());
  }
  
  @CallerSensitive
  public static Driver getDriver(String paramString) throws SQLException {
    println("DriverManager.getDriver(\"" + paramString + "\")");
    Class clazz = Reflection.getCallerClass();
    for (DriverInfo driverInfo : registeredDrivers) {
      if (isDriverAllowed(driverInfo.driver, clazz))
        try {
          if (driverInfo.driver.acceptsURL(paramString)) {
            println("getDriver returning " + driverInfo.driver.getClass().getName());
            return driverInfo.driver;
          } 
          continue;
        } catch (SQLException sQLException) {
          continue;
        }  
      println("    skipping: " + driverInfo.driver.getClass().getName());
    } 
    println("getDriver: no suitable driver");
    throw new SQLException("No suitable driver", "08001");
  }
  
  public static void registerDriver(Driver paramDriver) throws SQLException { registerDriver(paramDriver, null); }
  
  public static void registerDriver(Driver paramDriver, DriverAction paramDriverAction) throws SQLException {
    if (paramDriver != null) {
      registeredDrivers.addIfAbsent(new DriverInfo(paramDriver, paramDriverAction));
    } else {
      throw new NullPointerException();
    } 
    println("registerDriver: " + paramDriver);
  }
  
  @CallerSensitive
  public static void deregisterDriver(Driver paramDriver) throws SQLException {
    if (paramDriver == null)
      return; 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(DEREGISTER_DRIVER_PERMISSION); 
    println("DriverManager.deregisterDriver: " + paramDriver);
    DriverInfo driverInfo = new DriverInfo(paramDriver, null);
    if (registeredDrivers.contains(driverInfo)) {
      if (isDriverAllowed(paramDriver, Reflection.getCallerClass())) {
        DriverInfo driverInfo1 = (DriverInfo)registeredDrivers.get(registeredDrivers.indexOf(driverInfo));
        if (driverInfo1.action() != null)
          driverInfo1.action().deregister(); 
        registeredDrivers.remove(driverInfo);
      } else {
        throw new SecurityException();
      } 
    } else {
      println("    couldn't find driver to unload");
    } 
  }
  
  @CallerSensitive
  public static Enumeration<Driver> getDrivers() {
    Vector vector = new Vector();
    Class clazz = Reflection.getCallerClass();
    for (DriverInfo driverInfo : registeredDrivers) {
      if (isDriverAllowed(driverInfo.driver, clazz)) {
        vector.addElement(driverInfo.driver);
        continue;
      } 
      println("    skipping: " + driverInfo.getClass().getName());
    } 
    return vector.elements();
  }
  
  public static void setLoginTimeout(int paramInt) { loginTimeout = paramInt; }
  
  public static int getLoginTimeout() { return loginTimeout; }
  
  @Deprecated
  public static void setLogStream(PrintStream paramPrintStream) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(SET_LOG_PERMISSION); 
    logStream = paramPrintStream;
    if (paramPrintStream != null) {
      logWriter = new PrintWriter(paramPrintStream);
    } else {
      logWriter = null;
    } 
  }
  
  @Deprecated
  public static PrintStream getLogStream() { return logStream; }
  
  public static void println(String paramString) {
    synchronized (logSync) {
      if (logWriter != null) {
        logWriter.println(paramString);
        logWriter.flush();
      } 
    } 
  }
  
  private static boolean isDriverAllowed(Driver paramDriver, Class<?> paramClass) {
    ClassLoader classLoader = (paramClass != null) ? paramClass.getClassLoader() : null;
    return isDriverAllowed(paramDriver, classLoader);
  }
  
  private static boolean isDriverAllowed(Driver paramDriver, ClassLoader paramClassLoader) {
    boolean bool = false;
    if (paramDriver != null) {
      Class clazz = null;
      try {
        clazz = Class.forName(paramDriver.getClass().getName(), true, paramClassLoader);
      } catch (Exception exception) {
        bool = false;
      } 
      bool = (clazz == paramDriver.getClass());
    } 
    return bool;
  }
  
  private static void loadInitialDrivers() {
    String str;
    try {
      str = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() { return System.getProperty("jdbc.drivers"); }
          });
    } catch (Exception exception) {
      str = null;
    } 
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            ServiceLoader serviceLoader = ServiceLoader.load(Driver.class);
            Iterator iterator = serviceLoader.iterator();
            try {
              while (iterator.hasNext())
                iterator.next(); 
            } catch (Throwable throwable) {}
            return null;
          }
        });
    println("DriverManager.initialize: jdbc.drivers = " + str);
    if (str == null || str.equals(""))
      return; 
    String[] arrayOfString = str.split(":");
    println("number of Drivers:" + arrayOfString.length);
    for (String str1 : arrayOfString) {
      try {
        println("DriverManager.Initialize: loading " + str1);
        Class.forName(str1, true, ClassLoader.getSystemClassLoader());
      } catch (Exception exception) {
        println("DriverManager.Initialize: load failed: " + exception);
      } 
    } 
  }
  
  private static Connection getConnection(String paramString, Properties paramProperties, Class<?> paramClass) throws SQLException {
    ClassLoader classLoader = (paramClass != null) ? paramClass.getClassLoader() : null;
    synchronized (DriverManager.class) {
      if (classLoader == null)
        classLoader = Thread.currentThread().getContextClassLoader(); 
    } 
    if (paramString == null)
      throw new SQLException("The url cannot be null", "08001"); 
    println("DriverManager.getConnection(\"" + paramString + "\")");
    SQLException sQLException = null;
    for (DriverInfo driverInfo : registeredDrivers) {
      if (isDriverAllowed(driverInfo.driver, classLoader))
        try {
          println("    trying " + driverInfo.driver.getClass().getName());
          Connection connection = driverInfo.driver.connect(paramString, paramProperties);
          if (connection != null) {
            println("getConnection returning " + driverInfo.driver.getClass().getName());
            return connection;
          } 
          continue;
        } catch (SQLException sQLException1) {
          if (sQLException == null)
            sQLException = sQLException1; 
          continue;
        }  
      println("    skipping: " + driverInfo.getClass().getName());
    } 
    if (sQLException != null) {
      println("getConnection failed: " + sQLException);
      throw sQLException;
    } 
    println("getConnection: no suitable driver found for " + paramString);
    throw new SQLException("No suitable driver found for " + paramString, "08001");
  }
  
  static  {
    loadInitialDrivers();
    println("JDBC DriverManager initialized");
    SET_LOG_PERMISSION = new SQLPermission("setLog");
    DEREGISTER_DRIVER_PERMISSION = new SQLPermission("deregisterDriver");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\sql\DriverManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */