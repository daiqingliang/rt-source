package sun.net.www.protocol.mailto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketPermission;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.Permission;
import sun.net.smtp.SmtpClient;
import sun.net.www.MessageHeader;
import sun.net.www.ParseUtil;
import sun.net.www.URLConnection;

public class MailToURLConnection extends URLConnection {
  InputStream is = null;
  
  OutputStream os = null;
  
  SmtpClient client;
  
  Permission permission;
  
  private int connectTimeout = -1;
  
  private int readTimeout = -1;
  
  MailToURLConnection(URL paramURL) {
    super(paramURL);
    MessageHeader messageHeader = new MessageHeader();
    messageHeader.add("content-type", "text/html");
    setProperties(messageHeader);
  }
  
  String getFromAddress() {
    String str = System.getProperty("user.fromaddr");
    if (str == null) {
      str = System.getProperty("user.name");
      if (str != null) {
        String str1 = System.getProperty("mail.host");
        if (str1 == null)
          try {
            str1 = InetAddress.getLocalHost().getHostName();
          } catch (UnknownHostException unknownHostException) {} 
        str = str + "@" + str1;
      } else {
        str = "";
      } 
    } 
    return str;
  }
  
  public void connect() throws IOException {
    this.client = new SmtpClient(this.connectTimeout);
    this.client.setReadTimeout(this.readTimeout);
  }
  
  public OutputStream getOutputStream() throws IOException {
    if (this.os != null)
      return this.os; 
    if (this.is != null)
      throw new IOException("Cannot write output after reading input."); 
    connect();
    String str = ParseUtil.decode(this.url.getPath());
    this.client.from(getFromAddress());
    this.client.to(str);
    this.os = this.client.startMessage();
    return this.os;
  }
  
  public Permission getPermission() throws IOException {
    if (this.permission == null) {
      connect();
      String str = this.client.getMailHost() + ":" + '\031';
      this.permission = new SocketPermission(str, "connect");
    } 
    return this.permission;
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
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\protocol\mailto\MailToURLConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */