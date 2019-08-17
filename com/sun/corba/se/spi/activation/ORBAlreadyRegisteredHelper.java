package com.sun.corba.se.spi.activation;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ORBAlreadyRegisteredHelper {
  private static String _id = "IDL:activation/ORBAlreadyRegistered:1.0";
  
  private static TypeCode __typeCode = null;
  
  private static boolean __active = false;
  
  public static void insert(Any paramAny, ORBAlreadyRegistered paramORBAlreadyRegistered) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramORBAlreadyRegistered);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static ORBAlreadyRegistered extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      synchronized (TypeCode.class) {
        if (__typeCode == null) {
          if (__active)
            return ORB.init().create_recursive_tc(_id); 
          __active = true;
          StructMember[] arrayOfStructMember = new StructMember[1];
          TypeCode typeCode = null;
          typeCode = ORB.init().create_string_tc(0);
          typeCode = ORB.init().create_alias_tc(ORBidHelper.id(), "ORBid", typeCode);
          arrayOfStructMember[0] = new StructMember("orbId", typeCode, null);
          __typeCode = ORB.init().create_exception_tc(id(), "ORBAlreadyRegistered", arrayOfStructMember);
          __active = false;
        } 
      }  
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static ORBAlreadyRegistered read(InputStream paramInputStream) {
    ORBAlreadyRegistered oRBAlreadyRegistered = new ORBAlreadyRegistered();
    paramInputStream.read_string();
    oRBAlreadyRegistered.orbId = paramInputStream.read_string();
    return oRBAlreadyRegistered;
  }
  
  public static void write(OutputStream paramOutputStream, ORBAlreadyRegistered paramORBAlreadyRegistered) {
    paramOutputStream.write_string(id());
    paramOutputStream.write_string(paramORBAlreadyRegistered.orbId);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\ORBAlreadyRegisteredHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */