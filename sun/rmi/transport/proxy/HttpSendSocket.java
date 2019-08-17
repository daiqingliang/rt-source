package sun.rmi.transport.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketImpl;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.security.AccessController;
import sun.rmi.runtime.Log;
import sun.security.action.GetPropertyAction;

class HttpSendSocket extends Socket implements RMISocketInfo {
  protected String host;
  
  protected int port;
  
  protected URL url;
  
  protected URLConnection conn = null;
  
  protected InputStream in = null;
  
  protected OutputStream out = null;
  
  protected HttpSendInputStream inNotifier;
  
  protected HttpSendOutputStream outNotifier;
  
  private String lineSeparator = (String)AccessController.doPrivileged(new GetPropertyAction("line.separator"));
  
  public HttpSendSocket(String paramString, int paramInt, URL paramURL) throws IOException {
    super((SocketImpl)null);
    if (RMIMasterSocketFactory.proxyLog.isLoggable(Log.VERBOSE))
      RMIMasterSocketFactory.proxyLog.log(Log.VERBOSE, "host = " + paramString + ", port = " + paramInt + ", url = " + paramURL); 
    this.host = paramString;
    this.port = paramInt;
    this.url = paramURL;
    this.inNotifier = new HttpSendInputStream(null, this);
    this.outNotifier = new HttpSendOutputStream(writeNotify(), this);
  }
  
  public HttpSendSocket(String paramString, int paramInt) throws IOException { this(paramString, paramInt, new URL("http", paramString, paramInt, "/")); }
  
  public HttpSendSocket(InetAddress paramInetAddress, int paramInt) throws IOException { this(paramInetAddress.getHostName(), paramInt); }
  
  public boolean isReusable() { return false; }
  
  public OutputStream writeNotify() throws IOException {
    if (this.conn != null)
      throw new IOException("attempt to write on HttpSendSocket after request has been sent"); 
    this.conn = this.url.openConnection();
    this.conn.setDoOutput(true);
    this.conn.setUseCaches(false);
    this.conn.setRequestProperty("Content-type", "application/octet-stream");
    this.inNotifier.deactivate();
    this.in = null;
    return this.out = this.conn.getOutputStream();
  }
  
  public InputStream readNotify() throws IOException {
    RMIMasterSocketFactory.proxyLog.log(Log.VERBOSE, "sending request and activating input stream");
    this.outNotifier.deactivate();
    this.out.close();
    this.out = null;
    try {
      this.in = this.conn.getInputStream();
    } catch (IOException iOException) {
      RMIMasterSocketFactory.proxyLog.log(Log.BRIEF, "failed to get input stream, exception: ", iOException);
      throw new IOException("HTTP request failed");
    } 
    String str = this.conn.getContentType();
    if (str == null || !this.conn.getContentType().equals("application/octet-stream")) {
      if (RMIMasterSocketFactory.proxyLog.isLoggable(Log.BRIEF)) {
        if (str == null) {
          str1 = "missing content type in response" + this.lineSeparator;
        } else {
          str1 = "invalid content type in response: " + str + this.lineSeparator;
        } 
        String str1 = str1 + "HttpSendSocket.readNotify: response body: ";
        try {
          BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.in));
          String str2;
          while ((str2 = bufferedReader.readLine()) != null)
            str1 = str1 + str2 + this.lineSeparator; 
        } catch (IOException iOException) {}
        RMIMasterSocketFactory.proxyLog.log(Log.BRIEF, str1);
      } 
      throw new IOException("HTTP request failed");
    } 
    return this.in;
  }
  
  public InetAddress getInetAddress() {
    try {
      return InetAddress.getByName(this.host);
    } catch (UnknownHostException unknownHostException) {
      return null;
    } 
  }
  
  public InetAddress getLocalAddress() {
    try {
      return InetAddress.getLocalHost();
    } catch (UnknownHostException unknownHostException) {
      return null;
    } 
  }
  
  public int getPort() { return this.port; }
  
  public int getLocalPort() { return -1; }
  
  public InputStream getInputStream() throws IOException { return this.inNotifier; }
  
  public OutputStream getOutputStream() throws IOException { return this.outNotifier; }
  
  public void setTcpNoDelay(boolean paramBoolean) throws SocketException {}
  
  public boolean getTcpNoDelay() { return false; }
  
  public void setSoLinger(boolean paramBoolean, int paramInt) throws SocketException {}
  
  public int getSoLinger() { return -1; }
  
  public void setSoTimeout(int paramInt) throws SocketException {}
  
  public int getSoTimeout() { return 0; }
  
  public void close() throws IOException {
    if (this.out != null)
      this.out.close(); 
  }
  
  public String toString() { return "HttpSendSocket[host=" + this.host + ",port=" + this.port + ",url=" + this.url + "]"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\proxy\HttpSendSocket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */