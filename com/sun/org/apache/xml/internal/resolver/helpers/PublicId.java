package com.sun.org.apache.xml.internal.resolver.helpers;

public abstract class PublicId {
  public static String normalize(String paramString) {
    String str = paramString.replace('\t', ' ');
    str = str.replace('\r', ' ');
    str = str.replace('\n', ' ');
    int i;
    for (str = str.trim(); (i = str.indexOf("  ")) >= 0; str = str.substring(0, i) + str.substring(i + 1));
    return str;
  }
  
  public static String encodeURN(String paramString) {
    String str = normalize(paramString);
    str = stringReplace(str, "%", "%25");
    str = stringReplace(str, ";", "%3B");
    str = stringReplace(str, "'", "%27");
    str = stringReplace(str, "?", "%3F");
    str = stringReplace(str, "#", "%23");
    str = stringReplace(str, "+", "%2B");
    str = stringReplace(str, " ", "+");
    str = stringReplace(str, "::", ";");
    str = stringReplace(str, ":", "%3A");
    str = stringReplace(str, "//", ":");
    str = stringReplace(str, "/", "%2F");
    return "urn:publicid:" + str;
  }
  
  public static String decodeURN(String paramString) {
    null = "";
    if (paramString.startsWith("urn:publicid:")) {
      null = paramString.substring(13);
    } else {
      return paramString;
    } 
    null = stringReplace(null, "%2F", "/");
    null = stringReplace(null, ":", "//");
    null = stringReplace(null, "%3A", ":");
    null = stringReplace(null, ";", "::");
    null = stringReplace(null, "+", " ");
    null = stringReplace(null, "%2B", "+");
    null = stringReplace(null, "%23", "#");
    null = stringReplace(null, "%3F", "?");
    null = stringReplace(null, "%27", "'");
    null = stringReplace(null, "%3B", ";");
    return stringReplace(null, "%25", "%");
  }
  
  private static String stringReplace(String paramString1, String paramString2, String paramString3) {
    String str = "";
    int i;
    for (i = paramString1.indexOf(paramString2); i >= 0; i = paramString1.indexOf(paramString2)) {
      str = str + paramString1.substring(0, i);
      str = str + paramString3;
      paramString1 = paramString1.substring(i + 1);
    } 
    return str + paramString1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\resolver\helpers\PublicId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */