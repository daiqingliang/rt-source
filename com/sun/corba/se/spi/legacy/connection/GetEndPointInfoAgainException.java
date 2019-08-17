package com.sun.corba.se.spi.legacy.connection;

import com.sun.corba.se.spi.transport.SocketInfo;

public class GetEndPointInfoAgainException extends Exception {
  private SocketInfo socketInfo;
  
  public GetEndPointInfoAgainException(SocketInfo paramSocketInfo) { this.socketInfo = paramSocketInfo; }
  
  public SocketInfo getEndPointInfo() { return this.socketInfo; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\legacy\connection\GetEndPointInfoAgainException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */