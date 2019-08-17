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

public abstract class DynSequenceHelper {
  private static String _id = "IDL:omg.org/DynamicAny/DynSequence:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, DynSequence paramDynSequence) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramDynSequence);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static DynSequence extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      __typeCode = ORB.init().create_interface_tc(id(), "DynSequence"); 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static DynSequence read(InputStream paramInputStream) { throw new MARSHAL(); }
  
  public static void write(OutputStream paramOutputStream, DynSequence paramDynSequence) { throw new MARSHAL(); }
  
  public static DynSequence narrow(Object paramObject) {
    if (paramObject == null)
      return null; 
    if (paramObject instanceof DynSequence)
      return (DynSequence)paramObject; 
    if (!paramObject._is_a(id()))
      throw new BAD_PARAM(); 
    Delegate delegate = ((ObjectImpl)paramObject)._get_delegate();
    _DynSequenceStub _DynSequenceStub = new _DynSequenceStub();
    _DynSequenceStub._set_delegate(delegate);
    return _DynSequenceStub;
  }
  
  public static DynSequence unchecked_narrow(Object paramObject) {
    if (paramObject == null)
      return null; 
    if (paramObject instanceof DynSequence)
      return (DynSequence)paramObject; 
    Delegate delegate = ((ObjectImpl)paramObject)._get_delegate();
    _DynSequenceStub _DynSequenceStub = new _DynSequenceStub();
    _DynSequenceStub._set_delegate(delegate);
    return _DynSequenceStub;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\DynamicAny\DynSequenceHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */