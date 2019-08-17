package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class ServerAlreadyInstalled extends UserException {
  public int serverId = 0;
  
  public ServerAlreadyInstalled() { super(ServerAlreadyInstalledHelper.id()); }
  
  public ServerAlreadyInstalled(int paramInt) {
    super(ServerAlreadyInstalledHelper.id());
    this.serverId = paramInt;
  }
  
  public ServerAlreadyInstalled(String paramString, int paramInt) {
    super(ServerAlreadyInstalledHelper.id() + "  " + paramString);
    this.serverId = paramInt;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\ServerAlreadyInstalled.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */