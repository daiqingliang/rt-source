package com.sun.org.apache.xerces.internal.xinclude;

import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class XIncludeMessageFormatter implements MessageFormatter {
  public static final String XINCLUDE_DOMAIN = "http://www.w3.org/TR/xinclude";
  
  private Locale fLocale = null;
  
  private ResourceBundle fResourceBundle = null;
  
  public String formatMessage(Locale paramLocale, String paramString, Object[] paramArrayOfObject) throws MissingResourceException {
    if (this.fResourceBundle == null || paramLocale != this.fLocale) {
      if (paramLocale != null) {
        this.fResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XIncludeMessages", paramLocale);
        this.fLocale = paramLocale;
      } 
      if (this.fResourceBundle == null)
        this.fResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XIncludeMessages"); 
    } 
    String str = this.fResourceBundle.getString(paramString);
    if (paramArrayOfObject != null)
      try {
        str = MessageFormat.format(str, paramArrayOfObject);
      } catch (Exception exception) {
        str = this.fResourceBundle.getString("FormatFailed");
        str = str + " " + this.fResourceBundle.getString(paramString);
      }  
    if (str == null) {
      str = this.fResourceBundle.getString("BadMessageKey");
      throw new MissingResourceException(str, "com.sun.org.apache.xerces.internal.impl.msg.XIncludeMessages", paramString);
    } 
    return str;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xinclude\XIncludeMessageFormatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */