package org.omg.CORBA.portable;

import java.io.Serializable;

public interface BoxedValueHelper {
  Serializable read_value(InputStream paramInputStream);
  
  void write_value(OutputStream paramOutputStream, Serializable paramSerializable);
  
  String get_id();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\portable\BoxedValueHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */