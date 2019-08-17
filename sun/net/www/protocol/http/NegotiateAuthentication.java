package sun.net.www.protocol.http;

import java.io.IOException;
import java.net.Authenticator;
import java.net.URL;
import java.security.AccessController;
import java.util.Base64;
import java.util.HashMap;
import sun.net.www.HeaderParser;
import sun.security.action.GetPropertyAction;
import sun.util.logging.PlatformLogger;

class NegotiateAuthentication extends AuthenticationInfo {
  private static final long serialVersionUID = 100L;
  
  private static final PlatformLogger logger = HttpURLConnection.getHttpLogger();
  
  private final HttpCallerInfo hci;
  
  static HashMap<String, Boolean> supported = null;
  
  static ThreadLocal<HashMap<String, Negotiator>> cache = null;
  
  private static final boolean cacheSPNEGO;
  
  private Negotiator negotiator = null;
  
  public NegotiateAuthentication(HttpCallerInfo paramHttpCallerInfo) {
    super((Authenticator.RequestorType.PROXY == paramHttpCallerInfo.authType) ? 112 : 115, paramHttpCallerInfo.scheme.equalsIgnoreCase("Negotiate") ? AuthScheme.NEGOTIATE : AuthScheme.KERBEROS, paramHttpCallerInfo.url, "");
    this.hci = paramHttpCallerInfo;
  }
  
  public boolean supportsPreemptiveAuthorization() { return false; }
  
  public static boolean isSupported(HttpCallerInfo paramHttpCallerInfo) {
    ClassLoader classLoader = null;
    try {
      classLoader = Thread.currentThread().getContextClassLoader();
    } catch (SecurityException securityException) {
      if (logger.isLoggable(PlatformLogger.Level.FINER))
        logger.finer("NegotiateAuthentication: Attempt to get the context class loader failed - " + securityException); 
    } 
    if (classLoader != null)
      synchronized (classLoader) {
        return isSupportedImpl(paramHttpCallerInfo);
      }  
    return isSupportedImpl(paramHttpCallerInfo);
  }
  
  private static boolean isSupportedImpl(HttpCallerInfo paramHttpCallerInfo) {
    if (supported == null)
      supported = new HashMap(); 
    String str = paramHttpCallerInfo.host;
    str = str.toLowerCase();
    if (supported.containsKey(str))
      return ((Boolean)supported.get(str)).booleanValue(); 
    Negotiator negotiator1 = Negotiator.getNegotiator(paramHttpCallerInfo);
    if (negotiator1 != null) {
      supported.put(str, Boolean.valueOf(true));
      if (cache == null)
        cache = new ThreadLocal<HashMap<String, Negotiator>>() {
            protected HashMap<String, Negotiator> initialValue() { return new HashMap(); }
          }; 
      ((HashMap)cache.get()).put(str, negotiator1);
      return true;
    } 
    supported.put(str, Boolean.valueOf(false));
    return false;
  }
  
  private static HashMap<String, Negotiator> getCache() { return (cache == null) ? null : (HashMap)cache.get(); }
  
  protected boolean useAuthCache() { return (super.useAuthCache() && cacheSPNEGO); }
  
  public String getHeaderValue(URL paramURL, String paramString) { throw new RuntimeException("getHeaderValue not supported"); }
  
  public boolean isAuthorizationStale(String paramString) { return false; }
  
  public boolean setHeaders(HttpURLConnection paramHttpURLConnection, HeaderParser paramHeaderParser, String paramString) {
    try {
      byte[] arrayOfByte = null;
      String[] arrayOfString = paramString.split("\\s+");
      if (arrayOfString.length > 1)
        arrayOfByte = Base64.getDecoder().decode(arrayOfString[1]); 
      String str = this.hci.scheme + " " + Base64.getEncoder().encodeToString((arrayOfByte == null) ? firstToken() : nextToken(arrayOfByte));
      paramHttpURLConnection.setAuthenticationProperty(getHeaderName(), str);
      return true;
    } catch (IOException iOException) {
      return false;
    } 
  }
  
  private byte[] firstToken() throws IOException {
    this.negotiator = null;
    HashMap hashMap = getCache();
    if (hashMap != null) {
      this.negotiator = (Negotiator)hashMap.get(getHost());
      if (this.negotiator != null)
        hashMap.remove(getHost()); 
    } 
    if (this.negotiator == null) {
      this.negotiator = Negotiator.getNegotiator(this.hci);
      if (this.negotiator == null) {
        IOException iOException = new IOException("Cannot initialize Negotiator");
        throw iOException;
      } 
    } 
    return this.negotiator.firstToken();
  }
  
  private byte[] nextToken(byte[] paramArrayOfByte) throws IOException { return this.negotiator.nextToken(paramArrayOfByte); }
  
  static  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("jdk.spnego.cache", "true"));
    cacheSPNEGO = Boolean.parseBoolean(str);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\protocol\http\NegotiateAuthentication.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */