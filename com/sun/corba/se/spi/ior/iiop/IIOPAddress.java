package com.sun.corba.se.spi.ior.iiop;

import com.sun.corba.se.spi.ior.Writeable;

public interface IIOPAddress extends Writeable {
  String getHost();
  
  int getPort();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\ior\iiop\IIOPAddress.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */