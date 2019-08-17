package com.sun.org.apache.xml.internal.serializer.utils;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import java.text.MessageFormat;
import java.util.ListResourceBundle;
import java.util.Locale;

public final class Messages {
  private final Locale m_locale = Locale.getDefault();
  
  private ListResourceBundle m_resourceBundle;
  
  private String m_resourceBundleName;
  
  Messages(String paramString) { this.m_resourceBundleName = paramString; }
  
  private Locale getLocale() { return this.m_locale; }
  
  public final String createMessage(String paramString, Object[] paramArrayOfObject) {
    if (this.m_resourceBundle == null)
      this.m_resourceBundle = SecuritySupport.getResourceBundle(this.m_resourceBundleName); 
    return (this.m_resourceBundle != null) ? createMsg(this.m_resourceBundle, paramString, paramArrayOfObject) : ("Could not load the resource bundles: " + this.m_resourceBundleName);
  }
  
  private final String createMsg(ListResourceBundle paramListResourceBundle, String paramString, Object[] paramArrayOfObject) {
    String str1 = null;
    boolean bool = false;
    String str2 = null;
    if (paramString != null) {
      str2 = paramListResourceBundle.getString(paramString);
    } else {
      paramString = "";
    } 
    if (str2 == null) {
      bool = true;
      try {
        str2 = MessageFormat.format("BAD_MSGKEY", new Object[] { paramString, this.m_resourceBundleName });
      } catch (Exception exception) {
        str2 = "The message key '" + paramString + "' is not in the message class '" + this.m_resourceBundleName + "'";
      } 
    } else if (paramArrayOfObject != null) {
      try {
        int i = paramArrayOfObject.length;
        for (byte b = 0; b < i; b++) {
          if (null == paramArrayOfObject[b])
            paramArrayOfObject[b] = ""; 
        } 
        str1 = MessageFormat.format(str2, paramArrayOfObject);
      } catch (Exception exception) {
        bool = true;
        try {
          str1 = MessageFormat.format("BAD_MSGFORMAT", new Object[] { paramString, this.m_resourceBundleName });
          str1 = str1 + " " + str2;
        } catch (Exception exception1) {
          str1 = "The format of message '" + paramString + "' in message class '" + this.m_resourceBundleName + "' failed.";
        } 
      } 
    } else {
      str1 = str2;
    } 
    if (bool)
      throw new RuntimeException(str1); 
    return str1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serialize\\utils\Messages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */