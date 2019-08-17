package java.rmi;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public final class Naming {
  public static Remote lookup(String paramString) throws NotBoundException, MalformedURLException, RemoteException {
    ParsedNamingURL parsedNamingURL = parseURL(paramString);
    Registry registry = getRegistry(parsedNamingURL);
    return (parsedNamingURL.name == null) ? registry : registry.lookup(parsedNamingURL.name);
  }
  
  public static void bind(String paramString, Remote paramRemote) throws AlreadyBoundException, MalformedURLException, RemoteException {
    ParsedNamingURL parsedNamingURL = parseURL(paramString);
    Registry registry = getRegistry(parsedNamingURL);
    if (paramRemote == null)
      throw new NullPointerException("cannot bind to null"); 
    registry.bind(parsedNamingURL.name, paramRemote);
  }
  
  public static void unbind(String paramString) throws RemoteException, NotBoundException, MalformedURLException {
    ParsedNamingURL parsedNamingURL = parseURL(paramString);
    Registry registry = getRegistry(parsedNamingURL);
    registry.unbind(parsedNamingURL.name);
  }
  
  public static void rebind(String paramString, Remote paramRemote) throws AlreadyBoundException, MalformedURLException, RemoteException {
    ParsedNamingURL parsedNamingURL = parseURL(paramString);
    Registry registry = getRegistry(parsedNamingURL);
    if (paramRemote == null)
      throw new NullPointerException("cannot bind to null"); 
    registry.rebind(parsedNamingURL.name, paramRemote);
  }
  
  public static String[] list(String paramString) throws RemoteException, MalformedURLException {
    ParsedNamingURL parsedNamingURL = parseURL(paramString);
    Registry registry = getRegistry(parsedNamingURL);
    String str = "";
    if (parsedNamingURL.port > 0 || !parsedNamingURL.host.equals(""))
      str = str + "//" + parsedNamingURL.host; 
    if (parsedNamingURL.port > 0)
      str = str + ":" + parsedNamingURL.port; 
    str = str + "/";
    String[] arrayOfString = registry.list();
    for (byte b = 0; b < arrayOfString.length; b++)
      arrayOfString[b] = str + arrayOfString[b]; 
    return arrayOfString;
  }
  
  private static Registry getRegistry(ParsedNamingURL paramParsedNamingURL) throws RemoteException { return LocateRegistry.getRegistry(paramParsedNamingURL.host, paramParsedNamingURL.port); }
  
  private static ParsedNamingURL parseURL(String paramString) throws MalformedURLException {
    try {
      return intParseURL(paramString);
    } catch (URISyntaxException uRISyntaxException) {
      MalformedURLException malformedURLException = new MalformedURLException("invalid URL String: " + paramString);
      malformedURLException.initCause(uRISyntaxException);
      int i = paramString.indexOf(':');
      int j = paramString.indexOf("//:");
      if (j < 0)
        throw malformedURLException; 
      if (j == 0 || (i > 0 && j == i + 1)) {
        int k = j + 2;
        String str = paramString.substring(0, k) + "localhost" + paramString.substring(k);
        try {
          return intParseURL(str);
        } catch (URISyntaxException uRISyntaxException1) {
          throw malformedURLException;
        } catch (MalformedURLException malformedURLException1) {
          throw malformedURLException1;
        } 
      } 
      throw malformedURLException;
    } 
  }
  
  private static ParsedNamingURL intParseURL(String paramString) throws MalformedURLException {
    URI uRI = new URI(paramString);
    if (uRI.isOpaque())
      throw new MalformedURLException("not a hierarchical URL: " + paramString); 
    if (uRI.getFragment() != null)
      throw new MalformedURLException("invalid character, '#', in URL name: " + paramString); 
    if (uRI.getQuery() != null)
      throw new MalformedURLException("invalid character, '?', in URL name: " + paramString); 
    if (uRI.getUserInfo() != null)
      throw new MalformedURLException("invalid character, '@', in URL host: " + paramString); 
    String str1 = uRI.getScheme();
    if (str1 != null && !str1.equals("rmi"))
      throw new MalformedURLException("invalid URL scheme: " + paramString); 
    String str2 = uRI.getPath();
    if (str2 != null) {
      if (str2.startsWith("/"))
        str2 = str2.substring(1); 
      if (str2.length() == 0)
        str2 = null; 
    } 
    String str3 = uRI.getHost();
    if (str3 == null) {
      str3 = "";
      try {
        uRI.parseServerAuthority();
      } catch (URISyntaxException uRISyntaxException) {
        String str = uRI.getAuthority();
        if (str != null && str.startsWith(":")) {
          str = "localhost" + str;
          try {
            uRI = new URI(null, str, null, null, null);
            uRI.parseServerAuthority();
          } catch (URISyntaxException uRISyntaxException1) {
            throw new MalformedURLException("invalid authority: " + paramString);
          } 
        } else {
          throw new MalformedURLException("invalid authority: " + paramString);
        } 
      } 
    } 
    int i = uRI.getPort();
    if (i == -1)
      i = 1099; 
    return new ParsedNamingURL(str3, i, str2);
  }
  
  private static class ParsedNamingURL {
    String host;
    
    int port;
    
    String name;
    
    ParsedNamingURL(String param1String1, int param1Int, String param1String2) {
      this.host = param1String1;
      this.port = param1Int;
      this.name = param1String2;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\rmi\Naming.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */