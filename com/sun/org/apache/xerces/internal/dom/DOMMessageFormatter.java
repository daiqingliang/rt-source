package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class DOMMessageFormatter {
  public static final String DOM_DOMAIN = "http://www.w3.org/dom/DOMTR";
  
  public static final String XML_DOMAIN = "http://www.w3.org/TR/1998/REC-xml-19980210";
  
  public static final String SERIALIZER_DOMAIN = "http://apache.org/xml/serializer";
  
  private static ResourceBundle domResourceBundle = null;
  
  private static ResourceBundle xmlResourceBundle = null;
  
  private static ResourceBundle serResourceBundle = null;
  
  private static Locale locale = null;
  
  DOMMessageFormatter() { locale = Locale.getDefault(); }
  
  public static String formatMessage(String paramString1, String paramString2, Object[] paramArrayOfObject) throws MissingResourceException {
    String str;
    ResourceBundle resourceBundle = getResourceBundle(paramString1);
    if (resourceBundle == null) {
      init();
      resourceBundle = getResourceBundle(paramString1);
      if (resourceBundle == null)
        throw new MissingResourceException("Unknown domain" + paramString1, null, paramString2); 
    } 
    try {
      str = paramString2 + ": " + resourceBundle.getString(paramString2);
      if (paramArrayOfObject != null)
        try {
          str = MessageFormat.format(str, paramArrayOfObject);
        } catch (Exception exception) {
          str = resourceBundle.getString("FormatFailed");
          str = str + " " + resourceBundle.getString(paramString2);
        }  
    } catch (MissingResourceException missingResourceException) {
      str = resourceBundle.getString("BadMessageKey");
      throw new MissingResourceException(paramString2, str, paramString2);
    } 
    if (str == null) {
      str = paramString2;
      if (paramArrayOfObject.length > 0) {
        StringBuffer stringBuffer = new StringBuffer(str);
        stringBuffer.append('?');
        for (byte b = 0; b < paramArrayOfObject.length; b++) {
          if (b)
            stringBuffer.append('&'); 
          stringBuffer.append(String.valueOf(paramArrayOfObject[b]));
        } 
      } 
    } 
    return str;
  }
  
  static ResourceBundle getResourceBundle(String paramString) { return (paramString == "http://www.w3.org/dom/DOMTR" || paramString.equals("http://www.w3.org/dom/DOMTR")) ? domResourceBundle : ((paramString == "http://www.w3.org/TR/1998/REC-xml-19980210" || paramString.equals("http://www.w3.org/TR/1998/REC-xml-19980210")) ? xmlResourceBundle : ((paramString == "http://apache.org/xml/serializer" || paramString.equals("http://apache.org/xml/serializer")) ? serResourceBundle : null)); }
  
  public static void init() {
    if (locale != null) {
      domResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.DOMMessages", locale);
      serResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLSerializerMessages", locale);
      xmlResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLMessages", locale);
    } else {
      domResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.DOMMessages");
      serResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLSerializerMessages");
      xmlResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLMessages");
    } 
  }
  
  public static void setLocale(Locale paramLocale) { locale = paramLocale; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\DOMMessageFormatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */