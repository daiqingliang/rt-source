package sun.awt.image;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.Permission;
import sun.net.util.URLUtil;

public class URLImageSource extends InputStreamImageSource {
  URL url;
  
  URLConnection conn;
  
  String actualHost;
  
  int actualPort;
  
  public URLImageSource(URL paramURL) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      try {
        Permission permission = URLUtil.getConnectPermission(paramURL);
        if (permission != null)
          try {
            securityManager.checkPermission(permission);
          } catch (SecurityException securityException) {
            if (permission instanceof java.io.FilePermission && permission.getActions().indexOf("read") != -1) {
              securityManager.checkRead(permission.getName());
            } else if (permission instanceof java.net.SocketPermission && permission.getActions().indexOf("connect") != -1) {
              securityManager.checkConnect(paramURL.getHost(), paramURL.getPort());
            } else {
              throw securityException;
            } 
          }  
      } catch (IOException iOException) {
        securityManager.checkConnect(paramURL.getHost(), paramURL.getPort());
      }  
    this.url = paramURL;
  }
  
  public URLImageSource(String paramString) throws MalformedURLException { this(new URL(null, paramString)); }
  
  public URLImageSource(URL paramURL, URLConnection paramURLConnection) {
    this(paramURL);
    this.conn = paramURLConnection;
  }
  
  public URLImageSource(URLConnection paramURLConnection) { this(paramURLConnection.getURL(), paramURLConnection); }
  
  final boolean checkSecurity(Object paramObject, boolean paramBoolean) {
    if (this.actualHost != null)
      try {
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null)
          securityManager.checkConnect(this.actualHost, this.actualPort, paramObject); 
      } catch (SecurityException securityException) {
        if (!paramBoolean)
          throw securityException; 
        return false;
      }  
    return true;
  }
  
  private URLConnection getConnection() throws IOException {
    URLConnection uRLConnection;
    if (this.conn != null) {
      uRLConnection = this.conn;
      this.conn = null;
    } else {
      uRLConnection = this.url.openConnection();
    } 
    return uRLConnection;
  }
  
  protected ImageDecoder getDecoder() {
    InputStream inputStream = null;
    String str = null;
    URLConnection uRLConnection = null;
    try {
      uRLConnection = getConnection();
      inputStream = uRLConnection.getInputStream();
      str = uRLConnection.getContentType();
      URL uRL = uRLConnection.getURL();
      if (uRL != this.url && (!uRL.getHost().equals(this.url.getHost()) || uRL.getPort() != this.url.getPort())) {
        if (this.actualHost != null && (!this.actualHost.equals(uRL.getHost()) || this.actualPort != uRL.getPort()))
          throw new SecurityException("image moved!"); 
        this.actualHost = uRL.getHost();
        this.actualPort = uRL.getPort();
      } 
    } catch (IOException iOException) {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException iOException1) {}
      } else if (uRLConnection instanceof HttpURLConnection) {
        ((HttpURLConnection)uRLConnection).disconnect();
      } 
      return null;
    } 
    ImageDecoder imageDecoder = decoderForType(inputStream, str);
    if (imageDecoder == null)
      imageDecoder = getDecoder(inputStream); 
    if (imageDecoder == null)
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException iOException) {}
      } else if (uRLConnection instanceof HttpURLConnection) {
        ((HttpURLConnection)uRLConnection).disconnect();
      }  
    return imageDecoder;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\URLImageSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */