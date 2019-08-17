package org.omg.CORBA;

import org.omg.CORBA.DynAnyPackage.InvalidValue;

@Deprecated
public interface DynFixed extends Object, DynAny {
  byte[] get_value();
  
  void set_value(byte[] paramArrayOfByte) throws InvalidValue;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\DynFixed.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */