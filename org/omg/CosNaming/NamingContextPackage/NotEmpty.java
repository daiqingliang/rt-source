package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.UserException;

public final class NotEmpty extends UserException {
  public NotEmpty() { super(NotEmptyHelper.id()); }
  
  public NotEmpty(String paramString) { super(NotEmptyHelper.id() + "  " + paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CosNaming\NamingContextPackage\NotEmpty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */