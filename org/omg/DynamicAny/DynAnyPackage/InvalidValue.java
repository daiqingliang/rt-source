package org.omg.DynamicAny.DynAnyPackage;

import org.omg.CORBA.UserException;

public final class InvalidValue extends UserException {
  public InvalidValue() { super(InvalidValueHelper.id()); }
  
  public InvalidValue(String paramString) { super(InvalidValueHelper.id() + "  " + paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\DynamicAny\DynAnyPackage\InvalidValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */