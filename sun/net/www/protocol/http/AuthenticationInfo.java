package sun.net.www.protocol.http;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.security.AccessController;
import java.util.HashMap;
import sun.net.www.HeaderParser;
import sun.security.action.GetBooleanAction;

public abstract class AuthenticationInfo extends AuthCacheValue implements Cloneable {
  static final long serialVersionUID = -2588378268010453259L;
  
  public static final char SERVER_AUTHENTICATION = 's';
  
  public static final char PROXY_AUTHENTICATION = 'p';
  
  static final boolean serializeAuth = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("http.auth.serializeRequests"))).booleanValue();
  
  protected PasswordAuthentication pw;
  
  private static HashMap<String, Thread> requests = new HashMap();
  
  char type;
  
  AuthScheme authScheme;
  
  String protocol;
  
  String host;
  
  int port;
  
  String realm;
  
  String path;
  
  String s1;
  
  String s2;
  
  public PasswordAuthentication credentials() { return this.pw; }
  
  public AuthCacheValue.Type getAuthType() { return (this.type == 's') ? AuthCacheValue.Type.Server : AuthCacheValue.Type.Proxy; }
  
  AuthScheme getAuthScheme() { return this.authScheme; }
  
  public String getHost() { return this.host; }
  
  public int getPort() { return this.port; }
  
  public String getRealm() { return this.realm; }
  
  public String getPath() { return this.path; }
  
  public String getProtocolScheme() { return this.protocol; }
  
  protected boolean useAuthCache() { return true; }
  
  private static boolean requestIsInProgress(String paramString) {
    if (!serializeAuth)
      return false; 
    synchronized (requests) {
      Thread thread2 = Thread.currentThread();
      Thread thread1;
      if ((thread1 = (Thread)requests.get(paramString)) == null) {
        requests.put(paramString, thread2);
        return false;
      } 
      if (thread1 == thread2)
        return false; 
      while (requests.containsKey(paramString)) {
        try {
          requests.wait();
        } catch (InterruptedException interruptedException) {}
      } 
    } 
    return true;
  }
  
  private static void requestCompleted(String paramString) {
    synchronized (requests) {
      Thread thread;
      if (thread != null && thread == (thread = (Thread)requests.get(paramString)).currentThread()) {
        boolean bool = (requests.remove(paramString) != null) ? 1 : 0;
        assert bool;
      } 
      requests.notifyAll();
    } 
  }
  
  public AuthenticationInfo(char paramChar, AuthScheme paramAuthScheme, String paramString1, int paramInt, String paramString2) {
    this.type = paramChar;
    this.authScheme = paramAuthScheme;
    this.protocol = "";
    this.host = paramString1.toLowerCase();
    this.port = paramInt;
    this.realm = paramString2;
    this.path = null;
  }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      return null;
    } 
  }
  
  public AuthenticationInfo(char paramChar, AuthScheme paramAuthScheme, URL paramURL, String paramString) {
    this.type = paramChar;
    this.authScheme = paramAuthScheme;
    this.protocol = paramURL.getProtocol().toLowerCase();
    this.host = paramURL.getHost().toLowerCase();
    this.port = paramURL.getPort();
    if (this.port == -1)
      this.port = paramURL.getDefaultPort(); 
    this.realm = paramString;
    String str = paramURL.getPath();
    if (str.length() == 0) {
      this.path = str;
    } else {
      this.path = reducePath(str);
    } 
  }
  
  static String reducePath(String paramString) {
    int i = paramString.lastIndexOf('/');
    int j = paramString.lastIndexOf('.');
    return (i != -1) ? ((i < j) ? paramString.substring(0, i + 1) : paramString) : paramString;
  }
  
  static AuthenticationInfo getServerAuth(URL paramURL) {
    int i = paramURL.getPort();
    if (i == -1)
      i = paramURL.getDefaultPort(); 
    String str = "s:" + paramURL.getProtocol().toLowerCase() + ":" + paramURL.getHost().toLowerCase() + ":" + i;
    return getAuth(str, paramURL);
  }
  
  static String getServerAuthKey(URL paramURL, String paramString, AuthScheme paramAuthScheme) {
    int i = paramURL.getPort();
    if (i == -1)
      i = paramURL.getDefaultPort(); 
    return "s:" + paramAuthScheme + ":" + paramURL.getProtocol().toLowerCase() + ":" + paramURL.getHost().toLowerCase() + ":" + i + ":" + paramString;
  }
  
  static AuthenticationInfo getServerAuth(String paramString) {
    AuthenticationInfo authenticationInfo;
    if (authenticationInfo == null && (authenticationInfo = getAuth(paramString, null)).requestIsInProgress(paramString))
      authenticationInfo = getAuth(paramString, null); 
    return authenticationInfo;
  }
  
  static AuthenticationInfo getAuth(String paramString, URL paramURL) { return (paramURL == null) ? (AuthenticationInfo)cache.get(paramString, null) : (AuthenticationInfo)cache.get(paramString, paramURL.getPath()); }
  
  static AuthenticationInfo getProxyAuth(String paramString, int paramInt) {
    String str = "p::" + paramString.toLowerCase() + ":" + paramInt;
    return (AuthenticationInfo)cache.get(str, null);
  }
  
  static String getProxyAuthKey(String paramString1, int paramInt, String paramString2, AuthScheme paramAuthScheme) { return "p:" + paramAuthScheme + "::" + paramString1.toLowerCase() + ":" + paramInt + ":" + paramString2; }
  
  static AuthenticationInfo getProxyAuth(String paramString) {
    AuthenticationInfo authenticationInfo;
    if (authenticationInfo == null && (authenticationInfo = (AuthenticationInfo)cache.get(paramString, null)).requestIsInProgress(paramString))
      authenticationInfo = (AuthenticationInfo)cache.get(paramString, null); 
    return authenticationInfo;
  }
  
  void addToCache() {
    String str = cacheKey(true);
    if (useAuthCache()) {
      cache.put(str, this);
      if (supportsPreemptiveAuthorization())
        cache.put(cacheKey(false), this); 
    } 
    endAuthRequest(str);
  }
  
  static void endAuthRequest(String paramString) {
    if (!serializeAuth)
      return; 
    synchronized (requests) {
      requestCompleted(paramString);
    } 
  }
  
  void removeFromCache() {
    cache.remove(cacheKey(true), this);
    if (supportsPreemptiveAuthorization())
      cache.remove(cacheKey(false), this); 
  }
  
  public abstract boolean supportsPreemptiveAuthorization();
  
  public String getHeaderName() { return (this.type == 's') ? "Authorization" : "Proxy-authorization"; }
  
  public abstract String getHeaderValue(URL paramURL, String paramString);
  
  public abstract boolean setHeaders(HttpURLConnection paramHttpURLConnection, HeaderParser paramHeaderParser, String paramString);
  
  public abstract boolean isAuthorizationStale(String paramString);
  
  String cacheKey(boolean paramBoolean) { return paramBoolean ? (this.type + ":" + this.authScheme + ":" + this.protocol + ":" + this.host + ":" + this.port + ":" + this.realm) : (this.type + ":" + this.protocol + ":" + this.host + ":" + this.port); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    this.pw = new PasswordAuthentication(this.s1, this.s2.toCharArray());
    this.s1 = null;
    this.s2 = null;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    this.s1 = this.pw.getUserName();
    this.s2 = new String(this.pw.getPassword());
    paramObjectOutputStream.defaultWriteObject();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\protocol\http\AuthenticationInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */