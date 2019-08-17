package org.omg.DynamicAny;

import org.omg.DynamicAny.DynAnyPackage.InvalidValue;

public interface DynEnumOperations extends DynAnyOperations {
  String get_as_string();
  
  void set_as_string(String paramString) throws InvalidValue;
  
  int get_as_ulong();
  
  void set_as_ulong(int paramInt) throws InvalidValue;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\DynamicAny\DynEnumOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */