package sun.security.util;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import sun.net.www.ParseUtil;

public class PropertyExpander {
  public static String expand(String paramString) throws ExpandException { return expand(paramString, false); }
  
  public static String expand(String paramString, boolean paramBoolean) throws ExpandException {
    if (paramString == null)
      return null; 
    int i = paramString.indexOf("${", 0);
    if (i == -1)
      return paramString; 
    StringBuffer stringBuffer = new StringBuffer(paramString.length());
    int j = paramString.length();
    int k = 0;
    while (i < j) {
      if (i > k) {
        stringBuffer.append(paramString.substring(k, i));
        k = i;
      } 
      int m = i + 2;
      if (m < j && paramString.charAt(m) == '{') {
        m = paramString.indexOf("}}", m);
        if (m == -1 || m + 2 == j) {
          stringBuffer.append(paramString.substring(i));
          break;
        } 
        stringBuffer.append(paramString.substring(i, ++m + 1));
      } else {
        while (m < j && paramString.charAt(m) != '}')
          m++; 
        if (m == j) {
          stringBuffer.append(paramString.substring(i, m));
          break;
        } 
        String str = paramString.substring(i + 2, m);
        if (str.equals("/")) {
          stringBuffer.append(File.separatorChar);
        } else {
          String str1 = System.getProperty(str);
          if (str1 != null) {
            if (paramBoolean)
              try {
                if (stringBuffer.length() > 0 || !(new URI(str1)).isAbsolute())
                  str1 = ParseUtil.encodePath(str1); 
              } catch (URISyntaxException uRISyntaxException) {
                str1 = ParseUtil.encodePath(str1);
              }  
            stringBuffer.append(str1);
          } else {
            throw new ExpandException("unable to expand property " + str);
          } 
        } 
      } 
      k = m + 1;
      i = paramString.indexOf("${", k);
      if (i == -1) {
        if (k < j)
          stringBuffer.append(paramString.substring(k, j)); 
        break;
      } 
    } 
    return stringBuffer.toString();
  }
  
  public static class ExpandException extends GeneralSecurityException {
    private static final long serialVersionUID = -7941948581406161702L;
    
    public ExpandException(String param1String) { super(param1String); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\securit\\util\PropertyExpander.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */