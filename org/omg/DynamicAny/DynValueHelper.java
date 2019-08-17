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

public abstract class DynValueHelper {
  private static String _id = "IDL:omg.org/DynamicAny/DynValue:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, DynValue paramDynValue) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramDynValue);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static DynValue extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      __typeCode = ORB.init().create_interface_tc(id(), "DynValue"); 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static DynValue read(InputStream paramInputStream) { throw new MARSHAL(); }
  
  public static void write(OutputStream paramOutputStream, DynValue paramDynValue) { throw new MARSHAL(); }
  
  public static DynValue narrow(Object paramObject) {
    if (paramObject == null)
      return null; 
    if (paramObject instanceof DynValue)
      return (DynValue)paramObject; 
    if (!paramObject._is_a(id()))
      throw new BAD_PARAM(); 
    Delegate delegate = ((ObjectImpl)paramObject)._get_delegate();
    _DynValueStub _DynValueStub = new _DynValueStub();
    _DynValueStub._set_delegate(delegate);
    return _DynValueStub;
  }
  
  public static DynValue unchecked_narrow(Object paramObject) {
    if (paramObject == null)
      return null; 
    if (paramObject instanceof DynValue)
      return (DynValue)paramObject; 
    Delegate delegate = ((ObjectImpl)paramObject)._get_delegate();
    _DynValueStub _DynValueStub = new _DynValueStub();
    _DynValueStub._set_delegate(delegate);
    return _DynValueStub;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\DynamicAny\DynValueHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */