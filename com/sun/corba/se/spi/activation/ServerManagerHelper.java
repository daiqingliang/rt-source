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

public abstract class ServerManagerHelper {
  private static String _id = "IDL:activation/ServerManager:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, ServerManager paramServerManager) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramServerManager);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static ServerManager extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      __typeCode = ORB.init().create_interface_tc(id(), "ServerManager"); 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static ServerManager read(InputStream paramInputStream) { return narrow(paramInputStream.read_Object(_ServerManagerStub.class)); }
  
  public static void write(OutputStream paramOutputStream, ServerManager paramServerManager) { paramOutputStream.write_Object(paramServerManager); }
  
  public static ServerManager narrow(Object paramObject) {
    if (paramObject == null)
      return null; 
    if (paramObject instanceof ServerManager)
      return (ServerManager)paramObject; 
    if (!paramObject._is_a(id()))
      throw new BAD_PARAM(); 
    Delegate delegate = ((ObjectImpl)paramObject)._get_delegate();
    _ServerManagerStub _ServerManagerStub = new _ServerManagerStub();
    _ServerManagerStub._set_delegate(delegate);
    return _ServerManagerStub;
  }
  
  public static ServerManager unchecked_narrow(Object paramObject) {
    if (paramObject == null)
      return null; 
    if (paramObject instanceof ServerManager)
      return (ServerManager)paramObject; 
    Delegate delegate = ((ObjectImpl)paramObject)._get_delegate();
    _ServerManagerStub _ServerManagerStub = new _ServerManagerStub();
    _ServerManagerStub._set_delegate(delegate);
    return _ServerManagerStub;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\ServerManagerHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */