package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.UserException;

public final class InvalidName extends UserException {
  public InvalidName() { super(InvalidNameHelper.id()); }
  
  public InvalidName(String paramString) { super(InvalidNameHelper.id() + "  " + paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CosNaming\NamingContextPackage\InvalidName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */