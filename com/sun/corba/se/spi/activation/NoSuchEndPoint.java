package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class NoSuchEndPoint extends UserException {
  public NoSuchEndPoint() { super(NoSuchEndPointHelper.id()); }
  
  public NoSuchEndPoint(String paramString) { super(NoSuchEndPointHelper.id() + "  " + paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\NoSuchEndPoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */