package java.net;

public abstract class Authenticator {
  private static Authenticator theAuthenticator;
  
  private String requestingHost;
  
  private InetAddress requestingSite;
  
  private int requestingPort;
  
  private String requestingProtocol;
  
  private String requestingPrompt;
  
  private String requestingScheme;
  
  private URL requestingURL;
  
  private RequestorType requestingAuthType;
  
  private void reset() {
    this.requestingHost = null;
    this.requestingSite = null;
    this.requestingPort = -1;
    this.requestingProtocol = null;
    this.requestingPrompt = null;
    this.requestingScheme = null;
    this.requestingURL = null;
    this.requestingAuthType = RequestorType.SERVER;
  }
  
  public static void setDefault(Authenticator paramAuthenticator) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      NetPermission netPermission = new NetPermission("setDefaultAuthenticator");
      securityManager.checkPermission(netPermission);
    } 
    theAuthenticator = paramAuthenticator;
  }
  
  public static PasswordAuthentication requestPasswordAuthentication(InetAddress paramInetAddress, int paramInt, String paramString1, String paramString2, String paramString3) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      NetPermission netPermission = new NetPermission("requestPasswordAuthentication");
      securityManager.checkPermission(netPermission);
    } 
    Authenticator authenticator = theAuthenticator;
    if (authenticator == null)
      return null; 
    synchronized (authenticator) {
      authenticator.reset();
      authenticator.requestingSite = paramInetAddress;
      authenticator.requestingPort = paramInt;
      authenticator.requestingProtocol = paramString1;
      authenticator.requestingPrompt = paramString2;
      authenticator.requestingScheme = paramString3;
      return authenticator.getPasswordAuthentication();
    } 
  }
  
  public static PasswordAuthentication requestPasswordAuthentication(String paramString1, InetAddress paramInetAddress, int paramInt, String paramString2, String paramString3, String paramString4) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      NetPermission netPermission = new NetPermission("requestPasswordAuthentication");
      securityManager.checkPermission(netPermission);
    } 
    Authenticator authenticator = theAuthenticator;
    if (authenticator == null)
      return null; 
    synchronized (authenticator) {
      authenticator.reset();
      authenticator.requestingHost = paramString1;
      authenticator.requestingSite = paramInetAddress;
      authenticator.requestingPort = paramInt;
      authenticator.requestingProtocol = paramString2;
      authenticator.requestingPrompt = paramString3;
      authenticator.requestingScheme = paramString4;
      return authenticator.getPasswordAuthentication();
    } 
  }
  
  public static PasswordAuthentication requestPasswordAuthentication(String paramString1, InetAddress paramInetAddress, int paramInt, String paramString2, String paramString3, String paramString4, URL paramURL, RequestorType paramRequestorType) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      NetPermission netPermission = new NetPermission("requestPasswordAuthentication");
      securityManager.checkPermission(netPermission);
    } 
    Authenticator authenticator = theAuthenticator;
    if (authenticator == null)
      return null; 
    synchronized (authenticator) {
      authenticator.reset();
      authenticator.requestingHost = paramString1;
      authenticator.requestingSite = paramInetAddress;
      authenticator.requestingPort = paramInt;
      authenticator.requestingProtocol = paramString2;
      authenticator.requestingPrompt = paramString3;
      authenticator.requestingScheme = paramString4;
      authenticator.requestingURL = paramURL;
      authenticator.requestingAuthType = paramRequestorType;
      return authenticator.getPasswordAuthentication();
    } 
  }
  
  protected final String getRequestingHost() { return this.requestingHost; }
  
  protected final InetAddress getRequestingSite() { return this.requestingSite; }
  
  protected final int getRequestingPort() { return this.requestingPort; }
  
  protected final String getRequestingProtocol() { return this.requestingProtocol; }
  
  protected final String getRequestingPrompt() { return this.requestingPrompt; }
  
  protected final String getRequestingScheme() { return this.requestingScheme; }
  
  protected PasswordAuthentication getPasswordAuthentication() { return null; }
  
  protected URL getRequestingURL() { return this.requestingURL; }
  
  protected RequestorType getRequestorType() { return this.requestingAuthType; }
  
  public enum RequestorType {
    PROXY, SERVER;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\Authenticator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */