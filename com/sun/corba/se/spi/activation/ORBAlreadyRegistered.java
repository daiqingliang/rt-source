package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class ORBAlreadyRegistered extends UserException {
  public String orbId = null;
  
  public ORBAlreadyRegistered() { super(ORBAlreadyRegisteredHelper.id()); }
  
  public ORBAlreadyRegistered(String paramString) {
    super(ORBAlreadyRegisteredHelper.id());
    this.orbId = paramString;
  }
  
  public ORBAlreadyRegistered(String paramString1, String paramString2) {
    super(ORBAlreadyRegisteredHelper.id() + "  " + paramString1);
    this.orbId = paramString2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\ORBAlreadyRegistered.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */