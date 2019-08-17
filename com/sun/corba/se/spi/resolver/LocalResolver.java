package com.sun.corba.se.spi.resolver;

import com.sun.corba.se.spi.orbutil.closure.Closure;

public interface LocalResolver extends Resolver {
  void register(String paramString, Closure paramClosure);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\resolver\LocalResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */