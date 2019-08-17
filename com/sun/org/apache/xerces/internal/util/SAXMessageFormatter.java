package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class SAXMessageFormatter {
  public static String formatMessage(Locale paramLocale, String paramString, Object[] paramArrayOfObject) throws MissingResourceException {
    String str;
    ResourceBundle resourceBundle = null;
    if (paramLocale != null) {
      resourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.SAXMessages", paramLocale);
    } else {
      resourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.SAXMessages");
    } 
    try {
      str = resourceBundle.getString(paramString);
      if (paramArrayOfObject != null)
        try {
          str = MessageFormat.format(str, paramArrayOfObject);
        } catch (Exception exception) {
          str = resourceBundle.getString("FormatFailed");
          str = str + " " + resourceBundle.getString(paramString);
        }  
    } catch (MissingResourceException missingResourceException) {
      str = resourceBundle.getString("BadMessageKey");
      throw new MissingResourceException(paramString, str, paramString);
    } 
    if (str == null) {
      str = paramString;
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
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\SAXMessageFormatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */