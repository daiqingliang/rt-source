package sun.net;

public class URLCanonicalizer {
  public String canonicalize(String paramString) {
    String str = paramString;
    if (paramString.startsWith("ftp.")) {
      str = "ftp://" + paramString;
    } else if (paramString.startsWith("gopher.")) {
      str = "gopher://" + paramString;
    } else if (paramString.startsWith("/")) {
      str = "file:" + paramString;
    } else if (!hasProtocolName(paramString)) {
      if (isSimpleHostName(paramString))
        paramString = "www." + paramString + ".com"; 
      str = "http://" + paramString;
    } 
    return str;
  }
  
  public boolean hasProtocolName(String paramString) {
    int i = paramString.indexOf(':');
    if (i <= 0)
      return false; 
    byte b = 0;
    while (b < i) {
      char c = paramString.charAt(b);
      if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c == '-') {
        b++;
        continue;
      } 
      return false;
    } 
    return true;
  }
  
  protected boolean isSimpleHostName(String paramString) {
    byte b = 0;
    while (b < paramString.length()) {
      char c = paramString.charAt(b);
      if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '-') {
        b++;
        continue;
      } 
      return false;
    } 
    return true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\URLCanonicalizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */