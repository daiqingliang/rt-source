package org.omg.CORBA;

import java.io.Serializable;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public abstract class ValueBaseHelper {
  private static String _id = "IDL:omg.org/CORBA/ValueBase:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, Serializable paramSerializable) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramSerializable);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static Serializable extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      __typeCode = ORB.init().get_primitive_tc(TCKind.tk_value); 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static Serializable read(InputStream paramInputStream) { return ((InputStream)paramInputStream).read_value(); }
  
  public static void write(OutputStream paramOutputStream, Serializable paramSerializable) { ((OutputStream)paramOutputStream).write_value(paramSerializable); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\ValueBaseHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */