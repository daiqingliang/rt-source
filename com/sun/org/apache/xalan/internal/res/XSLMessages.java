package com.sun.org.apache.xalan.internal.res;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import java.util.ListResourceBundle;

public class XSLMessages extends XPATHMessages {
  private static ListResourceBundle XSLTBundle = null;
  
  private static final String XSLT_ERROR_RESOURCES = "com.sun.org.apache.xalan.internal.res.XSLTErrorResources";
  
  public static String createMessage(String paramString, Object[] paramArrayOfObject) {
    if (XSLTBundle == null)
      XSLTBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xalan.internal.res.XSLTErrorResources"); 
    return (XSLTBundle != null) ? createMsg(XSLTBundle, paramString, paramArrayOfObject) : "Could not load any resource bundles.";
  }
  
  public static String createWarning(String paramString, Object[] paramArrayOfObject) {
    if (XSLTBundle == null)
      XSLTBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xalan.internal.res.XSLTErrorResources"); 
    return (XSLTBundle != null) ? createMsg(XSLTBundle, paramString, paramArrayOfObject) : "Could not load any resource bundles.";
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\res\XSLMessages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */