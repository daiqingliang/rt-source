package org.omg.DynamicAny;

import org.omg.CORBA.TCKind;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

public interface DynUnionOperations extends DynAnyOperations {
  DynAny get_discriminator();
  
  void set_discriminator(DynAny paramDynAny) throws TypeMismatch;
  
  void set_to_default_member() throws TypeMismatch;
  
  void set_to_no_active_member() throws TypeMismatch;
  
  boolean has_no_active_member();
  
  TCKind discriminator_kind();
  
  TCKind member_kind();
  
  DynAny member();
  
  String member_name() throws InvalidValue;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\DynamicAny\DynUnionOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */