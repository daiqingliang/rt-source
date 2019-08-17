package com.sun.org.apache.xml.internal.utils;

public class XMLCharacterRecognizer {
  public static boolean isWhiteSpace(char paramChar) { return (paramChar == ' ' || paramChar == '\t' || paramChar == '\r' || paramChar == '\n'); }
  
  public static boolean isWhiteSpace(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    int i = paramInt1 + paramInt2;
    for (int j = paramInt1; j < i; j++) {
      if (!isWhiteSpace(paramArrayOfChar[j]))
        return false; 
    } 
    return true;
  }
  
  public static boolean isWhiteSpace(StringBuffer paramStringBuffer) {
    int i = paramStringBuffer.length();
    for (byte b = 0; b < i; b++) {
      if (!isWhiteSpace(paramStringBuffer.charAt(b)))
        return false; 
    } 
    return true;
  }
  
  public static boolean isWhiteSpace(String paramString) {
    if (null != paramString) {
      int i = paramString.length();
      for (byte b = 0; b < i; b++) {
        if (!isWhiteSpace(paramString.charAt(b)))
          return false; 
      } 
    } 
    return true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\XMLCharacterRecognizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */