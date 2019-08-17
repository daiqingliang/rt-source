package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.spi.ior.ObjectKey;

public interface LocateRequestMessage extends Message {
  int getRequestId();
  
  ObjectKey getObjectKey();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\LocateRequestMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */