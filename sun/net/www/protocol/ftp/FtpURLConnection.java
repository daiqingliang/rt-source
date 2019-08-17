package sun.net.www.protocol.ftp;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketPermission;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.util.StringTokenizer;
import sun.net.ProgressMonitor;
import sun.net.ProgressSource;
import sun.net.ftp.FtpClient;
import sun.net.ftp.FtpLoginException;
import sun.net.ftp.FtpProtocolException;
import sun.net.www.MessageHeader;
import sun.net.www.MeteredStream;
import sun.net.www.ParseUtil;
import sun.net.www.URLConnection;
import sun.net.www.protocol.http.HttpURLConnection;
import sun.security.action.GetPropertyAction;

public class FtpURLConnection extends URLConnection {
  HttpURLConnection http = null;
  
  private Proxy instProxy;
  
  InputStream is = null;
  
  OutputStream os = null;
  
  FtpClient ftp = null;
  
  Permission permission;
  
  String password;
  
  String user;
  
  String host;
  
  String pathname;
  
  String filename;
  
  String fullpath;
  
  int port;
  
  static final int NONE = 0;
  
  static final int ASCII = 1;
  
  static final int BIN = 2;
  
  static final int DIR = 3;
  
  int type = 0;
  
  private int connectTimeout = -1;
  
  private int readTimeout = -1;
  
  public FtpURLConnection(URL paramURL) { this(paramURL, null); }
  
  FtpURLConnection(URL paramURL, Proxy paramProxy) {
    super(paramURL);
    this.instProxy = paramProxy;
    this.host = paramURL.getHost();
    this.port = paramURL.getPort();
    String str = paramURL.getUserInfo();
    if (str != null) {
      int i = str.indexOf(':');
      if (i == -1) {
        this.user = ParseUtil.decode(str);
        this.password = null;
      } else {
        this.user = ParseUtil.decode(str.substring(0, i++));
        this.password = ParseUtil.decode(str.substring(i));
      } 
    } 
  }
  
  private void setTimeouts() {
    if (this.ftp != null) {
      if (this.connectTimeout >= 0)
        this.ftp.setConnectTimeout(this.connectTimeout); 
      if (this.readTimeout >= 0)
        this.ftp.setReadTimeout(this.readTimeout); 
    } 
  }
  
  public void connect() {
    if (this.connected)
      return; 
    proxy = null;
    if (this.instProxy == null) {
      ProxySelector proxySelector = (ProxySelector)AccessController.doPrivileged(new PrivilegedAction<ProxySelector>() {
            public ProxySelector run() { return ProxySelector.getDefault(); }
          });
      if (proxySelector != null) {
        URI uRI = ParseUtil.toURI(this.url);
        for (Proxy proxy : proxySelector.select(uRI)) {
          if (proxy == null || proxy == Proxy.NO_PROXY || proxy.type() == Proxy.Type.SOCKS)
            break; 
          if (proxy.type() != Proxy.Type.HTTP || !(proxy.address() instanceof InetSocketAddress)) {
            proxySelector.connectFailed(uRI, proxy.address(), new IOException("Wrong proxy type"));
            continue;
          } 
          InetSocketAddress inetSocketAddress = (InetSocketAddress)proxy.address();
          try {
            this.http = new HttpURLConnection(this.url, proxy);
            this.http.setDoInput(getDoInput());
            this.http.setDoOutput(getDoOutput());
            if (this.connectTimeout >= 0)
              this.http.setConnectTimeout(this.connectTimeout); 
            if (this.readTimeout >= 0)
              this.http.setReadTimeout(this.readTimeout); 
            this.http.connect();
            this.connected = true;
            return;
          } catch (IOException iOException) {
            proxySelector.connectFailed(uRI, inetSocketAddress, iOException);
            this.http = null;
          } 
        } 
      } 
    } else {
      proxy = this.instProxy;
      if (proxy.type() == Proxy.Type.HTTP) {
        this.http = new HttpURLConnection(this.url, this.instProxy);
        this.http.setDoInput(getDoInput());
        this.http.setDoOutput(getDoOutput());
        if (this.connectTimeout >= 0)
          this.http.setConnectTimeout(this.connectTimeout); 
        if (this.readTimeout >= 0)
          this.http.setReadTimeout(this.readTimeout); 
        this.http.connect();
        this.connected = true;
        return;
      } 
    } 
    if (this.user == null) {
      this.user = "anonymous";
      String str = (String)AccessController.doPrivileged(new GetPropertyAction("java.version"));
      this.password = (String)AccessController.doPrivileged(new GetPropertyAction("ftp.protocol.user", "Java" + str + "@"));
    } 
    try {
      this.ftp = FtpClient.create();
      if (proxy != null)
        this.ftp.setProxy(proxy); 
      setTimeouts();
      if (this.port != -1) {
        this.ftp.connect(new InetSocketAddress(this.host, this.port));
      } else {
        this.ftp.connect(new InetSocketAddress(this.host, FtpClient.defaultPort()));
      } 
    } catch (UnknownHostException unknownHostException) {
      throw unknownHostException;
    } catch (FtpProtocolException ftpProtocolException) {
      if (this.ftp != null)
        try {
          this.ftp.close();
        } catch (IOException iOException) {
          ftpProtocolException.addSuppressed(iOException);
        }  
      throw new IOException(ftpProtocolException);
    } 
    try {
      this.ftp.login(this.user, (this.password == null) ? null : this.password.toCharArray());
    } catch (FtpProtocolException ftpProtocolException) {
      this.ftp.close();
      throw new FtpLoginException("Invalid username/password");
    } 
    this.connected = true;
  }
  
  private void decodePath(String paramString) {
    int i = paramString.indexOf(";type=");
    if (i >= 0) {
      String str = paramString.substring(i + 6, paramString.length());
      if ("i".equalsIgnoreCase(str))
        this.type = 2; 
      if ("a".equalsIgnoreCase(str))
        this.type = 1; 
      if ("d".equalsIgnoreCase(str))
        this.type = 3; 
      paramString = paramString.substring(0, i);
    } 
    if (paramString != null && paramString.length() > 1 && paramString.charAt(0) == '/')
      paramString = paramString.substring(1); 
    if (paramString == null || paramString.length() == 0)
      paramString = "./"; 
    if (!paramString.endsWith("/")) {
      i = paramString.lastIndexOf('/');
      if (i > 0) {
        this.filename = paramString.substring(i + 1, paramString.length());
        this.filename = ParseUtil.decode(this.filename);
        this.pathname = paramString.substring(0, i);
      } else {
        this.filename = ParseUtil.decode(paramString);
        this.pathname = null;
      } 
    } else {
      this.pathname = paramString.substring(0, paramString.length() - 1);
      this.filename = null;
    } 
    if (this.pathname != null) {
      this.fullpath = this.pathname + "/" + ((this.filename != null) ? this.filename : "");
    } else {
      this.fullpath = this.filename;
    } 
  }
  
  private void cd(String paramString) {
    if (paramString == null || paramString.isEmpty())
      return; 
    if (paramString.indexOf('/') == -1) {
      this.ftp.changeDirectory(ParseUtil.decode(paramString));
      return;
    } 
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, "/");
    while (stringTokenizer.hasMoreTokens())
      this.ftp.changeDirectory(ParseUtil.decode(stringTokenizer.nextToken())); 
  }
  
  public InputStream getInputStream() throws IOException {
    if (!this.connected)
      connect(); 
    if (this.http != null)
      return this.http.getInputStream(); 
    if (this.os != null)
      throw new IOException("Already opened for output"); 
    if (this.is != null)
      return this.is; 
    MessageHeader messageHeader = new MessageHeader();
    boolean bool = false;
    try {
      decodePath(this.url.getPath());
      if (this.filename == null || this.type == 3) {
        this.ftp.setAsciiType();
        cd(this.pathname);
        if (this.filename == null) {
          this.is = new FtpInputStream(this.ftp, this.ftp.list(null));
        } else {
          this.is = new FtpInputStream(this.ftp, this.ftp.nameList(this.filename));
        } 
      } else {
        if (this.type == 1) {
          this.ftp.setAsciiType();
        } else {
          this.ftp.setBinaryType();
        } 
        cd(this.pathname);
        this.is = new FtpInputStream(this.ftp, this.ftp.getFileStream(this.filename));
      } 
      try {
        long l = this.ftp.getLastTransferSize();
        messageHeader.add("content-length", Long.toString(l));
        if (l > 0L) {
          boolean bool1 = ProgressMonitor.getDefault().shouldMeterInput(this.url, "GET");
          ProgressSource progressSource = null;
          if (bool1) {
            progressSource = new ProgressSource(this.url, "GET", l);
            progressSource.beginTracking();
          } 
          this.is = new MeteredStream(this.is, progressSource, l);
        } 
      } catch (Exception exception) {
        exception.printStackTrace();
      } 
      if (bool) {
        messageHeader.add("content-type", "text/plain");
        messageHeader.add("access-type", "directory");
      } else {
        messageHeader.add("access-type", "file");
        String str = guessContentTypeFromName(this.fullpath);
        if (str == null && this.is.markSupported())
          str = guessContentTypeFromStream(this.is); 
        if (str != null)
          messageHeader.add("content-type", str); 
      } 
    } catch (FileNotFoundException fileNotFoundException) {
      try {
        cd(this.fullpath);
        this.ftp.setAsciiType();
        this.is = new FtpInputStream(this.ftp, this.ftp.list(null));
        messageHeader.add("content-type", "text/plain");
        messageHeader.add("access-type", "directory");
      } catch (IOException iOException) {
        FileNotFoundException fileNotFoundException1 = new FileNotFoundException(this.fullpath);
        if (this.ftp != null)
          try {
            this.ftp.close();
          } catch (IOException iOException1) {
            fileNotFoundException1.addSuppressed(iOException1);
          }  
        throw fileNotFoundException1;
      } catch (FtpProtocolException ftpProtocolException) {
        FileNotFoundException fileNotFoundException1 = new FileNotFoundException(this.fullpath);
        if (this.ftp != null)
          try {
            this.ftp.close();
          } catch (IOException iOException) {
            fileNotFoundException1.addSuppressed(iOException);
          }  
        throw fileNotFoundException1;
      } 
    } catch (FtpProtocolException ftpProtocolException) {
      if (this.ftp != null)
        try {
          this.ftp.close();
        } catch (IOException iOException) {
          ftpProtocolException.addSuppressed(iOException);
        }  
      throw new IOException(ftpProtocolException);
    } 
    setProperties(messageHeader);
    return this.is;
  }
  
  public OutputStream getOutputStream() throws IOException {
    if (!this.connected)
      connect(); 
    if (this.http != null) {
      OutputStream outputStream = this.http.getOutputStream();
      this.http.getInputStream();
      return outputStream;
    } 
    if (this.is != null)
      throw new IOException("Already opened for input"); 
    if (this.os != null)
      return this.os; 
    decodePath(this.url.getPath());
    if (this.filename == null || this.filename.length() == 0)
      throw new IOException("illegal filename for a PUT"); 
    try {
      if (this.pathname != null)
        cd(this.pathname); 
      if (this.type == 1) {
        this.ftp.setAsciiType();
      } else {
        this.ftp.setBinaryType();
      } 
      this.os = new FtpOutputStream(this.ftp, this.ftp.putFileStream(this.filename, false));
    } catch (FtpProtocolException ftpProtocolException) {
      throw new IOException(ftpProtocolException);
    } 
    return this.os;
  }
  
  String guessContentTypeFromFilename(String paramString) { return guessContentTypeFromName(paramString); }
  
  public Permission getPermission() {
    if (this.permission == null) {
      int i = this.url.getPort();
      i = (i < 0) ? FtpClient.defaultPort() : i;
      String str = this.host + ":" + i;
      this.permission = new SocketPermission(str, "connect");
    } 
    return this.permission;
  }
  
  public void setRequestProperty(String paramString1, String paramString2) {
    super.setRequestProperty(paramString1, paramString2);
    if ("type".equals(paramString1))
      if ("i".equalsIgnoreCase(paramString2)) {
        this.type = 2;
      } else if ("a".equalsIgnoreCase(paramString2)) {
        this.type = 1;
      } else if ("d".equalsIgnoreCase(paramString2)) {
        this.type = 3;
      } else {
        throw new IllegalArgumentException("Value of '" + paramString1 + "' request property was '" + paramString2 + "' when it must be either 'i', 'a' or 'd'");
      }  
  }
  
  public String getRequestProperty(String paramString) {
    String str = super.getRequestProperty(paramString);
    if (str == null && "type".equals(paramString))
      str = (this.type == 1) ? "a" : ((this.type == 3) ? "d" : "i"); 
    return str;
  }
  
  public void setConnectTimeout(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("timeouts can't be negative"); 
    this.connectTimeout = paramInt;
  }
  
  public int getConnectTimeout() { return (this.connectTimeout < 0) ? 0 : this.connectTimeout; }
  
  public void setReadTimeout(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("timeouts can't be negative"); 
    this.readTimeout = paramInt;
  }
  
  public int getReadTimeout() { return (this.readTimeout < 0) ? 0 : this.readTimeout; }
  
  protected class FtpInputStream extends FilterInputStream {
    FtpClient ftp;
    
    FtpInputStream(FtpClient param1FtpClient, InputStream param1InputStream) {
      super(new BufferedInputStream(param1InputStream));
      this.ftp = param1FtpClient;
    }
    
    public void close() {
      super.close();
      if (this.ftp != null)
        this.ftp.close(); 
    }
  }
  
  protected class FtpOutputStream extends FilterOutputStream {
    FtpClient ftp;
    
    FtpOutputStream(FtpClient param1FtpClient, OutputStream param1OutputStream) {
      super(param1OutputStream);
      this.ftp = param1FtpClient;
    }
    
    public void close() {
      super.close();
      if (this.ftp != null)
        this.ftp.close(); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\protocol\ftp\FtpURLConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */