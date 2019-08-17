package com.sun.corba.se.spi.activation;

import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;

public abstract class ServerHelper {
  private static String _id = "IDL:activation/Server:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, Server paramServer) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramServer);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static Server extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      __typeCode = ORB.init().create_interface_tc(id(), "Server"); 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static Server read(InputStream paramInputStream) { return narrow(paramInputStream.read_Object(_ServerStub.class)); }
  
  public static void write(OutputStream paramOutputStream, Server paramServer) { paramOutputStream.write_Object(paramServer); }
  
  public static Server narrow(Object paramObject) {
    if (paramObject == null)
      return null; 
    if (paramObject instanceof Server)
      return (Server)paramObject; 
    if (!paramObject._is_a(id()))
      throw new BAD_PARAM(); 
    Delegate delegate = ((ObjectImpl)paramObject)._get_delegate();
    _ServerStub _ServerStub = new _ServerStub();
    _ServerStub._set_delegate(delegate);
    return _ServerStub;
  }
  
  public static Server unchecked_narrow(Object paramObject) {
    if (paramObject == null)
      return null; 
    if (paramObject instanceof Server)
      return (Server)paramObject; 
    Delegate delegate = ((ObjectImpl)paramObject)._get_delegate();
    _ServerStub _ServerStub = new _ServerStub();
    _ServerStub._set_delegate(delegate);
    return _ServerStub;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\ServerHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */