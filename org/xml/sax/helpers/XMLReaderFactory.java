package org.xml.sax.helpers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public final class XMLReaderFactory {
  private static final String property = "org.xml.sax.driver";
  
  private static SecuritySupport ss = new SecuritySupport();
  
  private static String _clsFromJar = null;
  
  private static boolean _jarread = false;
  
  public static XMLReader createXMLReader() throws SAXException {
    String str = null;
    ClassLoader classLoader = ss.getContextClassLoader();
    try {
      str = ss.getSystemProperty("org.xml.sax.driver");
    } catch (RuntimeException runtimeException) {}
    if (str == null) {
      if (!_jarread) {
        _jarread = true;
        String str1 = "META-INF/services/org.xml.sax.driver";
        try {
          InputStream inputStream;
          if (classLoader != null) {
            inputStream = ss.getResourceAsStream(classLoader, str1);
            if (inputStream == null) {
              classLoader = null;
              inputStream = ss.getResourceAsStream(classLoader, str1);
            } 
          } else {
            inputStream = ss.getResourceAsStream(classLoader, str1);
          } 
          if (inputStream != null) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF8"));
            _clsFromJar = bufferedReader.readLine();
            inputStream.close();
          } 
        } catch (Exception exception) {}
      } 
      str = _clsFromJar;
    } 
    if (str == null)
      str = "com.sun.org.apache.xerces.internal.parsers.SAXParser"; 
    if (str != null)
      return loadClass(classLoader, str); 
    try {
      return new ParserAdapter(ParserFactory.makeParser());
    } catch (Exception exception) {
      throw new SAXException("Can't create default XMLReader; is system property org.xml.sax.driver set?");
    } 
  }
  
  public static XMLReader createXMLReader(String paramString) throws SAXException { return loadClass(ss.getContextClassLoader(), paramString); }
  
  private static XMLReader loadClass(ClassLoader paramClassLoader, String paramString) throws SAXException {
    try {
      return (XMLReader)NewInstance.newInstance(paramClassLoader, paramString);
    } catch (ClassNotFoundException classNotFoundException) {
      throw new SAXException("SAX2 driver class " + paramString + " not found", classNotFoundException);
    } catch (IllegalAccessException illegalAccessException) {
      throw new SAXException("SAX2 driver class " + paramString + " found but cannot be loaded", illegalAccessException);
    } catch (InstantiationException instantiationException) {
      throw new SAXException("SAX2 driver class " + paramString + " loaded but cannot be instantiated (no empty public constructor?)", instantiationException);
    } catch (ClassCastException classCastException) {
      throw new SAXException("SAX2 driver class " + paramString + " does not implement XMLReader", classCastException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\xml\sax\helpers\XMLReaderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */