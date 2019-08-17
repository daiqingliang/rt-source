package com.sun.corba.se.spi.activation;

import org.omg.CORBA.portable.IDLEntity;

public final class EndPointInfo implements IDLEntity {
  public String endpointType = null;
  
  public int port = 0;
  
  public EndPointInfo() {}
  
  public EndPointInfo(String paramString, int paramInt) {
    this.endpointType = paramString;
    this.port = paramInt;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\EndPointInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */