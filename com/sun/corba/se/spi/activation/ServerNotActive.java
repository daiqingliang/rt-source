package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class ServerNotActive extends UserException {
  public int serverId = 0;
  
  public ServerNotActive() { super(ServerNotActiveHelper.id()); }
  
  public ServerNotActive(int paramInt) {
    super(ServerNotActiveHelper.id());
    this.serverId = paramInt;
  }
  
  public ServerNotActive(String paramString, int paramInt) {
    super(ServerNotActiveHelper.id() + "  " + paramString);
    this.serverId = paramInt;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\ServerNotActive.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */