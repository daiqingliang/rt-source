package javax.net.ssl;

import java.util.EventObject;

public class SSLSessionBindingEvent extends EventObject {
  private static final long serialVersionUID = 3989172637106345L;
  
  private String name;
  
  public SSLSessionBindingEvent(SSLSession paramSSLSession, String paramString) {
    super(paramSSLSession);
    this.name = paramString;
  }
  
  public String getName() { return this.name; }
  
  public SSLSession getSession() { return (SSLSession)getSource(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\net\ssl\SSLSessionBindingEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */