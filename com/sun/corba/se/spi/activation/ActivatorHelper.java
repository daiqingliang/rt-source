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

public abstract class ActivatorHelper {
  private static String _id = "IDL:activation/Activator:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, Activator paramActivator) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramActivator);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static Activator extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      __typeCode = ORB.init().create_interface_tc(id(), "Activator"); 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static Activator read(InputStream paramInputStream) { return narrow(paramInputStream.read_Object(_ActivatorStub.class)); }
  
  public static void write(OutputStream paramOutputStream, Activator paramActivator) { paramOutputStream.write_Object(paramActivator); }
  
  public static Activator narrow(Object paramObject) {
    if (paramObject == null)
      return null; 
    if (paramObject instanceof Activator)
      return (Activator)paramObject; 
    if (!paramObject._is_a(id()))
      throw new BAD_PARAM(); 
    Delegate delegate = ((ObjectImpl)paramObject)._get_delegate();
    _ActivatorStub _ActivatorStub = new _ActivatorStub();
    _ActivatorStub._set_delegate(delegate);
    return _ActivatorStub;
  }
  
  public static Activator unchecked_narrow(Object paramObject) {
    if (paramObject == null)
      return null; 
    if (paramObject instanceof Activator)
      return (Activator)paramObject; 
    Delegate delegate = ((ObjectImpl)paramObject)._get_delegate();
    _ActivatorStub _ActivatorStub = new _ActivatorStub();
    _ActivatorStub._set_delegate(delegate);
    return _ActivatorStub;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\ActivatorHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */