package com.sun.org.omg.CORBA.portable;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.BoxedValueHelper;

@Deprecated
public interface ValueHelper extends BoxedValueHelper {
  Class get_class();
  
  String[] get_truncatable_base_ids();
  
  TypeCode get_type();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\omg\CORBA\portable\ValueHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */