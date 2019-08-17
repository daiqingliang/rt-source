package org.omg.IOP.CodecPackage;

import org.omg.CORBA.UserException;

public final class TypeMismatch extends UserException {
  public TypeMismatch() { super(TypeMismatchHelper.id()); }
  
  public TypeMismatch(String paramString) { super(TypeMismatchHelper.id() + "  " + paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\IOP\CodecPackage\TypeMismatch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */