package org.omg.DynamicAny;

import org.omg.CORBA.Any;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

public interface DynArrayOperations extends DynAnyOperations {
  Any[] get_elements();
  
  void set_elements(Any[] paramArrayOfAny) throws TypeMismatch, InvalidValue;
  
  DynAny[] get_elements_as_dyn_any();
  
  void set_elements_as_dyn_any(DynAny[] paramArrayOfDynAny) throws TypeMismatch, InvalidValue;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\DynamicAny\DynArrayOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */