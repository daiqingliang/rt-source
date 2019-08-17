package com.sun.org.apache.xpath.internal.res;

import com.sun.org.apache.bcel.internal.util.SecuritySupport;
import com.sun.org.apache.xml.internal.res.XMLMessages;
import java.text.MessageFormat;
import java.util.ListResourceBundle;

public class XPATHMessages extends XMLMessages {
  private static ListResourceBundle XPATHBundle = null;
  
  private static final String XPATH_ERROR_RESOURCES = "com.sun.org.apache.xpath.internal.res.XPATHErrorResources";
  
  public static final String createXPATHMessage(String paramString, Object[] paramArrayOfObject) {
    if (XPATHBundle == null)
      XPATHBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xpath.internal.res.XPATHErrorResources"); 
    return (XPATHBundle != null) ? createXPATHMsg(XPATHBundle, paramString, paramArrayOfObject) : "Could not load any resource bundles.";
  }
  
  public static final String createXPATHWarning(String paramString, Object[] paramArrayOfObject) {
    if (XPATHBundle == null)
      XPATHBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xpath.internal.res.XPATHErrorResources"); 
    return (XPATHBundle != null) ? createXPATHMsg(XPATHBundle, paramString, paramArrayOfObject) : "Could not load any resource bundles.";
  }
  
  public static final String createXPATHMsg(ListResourceBundle paramListResourceBundle, String paramString, Object[] paramArrayOfObject) {
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\res\XPATHMessages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */