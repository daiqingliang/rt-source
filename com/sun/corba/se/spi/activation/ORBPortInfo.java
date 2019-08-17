package com.sun.corba.se.spi.activation;

import org.omg.CORBA.portable.IDLEntity;

public final class ORBPortInfo implements IDLEntity {
  public String orbId = null;
  
  public int port = 0;
  
  public ORBPortInfo() {}
  
  public ORBPortInfo(String paramString, int paramInt) {
    this.orbId = paramString;
    this.port = paramInt;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\ORBPortInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */