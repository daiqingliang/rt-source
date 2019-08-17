package javax.net.ssl;

import java.util.EventListener;

public interface SSLSessionBindingListener extends EventListener {
  void valueBound(SSLSessionBindingEvent paramSSLSessionBindingEvent);
  
  void valueUnbound(SSLSessionBindingEvent paramSSLSessionBindingEvent);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\net\ssl\SSLSessionBindingListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */