package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class ServerAlreadyUninstalled extends UserException {
  public int serverId = 0;
  
  public ServerAlreadyUninstalled() { super(ServerAlreadyUninstalledHelper.id()); }
  
  public ServerAlreadyUninstalled(int paramInt) {
    super(ServerAlreadyUninstalledHelper.id());
    this.serverId = paramInt;
  }
  
  public ServerAlreadyUninstalled(String paramString, int paramInt) {
    super(ServerAlreadyUninstalledHelper.id() + "  " + paramString);
    this.serverId = paramInt;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\ServerAlreadyUninstalled.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */