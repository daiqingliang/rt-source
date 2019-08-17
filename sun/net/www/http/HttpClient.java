package sun.net.www.http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.CacheRequest;
import java.net.CookieHandler;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Locale;
import sun.net.NetworkClient;
import sun.net.ProgressSource;
import sun.net.www.HeaderParser;
import sun.net.www.MessageHeader;
import sun.net.www.MeteredStream;
import sun.net.www.ParseUtil;
import sun.net.www.URLConnection;
import sun.net.www.protocol.http.HttpURLConnection;
import sun.security.action.GetPropertyAction;
import sun.util.logging.PlatformLogger;

public class HttpClient extends NetworkClient {
  protected boolean cachedHttpClient = false;
  
  protected boolean inCache;
  
  MessageHeader requests;
  
  PosterOutputStream poster = null;
  
  boolean streaming;
  
  boolean failedOnce = false;
  
  private boolean ignoreContinue = true;
  
  private static final int HTTP_CONTINUE = 100;
  
  static final int httpPortNumber = 80;
  
  protected boolean proxyDisabled;
  
  public boolean usingProxy = false;
  
  protected String host;
  
  protected int port;
  
  protected static KeepAliveCache kac = new KeepAliveCache();
  
  private static boolean keepAliveProp = true;
  
  private static boolean retryPostProp = true;
  
  private static final boolean cacheNTLMProp;
  
  private static final boolean cacheSPNEGOProp;
  
  int keepAliveConnections = -1;
  
  int keepAliveTimeout = 0;
  
  private CacheRequest cacheRequest = null;
  
  protected URL url;
  
  public boolean reuse = false;
  
  private HttpCapture capture = null;
  
  private static final PlatformLogger logger = HttpURLConnection.getHttpLogger();
  
  protected int getDefaultPort() { return 80; }
  
  private static int getDefaultPort(String paramString) { return "http".equalsIgnoreCase(paramString) ? 80 : ("https".equalsIgnoreCase(paramString) ? 443 : -1); }
  
  private static void logFinest(String paramString) {
    if (logger.isLoggable(PlatformLogger.Level.FINEST))
      logger.finest(paramString); 
  }
  
  @Deprecated
  public static void resetProperties() {}
  
  int getKeepAliveTimeout() { return this.keepAliveTimeout; }
  
  public boolean getHttpKeepAliveSet() { return keepAliveProp; }
  
  protected HttpClient() {}
  
  private HttpClient(URL paramURL) throws IOException { this(paramURL, (String)null, -1, false); }
  
  protected HttpClient(URL paramURL, boolean paramBoolean) throws IOException { this(paramURL, null, -1, paramBoolean); }
  
  public HttpClient(URL paramURL, String paramString, int paramInt) throws IOException { this(paramURL, paramString, paramInt, false); }
  
  protected HttpClient(URL paramURL, Proxy paramProxy, int paramInt) throws IOException {
    this.proxy = (paramProxy == null) ? Proxy.NO_PROXY : paramProxy;
    this.host = paramURL.getHost();
    this.url = paramURL;
    this.port = paramURL.getPort();
    if (this.port == -1)
      this.port = getDefaultPort(); 
    setConnectTimeout(paramInt);
    this.capture = HttpCapture.getCapture(paramURL);
    openServer();
  }
  
  protected static Proxy newHttpProxy(String paramString1, int paramInt, String paramString2) {
    if (paramString1 == null || paramString2 == null)
      return Proxy.NO_PROXY; 
    int i = (paramInt < 0) ? getDefaultPort(paramString2) : paramInt;
    InetSocketAddress inetSocketAddress = InetSocketAddress.createUnresolved(paramString1, i);
    return new Proxy(Proxy.Type.HTTP, inetSocketAddress);
  }
  
  private HttpClient(URL paramURL, String paramString, int paramInt, boolean paramBoolean) throws IOException { this(paramURL, paramBoolean ? Proxy.NO_PROXY : newHttpProxy(paramString, paramInt, "http"), -1); }
  
  public HttpClient(URL paramURL, String paramString, int paramInt1, boolean paramBoolean, int paramInt2) throws IOException { this(paramURL, paramBoolean ? Proxy.NO_PROXY : newHttpProxy(paramString, paramInt1, "http"), paramInt2); }
  
  public static HttpClient New(URL paramURL) throws IOException { return New(paramURL, Proxy.NO_PROXY, -1, true, null); }
  
  public static HttpClient New(URL paramURL, boolean paramBoolean) throws IOException { return New(paramURL, Proxy.NO_PROXY, -1, paramBoolean, null); }
  
  public static HttpClient New(URL paramURL, Proxy paramProxy, int paramInt, boolean paramBoolean, HttpURLConnection paramHttpURLConnection) throws IOException {
    if (paramProxy == null)
      paramProxy = Proxy.NO_PROXY; 
    HttpClient httpClient = null;
    if (paramBoolean) {
      httpClient = kac.get(paramURL, null);
      if (httpClient != null && paramHttpURLConnection != null && paramHttpURLConnection.streaming() && paramHttpURLConnection.getRequestMethod() == "POST" && !httpClient.available()) {
        httpClient.inCache = false;
        httpClient.closeServer();
        httpClient = null;
      } 
      if (httpClient != null)
        if ((httpClient.proxy != null && httpClient.proxy.equals(paramProxy)) || (httpClient.proxy == null && paramProxy == null)) {
          synchronized (httpClient) {
            httpClient.cachedHttpClient = true;
            assert httpClient.inCache;
            httpClient.inCache = false;
            if (paramHttpURLConnection != null && httpClient.needsTunneling())
              paramHttpURLConnection.setTunnelState(HttpURLConnection.TunnelState.TUNNELING); 
            logFinest("KeepAlive stream retrieved from the cache, " + httpClient);
          } 
        } else {
          synchronized (httpClient) {
            httpClient.inCache = false;
            httpClient.closeServer();
          } 
          httpClient = null;
        }  
    } 
    if (httpClient == null) {
      httpClient = new HttpClient(paramURL, paramProxy, paramInt);
    } else {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        if (httpClient.proxy == Proxy.NO_PROXY || httpClient.proxy == null) {
          securityManager.checkConnect(InetAddress.getByName(paramURL.getHost()).getHostAddress(), paramURL.getPort());
        } else {
          securityManager.checkConnect(paramURL.getHost(), paramURL.getPort());
        }  
      httpClient.url = paramURL;
    } 
    return httpClient;
  }
  
  public static HttpClient New(URL paramURL, Proxy paramProxy, int paramInt, HttpURLConnection paramHttpURLConnection) throws IOException { return New(paramURL, paramProxy, paramInt, true, paramHttpURLConnection); }
  
  public static HttpClient New(URL paramURL, String paramString, int paramInt, boolean paramBoolean) throws IOException { return New(paramURL, newHttpProxy(paramString, paramInt, "http"), -1, paramBoolean, null); }
  
  public static HttpClient New(URL paramURL, String paramString, int paramInt1, boolean paramBoolean, int paramInt2, HttpURLConnection paramHttpURLConnection) throws IOException { return New(paramURL, newHttpProxy(paramString, paramInt1, "http"), paramInt2, paramBoolean, paramHttpURLConnection); }
  
  public void finished() {
    if (this.reuse)
      return; 
    this.keepAliveConnections--;
    this.poster = null;
    if (this.keepAliveConnections > 0 && isKeepingAlive() && !this.serverOutput.checkError()) {
      putInKeepAliveCache();
    } else {
      closeServer();
    } 
  }
  
  protected boolean available() {
    boolean bool = true;
    i = -1;
    try {
      try {
        i = this.serverSocket.getSoTimeout();
        this.serverSocket.setSoTimeout(1);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(this.serverSocket.getInputStream());
        int j = bufferedInputStream.read();
        if (j == -1) {
          logFinest("HttpClient.available(): read returned -1: not available");
          bool = false;
        } 
      } catch (SocketTimeoutException socketTimeoutException) {
        logFinest("HttpClient.available(): SocketTimeout: its available");
      } finally {
        if (i != -1)
          this.serverSocket.setSoTimeout(i); 
      } 
    } catch (IOException iOException) {
      logFinest("HttpClient.available(): SocketException: not available");
      bool = false;
    } 
    return bool;
  }
  
  protected void putInKeepAliveCache() {
    if (this.inCache) {
      assert false : "Duplicate put to keep alive cache";
      return;
    } 
    this.inCache = true;
    kac.put(this.url, null, this);
  }
  
  protected boolean isInKeepAliveCache() { return this.inCache; }
  
  public void closeIdleConnection() {
    HttpClient httpClient = kac.get(this.url, null);
    if (httpClient != null)
      httpClient.closeServer(); 
  }
  
  public void openServer(String paramString, int paramInt) throws IOException {
    this.serverSocket = doConnect(paramString, paramInt);
    try {
      OutputStream outputStream = this.serverSocket.getOutputStream();
      if (this.capture != null)
        outputStream = new HttpCaptureOutputStream(outputStream, this.capture); 
      this.serverOutput = new PrintStream(new BufferedOutputStream(outputStream), false, encoding);
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new InternalError(encoding + " encoding not found", unsupportedEncodingException);
    } 
    this.serverSocket.setTcpNoDelay(true);
  }
  
  public boolean needsTunneling() { return false; }
  
  public boolean isCachedConnection() { return this.cachedHttpClient; }
  
  public void afterConnect() {}
  
  private void privilegedOpenServer(final InetSocketAddress server) throws IOException {
    try {
      AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws IOException {
              HttpClient.this.openServer(server.getHostString(), server.getPort());
              return null;
            }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      throw (IOException)privilegedActionException.getException();
    } 
  }
  
  private void superOpenServer(String paramString, int paramInt) throws IOException { super.openServer(paramString, paramInt); }
  
  protected void openServer() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkConnect(this.host, this.port); 
    if (this.keepingAlive)
      return; 
    if (this.url.getProtocol().equals("http") || this.url.getProtocol().equals("https")) {
      if (this.proxy != null && this.proxy.type() == Proxy.Type.HTTP) {
        URLConnection.setProxiedHost(this.host);
        privilegedOpenServer((InetSocketAddress)this.proxy.address());
        this.usingProxy = true;
        return;
      } 
      openServer(this.host, this.port);
      this.usingProxy = false;
      return;
    } 
    if (this.proxy != null && this.proxy.type() == Proxy.Type.HTTP) {
      URLConnection.setProxiedHost(this.host);
      privilegedOpenServer((InetSocketAddress)this.proxy.address());
      this.usingProxy = true;
      return;
    } 
    super.openServer(this.host, this.port);
    this.usingProxy = false;
  }
  
  public String getURLFile() throws IOException {
    String str;
    if (this.usingProxy && !this.proxyDisabled) {
      StringBuffer stringBuffer = new StringBuffer(128);
      stringBuffer.append(this.url.getProtocol());
      stringBuffer.append(":");
      if (this.url.getAuthority() != null && this.url.getAuthority().length() > 0) {
        stringBuffer.append("//");
        stringBuffer.append(this.url.getAuthority());
      } 
      if (this.url.getPath() != null)
        stringBuffer.append(this.url.getPath()); 
      if (this.url.getQuery() != null) {
        stringBuffer.append('?');
        stringBuffer.append(this.url.getQuery());
      } 
      str = stringBuffer.toString();
    } else {
      str = this.url.getFile();
      if (str == null || str.length() == 0) {
        str = "/";
      } else if (str.charAt(0) == '?') {
        str = "/" + str;
      } 
    } 
    if (str.indexOf('\n') == -1)
      return str; 
    throw new MalformedURLException("Illegal character in URL");
  }
  
  @Deprecated
  public void writeRequests(MessageHeader paramMessageHeader) {
    this.requests = paramMessageHeader;
    this.requests.print(this.serverOutput);
    this.serverOutput.flush();
  }
  
  public void writeRequests(MessageHeader paramMessageHeader, PosterOutputStream paramPosterOutputStream) throws IOException {
    this.requests = paramMessageHeader;
    this.requests.print(this.serverOutput);
    this.poster = paramPosterOutputStream;
    if (this.poster != null)
      this.poster.writeTo(this.serverOutput); 
    this.serverOutput.flush();
  }
  
  public void writeRequests(MessageHeader paramMessageHeader, PosterOutputStream paramPosterOutputStream, boolean paramBoolean) throws IOException {
    this.streaming = paramBoolean;
    writeRequests(paramMessageHeader, paramPosterOutputStream);
  }
  
  public boolean parseHTTP(MessageHeader paramMessageHeader, ProgressSource paramProgressSource, HttpURLConnection paramHttpURLConnection) throws IOException {
    try {
      this.serverInput = this.serverSocket.getInputStream();
      if (this.capture != null)
        this.serverInput = new HttpCaptureInputStream(this.serverInput, this.capture); 
      this.serverInput = new BufferedInputStream(this.serverInput);
      return parseHTTPHeader(paramMessageHeader, paramProgressSource, paramHttpURLConnection);
    } catch (SocketTimeoutException socketTimeoutException) {
      if (this.ignoreContinue)
        closeServer(); 
      throw socketTimeoutException;
    } catch (IOException iOException) {
      closeServer();
      this.cachedHttpClient = false;
      if (!this.failedOnce && this.requests != null) {
        this.failedOnce = true;
        if (!getRequestMethod().equals("CONNECT") && !this.streaming && (!paramHttpURLConnection.getRequestMethod().equals("POST") || retryPostProp)) {
          openServer();
          if (needsTunneling()) {
            MessageHeader messageHeader = this.requests;
            paramHttpURLConnection.doTunneling();
            this.requests = messageHeader;
          } 
          afterConnect();
          writeRequests(this.requests, this.poster);
          return parseHTTP(paramMessageHeader, paramProgressSource, paramHttpURLConnection);
        } 
      } 
      throw iOException;
    } 
  }
  
  private boolean parseHTTPHeader(MessageHeader paramMessageHeader, ProgressSource paramProgressSource, HttpURLConnection paramHttpURLConnection) throws IOException {
    this.keepAliveConnections = -1;
    this.keepAliveTimeout = 0;
    boolean bool = false;
    byte[] arrayOfByte = new byte[8];
    try {
      int j = 0;
      this.serverInput.mark(10);
      while (j < 8) {
        int k = this.serverInput.read(arrayOfByte, j, 8 - j);
        if (k < 0)
          break; 
        j += k;
      } 
      String str1 = null;
      String str2 = null;
      bool = (arrayOfByte[0] == 72 && arrayOfByte[1] == 84 && arrayOfByte[2] == 84 && arrayOfByte[3] == 80 && arrayOfByte[4] == 47 && arrayOfByte[5] == 49 && arrayOfByte[6] == 46);
      this.serverInput.reset();
      if (bool) {
        paramMessageHeader.parseHeader(this.serverInput);
        CookieHandler cookieHandler = paramHttpURLConnection.getCookieHandler();
        if (cookieHandler != null) {
          URI uRI = ParseUtil.toURI(this.url);
          if (uRI != null)
            cookieHandler.put(uRI, paramMessageHeader.getHeaders()); 
        } 
        if (this.usingProxy) {
          str1 = paramMessageHeader.findValue("Proxy-Connection");
          str2 = paramMessageHeader.findValue("Proxy-Authenticate");
        } 
        if (str1 == null) {
          str1 = paramMessageHeader.findValue("Connection");
          str2 = paramMessageHeader.findValue("WWW-Authenticate");
        } 
        boolean bool1 = !this.disableKeepAlive ? 1 : 0;
        if (bool1 && (!cacheNTLMProp || !cacheSPNEGOProp) && str2 != null) {
          str2 = str2.toLowerCase(Locale.US);
          if (!cacheNTLMProp)
            bool1 &= (!str2.startsWith("ntlm ") ? 1 : 0); 
          if (!cacheSPNEGOProp) {
            bool1 &= (!str2.startsWith("negotiate ") ? 1 : 0);
            bool1 &= (!str2.startsWith("kerberos ") ? 1 : 0);
          } 
        } 
        this.disableKeepAlive |= (!bool1);
        if (str1 != null && str1.toLowerCase(Locale.US).equals("keep-alive")) {
          if (this.disableKeepAlive) {
            this.keepAliveConnections = 1;
          } else {
            HeaderParser headerParser = new HeaderParser(paramMessageHeader.findValue("Keep-Alive"));
            this.keepAliveConnections = headerParser.findInt("max", this.usingProxy ? 50 : 5);
            this.keepAliveTimeout = headerParser.findInt("timeout", this.usingProxy ? 60 : 5);
          } 
        } else if (arrayOfByte[7] != 48) {
          if (str1 != null || this.disableKeepAlive) {
            this.keepAliveConnections = 1;
          } else {
            this.keepAliveConnections = 5;
          } 
        } 
      } else {
        if (j != 8) {
          if (!this.failedOnce && this.requests != null) {
            this.failedOnce = true;
            if (!getRequestMethod().equals("CONNECT") && !this.streaming && (!paramHttpURLConnection.getRequestMethod().equals("POST") || retryPostProp)) {
              closeServer();
              this.cachedHttpClient = false;
              openServer();
              if (needsTunneling()) {
                MessageHeader messageHeader = this.requests;
                paramHttpURLConnection.doTunneling();
                this.requests = messageHeader;
              } 
              afterConnect();
              writeRequests(this.requests, this.poster);
              return parseHTTP(paramMessageHeader, paramProgressSource, paramHttpURLConnection);
            } 
          } 
          throw new SocketException("Unexpected end of file from server");
        } 
        paramMessageHeader.set("Content-type", "unknown/unknown");
      } 
    } catch (IOException iOException) {
      throw iOException;
    } 
    int i = -1;
    try {
      String str1 = paramMessageHeader.getValue(0);
      int j;
      for (j = str1.indexOf(' '); str1.charAt(j) == ' '; j++);
      i = Integer.parseInt(str1.substring(j, j + 3));
    } catch (Exception exception) {}
    if (i == 100 && this.ignoreContinue) {
      paramMessageHeader.reset();
      return parseHTTPHeader(paramMessageHeader, paramProgressSource, paramHttpURLConnection);
    } 
    long l = -1L;
    String str = paramMessageHeader.findValue("Transfer-Encoding");
    if (str != null && str.equalsIgnoreCase("chunked")) {
      this.serverInput = new ChunkedInputStream(this.serverInput, this, paramMessageHeader);
      if (this.keepAliveConnections <= 1) {
        this.keepAliveConnections = 1;
        this.keepingAlive = false;
      } else {
        this.keepingAlive = !this.disableKeepAlive;
      } 
      this.failedOnce = false;
    } else {
      String str1 = paramMessageHeader.findValue("content-length");
      if (str1 != null)
        try {
          l = Long.parseLong(str1);
        } catch (NumberFormatException numberFormatException) {
          l = -1L;
        }  
      String str2 = this.requests.getKey(0);
      if ((str2 != null && str2.startsWith("HEAD")) || i == 304 || i == 204)
        l = 0L; 
      if (this.keepAliveConnections > 1 && (l >= 0L || i == 304 || i == 204)) {
        this.keepingAlive = !this.disableKeepAlive;
        this.failedOnce = false;
      } else if (this.keepingAlive) {
        this.keepingAlive = false;
      } 
    } 
    if (l > 0L) {
      if (paramProgressSource != null)
        paramProgressSource.setContentType(paramMessageHeader.findValue("content-type")); 
      boolean bool1 = (isKeepingAlive() || this.disableKeepAlive) ? 1 : 0;
      if (bool1) {
        logFinest("KeepAlive stream used: " + this.url);
        this.serverInput = new KeepAliveStream(this.serverInput, paramProgressSource, l, this);
        this.failedOnce = false;
      } else {
        this.serverInput = new MeteredStream(this.serverInput, paramProgressSource, l);
      } 
    } else if (l == -1L) {
      if (paramProgressSource != null) {
        paramProgressSource.setContentType(paramMessageHeader.findValue("content-type"));
        this.serverInput = new MeteredStream(this.serverInput, paramProgressSource, l);
      } 
    } else if (paramProgressSource != null) {
      paramProgressSource.finishTracking();
    } 
    return bool;
  }
  
  public InputStream getInputStream() { return this.serverInput; }
  
  public OutputStream getOutputStream() { return this.serverOutput; }
  
  public String toString() throws IOException { return getClass().getName() + "(" + this.url + ")"; }
  
  public final boolean isKeepingAlive() { return (getHttpKeepAliveSet() && this.keepingAlive); }
  
  public void setCacheRequest(CacheRequest paramCacheRequest) { this.cacheRequest = paramCacheRequest; }
  
  CacheRequest getCacheRequest() { return this.cacheRequest; }
  
  String getRequestMethod() throws IOException {
    if (this.requests != null) {
      String str = this.requests.getKey(0);
      if (str != null)
        return str.split("\\s+")[0]; 
    } 
    return "";
  }
  
  protected void finalize() {}
  
  public void setDoNotRetry(boolean paramBoolean) { this.failedOnce = paramBoolean; }
  
  public void setIgnoreContinue(boolean paramBoolean) { this.ignoreContinue = paramBoolean; }
  
  public void closeServer() {
    try {
      this.keepingAlive = false;
      this.serverSocket.close();
    } catch (Exception exception) {}
  }
  
  public String getProxyHostUsed() throws IOException { return !this.usingProxy ? null : ((InetSocketAddress)this.proxy.address()).getHostString(); }
  
  public int getProxyPortUsed() { return this.usingProxy ? ((InetSocketAddress)this.proxy.address()).getPort() : -1; }
  
  static  {
    String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("http.keepAlive"));
    String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.net.http.retryPost"));
    String str3 = (String)AccessController.doPrivileged(new GetPropertyAction("jdk.ntlm.cache"));
    String str4 = (String)AccessController.doPrivileged(new GetPropertyAction("jdk.spnego.cache"));
    if (str1 != null) {
      keepAliveProp = Boolean.valueOf(str1).booleanValue();
    } else {
      keepAliveProp = true;
    } 
    if (str2 != null) {
      retryPostProp = Boolean.valueOf(str2).booleanValue();
    } else {
      retryPostProp = true;
    } 
    if (str3 != null) {
      cacheNTLMProp = Boolean.parseBoolean(str3);
    } else {
      cacheNTLMProp = true;
    } 
    if (str4 != null) {
      cacheSPNEGOProp = Boolean.parseBoolean(str4);
    } else {
      cacheSPNEGOProp = true;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\http\HttpClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */