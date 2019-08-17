package sun.net.www.protocol.http;

import java.net.Authenticator;
import java.net.InetAddress;
import java.net.URL;

public final class HttpCallerInfo {
  public final URL url;
  
  public final String host;
  
  public final String protocol;
  
  public final String prompt;
  
  public final String scheme;
  
  public final int port;
  
  public final InetAddress addr;
  
  public final Authenticator.RequestorType authType;
  
  public HttpCallerInfo(HttpCallerInfo paramHttpCallerInfo, String paramString) {
    this.url = paramHttpCallerInfo.url;
    this.host = paramHttpCallerInfo.host;
    this.protocol = paramHttpCallerInfo.protocol;
    this.prompt = paramHttpCallerInfo.prompt;
    this.port = paramHttpCallerInfo.port;
    this.addr = paramHttpCallerInfo.addr;
    this.authType = paramHttpCallerInfo.authType;
    this.scheme = paramString;
  }
  
  public HttpCallerInfo(URL paramURL) {
    this.url = paramURL;
    this.prompt = "";
    this.host = paramURL.getHost();
    int i = paramURL.getPort();
    if (i == -1) {
      this.port = paramURL.getDefaultPort();
    } else {
      this.port = i;
    } 
    try {
      object = InetAddress.getByName(paramURL.getHost());
    } catch (Exception exception) {
      object = null;
    } 
    this.addr = object;
    this.protocol = paramURL.getProtocol();
    this.authType = Authenticator.RequestorType.SERVER;
    this.scheme = "";
  }
  
  public HttpCallerInfo(URL paramURL, String paramString, int paramInt) {
    this.url = paramURL;
    this.host = paramString;
    this.port = paramInt;
    this.prompt = "";
    this.addr = null;
    this.protocol = paramURL.getProtocol();
    this.authType = Authenticator.RequestorType.PROXY;
    this.scheme = "";
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\protocol\http\HttpCallerInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */