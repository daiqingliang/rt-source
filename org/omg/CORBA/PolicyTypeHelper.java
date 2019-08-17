package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class PolicyTypeHelper {
  private static String _id = "IDL:omg.org/CORBA/PolicyType:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, int paramInt) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramInt);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static int extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null) {
      __typeCode = ORB.init().get_primitive_tc(TCKind.tk_ulong);
      __typeCode = ORB.init().create_alias_tc(id(), "PolicyType", __typeCode);
    } 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static int read(InputStream paramInputStream) {
    null = 0;
    return paramInputStream.read_ulong();
  }
  
  public static void write(OutputStream paramOutputStream, int paramInt) { paramOutputStream.write_ulong(paramInt); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\PolicyTypeHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */