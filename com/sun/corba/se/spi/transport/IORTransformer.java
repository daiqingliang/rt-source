package com.sun.corba.se.spi.transport;

import com.sun.corba.se.spi.encoding.CorbaInputObject;
import com.sun.corba.se.spi.encoding.CorbaOutputObject;
import com.sun.corba.se.spi.ior.IOR;

public interface IORTransformer {
  IOR unmarshal(CorbaInputObject paramCorbaInputObject);
  
  void marshal(CorbaOutputObject paramCorbaOutputObject, IOR paramIOR);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\transport\IORTransformer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */