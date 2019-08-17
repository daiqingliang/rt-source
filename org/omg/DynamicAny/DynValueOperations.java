package org.omg.DynamicAny;

import org.omg.CORBA.TCKind;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

public interface DynValueOperations extends DynValueCommonOperations {
  String current_member_name() throws TypeMismatch, InvalidValue;
  
  TCKind current_member_kind() throws TypeMismatch, InvalidValue;
  
  NameValuePair[] get_members() throws InvalidValue;
  
  void set_members(NameValuePair[] paramArrayOfNameValuePair) throws TypeMismatch, InvalidValue;
  
  NameDynAnyPair[] get_members_as_dyn_any() throws InvalidValue;
  
  void set_members_as_dyn_any(NameDynAnyPair[] paramArrayOfNameDynAnyPair) throws TypeMismatch, InvalidValue;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\DynamicAny\DynValueOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */