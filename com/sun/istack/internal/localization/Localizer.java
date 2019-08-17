package com.sun.istack.internal.localization;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Localizer {
  private final Locale _locale;
  
  private final HashMap _resourceBundles;
  
  public Localizer() { this(Locale.getDefault()); }
  
  public Localizer(Locale paramLocale) {
    this._locale = paramLocale;
    this._resourceBundles = new HashMap();
  }
  
  public Locale getLocale() { return this._locale; }
  
  public String localize(Localizable paramLocalizable) {
    String str1 = paramLocalizable.getKey();
    if (str1 == "\000")
      return (String)paramLocalizable.getArguments()[0]; 
    String str2 = paramLocalizable.getResourceBundleName();
    try {
      String str;
      ResourceBundle resourceBundle = (ResourceBundle)this._resourceBundles.get(str2);
      if (resourceBundle == null) {
        try {
          resourceBundle = ResourceBundle.getBundle(str2, this._locale);
        } catch (MissingResourceException null) {
          int i = str2.lastIndexOf('.');
          if (i != -1) {
            String str3 = str2.substring(i + 1);
            try {
              resourceBundle = ResourceBundle.getBundle(str3, this._locale);
            } catch (MissingResourceException missingResourceException) {
              try {
                resourceBundle = ResourceBundle.getBundle(str2, this._locale, Thread.currentThread().getContextClassLoader());
              } catch (MissingResourceException missingResourceException1) {
                return getDefaultMessage(paramLocalizable);
              } 
            } 
          } 
        } 
        this._resourceBundles.put(str2, resourceBundle);
      } 
      if (resourceBundle == null)
        return getDefaultMessage(paramLocalizable); 
      if (str1 == null)
        str1 = "undefined"; 
      try {
        str = resourceBundle.getString(str1);
      } catch (MissingResourceException missingResourceException) {
        str = resourceBundle.getString("undefined");
      } 
      Object[] arrayOfObject = paramLocalizable.getArguments();
      for (byte b = 0; b < arrayOfObject.length; b++) {
        if (arrayOfObject[b] instanceof Localizable)
          arrayOfObject[b] = localize((Localizable)arrayOfObject[b]); 
      } 
      return MessageFormat.format(str, arrayOfObject);
    } catch (MissingResourceException missingResourceException) {
      return getDefaultMessage(paramLocalizable);
    } 
  }
  
  private String getDefaultMessage(Localizable paramLocalizable) {
    String str = paramLocalizable.getKey();
    Object[] arrayOfObject = paramLocalizable.getArguments();
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("[failed to localize] ");
    stringBuilder.append(str);
    if (arrayOfObject != null) {
      stringBuilder.append('(');
      for (byte b = 0; b < arrayOfObject.length; b++) {
        if (b)
          stringBuilder.append(", "); 
        stringBuilder.append(String.valueOf(arrayOfObject[b]));
      } 
      stringBuilder.append(')');
    } 
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\istack\internal\localization\Localizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */