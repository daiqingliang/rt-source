package com.sun.xml.internal.fastinfoset;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

public abstract class AbstractResourceBundle extends ResourceBundle {
  public static final String LOCALE = "com.sun.xml.internal.fastinfoset.locale";
  
  public String getString(String paramString, Object[] paramArrayOfObject) {
    String str = getBundle().getString(paramString);
    return MessageFormat.format(str, paramArrayOfObject);
  }
  
  public static Locale parseLocale(String paramString) {
    Locale locale = null;
    if (paramString == null) {
      locale = Locale.getDefault();
    } else {
      try {
        String[] arrayOfString = paramString.split("_");
        if (arrayOfString.length == 1) {
          locale = new Locale(arrayOfString[0]);
        } else if (arrayOfString.length == 2) {
          locale = new Locale(arrayOfString[0], arrayOfString[1]);
        } else if (arrayOfString.length == 3) {
          locale = new Locale(arrayOfString[0], arrayOfString[1], arrayOfString[2]);
        } 
      } catch (Throwable throwable) {
        locale = Locale.getDefault();
      } 
    } 
    return locale;
  }
  
  public abstract ResourceBundle getBundle();
  
  protected Object handleGetObject(String paramString) { return getBundle().getObject(paramString); }
  
  public final Enumeration getKeys() { return getBundle().getKeys(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\AbstractResourceBundle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */