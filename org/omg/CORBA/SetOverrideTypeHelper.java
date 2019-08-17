package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class SetOverrideTypeHelper {
  private static String _id = "IDL:omg.org/CORBA/SetOverrideType:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, SetOverrideType paramSetOverrideType) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramSetOverrideType);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static SetOverrideType extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      __typeCode = ORB.init().create_enum_tc(id(), "SetOverrideType", new String[] { "SET_OVERRIDE", "ADD_OVERRIDE" }); 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static SetOverrideType read(InputStream paramInputStream) { return SetOverrideType.from_int(paramInputStream.read_long()); }
  
  public static void write(OutputStream paramOutputStream, SetOverrideType paramSetOverrideType) { paramOutputStream.write_long(paramSetOverrideType.value()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\SetOverrideTypeHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */