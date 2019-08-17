package org.omg.CORBA;

import org.omg.CORBA.DynAnyPackage.InvalidSeq;

@Deprecated
public interface DynValue extends Object, DynAny {
  String current_member_name();
  
  TCKind current_member_kind();
  
  NameValuePair[] get_members();
  
  void set_members(NameValuePair[] paramArrayOfNameValuePair) throws InvalidSeq;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\DynValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */