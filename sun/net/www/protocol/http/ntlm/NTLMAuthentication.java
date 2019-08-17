package sun.net.www.protocol.http.ntlm;

import java.io.IOException;
import java.net.InetAddress;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.net.www.HeaderParser;
import sun.net.www.protocol.http.AuthScheme;
import sun.net.www.protocol.http.AuthenticationInfo;
import sun.net.www.protocol.http.HttpURLConnection;
import sun.security.action.GetPropertyAction;

public class NTLMAuthentication extends AuthenticationInfo {
  private static final long serialVersionUID = 100L;
  
  private static final NTLMAuthenticationCallback NTLMAuthCallback = NTLMAuthenticationCallback.getNTLMAuthenticationCallback();
  
  private String hostname;
  
  private static String defaultDomain = (String)AccessController.doPrivileged(new GetPropertyAction("http.auth.ntlm.domain", "domain"));
  
  private static final boolean ntlmCache;
  
  String username;
  
  String ntdomain;
  
  String password;
  
  private void init0() {
    this.hostname = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
          public String run() {
            String str;
            try {
              str = InetAddress.getLocalHost().getHostName().toUpperCase();
            } catch (UnknownHostException unknownHostException) {
              str = "localhost";
            } 
            return str;
          }
        });
    int i = this.hostname.indexOf('.');
    if (i != -1)
      this.hostname = this.hostname.substring(0, i); 
  }
  
  public NTLMAuthentication(boolean paramBoolean, URL paramURL, PasswordAuthentication paramPasswordAuthentication) {
    super(paramBoolean ? 112 : 115, AuthScheme.NTLM, paramURL, "");
    init(paramPasswordAuthentication);
  }
  
  private void init(PasswordAuthentication paramPasswordAuthentication) {
    this.pw = paramPasswordAuthentication;
    if (paramPasswordAuthentication != null) {
      String str = paramPasswordAuthentication.getUserName();
      int i = str.indexOf('\\');
      if (i == -1) {
        this.username = str;
        this.ntdomain = defaultDomain;
      } else {
        this.ntdomain = str.substring(0, i).toUpperCase();
        this.username = str.substring(i + 1);
      } 
      this.password = new String(paramPasswordAuthentication.getPassword());
    } else {
      this.username = null;
      this.ntdomain = null;
      this.password = null;
    } 
    init0();
  }
  
  public NTLMAuthentication(boolean paramBoolean, String paramString, int paramInt, PasswordAuthentication paramPasswordAuthentication) {
    super(paramBoolean ? 112 : 115, AuthScheme.NTLM, paramString, paramInt, "");
    init(paramPasswordAuthentication);
  }
  
  protected boolean useAuthCache() { return (ntlmCache && super.useAuthCache()); }
  
  public boolean supportsPreemptiveAuthorization() { return false; }
  
  public static boolean supportsTransparentAuth() { return true; }
  
  public static boolean isTrustedSite(URL paramURL) { return NTLMAuthCallback.isTrustedSite(paramURL); }
  
  public String getHeaderValue(URL paramURL, String paramString) { throw new RuntimeException("getHeaderValue not supported"); }
  
  public boolean isAuthorizationStale(String paramString) { return false; }
  
  public boolean setHeaders(HttpURLConnection paramHttpURLConnection, HeaderParser paramHeaderParser, String paramString) {
    try {
      NTLMAuthSequence nTLMAuthSequence = (NTLMAuthSequence)paramHttpURLConnection.authObj();
      if (nTLMAuthSequence == null) {
        nTLMAuthSequence = new NTLMAuthSequence(this.username, this.password, this.ntdomain);
        paramHttpURLConnection.authObj(nTLMAuthSequence);
      } 
      String str = "NTLM " + nTLMAuthSequence.getAuthHeader((paramString.length() > 6) ? paramString.substring(5) : null);
      paramHttpURLConnection.setAuthenticationProperty(getHeaderName(), str);
      if (nTLMAuthSequence.isComplete())
        paramHttpURLConnection.authObj(null); 
      return true;
    } catch (IOException iOException) {
      paramHttpURLConnection.authObj(null);
      return false;
    } 
  }
  
  static  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("jdk.ntlm.cache", "true"));
    ntlmCache = Boolean.parseBoolean(str);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\protocol\http\ntlm\NTLMAuthentication.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */