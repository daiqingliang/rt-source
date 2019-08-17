package com.sun.xml.internal.ws.util;

import java.util.StringTokenizer;

public final class VersionUtil {
  public static final String JAXWS_VERSION_20 = "2.0";
  
  public static final String JAXWS_VERSION_DEFAULT = "2.0";
  
  public static boolean isVersion20(String paramString) { return "2.0".equals(paramString); }
  
  public static boolean isValidVersion(String paramString) { return isVersion20(paramString); }
  
  public static String getValidVersionString() { return "2.0"; }
  
  public static int[] getCanonicalVersion(String paramString) {
    int[] arrayOfInt = new int[4];
    arrayOfInt[0] = 1;
    arrayOfInt[1] = 1;
    arrayOfInt[2] = 0;
    arrayOfInt[3] = 0;
    String str1 = "_";
    String str2 = ".";
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, ".");
    String str3 = stringTokenizer.nextToken();
    arrayOfInt[0] = Integer.parseInt(str3);
    str3 = stringTokenizer.nextToken();
    if (str3.indexOf("_") == -1) {
      arrayOfInt[1] = Integer.parseInt(str3);
    } else {
      StringTokenizer stringTokenizer1 = new StringTokenizer(str3, "_");
      arrayOfInt[1] = Integer.parseInt(stringTokenizer1.nextToken());
      arrayOfInt[3] = Integer.parseInt(stringTokenizer1.nextToken());
    } 
    if (stringTokenizer.hasMoreTokens()) {
      str3 = stringTokenizer.nextToken();
      if (str3.indexOf("_") == -1) {
        arrayOfInt[2] = Integer.parseInt(str3);
        if (stringTokenizer.hasMoreTokens())
          arrayOfInt[3] = Integer.parseInt(stringTokenizer.nextToken()); 
      } else {
        StringTokenizer stringTokenizer1 = new StringTokenizer(str3, "_");
        arrayOfInt[2] = Integer.parseInt(stringTokenizer1.nextToken());
        arrayOfInt[3] = Integer.parseInt(stringTokenizer1.nextToken());
      } 
    } 
    return arrayOfInt;
  }
  
  public static int compare(String paramString1, String paramString2) {
    int[] arrayOfInt1 = getCanonicalVersion(paramString1);
    int[] arrayOfInt2 = getCanonicalVersion(paramString2);
    return (arrayOfInt1[0] < arrayOfInt2[0]) ? -1 : ((arrayOfInt1[0] > arrayOfInt2[0]) ? 1 : ((arrayOfInt1[1] < arrayOfInt2[1]) ? -1 : ((arrayOfInt1[1] > arrayOfInt2[1]) ? 1 : ((arrayOfInt1[2] < arrayOfInt2[2]) ? -1 : ((arrayOfInt1[2] > arrayOfInt2[2]) ? 1 : ((arrayOfInt1[3] < arrayOfInt2[3]) ? -1 : ((arrayOfInt1[3] > arrayOfInt2[3]) ? 1 : 0)))))));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\VersionUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */