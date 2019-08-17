package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class NameValuePairHelper {
  private static String _id = "IDL:omg.org/CORBA/NameValuePair:1.0";
  
  private static TypeCode __typeCode = null;
  
  private static boolean __active = false;
  
  public static void insert(Any paramAny, NameValuePair paramNameValuePair) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramNameValuePair);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static NameValuePair extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      synchronized (TypeCode.class) {
        if (__typeCode == null) {
          if (__active)
            return ORB.init().create_recursive_tc(_id); 
          __active = true;
          StructMember[] arrayOfStructMember = new StructMember[2];
          TypeCode typeCode = null;
          typeCode = ORB.init().create_string_tc(0);
          typeCode = ORB.init().create_alias_tc(FieldNameHelper.id(), "FieldName", typeCode);
          arrayOfStructMember[0] = new StructMember("id", typeCode, null);
          typeCode = ORB.init().get_primitive_tc(TCKind.tk_any);
          arrayOfStructMember[1] = new StructMember("value", typeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "NameValuePair", arrayOfStructMember);
          __active = false;
        } 
      }  
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static NameValuePair read(InputStream paramInputStream) {
    NameValuePair nameValuePair = new NameValuePair();
    nameValuePair.id = paramInputStream.read_string();
    nameValuePair.value = paramInputStream.read_any();
    return nameValuePair;
  }
  
  public static void write(OutputStream paramOutputStream, NameValuePair paramNameValuePair) {
    paramOutputStream.write_string(paramNameValuePair.id);
    paramOutputStream.write_any(paramNameValuePair.value);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\NameValuePairHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */