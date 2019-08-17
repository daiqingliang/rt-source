package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class ServerAlreadyActive extends UserException {
  public int serverId = 0;
  
  public ServerAlreadyActive() { super(ServerAlreadyActiveHelper.id()); }
  
  public ServerAlreadyActive(int paramInt) {
    super(ServerAlreadyActiveHelper.id());
    this.serverId = paramInt;
  }
  
  public ServerAlreadyActive(String paramString, int paramInt) {
    super(ServerAlreadyActiveHelper.id() + "  " + paramString);
    this.serverId = paramInt;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\ServerAlreadyActive.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */