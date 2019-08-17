package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.UserException;

public final class AlreadyBound extends UserException {
  public AlreadyBound() { super(AlreadyBoundHelper.id()); }
  
  public AlreadyBound(String paramString) { super(AlreadyBoundHelper.id() + "  " + paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CosNaming\NamingContextPackage\AlreadyBound.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */