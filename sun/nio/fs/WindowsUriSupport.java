package sun.nio.fs;

import java.net.URI;
import java.net.URISyntaxException;

class WindowsUriSupport {
  private static final String IPV6_LITERAL_SUFFIX = ".ipv6-literal.net";
  
  private static URI toUri(String paramString, boolean paramBoolean1, boolean paramBoolean2) {
    String str2;
    String str1;
    if (paramBoolean1) {
      int i = paramString.indexOf('\\', 2);
      str1 = paramString.substring(2, i);
      str2 = paramString.substring(i).replace('\\', '/');
      if (str1.endsWith(".ipv6-literal.net"))
        str1 = str1.substring(0, str1.length() - ".ipv6-literal.net".length()).replace('-', ':').replace('s', '%'); 
    } else {
      str1 = "";
      str2 = "/" + paramString.replace('\\', '/');
    } 
    if (paramBoolean2)
      str2 = str2 + "/"; 
    try {
      return new URI("file", str1, str2, null);
    } catch (URISyntaxException uRISyntaxException) {
      if (!paramBoolean1)
        throw new AssertionError(uRISyntaxException); 
      str2 = "//" + paramString.replace('\\', '/');
      if (paramBoolean2)
        str2 = str2 + "/"; 
      try {
        return new URI("file", null, str2, null);
      } catch (URISyntaxException uRISyntaxException) {
        throw new AssertionError(uRISyntaxException);
      } 
    } 
  }
  
  static URI toUri(WindowsPath paramWindowsPath) {
    paramWindowsPath = paramWindowsPath.toAbsolutePath();
    String str = paramWindowsPath.toString();
    boolean bool = false;
    if (!str.endsWith("\\"))
      try {
        bool = WindowsFileAttributes.get(paramWindowsPath, true).isDirectory();
      } catch (WindowsException windowsException) {} 
    return toUri(str, paramWindowsPath.isUnc(), bool);
  }
  
  static WindowsPath fromUri(WindowsFileSystem paramWindowsFileSystem, URI paramURI) {
    if (!paramURI.isAbsolute())
      throw new IllegalArgumentException("URI is not absolute"); 
    if (paramURI.isOpaque())
      throw new IllegalArgumentException("URI is not hierarchical"); 
    String str1 = paramURI.getScheme();
    if (str1 == null || !str1.equalsIgnoreCase("file"))
      throw new IllegalArgumentException("URI scheme is not \"file\""); 
    if (paramURI.getFragment() != null)
      throw new IllegalArgumentException("URI has a fragment component"); 
    if (paramURI.getQuery() != null)
      throw new IllegalArgumentException("URI has a query component"); 
    String str2 = paramURI.getPath();
    if (str2.equals(""))
      throw new IllegalArgumentException("URI path component is empty"); 
    String str3 = paramURI.getAuthority();
    if (str3 != null && !str3.equals("")) {
      String str = paramURI.getHost();
      if (str == null)
        throw new IllegalArgumentException("URI authority component has undefined host"); 
      if (paramURI.getUserInfo() != null)
        throw new IllegalArgumentException("URI authority component has user-info"); 
      if (paramURI.getPort() != -1)
        throw new IllegalArgumentException("URI authority component has port number"); 
      if (str.startsWith("[")) {
        str = str.substring(1, str.length() - 1).replace(':', '-').replace('%', 's');
        str = str + ".ipv6-literal.net";
      } 
      str2 = "\\\\" + str + str2;
    } else if (str2.length() > 2 && str2.charAt(2) == ':') {
      str2 = str2.substring(1);
    } 
    return WindowsPath.parse(paramWindowsFileSystem, str2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\fs\WindowsUriSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */