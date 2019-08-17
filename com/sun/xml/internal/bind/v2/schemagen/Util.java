package com.sun.xml.internal.bind.v2.schemagen;

public final class Util {
  public static String escapeURI(String paramString) {
    StringBuilder stringBuilder = new StringBuilder();
    for (byte b = 0; b < paramString.length(); b++) {
      char c = paramString.charAt(b);
      if (Character.isSpaceChar(c)) {
        stringBuilder.append("%20");
      } else {
        stringBuilder.append(c);
      } 
    } 
    return stringBuilder.toString();
  }
  
  public static String getParentUriPath(String paramString) {
    int i = paramString.lastIndexOf('/');
    if (paramString.endsWith("/")) {
      paramString = paramString.substring(0, i);
      i = paramString.lastIndexOf('/');
    } 
    return paramString.substring(0, i) + "/";
  }
  
  public static String normalizeUriPath(String paramString) {
    if (paramString.endsWith("/"))
      return paramString; 
    int i = paramString.lastIndexOf('/');
    return paramString.substring(0, i + 1);
  }
  
  public static boolean equalsIgnoreCase(String paramString1, String paramString2) { return (paramString1 == paramString2) ? true : ((paramString1 != null && paramString2 != null) ? paramString1.equalsIgnoreCase(paramString2) : 0); }
  
  public static boolean equal(String paramString1, String paramString2) { return (paramString1 == paramString2) ? true : ((paramString1 != null && paramString2 != null) ? paramString1.equals(paramString2) : 0); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\schemagen\Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */