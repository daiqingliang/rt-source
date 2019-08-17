package org.omg.CosNaming.NamingContextExtPackage;

import org.omg.CORBA.UserException;

public final class InvalidAddress extends UserException {
  public InvalidAddress() { super(InvalidAddressHelper.id()); }
  
  public InvalidAddress(String paramString) { super(InvalidAddressHelper.id() + "  " + paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CosNaming\NamingContextExtPackage\InvalidAddress.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */