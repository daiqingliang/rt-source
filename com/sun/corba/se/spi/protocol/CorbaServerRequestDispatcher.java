package com.sun.corba.se.spi.protocol;

import com.sun.corba.se.pept.protocol.ServerRequestDispatcher;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectKey;

public interface CorbaServerRequestDispatcher extends ServerRequestDispatcher {
  IOR locate(ObjectKey paramObjectKey);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\protocol\CorbaServerRequestDispatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */