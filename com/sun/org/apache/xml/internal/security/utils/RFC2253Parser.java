package com.sun.org.apache.xml.internal.security.utils;

import java.io.IOException;
import java.io.StringReader;

public class RFC2253Parser {
  public static String rfc2253toXMLdsig(String paramString) {
    String str = normalize(paramString, true);
    return rfctoXML(str);
  }
  
  public static String xmldsigtoRFC2253(String paramString) {
    String str = normalize(paramString, false);
    return xmltoRFC(str);
  }
  
  public static String normalize(String paramString) { return normalize(paramString, true); }
  
  public static String normalize(String paramString, boolean paramBoolean) {
    if (paramString == null || paramString.equals(""))
      return ""; 
    try {
      String str = semicolonToComma(paramString);
      StringBuilder stringBuilder = new StringBuilder();
      int i = 0;
      int j = 0;
      int k;
      int m;
      for (m = 0; (k = str.indexOf(',', m)) >= 0; m = k + 1) {
        j += countQuotes(str, m, k);
        if (k > 0 && str.charAt(k - 1) != '\\' && j % 2 == 0) {
          stringBuilder.append(parseRDN(str.substring(i, k).trim(), paramBoolean) + ",");
          i = k + 1;
          j = 0;
        } 
      } 
      stringBuilder.append(parseRDN(trim(str.substring(i)), paramBoolean));
      return stringBuilder.toString();
    } catch (IOException iOException) {
      return paramString;
    } 
  }
  
  static String parseRDN(String paramString, boolean paramBoolean) {
    StringBuilder stringBuilder = new StringBuilder();
    int i = 0;
    int j = 0;
    int k;
    int m;
    for (m = 0; (k = paramString.indexOf('+', m)) >= 0; m = k + 1) {
      j += countQuotes(paramString, m, k);
      if (k > 0 && paramString.charAt(k - 1) != '\\' && j % 2 == 0) {
        stringBuilder.append(parseATAV(trim(paramString.substring(i, k)), paramBoolean) + "+");
        i = k + 1;
        j = 0;
      } 
    } 
    stringBuilder.append(parseATAV(trim(paramString.substring(i)), paramBoolean));
    return stringBuilder.toString();
  }
  
  static String parseATAV(String paramString, boolean paramBoolean) {
    int i = paramString.indexOf('=');
    if (i == -1 || (i > 0 && paramString.charAt(i - 1) == '\\'))
      return paramString; 
    String str1 = normalizeAT(paramString.substring(0, i));
    String str2 = null;
    if (str1.charAt(0) >= '0' && str1.charAt(0) <= '9') {
      str2 = paramString.substring(i + 1);
    } else {
      str2 = normalizeV(paramString.substring(i + 1), paramBoolean);
    } 
    return str1 + "=" + str2;
  }
  
  static String normalizeAT(String paramString) {
    String str = paramString.toUpperCase().trim();
    if (str.startsWith("OID"))
      str = str.substring(3); 
    return str;
  }
  
  static String normalizeV(String paramString, boolean paramBoolean) {
    String str = trim(paramString);
    if (str.startsWith("\"")) {
      StringBuilder stringBuilder = new StringBuilder();
      StringReader stringReader = new StringReader(str.substring(1, str.length() - 1));
      int i = 0;
      while ((i = stringReader.read()) > -1) {
        char c = (char)i;
        if (c == ',' || c == '=' || c == '+' || c == '<' || c == '>' || c == '#' || c == ';')
          stringBuilder.append('\\'); 
        stringBuilder.append(c);
      } 
      str = trim(stringBuilder.toString());
    } 
    if (paramBoolean) {
      if (str.startsWith("#"))
        str = '\\' + str; 
    } else if (str.startsWith("\\#")) {
      str = str.substring(1);
    } 
    return str;
  }
  
  static String rfctoXML(String paramString) {
    try {
      String str = changeLess32toXML(paramString);
      return changeWStoXML(str);
    } catch (Exception exception) {
      return paramString;
    } 
  }
  
  static String xmltoRFC(String paramString) {
    try {
      String str = changeLess32toRFC(paramString);
      return changeWStoRFC(str);
    } catch (Exception exception) {
      return paramString;
    } 
  }
  
  static String changeLess32toRFC(String paramString) {
    StringBuilder stringBuilder = new StringBuilder();
    StringReader stringReader = new StringReader(paramString);
    int i = 0;
    while ((i = stringReader.read()) > -1) {
      char c = (char)i;
      if (c == '\\') {
        stringBuilder.append(c);
        char c1 = (char)stringReader.read();
        char c2 = (char)stringReader.read();
        if (((c1 >= '0' && c1 <= '9') || (c1 >= 'A' && c1 <= 'F') || (c1 >= 'a' && c1 <= 'f')) && ((c2 >= '0' && c2 <= '9') || (c2 >= 'A' && c2 <= 'F') || (c2 >= 'a' && c2 <= 'f'))) {
          char c3 = (char)Byte.parseByte("" + c1 + c2, 16);
          stringBuilder.append(c3);
          continue;
        } 
        stringBuilder.append(c1);
        stringBuilder.append(c2);
        continue;
      } 
      stringBuilder.append(c);
    } 
    return stringBuilder.toString();
  }
  
  static String changeLess32toXML(String paramString) {
    StringBuilder stringBuilder = new StringBuilder();
    StringReader stringReader = new StringReader(paramString);
    int i = 0;
    while ((i = stringReader.read()) > -1) {
      if (i < 32) {
        stringBuilder.append('\\');
        stringBuilder.append(Integer.toHexString(i));
        continue;
      } 
      stringBuilder.append((char)i);
    } 
    return stringBuilder.toString();
  }
  
  static String changeWStoXML(String paramString) {
    StringBuilder stringBuilder = new StringBuilder();
    StringReader stringReader = new StringReader(paramString);
    int i = 0;
    while ((i = stringReader.read()) > -1) {
      char c = (char)i;
      if (c == '\\') {
        char c1 = (char)stringReader.read();
        if (c1 == ' ') {
          stringBuilder.append('\\');
          String str = "20";
          stringBuilder.append(str);
          continue;
        } 
        stringBuilder.append('\\');
        stringBuilder.append(c1);
        continue;
      } 
      stringBuilder.append(c);
    } 
    return stringBuilder.toString();
  }
  
  static String changeWStoRFC(String paramString) {
    StringBuilder stringBuilder = new StringBuilder();
    int i = 0;
    int j;
    int k;
    for (k = 0; (j = paramString.indexOf("\\20", k)) >= 0; k = j + 3) {
      stringBuilder.append(trim(paramString.substring(i, j)) + "\\ ");
      i = j + 3;
    } 
    stringBuilder.append(paramString.substring(i));
    return stringBuilder.toString();
  }
  
  static String semicolonToComma(String paramString) { return removeWSandReplace(paramString, ";", ","); }
  
  static String removeWhiteSpace(String paramString1, String paramString2) { return removeWSandReplace(paramString1, paramString2, paramString2); }
  
  static String removeWSandReplace(String paramString1, String paramString2, String paramString3) {
    StringBuilder stringBuilder = new StringBuilder();
    int i = 0;
    int j = 0;
    int k;
    int m;
    for (m = 0; (k = paramString1.indexOf(paramString2, m)) >= 0; m = k + 1) {
      j += countQuotes(paramString1, m, k);
      if (k > 0 && paramString1.charAt(k - 1) != '\\' && j % 2 == 0) {
        stringBuilder.append(trim(paramString1.substring(i, k)) + paramString3);
        i = k + 1;
        j = 0;
      } 
    } 
    stringBuilder.append(trim(paramString1.substring(i)));
    return stringBuilder.toString();
  }
  
  private static int countQuotes(String paramString, int paramInt1, int paramInt2) {
    byte b = 0;
    for (int i = paramInt1; i < paramInt2; i++) {
      if (paramString.charAt(i) == '"')
        b++; 
    } 
    return b;
  }
  
  static String trim(String paramString) {
    String str = paramString.trim();
    int i = paramString.indexOf(str) + str.length();
    if (paramString.length() > i && str.endsWith("\\") && !str.endsWith("\\\\") && paramString.charAt(i) == ' ')
      str = str + " "; 
    return str;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\securit\\utils\RFC2253Parser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */