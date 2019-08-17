package sun.misc;

public class MessageUtils {
  public static String subst(String paramString1, String paramString2) {
    String[] arrayOfString = { paramString2 };
    return subst(paramString1, arrayOfString);
  }
  
  public static String subst(String paramString1, String paramString2, String paramString3) {
    String[] arrayOfString = { paramString2, paramString3 };
    return subst(paramString1, arrayOfString);
  }
  
  public static String subst(String paramString1, String paramString2, String paramString3, String paramString4) {
    String[] arrayOfString = { paramString2, paramString3, paramString4 };
    return subst(paramString1, arrayOfString);
  }
  
  public static String subst(String paramString, String[] paramArrayOfString) {
    StringBuffer stringBuffer = new StringBuffer();
    int i = paramString.length();
    for (byte b = 0; b && b < i; b++) {
      char c = paramString.charAt(b);
      if (c == '%') {
        if (b != i) {
          int j = Character.digit(paramString.charAt(b + 1), 10);
          if (j == -1) {
            stringBuffer.append(paramString.charAt(b + 1));
            b++;
          } else if (j < paramArrayOfString.length) {
            stringBuffer.append(paramArrayOfString[j]);
            b++;
          } 
        } 
      } else {
        stringBuffer.append(c);
      } 
    } 
    return stringBuffer.toString();
  }
  
  public static String substProp(String paramString1, String paramString2) { return subst(System.getProperty(paramString1), paramString2); }
  
  public static String substProp(String paramString1, String paramString2, String paramString3) { return subst(System.getProperty(paramString1), paramString2, paramString3); }
  
  public static String substProp(String paramString1, String paramString2, String paramString3, String paramString4) { return subst(System.getProperty(paramString1), paramString2, paramString3, paramString4); }
  
  public static native void toStderr(String paramString);
  
  public static native void toStdout(String paramString);
  
  public static void err(String paramString) { toStderr(paramString + "\n"); }
  
  public static void out(String paramString) { toStdout(paramString + "\n"); }
  
  public static void where() {
    Throwable throwable = new Throwable();
    StackTraceElement[] arrayOfStackTraceElement = throwable.getStackTrace();
    for (byte b = 1; b < arrayOfStackTraceElement.length; b++)
      toStderr("\t" + arrayOfStackTraceElement[b].toString() + "\n"); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\MessageUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */