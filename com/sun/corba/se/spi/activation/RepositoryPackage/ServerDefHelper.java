package com.sun.corba.se.spi.activation.RepositoryPackage;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ServerDefHelper {
  private static String _id = "IDL:activation/Repository/ServerDef:1.0";
  
  private static TypeCode __typeCode = null;
  
  private static boolean __active = false;
  
  public static void insert(Any paramAny, ServerDef paramServerDef) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramServerDef);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static ServerDef extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      synchronized (TypeCode.class) {
        if (__typeCode == null) {
          if (__active)
            return ORB.init().create_recursive_tc(_id); 
          __active = true;
          StructMember[] arrayOfStructMember = new StructMember[5];
          TypeCode typeCode = null;
          typeCode = ORB.init().create_string_tc(0);
          arrayOfStructMember[0] = new StructMember("applicationName", typeCode, null);
          typeCode = ORB.init().create_string_tc(0);
          arrayOfStructMember[1] = new StructMember("serverName", typeCode, null);
          typeCode = ORB.init().create_string_tc(0);
          arrayOfStructMember[2] = new StructMember("serverClassPath", typeCode, null);
          typeCode = ORB.init().create_string_tc(0);
          arrayOfStructMember[3] = new StructMember("serverArgs", typeCode, null);
          typeCode = ORB.init().create_string_tc(0);
          arrayOfStructMember[4] = new StructMember("serverVmArgs", typeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "ServerDef", arrayOfStructMember);
          __active = false;
        } 
      }  
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static ServerDef read(InputStream paramInputStream) {
    ServerDef serverDef = new ServerDef();
    serverDef.applicationName = paramInputStream.read_string();
    serverDef.serverName = paramInputStream.read_string();
    serverDef.serverClassPath = paramInputStream.read_string();
    serverDef.serverArgs = paramInputStream.read_string();
    serverDef.serverVmArgs = paramInputStream.read_string();
    return serverDef;
  }
  
  public static void write(OutputStream paramOutputStream, ServerDef paramServerDef) {
    paramOutputStream.write_string(paramServerDef.applicationName);
    paramOutputStream.write_string(paramServerDef.serverName);
    paramOutputStream.write_string(paramServerDef.serverClassPath);
    paramOutputStream.write_string(paramServerDef.serverArgs);
    paramOutputStream.write_string(paramServerDef.serverVmArgs);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\RepositoryPackage\ServerDefHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */