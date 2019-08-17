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

public abstract class InitialNameServiceHelper {
  private static String _id = "IDL:activation/InitialNameService:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, InitialNameService paramInitialNameService) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramInitialNameService);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static InitialNameService extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      __typeCode = ORB.init().create_interface_tc(id(), "InitialNameService"); 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static InitialNameService read(InputStream paramInputStream) { return narrow(paramInputStream.read_Object(_InitialNameServiceStub.class)); }
  
  public static void write(OutputStream paramOutputStream, InitialNameService paramInitialNameService) { paramOutputStream.write_Object(paramInitialNameService); }
  
  public static InitialNameService narrow(Object paramObject) {
    if (paramObject == null)
      return null; 
    if (paramObject instanceof InitialNameService)
      return (InitialNameService)paramObject; 
    if (!paramObject._is_a(id()))
      throw new BAD_PARAM(); 
    Delegate delegate = ((ObjectImpl)paramObject)._get_delegate();
    _InitialNameServiceStub _InitialNameServiceStub = new _InitialNameServiceStub();
    _InitialNameServiceStub._set_delegate(delegate);
    return _InitialNameServiceStub;
  }
  
  public static InitialNameService unchecked_narrow(Object paramObject) {
    if (paramObject == null)
      return null; 
    if (paramObject instanceof InitialNameService)
      return (InitialNameService)paramObject; 
    Delegate delegate = ((ObjectImpl)paramObject)._get_delegate();
    _InitialNameServiceStub _InitialNameServiceStub = new _InitialNameServiceStub();
    _InitialNameServiceStub._set_delegate(delegate);
    return _InitialNameServiceStub;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\InitialNameServiceHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */