package com.sun.org.omg.CORBA;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class VisibilityHelper {
  private static String _id = "IDL:omg.org/CORBA/Visibility:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, short paramShort) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramShort);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static short extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null) {
      __typeCode = ORB.init().get_primitive_tc(TCKind.tk_short);
      __typeCode = ORB.init().create_alias_tc(id(), "Visibility", __typeCode);
    } 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static short read(InputStream paramInputStream) {
    null = 0;
    return paramInputStream.read_short();
  }
  
  public static void write(OutputStream paramOutputStream, short paramShort) { paramOutputStream.write_short(paramShort); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\omg\CORBA\VisibilityHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */