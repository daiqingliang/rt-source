package com.sun.corba.se.spi.resolver;

import java.util.Set;
import org.omg.CORBA.Object;

public interface Resolver {
  Object resolve(String paramString);
  
  Set list();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\resolver\Resolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */