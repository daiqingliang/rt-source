package com.sun.corba.se.spi.activation.LocatorPackage;

import com.sun.corba.se.spi.activation.EndPointInfoHelper;
import com.sun.corba.se.spi.activation.EndpointInfoListHelper;
import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ServerLocationPerORBHelper {
  private static String _id = "IDL:activation/Locator/ServerLocationPerORB:1.0";
  
  private static TypeCode __typeCode = null;
  
  private static boolean __active = false;
  
  public static void insert(Any paramAny, ServerLocationPerORB paramServerLocationPerORB) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramServerLocationPerORB);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static ServerLocationPerORB extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
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
          arrayOfStructMember[0] = new StructMember("hostname", typeCode, null);
          typeCode = EndPointInfoHelper.type();
          typeCode = ORB.init().create_sequence_tc(0, typeCode);
          typeCode = ORB.init().create_alias_tc(EndpointInfoListHelper.id(), "EndpointInfoList", typeCode);
          arrayOfStructMember[1] = new StructMember("ports", typeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "ServerLocationPerORB", arrayOfStructMember);
          __active = false;
        } 
      }  
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static ServerLocationPerORB read(InputStream paramInputStream) {
    ServerLocationPerORB serverLocationPerORB = new ServerLocationPerORB();
    serverLocationPerORB.hostname = paramInputStream.read_string();
    serverLocationPerORB.ports = EndpointInfoListHelper.read(paramInputStream);
    return serverLocationPerORB;
  }
  
  public static void write(OutputStream paramOutputStream, ServerLocationPerORB paramServerLocationPerORB) {
    paramOutputStream.write_string(paramServerLocationPerORB.hostname);
    EndpointInfoListHelper.write(paramOutputStream, paramServerLocationPerORB.ports);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\LocatorPackage\ServerLocationPerORBHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */