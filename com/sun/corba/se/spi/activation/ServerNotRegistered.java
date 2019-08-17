package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class ServerNotRegistered extends UserException {
  public int serverId = 0;
  
  public ServerNotRegistered() { super(ServerNotRegisteredHelper.id()); }
  
  public ServerNotRegistered(int paramInt) {
    super(ServerNotRegisteredHelper.id());
    this.serverId = paramInt;
  }
  
  public ServerNotRegistered(String paramString, int paramInt) {
    super(ServerNotRegisteredHelper.id() + "  " + paramString);
    this.serverId = paramInt;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\ServerNotRegistered.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */