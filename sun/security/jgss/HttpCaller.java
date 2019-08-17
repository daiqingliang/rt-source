package sun.security.jgss;

import sun.net.www.protocol.http.HttpCallerInfo;

public class HttpCaller extends GSSCaller {
  private final HttpCallerInfo hci;
  
  public HttpCaller(HttpCallerInfo paramHttpCallerInfo) {
    super("HTTP_CLIENT");
    this.hci = paramHttpCallerInfo;
  }
  
  public HttpCallerInfo info() { return this.hci; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\HttpCaller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */