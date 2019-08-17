package com.sun.corba.se.spi.activation.InitialNameServicePackage;

import org.omg.CORBA.UserException;

public final class NameAlreadyBound extends UserException {
  public NameAlreadyBound() { super(NameAlreadyBoundHelper.id()); }
  
  public NameAlreadyBound(String paramString) { super(NameAlreadyBoundHelper.id() + "  " + paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\InitialNameServicePackage\NameAlreadyBound.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */