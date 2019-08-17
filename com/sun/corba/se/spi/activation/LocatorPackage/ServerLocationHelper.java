package com.sun.corba.se.spi.activation.LocatorPackage;

import com.sun.corba.se.spi.activation.ORBPortInfoHelper;
import com.sun.corba.se.spi.activation.ORBPortInfoListHelper;
import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ServerLocationHelper {
  private static String _id = "IDL:activation/Locator/ServerLocation:1.0";
  
  private static TypeCode __typeCode = null;
  
  private static boolean __active = false;
  
  public static void insert(Any paramAny, ServerLocation paramServerLocation) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramServerLocation);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static ServerLocation extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
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
          typeCode = ORBPortInfoHelper.type();
          typeCode = ORB.init().create_sequence_tc(0, typeCode);
          typeCode = ORB.init().create_alias_tc(ORBPortInfoListHelper.id(), "ORBPortInfoList", typeCode);
          arrayOfStructMember[1] = new StructMember("ports", typeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "ServerLocation", arrayOfStructMember);
          __active = false;
        } 
      }  
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static ServerLocation read(InputStream paramInputStream) {
    ServerLocation serverLocation = new ServerLocation();
    serverLocation.hostname = paramInputStream.read_string();
    serverLocation.ports = ORBPortInfoListHelper.read(paramInputStream);
    return serverLocation;
  }
  
  public static void write(OutputStream paramOutputStream, ServerLocation paramServerLocation) {
    paramOutputStream.write_string(paramServerLocation.hostname);
    ORBPortInfoListHelper.write(paramOutputStream, paramServerLocation.ports);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\LocatorPackage\ServerLocationHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */