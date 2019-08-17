package org.omg.DynamicAny;

import org.omg.CORBA.Any;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

public interface DynValueBoxOperations extends DynValueCommonOperations {
  Any get_boxed_value() throws InvalidValue;
  
  void set_boxed_value(Any paramAny) throws TypeMismatch;
  
  DynAny get_boxed_value_as_dyn_any() throws InvalidValue;
  
  void set_boxed_value_as_dyn_any(DynAny paramDynAny) throws TypeMismatch;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\DynamicAny\DynValueBoxOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */