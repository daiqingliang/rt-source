package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class ServerAlreadyRegistered extends UserException {
  public int serverId = 0;
  
  public ServerAlreadyRegistered() { super(ServerAlreadyRegisteredHelper.id()); }
  
  public ServerAlreadyRegistered(int paramInt) {
    super(ServerAlreadyRegisteredHelper.id());
    this.serverId = paramInt;
  }
  
  public ServerAlreadyRegistered(String paramString, int paramInt) {
    super(ServerAlreadyRegisteredHelper.id() + "  " + paramString);
    this.serverId = paramInt;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\ServerAlreadyRegistered.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */