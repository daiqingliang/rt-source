package org.omg.CORBA;

import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;

public abstract class IDLTypeHelper {
  private static String _id = "IDL:omg.org/CORBA/IDLType:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, IDLType paramIDLType) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramIDLType);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static IDLType extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      __typeCode = ORB.init().create_interface_tc(id(), "IDLType"); 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static IDLType read(InputStream paramInputStream) { return narrow(paramInputStream.read_Object(_IDLTypeStub.class)); }
  
  public static void write(OutputStream paramOutputStream, IDLType paramIDLType) { paramOutputStream.write_Object(paramIDLType); }
  
  public static IDLType narrow(Object paramObject) {
    if (paramObject == null)
      return null; 
    if (paramObject instanceof IDLType)
      return (IDLType)paramObject; 
    if (!paramObject._is_a(id()))
      throw new BAD_PARAM(); 
    Delegate delegate = ((ObjectImpl)paramObject)._get_delegate();
    return new _IDLTypeStub(delegate);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\IDLTypeHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */