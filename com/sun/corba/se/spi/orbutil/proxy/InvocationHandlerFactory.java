package com.sun.corba.se.spi.orbutil.proxy;

import java.lang.reflect.InvocationHandler;

public interface InvocationHandlerFactory {
  InvocationHandler getInvocationHandler();
  
  Class[] getProxyInterfaces();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\orbutil\proxy\InvocationHandlerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */