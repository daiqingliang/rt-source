package sun.net.www.http;

import java.net.URL;

class KeepAliveKey {
  private String protocol = null;
  
  private String host = null;
  
  private int port = 0;
  
  private Object obj = null;
  
  public KeepAliveKey(URL paramURL, Object paramObject) {
    this.protocol = paramURL.getProtocol();
    this.host = paramURL.getHost();
    this.port = paramURL.getPort();
    this.obj = paramObject;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof KeepAliveKey))
      return false; 
    KeepAliveKey keepAliveKey = (KeepAliveKey)paramObject;
    return (this.host.equals(keepAliveKey.host) && this.port == keepAliveKey.port && this.protocol.equals(keepAliveKey.protocol) && this.obj == keepAliveKey.obj);
  }
  
  public int hashCode() {
    String str = this.protocol + this.host + this.port;
    return (this.obj == null) ? str.hashCode() : (str.hashCode() + this.obj.hashCode());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\http\KeepAliveKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */