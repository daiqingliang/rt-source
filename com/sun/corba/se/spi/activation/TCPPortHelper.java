package com.sun.corba.se.spi.activation;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class TCPPortHelper {
  private static String _id = "IDL:activation/TCPPort:1.0";
  
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
      __typeCode = ORB.init().get_primitive_tc(TCKind.tk_long);
      __typeCode = ORB.init().create_alias_tc(id(), "TCPPort", __typeCode);
    } 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static int read(InputStream paramInputStream) {
    null = 0;
    return paramInputStream.read_long();
  }
  
  public static void write(OutputStream paramOutputStream, int paramInt) { paramOutputStream.write_long(paramInt); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\TCPPortHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */