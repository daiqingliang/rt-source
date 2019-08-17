package javax.management.remote;

import com.sun.jmx.remote.util.ClassLogger;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.management.MBeanServer;

public class JMXConnectorServerFactory {
  public static final String DEFAULT_CLASS_LOADER = "jmx.remote.default.class.loader";
  
  public static final String DEFAULT_CLASS_LOADER_NAME = "jmx.remote.default.class.loader.name";
  
  public static final String PROTOCOL_PROVIDER_PACKAGES = "jmx.remote.protocol.provider.pkgs";
  
  public static final String PROTOCOL_PROVIDER_CLASS_LOADER = "jmx.remote.protocol.provider.class.loader";
  
  private static final String PROTOCOL_PROVIDER_DEFAULT_PACKAGE = "com.sun.jmx.remote.protocol";
  
  private static final ClassLogger logger = new ClassLogger("javax.management.remote.misc", "JMXConnectorServerFactory");
  
  private static JMXConnectorServer getConnectorServerAsService(ClassLoader paramClassLoader, JMXServiceURL paramJMXServiceURL, Map<String, ?> paramMap, MBeanServer paramMBeanServer) throws IOException {
    Iterator iterator = JMXConnectorFactory.getProviderIterator(JMXConnectorServerProvider.class, paramClassLoader);
    IOException iOException = null;
    while (iterator.hasNext()) {
      try {
        return ((JMXConnectorServerProvider)iterator.next()).newJMXConnectorServer(paramJMXServiceURL, paramMap, paramMBeanServer);
      } catch (JMXProviderException jMXProviderException) {
        throw jMXProviderException;
      } catch (Exception exception) {
        if (logger.traceOn())
          logger.trace("getConnectorAsService", "URL[" + paramJMXServiceURL + "] Service provider exception: " + exception); 
        if (!(exception instanceof MalformedURLException) && iOException == null) {
          if (exception instanceof IOException) {
            iOException = (IOException)exception;
            continue;
          } 
          iOException = (IOException)EnvHelp.initCause(new IOException(exception.getMessage()), exception);
        } 
      } 
    } 
    if (iOException == null)
      return null; 
    throw iOException;
  }
  
  public static JMXConnectorServer newJMXConnectorServer(JMXServiceURL paramJMXServiceURL, Map<String, ?> paramMap, MBeanServer paramMBeanServer) throws IOException {
    HashMap hashMap;
    if (paramMap == null) {
      hashMap = new HashMap();
    } else {
      EnvHelp.checkAttributes(paramMap);
      hashMap = new HashMap(paramMap);
    } 
    Class clazz = JMXConnectorServerProvider.class;
    ClassLoader classLoader = JMXConnectorFactory.resolveClassLoader(hashMap);
    String str = paramJMXServiceURL.getProtocol();
    JMXConnectorServerProvider jMXConnectorServerProvider = (JMXConnectorServerProvider)JMXConnectorFactory.getProvider(paramJMXServiceURL, hashMap, "ServerProvider", clazz, classLoader);
    IOException iOException = null;
    if (jMXConnectorServerProvider == null) {
      if (classLoader != null)
        try {
          JMXConnectorServer jMXConnectorServer = getConnectorServerAsService(classLoader, paramJMXServiceURL, hashMap, paramMBeanServer);
          if (jMXConnectorServer != null)
            return jMXConnectorServer; 
        } catch (JMXProviderException jMXProviderException) {
          throw jMXProviderException;
        } catch (IOException iOException1) {
          iOException = iOException1;
        }  
      jMXConnectorServerProvider = (JMXConnectorServerProvider)JMXConnectorFactory.getProvider(str, "com.sun.jmx.remote.protocol", JMXConnectorFactory.class.getClassLoader(), "ServerProvider", clazz);
    } 
    if (jMXConnectorServerProvider == null) {
      MalformedURLException malformedURLException = new MalformedURLException("Unsupported protocol: " + str);
      if (iOException == null)
        throw malformedURLException; 
      throw (MalformedURLException)EnvHelp.initCause(malformedURLException, iOException);
    } 
    Map map = Collections.unmodifiableMap(hashMap);
    return jMXConnectorServerProvider.newJMXConnectorServer(paramJMXServiceURL, map, paramMBeanServer);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\remote\JMXConnectorServerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */