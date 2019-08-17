package com.sun.org.omg.CORBA;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class ParameterModeHelper {
  private static String _id = "IDL:omg.org/CORBA/ParameterMode:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, ParameterMode paramParameterMode) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramParameterMode);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static ParameterMode extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      __typeCode = ORB.init().create_enum_tc(id(), "ParameterMode", new String[] { "PARAM_IN", "PARAM_OUT", "PARAM_INOUT" }); 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static ParameterMode read(InputStream paramInputStream) { return ParameterMode.from_int(paramInputStream.read_long()); }
  
  public static void write(OutputStream paramOutputStream, ParameterMode paramParameterMode) { paramOutputStream.write_long(paramParameterMode.value()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\omg\CORBA\ParameterModeHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */