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

public abstract class DynAnyHelper {
  private static String _id = "IDL:omg.org/DynamicAny/DynAny:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, DynAny paramDynAny) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramDynAny);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static DynAny extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      __typeCode = ORB.init().create_interface_tc(id(), "DynAny"); 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static DynAny read(InputStream paramInputStream) { throw new MARSHAL(); }
  
  public static void write(OutputStream paramOutputStream, DynAny paramDynAny) { throw new MARSHAL(); }
  
  public static DynAny narrow(Object paramObject) {
    if (paramObject == null)
      return null; 
    if (paramObject instanceof DynAny)
      return (DynAny)paramObject; 
    if (!paramObject._is_a(id()))
      throw new BAD_PARAM(); 
    Delegate delegate = ((ObjectImpl)paramObject)._get_delegate();
    _DynAnyStub _DynAnyStub = new _DynAnyStub();
    _DynAnyStub._set_delegate(delegate);
    return _DynAnyStub;
  }
  
  public static DynAny unchecked_narrow(Object paramObject) {
    if (paramObject == null)
      return null; 
    if (paramObject instanceof DynAny)
      return (DynAny)paramObject; 
    Delegate delegate = ((ObjectImpl)paramObject)._get_delegate();
    _DynAnyStub _DynAnyStub = new _DynAnyStub();
    _DynAnyStub._set_delegate(delegate);
    return _DynAnyStub;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\DynamicAny\DynAnyHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */