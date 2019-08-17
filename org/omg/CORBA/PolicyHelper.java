package org.omg.CORBA;

import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;

public abstract class PolicyHelper {
  private static String _id = "IDL:omg.org/CORBA/Policy:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, Policy paramPolicy) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramPolicy);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static Policy extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      __typeCode = ORB.init().create_interface_tc(id(), "Policy"); 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static Policy read(InputStream paramInputStream) { return narrow(paramInputStream.read_Object(_PolicyStub.class)); }
  
  public static void write(OutputStream paramOutputStream, Policy paramPolicy) { paramOutputStream.write_Object(paramPolicy); }
  
  public static Policy narrow(Object paramObject) {
    if (paramObject == null)
      return null; 
    if (paramObject instanceof Policy)
      return (Policy)paramObject; 
    if (!paramObject._is_a(id()))
      throw new BAD_PARAM(); 
    Delegate delegate = ((ObjectImpl)paramObject)._get_delegate();
    return new _PolicyStub(delegate);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\PolicyHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */