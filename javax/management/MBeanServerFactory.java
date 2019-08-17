package javax.management;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.logging.Level;
import javax.management.loading.ClassLoaderRepository;
import sun.reflect.misc.ReflectUtil;

public class MBeanServerFactory {
  private static MBeanServerBuilder builder = null;
  
  private static final ArrayList<MBeanServer> mBeanServerList = new ArrayList();
  
  public static void releaseMBeanServer(MBeanServer paramMBeanServer) {
    checkPermission("releaseMBeanServer");
    removeMBeanServer(paramMBeanServer);
  }
  
  public static MBeanServer createMBeanServer() { return createMBeanServer(null); }
  
  public static MBeanServer createMBeanServer(String paramString) {
    checkPermission("createMBeanServer");
    MBeanServer mBeanServer = newMBeanServer(paramString);
    addMBeanServer(mBeanServer);
    return mBeanServer;
  }
  
  public static MBeanServer newMBeanServer() { return newMBeanServer(null); }
  
  public static MBeanServer newMBeanServer(String paramString) {
    checkPermission("newMBeanServer");
    MBeanServerBuilder mBeanServerBuilder = getNewMBeanServerBuilder();
    synchronized (mBeanServerBuilder) {
      MBeanServerDelegate mBeanServerDelegate = mBeanServerBuilder.newMBeanServerDelegate();
      if (mBeanServerDelegate == null)
        throw new JMRuntimeException("MBeanServerBuilder.newMBeanServerDelegate() returned null"); 
      MBeanServer mBeanServer = mBeanServerBuilder.newMBeanServer(paramString, null, mBeanServerDelegate);
      if (mBeanServer == null)
        throw new JMRuntimeException("MBeanServerBuilder.newMBeanServer() returned null"); 
      return mBeanServer;
    } 
  }
  
  public static ArrayList<MBeanServer> findMBeanServer(String paramString) {
    checkPermission("findMBeanServer");
    if (paramString == null)
      return new ArrayList(mBeanServerList); 
    ArrayList arrayList = new ArrayList();
    for (MBeanServer mBeanServer : mBeanServerList) {
      String str = mBeanServerId(mBeanServer);
      if (paramString.equals(str))
        arrayList.add(mBeanServer); 
    } 
    return arrayList;
  }
  
  public static ClassLoaderRepository getClassLoaderRepository(MBeanServer paramMBeanServer) { return paramMBeanServer.getClassLoaderRepository(); }
  
  private static String mBeanServerId(MBeanServer paramMBeanServer) {
    try {
      return (String)paramMBeanServer.getAttribute(MBeanServerDelegate.DELEGATE_NAME, "MBeanServerId");
    } catch (JMException jMException) {
      JmxProperties.MISC_LOGGER.finest("Ignoring exception while getting MBeanServerId: " + jMException);
      return null;
    } 
  }
  
  private static void checkPermission(String paramString) throws SecurityException {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      MBeanServerPermission mBeanServerPermission = new MBeanServerPermission(paramString);
      securityManager.checkPermission(mBeanServerPermission);
    } 
  }
  
  private static void addMBeanServer(MBeanServer paramMBeanServer) { mBeanServerList.add(paramMBeanServer); }
  
  private static void removeMBeanServer(MBeanServer paramMBeanServer) {
    boolean bool = mBeanServerList.remove(paramMBeanServer);
    if (!bool) {
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, MBeanServerFactory.class.getName(), "removeMBeanServer(MBeanServer)", "MBeanServer was not in list!");
      throw new IllegalArgumentException("MBeanServer was not in list!");
    } 
  }
  
  private static Class<?> loadBuilderClass(String paramString) throws ClassNotFoundException {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    return (classLoader != null) ? classLoader.loadClass(paramString) : ReflectUtil.forName(paramString);
  }
  
  private static MBeanServerBuilder newBuilder(Class<?> paramClass) {
    try {
      Object object = paramClass.newInstance();
      return (MBeanServerBuilder)object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (Exception exception) {
      String str = "Failed to instantiate a MBeanServerBuilder from " + paramClass + ": " + exception;
      throw new JMRuntimeException(str, exception);
    } 
  }
  
  private static void checkMBeanServerBuilder() {
    try {
      GetPropertyAction getPropertyAction = new GetPropertyAction("javax.management.builder.initial");
      String str = (String)AccessController.doPrivileged(getPropertyAction);
      try {
        Class clazz;
        if (str == null || str.length() == 0) {
          clazz = MBeanServerBuilder.class;
        } else {
          clazz = loadBuilderClass(str);
        } 
        if (builder != null) {
          Class clazz1 = builder.getClass();
          if (clazz == clazz1)
            return; 
        } 
        builder = newBuilder(clazz);
      } catch (ClassNotFoundException classNotFoundException) {
        String str1 = "Failed to load MBeanServerBuilder class " + str + ": " + classNotFoundException;
        throw new JMRuntimeException(str1, classNotFoundException);
      } 
    } catch (RuntimeException runtimeException) {
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
        StringBuilder stringBuilder = (new StringBuilder()).append("Failed to instantiate MBeanServerBuilder: ").append(runtimeException).append("\n\t\tCheck the value of the ").append("javax.management.builder.initial").append(" property.");
        JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, MBeanServerFactory.class.getName(), "checkMBeanServerBuilder", stringBuilder.toString());
      } 
      throw runtimeException;
    } 
  }
  
  private static MBeanServerBuilder getNewMBeanServerBuilder() {
    checkMBeanServerBuilder();
    return builder;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\MBeanServerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */