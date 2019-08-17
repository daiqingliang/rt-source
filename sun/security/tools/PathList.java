package sun.security.tools;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

public class PathList {
  public static String appendPath(String paramString1, String paramString2) { return (paramString1 == null || paramString1.length() == 0) ? paramString2 : ((paramString2 == null || paramString2.length() == 0) ? paramString1 : (paramString1 + File.pathSeparator + paramString2)); }
  
  public static URL[] pathToURLs(String paramString) {
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, File.pathSeparator);
    URL[] arrayOfURL = new URL[stringTokenizer.countTokens()];
    byte b = 0;
    while (stringTokenizer.hasMoreTokens()) {
      URL uRL = fileToURL(new File(stringTokenizer.nextToken()));
      if (uRL != null)
        arrayOfURL[b++] = uRL; 
    } 
    if (arrayOfURL.length != b) {
      URL[] arrayOfURL1 = new URL[b];
      System.arraycopy(arrayOfURL, 0, arrayOfURL1, 0, b);
      arrayOfURL = arrayOfURL1;
    } 
    return arrayOfURL;
  }
  
  private static URL fileToURL(File paramFile) {
    try {
      str = paramFile.getCanonicalPath();
    } catch (IOException iOException) {
      str = paramFile.getAbsolutePath();
    } 
    String str = str.replace(File.separatorChar, '/');
    if (!str.startsWith("/"))
      str = "/" + str; 
    if (!paramFile.isFile())
      str = str + "/"; 
    try {
      return new URL("file", "", str);
    } catch (MalformedURLException malformedURLException) {
      throw new IllegalArgumentException("file");
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\PathList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */