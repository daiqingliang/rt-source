package com.sun.corba.se.spi.orbutil.proxy;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;

public interface CompositeInvocationHandler extends InvocationHandler, Serializable {
  void addInvocationHandler(Class paramClass, InvocationHandler paramInvocationHandler);
  
  void setDefaultHandler(InvocationHandler paramInvocationHandler);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\orbutil\proxy\CompositeInvocationHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */