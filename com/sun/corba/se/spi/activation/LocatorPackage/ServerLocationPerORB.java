package com.sun.corba.se.spi.activation.LocatorPackage;

import com.sun.corba.se.spi.activation.EndPointInfo;
import org.omg.CORBA.portable.IDLEntity;

public final class ServerLocationPerORB implements IDLEntity {
  public String hostname = null;
  
  public EndPointInfo[] ports = null;
  
  public ServerLocationPerORB() {}
  
  public ServerLocationPerORB(String paramString, EndPointInfo[] paramArrayOfEndPointInfo) {
    this.hostname = paramString;
    this.ports = paramArrayOfEndPointInfo;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\LocatorPackage\ServerLocationPerORB.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */