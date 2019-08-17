package java.net;

import java.io.IOException;
import sun.net.util.IPAddressUtil;

public abstract class URLStreamHandler {
  protected abstract URLConnection openConnection(URL paramURL) throws IOException;
  
  protected URLConnection openConnection(URL paramURL, Proxy paramProxy) throws IOException { throw new UnsupportedOperationException("Method not implemented."); }
  
  protected void parseURL(URL paramURL, String paramString, int paramInt1, int paramInt2) {
    String str1 = paramURL.getProtocol();
    String str2 = paramURL.getAuthority();
    String str3 = paramURL.getUserInfo();
    String str4 = paramURL.getHost();
    int i = paramURL.getPort();
    String str5 = paramURL.getPath();
    String str6 = paramURL.getQuery();
    String str7 = paramURL.getRef();
    boolean bool1 = false;
    boolean bool2 = false;
    if (paramInt1 < paramInt2) {
      int k = paramString.indexOf('?');
      bool2 = (k == paramInt1) ? 1 : 0;
      if (k != -1 && k < paramInt2) {
        str6 = paramString.substring(k + 1, paramInt2);
        if (paramInt2 > k)
          paramInt2 = k; 
        paramString = paramString.substring(0, k);
      } 
    } 
    int j = 0;
    boolean bool3 = (paramInt1 <= paramInt2 - 4 && paramString.charAt(paramInt1) == '/' && paramString.charAt(paramInt1 + 1) == '/' && paramString.charAt(paramInt1 + 2) == '/' && paramString.charAt(paramInt1 + 3) == '/') ? 1 : 0;
    if (!bool3 && paramInt1 <= paramInt2 - 2 && paramString.charAt(paramInt1) == '/' && paramString.charAt(paramInt1 + 1) == '/') {
      paramInt1 += 2;
      j = paramString.indexOf('/', paramInt1);
      if (j < 0 || j > paramInt2) {
        j = paramString.indexOf('?', paramInt1);
        if (j < 0 || j > paramInt2)
          j = paramInt2; 
      } 
      str4 = str2 = paramString.substring(paramInt1, j);
      int k = str2.indexOf('@');
      if (k != -1) {
        if (k != str2.lastIndexOf('@')) {
          str3 = null;
          str4 = null;
        } else {
          str3 = str2.substring(0, k);
          str4 = str2.substring(k + 1);
        } 
      } else {
        str3 = null;
      } 
      if (str4 != null) {
        if (str4.length() > 0 && str4.charAt(0) == '[') {
          if ((k = str4.indexOf(']')) > 2) {
            String str = str4;
            str4 = str.substring(0, k + 1);
            if (!IPAddressUtil.isIPv6LiteralAddress(str4.substring(1, k)))
              throw new IllegalArgumentException("Invalid host: " + str4); 
            i = -1;
            if (str.length() > k + 1)
              if (str.charAt(k + 1) == ':') {
                if (str.length() > ++k + 1)
                  i = Integer.parseInt(str.substring(k + 1)); 
              } else {
                throw new IllegalArgumentException("Invalid authority field: " + str2);
              }  
          } else {
            throw new IllegalArgumentException("Invalid authority field: " + str2);
          } 
        } else {
          k = str4.indexOf(':');
          i = -1;
          if (k >= 0) {
            if (str4.length() > k + 1)
              i = Integer.parseInt(str4.substring(k + 1)); 
            str4 = str4.substring(0, k);
          } 
        } 
      } else {
        str4 = "";
      } 
      if (i < -1)
        throw new IllegalArgumentException("Invalid port number :" + i); 
      paramInt1 = j;
      if (str2 != null && str2.length() > 0)
        str5 = ""; 
    } 
    if (str4 == null)
      str4 = ""; 
    if (paramInt1 < paramInt2) {
      if (paramString.charAt(paramInt1) == '/') {
        str5 = paramString.substring(paramInt1, paramInt2);
      } else if (str5 != null && str5.length() > 0) {
        bool1 = true;
        int k = str5.lastIndexOf('/');
        String str = "";
        if (k == -1 && str2 != null)
          str = "/"; 
        str5 = str5.substring(0, k + 1) + str + paramString.substring(paramInt1, paramInt2);
      } else {
        String str = (str2 != null) ? "/" : "";
        str5 = str + paramString.substring(paramInt1, paramInt2);
      } 
    } else if (bool2 && str5 != null) {
      int k = str5.lastIndexOf('/');
      if (k < 0)
        k = 0; 
      str5 = str5.substring(0, k) + "/";
    } 
    if (str5 == null)
      str5 = ""; 
    if (bool1) {
      while ((j = str5.indexOf("/./")) >= 0)
        str5 = str5.substring(0, j) + str5.substring(j + 2); 
      for (j = 0; (j = str5.indexOf("/../", j)) >= 0; j += 3) {
        if (j > 0 && (paramInt2 = str5.lastIndexOf('/', j - 1)) >= 0 && str5.indexOf("/../", paramInt2) != 0) {
          str5 = str5.substring(0, paramInt2) + str5.substring(j + 3);
          j = 0;
          continue;
        } 
      } 
      j = str5.indexOf("/..");
      while (str5.endsWith("/..") && (paramInt2 = str5.lastIndexOf('/', j - 1)) >= 0)
        str5 = str5.substring(0, paramInt2 + 1); 
      if (str5.startsWith("./") && str5.length() > 2)
        str5 = str5.substring(2); 
      if (str5.endsWith("/."))
        str5 = str5.substring(0, str5.length() - 1); 
    } 
    setURL(paramURL, str1, str4, i, str2, str3, str5, str6, str7);
  }
  
  protected int getDefaultPort() { return -1; }
  
  protected boolean equals(URL paramURL1, URL paramURL2) {
    String str1 = paramURL1.getRef();
    String str2 = paramURL2.getRef();
    return ((str1 == str2 || (str1 != null && str1.equals(str2))) && sameFile(paramURL1, paramURL2));
  }
  
  protected int hashCode(URL paramURL) {
    int i = 0;
    String str1 = paramURL.getProtocol();
    if (str1 != null)
      i += str1.hashCode(); 
    InetAddress inetAddress = getHostAddress(paramURL);
    if (inetAddress != null) {
      i += inetAddress.hashCode();
    } else {
      String str = paramURL.getHost();
      if (str != null)
        i += str.toLowerCase().hashCode(); 
    } 
    String str2 = paramURL.getFile();
    if (str2 != null)
      i += str2.hashCode(); 
    if (paramURL.getPort() == -1) {
      i += getDefaultPort();
    } else {
      i += paramURL.getPort();
    } 
    String str3 = paramURL.getRef();
    if (str3 != null)
      i += str3.hashCode(); 
    return i;
  }
  
  protected boolean sameFile(URL paramURL1, URL paramURL2) {
    if (paramURL1.getProtocol() != paramURL2.getProtocol() && (paramURL1.getProtocol() == null || !paramURL1.getProtocol().equalsIgnoreCase(paramURL2.getProtocol())))
      return false; 
    if (paramURL1.getFile() != paramURL2.getFile() && (paramURL1.getFile() == null || !paramURL1.getFile().equals(paramURL2.getFile())))
      return false; 
    int i = (paramURL1.getPort() != -1) ? paramURL1.getPort() : paramURL1.handler.getDefaultPort();
    int j = (paramURL2.getPort() != -1) ? paramURL2.getPort() : paramURL2.handler.getDefaultPort();
    return (i != j) ? false : (!!hostsEqual(paramURL1, paramURL2));
  }
  
  protected InetAddress getHostAddress(URL paramURL) {
    if (paramURL.hostAddress != null)
      return paramURL.hostAddress; 
    String str = paramURL.getHost();
    if (str == null || str.equals(""))
      return null; 
    try {
      paramURL.hostAddress = InetAddress.getByName(str);
    } catch (UnknownHostException unknownHostException) {
      return null;
    } catch (SecurityException securityException) {
      return null;
    } 
    return paramURL.hostAddress;
  }
  
  protected boolean hostsEqual(URL paramURL1, URL paramURL2) {
    InetAddress inetAddress1 = getHostAddress(paramURL1);
    InetAddress inetAddress2 = getHostAddress(paramURL2);
    return (inetAddress1 != null && inetAddress2 != null) ? inetAddress1.equals(inetAddress2) : ((paramURL1.getHost() != null && paramURL2.getHost() != null) ? paramURL1.getHost().equalsIgnoreCase(paramURL2.getHost()) : ((paramURL1.getHost() == null && paramURL2.getHost() == null) ? 1 : 0));
  }
  
  protected String toExternalForm(URL paramURL) {
    int i = paramURL.getProtocol().length() + 1;
    if (paramURL.getAuthority() != null && paramURL.getAuthority().length() > 0)
      i += 2 + paramURL.getAuthority().length(); 
    if (paramURL.getPath() != null)
      i += paramURL.getPath().length(); 
    if (paramURL.getQuery() != null)
      i += 1 + paramURL.getQuery().length(); 
    if (paramURL.getRef() != null)
      i += 1 + paramURL.getRef().length(); 
    StringBuffer stringBuffer = new StringBuffer(i);
    stringBuffer.append(paramURL.getProtocol());
    stringBuffer.append(":");
    if (paramURL.getAuthority() != null && paramURL.getAuthority().length() > 0) {
      stringBuffer.append("//");
      stringBuffer.append(paramURL.getAuthority());
    } 
    if (paramURL.getPath() != null)
      stringBuffer.append(paramURL.getPath()); 
    if (paramURL.getQuery() != null) {
      stringBuffer.append('?');
      stringBuffer.append(paramURL.getQuery());
    } 
    if (paramURL.getRef() != null) {
      stringBuffer.append("#");
      stringBuffer.append(paramURL.getRef());
    } 
    return stringBuffer.toString();
  }
  
  protected void setURL(URL paramURL, String paramString1, String paramString2, int paramInt, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7) {
    if (this != paramURL.handler)
      throw new SecurityException("handler for url different from this handler"); 
    paramURL.set(paramURL.getProtocol(), paramString2, paramInt, paramString3, paramString4, paramString5, paramString6, paramString7);
  }
  
  @Deprecated
  protected void setURL(URL paramURL, String paramString1, String paramString2, int paramInt, String paramString3, String paramString4) {
    String str1 = null;
    String str2 = null;
    if (paramString2 != null && paramString2.length() != 0) {
      str1 = (paramInt == -1) ? paramString2 : (paramString2 + ":" + paramInt);
      int i = paramString2.lastIndexOf('@');
      if (i != -1) {
        str2 = paramString2.substring(0, i);
        paramString2 = paramString2.substring(i + 1);
      } 
    } 
    String str3 = null;
    String str4 = null;
    if (paramString3 != null) {
      int i = paramString3.lastIndexOf('?');
      if (i != -1) {
        str4 = paramString3.substring(i + 1);
        str3 = paramString3.substring(0, i);
      } else {
        str3 = paramString3;
      } 
    } 
    setURL(paramURL, paramString1, paramString2, paramInt, str1, str2, str3, str4, paramString4);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\URLStreamHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */