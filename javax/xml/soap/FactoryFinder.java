package javax.xml.soap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

class FactoryFinder {
  private static Object newInstance(String paramString, ClassLoader paramClassLoader) throws SOAPException {
    try {
      Class clazz = safeLoadClass(paramString, paramClassLoader);
      return clazz.newInstance();
    } catch (ClassNotFoundException classNotFoundException) {
      throw new SOAPException("Provider " + paramString + " not found", classNotFoundException);
    } catch (Exception exception) {
      throw new SOAPException("Provider " + paramString + " could not be instantiated: " + exception, exception);
    } 
  }
  
  static Object find(String paramString) throws SOAPException { return find(paramString, null, false); }
  
  static Object find(String paramString1, String paramString2) throws SOAPException { return find(paramString1, paramString2, true); }
  
  static Object find(String paramString1, String paramString2, boolean paramBoolean) throws SOAPException {
    ClassLoader classLoader;
    try {
      classLoader = Thread.currentThread().getContextClassLoader();
    } catch (Exception exception) {
      throw new SOAPException(exception.toString(), exception);
    } 
    try {
      String str1 = System.getProperty(paramString1);
      if (str1 != null)
        return newInstance(str1, classLoader); 
    } catch (SecurityException securityException) {}
    try {
      String str1 = System.getProperty("java.home");
      String str2 = str1 + File.separator + "lib" + File.separator + "jaxm.properties";
      File file = new File(str2);
      if (file.exists()) {
        Properties properties = new Properties();
        properties.load(new FileInputStream(file));
        String str3 = properties.getProperty(paramString1);
        return newInstance(str3, classLoader);
      } 
    } catch (Exception exception) {}
    String str = "META-INF/services/" + paramString1;
    try {
      InputStream inputStream = null;
      if (classLoader == null) {
        inputStream = ClassLoader.getSystemResourceAsStream(str);
      } else {
        inputStream = classLoader.getResourceAsStream(str);
      } 
      if (inputStream != null) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        String str1 = bufferedReader.readLine();
        bufferedReader.close();
        if (str1 != null && !"".equals(str1))
          return newInstance(str1, classLoader); 
      } 
    } catch (Exception exception) {}
    if (!paramBoolean)
      return null; 
    if (paramString2 == null)
      throw new SOAPException("Provider for " + paramString1 + " cannot be found", null); 
    return newInstance(paramString2, classLoader);
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
      if (isDefaultImplementation(paramString))
        return Class.forName(paramString); 
      throw securityException;
    } 
  }
  
  private static boolean isDefaultImplementation(String paramString) { return ("com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPMessageFactory1_1Impl".equals(paramString) || "com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPFactory1_1Impl".equals(paramString) || "com.sun.xml.internal.messaging.saaj.client.p2p.HttpSOAPConnectionFactory".equals(paramString) || "com.sun.xml.internal.messaging.saaj.soap.SAAJMetaFactoryImpl".equals(paramString)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\soap\FactoryFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */