package org.omg.DynamicAny.DynAnyFactoryPackage;

import org.omg.CORBA.UserException;

public final class InconsistentTypeCode extends UserException {
  public InconsistentTypeCode() { super(InconsistentTypeCodeHelper.id()); }
  
  public InconsistentTypeCode(String paramString) { super(InconsistentTypeCodeHelper.id() + "  " + paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\DynamicAny\DynAnyFactoryPackage\InconsistentTypeCode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */