package sun.misc;

import java.util.Comparator;

public class ASCIICaseInsensitiveComparator extends Object implements Comparator<String> {
  public static final Comparator<String> CASE_INSENSITIVE_ORDER = new ASCIICaseInsensitiveComparator();
  
  public int compare(String paramString1, String paramString2) {
    int i = paramString1.length();
    int j = paramString2.length();
    int k = (i < j) ? i : j;
    for (byte b = 0; b < k; b++) {
      char c1 = paramString1.charAt(b);
      char c2 = paramString2.charAt(b);
      assert c1 <= '' && c2 <= '';
      if (c1 != c2) {
        c1 = (char)toLower(c1);
        c2 = (char)toLower(c2);
        if (c1 != c2)
          return c1 - c2; 
      } 
    } 
    return i - j;
  }
  
  public static int lowerCaseHashCode(String paramString) {
    int i = 0;
    int j = paramString.length();
    for (byte b = 0; b < j; b++)
      i = 31 * i + toLower(paramString.charAt(b)); 
    return i;
  }
  
  static boolean isLower(int paramInt) { return ((paramInt - 97 | 122 - paramInt) >= 0); }
  
  static boolean isUpper(int paramInt) { return ((paramInt - 65 | 90 - paramInt) >= 0); }
  
  static int toLower(int paramInt) { return isUpper(paramInt) ? (paramInt + 32) : paramInt; }
  
  static int toUpper(int paramInt) { return isLower(paramInt) ? (paramInt - 32) : paramInt; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\ASCIICaseInsensitiveComparator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */