package sun.util.locale;

import java.util.List;
import java.util.Map;
import java.util.Set;

public final class LocaleUtils {
  public static boolean caseIgnoreMatch(String paramString1, String paramString2) {
    if (paramString1 == paramString2)
      return true; 
    int i = paramString1.length();
    if (i != paramString2.length())
      return false; 
    for (byte b = 0; b < i; b++) {
      char c1 = paramString1.charAt(b);
      char c2 = paramString2.charAt(b);
      if (c1 != c2 && toLower(c1) != toLower(c2))
        return false; 
    } 
    return true;
  }
  
  static int caseIgnoreCompare(String paramString1, String paramString2) { return (paramString1 == paramString2) ? 0 : toLowerString(paramString1).compareTo(toLowerString(paramString2)); }
  
  static char toUpper(char paramChar) { return isLower(paramChar) ? (char)(paramChar - ' ') : paramChar; }
  
  static char toLower(char paramChar) { return isUpper(paramChar) ? (char)(paramChar + ' ') : paramChar; }
  
  public static String toLowerString(String paramString) {
    int i = paramString.length();
    byte b1;
    for (b1 = 0; b1 < i && !isUpper(paramString.charAt(b1)); b1++);
    if (b1 == i)
      return paramString; 
    char[] arrayOfChar = new char[i];
    for (byte b2 = 0; b2 < i; b2++) {
      char c = paramString.charAt(b2);
      arrayOfChar[b2] = (b2 < b1) ? c : toLower(c);
    } 
    return new String(arrayOfChar);
  }
  
  static String toUpperString(String paramString) {
    int i = paramString.length();
    byte b1;
    for (b1 = 0; b1 < i && !isLower(paramString.charAt(b1)); b1++);
    if (b1 == i)
      return paramString; 
    char[] arrayOfChar = new char[i];
    for (byte b2 = 0; b2 < i; b2++) {
      char c = paramString.charAt(b2);
      arrayOfChar[b2] = (b2 < b1) ? c : toUpper(c);
    } 
    return new String(arrayOfChar);
  }
  
  static String toTitleString(String paramString) {
    int i;
    if ((i = paramString.length()) == 0)
      return paramString; 
    byte b1 = 0;
    if (!isLower(paramString.charAt(b1)))
      for (b1 = 1; b1 < i && !isUpper(paramString.charAt(b1)); b1++); 
    if (b1 == i)
      return paramString; 
    char[] arrayOfChar = new char[i];
    for (byte b2 = 0; b2 < i; b2++) {
      char c = paramString.charAt(b2);
      if (b2 == 0 && b1 == 0) {
        arrayOfChar[b2] = toUpper(c);
      } else if (b2 < b1) {
        arrayOfChar[b2] = c;
      } else {
        arrayOfChar[b2] = toLower(c);
      } 
    } 
    return new String(arrayOfChar);
  }
  
  private static boolean isUpper(char paramChar) { return (paramChar >= 'A' && paramChar <= 'Z'); }
  
  private static boolean isLower(char paramChar) { return (paramChar >= 'a' && paramChar <= 'z'); }
  
  static boolean isAlpha(char paramChar) { return ((paramChar >= 'A' && paramChar <= 'Z') || (paramChar >= 'a' && paramChar <= 'z')); }
  
  static boolean isAlphaString(String paramString) {
    int i = paramString.length();
    for (byte b = 0; b < i; b++) {
      if (!isAlpha(paramString.charAt(b)))
        return false; 
    } 
    return true;
  }
  
  static boolean isNumeric(char paramChar) { return (paramChar >= '0' && paramChar <= '9'); }
  
  static boolean isNumericString(String paramString) {
    int i = paramString.length();
    for (byte b = 0; b < i; b++) {
      if (!isNumeric(paramString.charAt(b)))
        return false; 
    } 
    return true;
  }
  
  static boolean isAlphaNumeric(char paramChar) { return (isAlpha(paramChar) || isNumeric(paramChar)); }
  
  public static boolean isAlphaNumericString(String paramString) {
    int i = paramString.length();
    for (byte b = 0; b < i; b++) {
      if (!isAlphaNumeric(paramString.charAt(b)))
        return false; 
    } 
    return true;
  }
  
  static boolean isEmpty(String paramString) { return (paramString == null || paramString.length() == 0); }
  
  static boolean isEmpty(Set<?> paramSet) { return (paramSet == null || paramSet.isEmpty()); }
  
  static boolean isEmpty(Map<?, ?> paramMap) { return (paramMap == null || paramMap.isEmpty()); }
  
  static boolean isEmpty(List<?> paramList) { return (paramList == null || paramList.isEmpty()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\locale\LocaleUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */