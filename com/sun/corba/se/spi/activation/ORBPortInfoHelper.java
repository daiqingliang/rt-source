package com.sun.corba.se.spi.activation;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ORBPortInfoHelper {
  private static String _id = "IDL:activation/ORBPortInfo:1.0";
  
  private static TypeCode __typeCode = null;
  
  private static boolean __active = false;
  
  public static void insert(Any paramAny, ORBPortInfo paramORBPortInfo) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramORBPortInfo);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static ORBPortInfo extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
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
          typeCode = ORB.init().create_alias_tc(ORBidHelper.id(), "ORBid", typeCode);
          arrayOfStructMember[0] = new StructMember("orbId", typeCode, null);
          typeCode = ORB.init().get_primitive_tc(TCKind.tk_long);
          typeCode = ORB.init().create_alias_tc(TCPPortHelper.id(), "TCPPort", typeCode);
          arrayOfStructMember[1] = new StructMember("port", typeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "ORBPortInfo", arrayOfStructMember);
          __active = false;
        } 
      }  
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static ORBPortInfo read(InputStream paramInputStream) {
    ORBPortInfo oRBPortInfo = new ORBPortInfo();
    oRBPortInfo.orbId = paramInputStream.read_string();
    oRBPortInfo.port = paramInputStream.read_long();
    return oRBPortInfo;
  }
  
  public static void write(OutputStream paramOutputStream, ORBPortInfo paramORBPortInfo) {
    paramOutputStream.write_string(paramORBPortInfo.orbId);
    paramOutputStream.write_long(paramORBPortInfo.port);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\ORBPortInfoHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */