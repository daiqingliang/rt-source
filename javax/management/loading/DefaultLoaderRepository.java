package javax.management.loading;

import com.sun.jmx.defaults.JmxProperties;
import java.util.ArrayList;
import java.util.logging.Level;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;

@Deprecated
public class DefaultLoaderRepository {
  public static Class<?> loadClass(String paramString) throws ClassNotFoundException {
    JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, DefaultLoaderRepository.class.getName(), "loadClass", paramString);
    return load(null, paramString);
  }
  
  public static Class<?> loadClassWithout(ClassLoader paramClassLoader, String paramString) throws ClassNotFoundException {
    JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, DefaultLoaderRepository.class.getName(), "loadClassWithout", paramString);
    return load(paramClassLoader, paramString);
  }
  
  private static Class<?> load(ClassLoader paramClassLoader, String paramString) throws ClassNotFoundException {
    ArrayList arrayList = MBeanServerFactory.findMBeanServer(null);
    for (MBeanServer mBeanServer : arrayList) {
      ClassLoaderRepository classLoaderRepository = mBeanServer.getClassLoaderRepository();
      try {
        return classLoaderRepository.loadClassWithout(paramClassLoader, paramString);
      } catch (ClassNotFoundException classNotFoundException) {}
    } 
    throw new ClassNotFoundException(paramString);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\loading\DefaultLoaderRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */