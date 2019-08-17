package com.sun.corba.se.spi.orbutil.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public interface LinkedInvocationHandler extends InvocationHandler {
  void setProxy(Proxy paramProxy);
  
  Proxy getProxy();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\orbutil\proxy\LinkedInvocationHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */