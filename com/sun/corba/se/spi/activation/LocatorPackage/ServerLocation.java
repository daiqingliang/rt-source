package com.sun.corba.se.spi.activation.LocatorPackage;

import com.sun.corba.se.spi.activation.ORBPortInfo;
import org.omg.CORBA.portable.IDLEntity;

public final class ServerLocation implements IDLEntity {
  public String hostname = null;
  
  public ORBPortInfo[] ports = null;
  
  public ServerLocation() {}
  
  public ServerLocation(String paramString, ORBPortInfo[] paramArrayOfORBPortInfo) {
    this.hostname = paramString;
    this.ports = paramArrayOfORBPortInfo;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\LocatorPackage\ServerLocation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */