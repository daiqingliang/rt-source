package org.omg.DynamicAny;

import org.omg.CORBA.Any;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

public interface DynSequenceOperations extends DynAnyOperations {
  int get_length();
  
  void set_length(int paramInt) throws InvalidValue;
  
  Any[] get_elements();
  
  void set_elements(Any[] paramArrayOfAny) throws TypeMismatch, InvalidValue;
  
  DynAny[] get_elements_as_dyn_any();
  
  void set_elements_as_dyn_any(DynAny[] paramArrayOfDynAny) throws TypeMismatch, InvalidValue;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\DynamicAny\DynSequenceOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */