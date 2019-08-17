package org.omg.DynamicAny;

import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

public interface DynFixedOperations extends DynAnyOperations {
  String get_value();
  
  boolean set_value(String paramString) throws TypeMismatch, InvalidValue;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\DynamicAny\DynFixedOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */