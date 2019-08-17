package com.sun.org.apache.xml.internal.res;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import java.text.MessageFormat;
import java.util.ListResourceBundle;
import java.util.Locale;

public class XMLMessages {
  protected Locale fLocale = Locale.getDefault();
  
  private static ListResourceBundle XMLBundle = null;
  
  private static final String XML_ERROR_RESOURCES = "com.sun.org.apache.xml.internal.res.XMLErrorResources";
  
  protected static final String BAD_CODE = "BAD_CODE";
  
  protected static final String FORMAT_FAILED = "FORMAT_FAILED";
  
  public void setLocale(Locale paramLocale) { this.fLocale = paramLocale; }
  
  public Locale getLocale() { return this.fLocale; }
  
  public static final String createXMLMessage(String paramString, Object[] paramArrayOfObject) {
    if (XMLBundle == null)
      XMLBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xml.internal.res.XMLErrorResources"); 
    return (XMLBundle != null) ? createMsg(XMLBundle, paramString, paramArrayOfObject) : "Could not load any resource bundles.";
  }
  
  public static final String createMsg(ListResourceBundle paramListResourceBundle, String paramString, Object[] paramArrayOfObject) {
    String str1 = null;
    boolean bool = false;
    String str2 = null;
    if (paramString != null)
      str2 = paramListResourceBundle.getString(paramString); 
    if (str2 == null) {
      str2 = paramListResourceBundle.getString("BAD_CODE");
      bool = true;
    } 
    if (paramArrayOfObject != null) {
      try {
        int i = paramArrayOfObject.length;
        for (byte b = 0; b < i; b++) {
          if (null == paramArrayOfObject[b])
            paramArrayOfObject[b] = ""; 
        } 
        str1 = MessageFormat.format(str2, paramArrayOfObject);
      } catch (Exception exception) {
        str1 = paramListResourceBundle.getString("FORMAT_FAILED");
        str1 = str1 + " " + str2;
      } 
    } else {
      str1 = str2;
    } 
    if (bool)
      throw new RuntimeException(str1); 
    return str1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\res\XMLMessages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */