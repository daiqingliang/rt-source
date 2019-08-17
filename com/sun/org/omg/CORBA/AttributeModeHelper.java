package com.sun.org.omg.CORBA;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class AttributeModeHelper {
  private static String _id = "IDL:omg.org/CORBA/AttributeMode:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, AttributeMode paramAttributeMode) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramAttributeMode);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static AttributeMode extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      __typeCode = ORB.init().create_enum_tc(id(), "AttributeMode", new String[] { "ATTR_NORMAL", "ATTR_READONLY" }); 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static AttributeMode read(InputStream paramInputStream) { return AttributeMode.from_int(paramInputStream.read_long()); }
  
  public static void write(OutputStream paramOutputStream, AttributeMode paramAttributeMode) { paramOutputStream.write_long(paramAttributeMode.value()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\omg\CORBA\AttributeModeHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */