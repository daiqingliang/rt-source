package com.sun.org.apache.xml.internal.utils;

import java.io.File;
import javax.xml.transform.TransformerException;

public class SystemIDResolver {
  public static String getAbsoluteURIFromRelative(String paramString) {
    String str2;
    if (paramString == null || paramString.length() == 0)
      return ""; 
    String str1 = paramString;
    if (!isAbsolutePath(paramString))
      try {
        str1 = getAbsolutePathFromRelativePath(paramString);
      } catch (SecurityException null) {
        return "file:" + paramString;
      }  
    if (null != str1) {
      if (str1.startsWith(File.separator)) {
        str2 = "file://" + str1;
      } else {
        str2 = "file:///" + str1;
      } 
    } else {
      str2 = "file:" + paramString;
    } 
    return replaceChars(str2);
  }
  
  private static String getAbsolutePathFromRelativePath(String paramString) { return (new File(paramString)).getAbsolutePath(); }
  
  public static boolean isAbsoluteURI(String paramString) {
    if (isWindowsAbsolutePath(paramString))
      return false; 
    int i = paramString.indexOf('#');
    int j = paramString.indexOf('?');
    int k = paramString.indexOf('/');
    int m = paramString.indexOf(':');
    int n = paramString.length() - 1;
    if (i > 0)
      n = i; 
    if (j > 0 && j < n)
      n = j; 
    if (k > 0 && k < n)
      n = k; 
    return (m > 0 && m < n);
  }
  
  public static boolean isAbsolutePath(String paramString) {
    if (paramString == null)
      return false; 
    File file = new File(paramString);
    return file.isAbsolute();
  }
  
  private static boolean isWindowsAbsolutePath(String paramString) { return !isAbsolutePath(paramString) ? false : ((paramString.length() > 2 && paramString.charAt(1) == ':' && Character.isLetter(paramString.charAt(0)) && (paramString.charAt(2) == '\\' || paramString.charAt(2) == '/'))); }
  
  private static String replaceChars(String paramString) {
    StringBuffer stringBuffer = new StringBuffer(paramString);
    int i = stringBuffer.length();
    for (byte b = 0; b < i; b++) {
      char c = stringBuffer.charAt(b);
      if (c == ' ') {
        stringBuffer.setCharAt(b, '%');
        stringBuffer.insert(b + 1, "20");
        i += 2;
        b += 2;
      } else if (c == '\\') {
        stringBuffer.setCharAt(b, '/');
      } 
    } 
    return stringBuffer.toString();
  }
  
  public static String getAbsoluteURI(String paramString) {
    String str = paramString;
    if (isAbsoluteURI(paramString)) {
      if (paramString.startsWith("file:")) {
        String str1 = paramString.substring(5);
        if (str1 != null && str1.startsWith("/")) {
          if (str1.startsWith("///") || !str1.startsWith("//")) {
            int i = paramString.indexOf(':', 5);
            if (i > 0) {
              String str2 = paramString.substring(i - 1);
              try {
                if (!isAbsolutePath(str2))
                  str = paramString.substring(0, i - 1) + getAbsolutePathFromRelativePath(str2); 
              } catch (SecurityException securityException) {
                return paramString;
              } 
            } 
          } 
        } else {
          return getAbsoluteURIFromRelative(paramString.substring(5));
        } 
        return replaceChars(str);
      } 
      return paramString;
    } 
    return getAbsoluteURIFromRelative(paramString);
  }
  
  public static String getAbsoluteURI(String paramString1, String paramString2) throws TransformerException {
    if (paramString2 == null)
      return getAbsoluteURI(paramString1); 
    String str = getAbsoluteURI(paramString2);
    URI uRI = null;
    try {
      URI uRI1 = new URI(str);
      uRI = new URI(uRI1, paramString1);
    } catch (MalformedURIException malformedURIException) {
      throw new TransformerException(malformedURIException);
    } 
    return replaceChars(uRI.toString());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\SystemIDResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */