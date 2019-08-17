package sun.net;

import java.net.Proxy;

public final class ApplicationProxy extends Proxy {
  private ApplicationProxy(Proxy paramProxy) { super(paramProxy.type(), paramProxy.address()); }
  
  public static ApplicationProxy create(Proxy paramProxy) { return new ApplicationProxy(paramProxy); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\ApplicationProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */