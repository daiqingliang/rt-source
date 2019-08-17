package com.sun.org.apache.xml.internal.utils;

import java.util.Locale;

public class LocaleUtility {
  public static final char IETF_SEPARATOR = '-';
  
  public static final String EMPTY_STRING = "";
  
  public static Locale langToLocale(String paramString) {
    if (paramString == null || paramString.equals(""))
      return Locale.getDefault(); 
    String str1 = "";
    String str2 = "";
    String str3 = "";
    int i = paramString.indexOf('-');
    if (i < 0) {
      str1 = paramString;
    } else {
      str1 = paramString.substring(0, i);
      int j = paramString.indexOf('-', ++i);
      if (j < 0) {
        str2 = paramString.substring(i);
      } else {
        str2 = paramString.substring(i, j);
        str3 = paramString.substring(j + 1);
      } 
    } 
    if (str1.length() == 2) {
      str1 = str1.toLowerCase();
    } else {
      str1 = "";
    } 
    if (str2.length() == 2) {
      str2 = str2.toUpperCase();
    } else {
      str2 = "";
    } 
    if (str3.length() > 0 && (str1.length() == 2 || str2.length() == 2)) {
      str3 = str3.toUpperCase();
    } else {
      str3 = "";
    } 
    return new Locale(str1, str2, str3);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\LocaleUtility.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */