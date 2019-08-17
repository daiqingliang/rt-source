package com.sun.org.apache.xerces.internal.impl.msg;

import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class XMLMessageFormatter_it implements MessageFormatter {
  public static final String XML_DOMAIN = "http://www.w3.org/TR/1998/REC-xml-19980210";
  
  public static final String XMLNS_DOMAIN = "http://www.w3.org/TR/1999/REC-xml-names-19990114";
  
  private Locale fLocale = null;
  
  private ResourceBundle fResourceBundle = null;
  
  public String formatMessage(Locale paramLocale, String paramString, Object[] paramArrayOfObject) throws MissingResourceException {
    String str;
    if (this.fResourceBundle == null || paramLocale != this.fLocale) {
      if (paramLocale != null) {
        this.fResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLMessages", paramLocale);
        this.fLocale = paramLocale;
      } 
      if (this.fResourceBundle == null)
        this.fResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLMessages"); 
    } 
    try {
      str = this.fResourceBundle.getString(paramString);
      if (paramArrayOfObject != null)
        try {
          str = MessageFormat.format(str, paramArrayOfObject);
        } catch (Exception exception) {
          str = this.fResourceBundle.getString("FormatFailed");
          str = str + " " + this.fResourceBundle.getString(paramString);
        }  
    } catch (MissingResourceException missingResourceException) {
      str = this.fResourceBundle.getString("BadMessageKey");
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\msg\XMLMessageFormatter_it.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */