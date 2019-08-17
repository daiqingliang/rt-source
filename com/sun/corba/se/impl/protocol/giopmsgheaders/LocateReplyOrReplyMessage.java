package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.spi.ior.IOR;
import org.omg.CORBA.SystemException;

public interface LocateReplyOrReplyMessage extends Message {
  int getRequestId();
  
  int getReplyStatus();
  
  SystemException getSystemException(String paramString);
  
  IOR getIOR();
  
  short getAddrDisposition();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\LocateReplyOrReplyMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */