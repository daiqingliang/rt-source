package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

class FactoryFinder {
  private static ClassLoader cl = FactoryFinder.class.getClassLoader();
  
  static Object find(String paramString) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
    String str1 = System.getProperty(paramString);
    if (str1 != null)
      return newInstance(str1); 
    String str2 = findJarServiceProviderName(paramString);
    return (str2 != null && str2.trim().length() > 0) ? newInstance(str2) : null;
  }
  
  static Object newInstance(String paramString) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
    Class clazz = cl.loadClass(paramString);
    return clazz.newInstance();
  }
  
  private static String findJarServiceProviderName(String paramString) {
    String str2;
    String str1 = "META-INF/services/" + paramString;
    InputStream inputStream = cl.getResourceAsStream(str1);
    if (inputStream == null)
      return null; 
    bufferedReader = null;
    try {
      try {
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
      } catch (UnsupportedEncodingException unsupportedEncodingException) {
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      } 
      try {
        str2 = bufferedReader.readLine();
      } catch (IOException iOException) {
        return null;
      } 
    } finally {
      if (bufferedReader != null)
        try {
          bufferedReader.close();
        } catch (IOException iOException) {
          Logger.getLogger(FactoryFinder.class.getName()).log(Level.INFO, null, iOException);
        }  
    } 
    return str2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\FactoryFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */