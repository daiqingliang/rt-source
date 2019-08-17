package javax.xml.ws.spi;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Properties;
import javax.xml.ws.WebServiceException;

class FactoryFinder {
  private static final String OSGI_SERVICE_LOADER_CLASS_NAME = "com.sun.org.glassfish.hk2.osgiresourcelocator.ServiceLoader";
  
  private static Object newInstance(String paramString, ClassLoader paramClassLoader) {
    try {
      Class clazz = safeLoadClass(paramString, paramClassLoader);
      return clazz.newInstance();
    } catch (ClassNotFoundException classNotFoundException) {
      throw new WebServiceException("Provider " + paramString + " not found", classNotFoundException);
    } catch (Exception exception) {
      throw new WebServiceException("Provider " + paramString + " could not be instantiated: " + exception, exception);
    } 
  }
  
  static Object find(String paramString1, String paramString2) {
    ClassLoader classLoader;
    if (isOsgi())
      return lookupUsingOSGiServiceLoader(paramString1); 
    try {
      classLoader = Thread.currentThread().getContextClassLoader();
    } catch (Exception exception) {
      throw new WebServiceException(exception.toString(), exception);
    } 
    String str = "META-INF/services/" + paramString1;
    bufferedReader = null;
    try {
      InputStream inputStream;
      if (classLoader == null) {
        inputStream = ClassLoader.getSystemResourceAsStream(str);
      } else {
        inputStream = classLoader.getResourceAsStream(str);
      } 
      if (inputStream != null) {
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        String str1 = bufferedReader.readLine();
        if (str1 != null && !"".equals(str1))
          return newInstance(str1, classLoader); 
      } 
    } catch (Exception exception) {
    
    } finally {
      close(bufferedReader);
    } 
    fileInputStream = null;
    try {
      String str1 = System.getProperty("java.home");
      String str2 = str1 + File.separator + "lib" + File.separator + "jaxws.properties";
      File file = new File(str2);
      if (file.exists()) {
        Properties properties = new Properties();
        fileInputStream = new FileInputStream(file);
        properties.load(fileInputStream);
        String str3 = properties.getProperty(paramString1);
        return newInstance(str3, classLoader);
      } 
    } catch (Exception exception) {
    
    } finally {
      close(fileInputStream);
    } 
    try {
      String str1 = System.getProperty(paramString1);
      if (str1 != null)
        return newInstance(str1, classLoader); 
    } catch (SecurityException securityException) {}
    if (paramString2 == null)
      throw new WebServiceException("Provider for " + paramString1 + " cannot be found", null); 
    return newInstance(paramString2, classLoader);
  }
  
  private static void close(Closeable paramCloseable) {
    if (paramCloseable != null)
      try {
        paramCloseable.close();
      } catch (IOException iOException) {} 
  }
  
  private static Class safeLoadClass(String paramString, ClassLoader paramClassLoader) throws ClassNotFoundException {
    try {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null) {
        int i = paramString.lastIndexOf('.');
        if (i != -1)
          securityManager.checkPackageAccess(paramString.substring(0, i)); 
      } 
      return (paramClassLoader == null) ? Class.forName(paramString) : paramClassLoader.loadClass(paramString);
    } catch (SecurityException securityException) {
      if ("com.sun.xml.internal.ws.spi.ProviderImpl".equals(paramString))
        return Class.forName(paramString); 
      throw securityException;
    } 
  }
  
  private static boolean isOsgi() {
    try {
      Class.forName("com.sun.org.glassfish.hk2.osgiresourcelocator.ServiceLoader");
      return true;
    } catch (ClassNotFoundException classNotFoundException) {
      return false;
    } 
  }
  
  private static Object lookupUsingOSGiServiceLoader(String paramString) {
    try {
      Class clazz1 = Class.forName(paramString);
      Class[] arrayOfClass = { clazz1 };
      Class clazz2 = Class.forName("com.sun.org.glassfish.hk2.osgiresourcelocator.ServiceLoader");
      Method method = clazz2.getMethod("lookupProviderInstances", new Class[] { Class.class });
      Iterator iterator = ((Iterable)method.invoke(null, (Object[])arrayOfClass)).iterator();
      return iterator.hasNext() ? iterator.next() : null;
    } catch (Exception exception) {
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\ws\spi\FactoryFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */