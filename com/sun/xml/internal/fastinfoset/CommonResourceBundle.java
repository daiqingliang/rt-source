package com.sun.xml.internal.fastinfoset;

import java.util.Locale;
import java.util.ResourceBundle;

public class CommonResourceBundle extends AbstractResourceBundle {
  public static final String BASE_NAME = "com.sun.xml.internal.fastinfoset.resources.ResourceBundle";
  
  private static Locale locale = null;
  
  private ResourceBundle bundle = (this.bundle = null).getBundle("com.sun.xml.internal.fastinfoset.resources.ResourceBundle");
  
  protected CommonResourceBundle() {}
  
  protected CommonResourceBundle(Locale paramLocale) {}
  
  public static CommonResourceBundle getInstance() {
    if (instance == null)
      synchronized (CommonResourceBundle.class) {
        locale = (instance = new CommonResourceBundle()).parseLocale(null);
      }  
    return instance;
  }
  
  public static CommonResourceBundle getInstance(Locale paramLocale) {
    if (instance == null) {
      synchronized (CommonResourceBundle.class) {
        instance = new CommonResourceBundle(paramLocale);
      } 
    } else {
      synchronized (CommonResourceBundle.class) {
        if (locale != paramLocale)
          instance = new CommonResourceBundle(paramLocale); 
      } 
    } 
    return instance;
  }
  
  public ResourceBundle getBundle() { return this.bundle; }
  
  public ResourceBundle getBundle(Locale paramLocale) { return ResourceBundle.getBundle("com.sun.xml.internal.fastinfoset.resources.ResourceBundle", paramLocale); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\CommonResourceBundle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */