package com.sun.corba.se.impl.corba;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class AnyImplHelper {
  private static String _id = "IDL:omg.org/CORBA/Any:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny1, Any paramAny2) {
    OutputStream outputStream = paramAny1.create_output_stream();
    paramAny1.type(type());
    write(outputStream, paramAny2);
    paramAny1.read_value(outputStream.create_input_stream(), type());
  }
  
  public static Any extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      __typeCode = ORB.init().get_primitive_tc(TCKind.tk_any); 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static Any read(InputStream paramInputStream) { return paramInputStream.read_any(); }
  
  public static void write(OutputStream paramOutputStream, Any paramAny) { paramOutputStream.write_any(paramAny); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\corba\AnyImplHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */