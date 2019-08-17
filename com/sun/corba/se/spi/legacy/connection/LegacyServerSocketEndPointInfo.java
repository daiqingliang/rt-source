package com.sun.corba.se.spi.legacy.connection;

public interface LegacyServerSocketEndPointInfo {
  public static final String DEFAULT_ENDPOINT = "DEFAULT_ENDPOINT";
  
  public static final String BOOT_NAMING = "BOOT_NAMING";
  
  public static final String NO_NAME = "NO_NAME";
  
  String getType();
  
  String getHostName();
  
  int getPort();
  
  int getLocatorPort();
  
  void setLocatorPort(int paramInt);
  
  String getName();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\legacy\connection\LegacyServerSocketEndPointInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */