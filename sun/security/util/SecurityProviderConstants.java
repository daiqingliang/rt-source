package sun.security.util;

import java.security.InvalidParameterException;
import java.util.regex.PatternSyntaxException;
import sun.security.action.GetPropertyAction;

public final class SecurityProviderConstants {
  private static final Debug debug = Debug.getInstance("jca", "ProviderConfig");
  
  public static final int DEF_DSA_KEY_SIZE;
  
  public static final int DEF_RSA_KEY_SIZE;
  
  public static final int DEF_DH_KEY_SIZE;
  
  public static final int DEF_EC_KEY_SIZE;
  
  private static final String KEY_LENGTH_PROP = "jdk.security.defaultKeySize";
  
  public static final int getDefDSASubprimeSize(int paramInt) {
    if (paramInt <= 1024)
      return 160; 
    if (paramInt == 2048)
      return 224; 
    if (paramInt == 3072)
      return 256; 
    throw new InvalidParameterException("Invalid DSA Prime Size: " + paramInt);
  }
  
  static  {
    String str = GetPropertyAction.privilegedGetProperty("jdk.security.defaultKeySize");
    int i = 2048;
    int j = 2048;
    int k = 2048;
    int m = 256;
    if (str != null)
      try {
        String[] arrayOfString1 = str.split(",");
        String[] arrayOfString2 = arrayOfString1;
        int n = arrayOfString2.length;
        for (byte b = 0;; b++) {
          if (b < n) {
            String str1 = arrayOfString2[b];
            String[] arrayOfString = str1.split(":");
            if (arrayOfString.length != 2) {
              if (debug != null)
                debug.println("Ignoring invalid pair in jdk.security.defaultKeySize property: " + str1); 
            } else {
              String str2 = arrayOfString[0].trim().toUpperCase();
              int i1 = -1;
              try {
                i1 = Integer.parseInt(arrayOfString[1].trim());
              } catch (NumberFormatException numberFormatException) {
                if (debug != null)
                  debug.println("Ignoring invalid value in jdk.security.defaultKeySize property: " + str1); 
              } 
              if (str2.equals("DSA")) {
                i = i1;
              } else if (str2.equals("RSA")) {
                j = i1;
              } else if (str2.equals("DH")) {
                k = i1;
              } else if (str2.equals("EC")) {
                m = i1;
              } else {
                if (debug != null)
                  debug.println("Ignoring unsupported algo in jdk.security.defaultKeySize property: " + str1); 
                b++;
              } 
              if (debug != null)
                debug.println("Overriding default " + str2 + " keysize with value from " + "jdk.security.defaultKeySize" + " property: " + i1); 
            } 
          } else {
            break;
          } 
        } 
      } catch (PatternSyntaxException patternSyntaxException) {
        if (debug != null)
          debug.println("Unexpected exception while parsing jdk.security.defaultKeySize property: " + patternSyntaxException); 
      }  
    DEF_DSA_KEY_SIZE = i;
    DEF_RSA_KEY_SIZE = j;
    DEF_DH_KEY_SIZE = k;
    DEF_EC_KEY_SIZE = m;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\securit\\util\SecurityProviderConstants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */