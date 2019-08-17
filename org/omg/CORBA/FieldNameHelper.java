package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class FieldNameHelper {
  private static String _id = "IDL:omg.org/CORBA/FieldName:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, String paramString) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramString);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static String extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null) {
      __typeCode = ORB.init().create_string_tc(0);
      __typeCode = ORB.init().create_alias_tc(id(), "FieldName", __typeCode);
    } 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static String read(InputStream paramInputStream) {
    null = null;
    return paramInputStream.read_string();
  }
  
  public static void write(OutputStream paramOutputStream, String paramString) { paramOutputStream.write_string(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\FieldNameHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */