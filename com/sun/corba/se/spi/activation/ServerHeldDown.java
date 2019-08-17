package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class ServerHeldDown extends UserException {
  public int serverId = 0;
  
  public ServerHeldDown() { super(ServerHeldDownHelper.id()); }
  
  public ServerHeldDown(int paramInt) {
    super(ServerHeldDownHelper.id());
    this.serverId = paramInt;
  }
  
  public ServerHeldDown(String paramString, int paramInt) {
    super(ServerHeldDownHelper.id() + "  " + paramString);
    this.serverId = paramInt;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\ServerHeldDown.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */