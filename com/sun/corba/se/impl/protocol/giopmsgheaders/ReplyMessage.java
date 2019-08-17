package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;

public interface ReplyMessage extends Message, LocateReplyOrReplyMessage {
  public static final int NO_EXCEPTION = 0;
  
  public static final int USER_EXCEPTION = 1;
  
  public static final int SYSTEM_EXCEPTION = 2;
  
  public static final int LOCATION_FORWARD = 3;
  
  public static final int LOCATION_FORWARD_PERM = 4;
  
  public static final int NEEDS_ADDRESSING_MODE = 5;
  
  ServiceContexts getServiceContexts();
  
  void setServiceContexts(ServiceContexts paramServiceContexts);
  
  void setIOR(IOR paramIOR);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\ReplyMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */