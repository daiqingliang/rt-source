package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.apache.xml.internal.security.Init;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class I18n {
  public static final String NOT_INITIALIZED_MSG = "You must initialize the xml-security library correctly before you use it. Call the static method \"com.sun.org.apache.xml.internal.security.Init.init();\" to do that before you use any functionality from that library.";
  
  private static ResourceBundle resourceBundle;
  
  private static boolean alreadyInitialized = false;
  
  public static String translate(String paramString, Object[] paramArrayOfObject) { return getExceptionMessage(paramString, paramArrayOfObject); }
  
  public static String translate(String paramString) { return getExceptionMessage(paramString); }
  
  public static String getExceptionMessage(String paramString) {
    try {
      return resourceBundle.getString(paramString);
    } catch (Throwable throwable) {
      return Init.isInitialized() ? ("No message with ID \"" + paramString + "\" found in resource bundle \"" + "com/sun/org/apache/xml/internal/security/resource/xmlsecurity" + "\"") : "You must initialize the xml-security library correctly before you use it. Call the static method \"com.sun.org.apache.xml.internal.security.Init.init();\" to do that before you use any functionality from that library.";
    } 
  }
  
  public static String getExceptionMessage(String paramString, Exception paramException) {
    try {
      Object[] arrayOfObject = { paramException.getMessage() };
      return MessageFormat.format(resourceBundle.getString(paramString), arrayOfObject);
    } catch (Throwable throwable) {
      return Init.isInitialized() ? ("No message with ID \"" + paramString + "\" found in resource bundle \"" + "com/sun/org/apache/xml/internal/security/resource/xmlsecurity" + "\". Original Exception was a " + paramException.getClass().getName() + " and message " + paramException.getMessage()) : "You must initialize the xml-security library correctly before you use it. Call the static method \"com.sun.org.apache.xml.internal.security.Init.init();\" to do that before you use any functionality from that library.";
    } 
  }
  
  public static String getExceptionMessage(String paramString, Object[] paramArrayOfObject) {
    try {
      return MessageFormat.format(resourceBundle.getString(paramString), paramArrayOfObject);
    } catch (Throwable throwable) {
      return Init.isInitialized() ? ("No message with ID \"" + paramString + "\" found in resource bundle \"" + "com/sun/org/apache/xml/internal/security/resource/xmlsecurity" + "\"") : "You must initialize the xml-security library correctly before you use it. Call the static method \"com.sun.org.apache.xml.internal.security.Init.init();\" to do that before you use any functionality from that library.";
    } 
  }
  
  public static void init(String paramString1, String paramString2) {
    if (alreadyInitialized)
      return; 
    resourceBundle = ResourceBundle.getBundle("com/sun/org/apache/xml/internal/security/resource/xmlsecurity", new Locale(paramString1, paramString2));
    alreadyInitialized = true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\securit\\utils\I18n.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */