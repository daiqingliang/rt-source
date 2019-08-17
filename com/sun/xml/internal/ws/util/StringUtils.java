package com.sun.xml.internal.ws.util;

public class StringUtils {
  public static String decapitalize(String paramString) {
    if (paramString == null || paramString.length() == 0)
      return paramString; 
    if (paramString.length() > 1 && Character.isUpperCase(paramString.charAt(1)) && Character.isUpperCase(paramString.charAt(0)))
      return paramString; 
    char[] arrayOfChar = paramString.toCharArray();
    arrayOfChar[0] = Character.toLowerCase(arrayOfChar[0]);
    return new String(arrayOfChar);
  }
  
  public static String capitalize(String paramString) {
    if (paramString == null || paramString.length() == 0)
      return paramString; 
    char[] arrayOfChar = paramString.toCharArray();
    arrayOfChar[0] = Character.toUpperCase(arrayOfChar[0]);
    return new String(arrayOfChar);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\StringUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */