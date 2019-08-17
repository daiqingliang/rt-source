package com.sun.corba.se.spi.legacy.connection;

public interface LegacyServerSocketManager {
  int legacyGetTransientServerPort(String paramString);
  
  int legacyGetPersistentServerPort(String paramString);
  
  int legacyGetTransientOrPersistentServerPort(String paramString);
  
  LegacyServerSocketEndPointInfo legacyGetEndpoint(String paramString);
  
  boolean legacyIsLocalServerPort(int paramInt);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\legacy\connection\LegacyServerSocketManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */