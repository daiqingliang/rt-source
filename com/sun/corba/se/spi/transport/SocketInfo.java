package com.sun.corba.se.spi.transport;

public interface SocketInfo {
  public static final String IIOP_CLEAR_TEXT = "IIOP_CLEAR_TEXT";
  
  String getType();
  
  String getHost();
  
  int getPort();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\transport\SocketInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */