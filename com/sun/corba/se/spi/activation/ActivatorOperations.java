package com.sun.corba.se.spi.activation;

public interface ActivatorOperations {
  void active(int paramInt, Server paramServer) throws ServerNotRegistered;
  
  void registerEndpoints(int paramInt, String paramString, EndPointInfo[] paramArrayOfEndPointInfo) throws ServerNotRegistered, NoSuchEndPoint, ORBAlreadyRegistered;
  
  int[] getActiveServers();
  
  void activate(int paramInt) throws ServerAlreadyActive, ServerNotRegistered, ServerHeldDown;
  
  void shutdown(int paramInt) throws ServerAlreadyActive, ServerNotRegistered, ServerHeldDown;
  
  void install(int paramInt) throws ServerAlreadyActive, ServerNotRegistered, ServerHeldDown;
  
  String[] getORBNames(int paramInt) throws ServerNotRegistered;
  
  void uninstall(int paramInt) throws ServerAlreadyActive, ServerNotRegistered, ServerHeldDown;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\ActivatorOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */