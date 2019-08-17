package org.omg.DynamicAny;

import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;

public abstract class DynAnyFactoryHelper {
  private static String _id = "IDL:omg.org/DynamicAny/DynAnyFactory:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, DynAnyFactory paramDynAnyFactory) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramDynAnyFactory);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static DynAnyFactory extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      __typeCode = ORB.init().create_interface_tc(id(), "DynAnyFactory"); 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static DynAnyFactory read(InputStream paramInputStream) { throw new MARSHAL(); }
  
  public static void write(OutputStream paramOutputStream, DynAnyFactory paramDynAnyFactory) { throw new MARSHAL(); }
  
  public static DynAnyFactory narrow(Object paramObject) {
    if (paramObject == null)
      return null; 
    if (paramObject instanceof DynAnyFactory)
      return (DynAnyFactory)paramObject; 
    if (!paramObject._is_a(id()))
      throw new BAD_PARAM(); 
    Delegate delegate = ((ObjectImpl)paramObject)._get_delegate();
    _DynAnyFactoryStub _DynAnyFactoryStub = new _DynAnyFactoryStub();
    _DynAnyFactoryStub._set_delegate(delegate);
    return _DynAnyFactoryStub;
  }
  
  public static DynAnyFactory unchecked_narrow(Object paramObject) {
    if (paramObject == null)
      return null; 
    if (paramObject instanceof DynAnyFactory)
      return (DynAnyFactory)paramObject; 
    Delegate delegate = ((ObjectImpl)paramObject)._get_delegate();
    _DynAnyFactoryStub _DynAnyFactoryStub = new _DynAnyFactoryStub();
    _DynAnyFactoryStub._set_delegate(delegate);
    return _DynAnyFactoryStub;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\DynamicAny\DynAnyFactoryHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */