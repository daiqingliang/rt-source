package org.omg.PortableServer;

import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class CurrentHelper {
  private static String _id = "IDL:omg.org/PortableServer/Current:2.3";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, Current paramCurrent) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramCurrent);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static Current extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      __typeCode = ORB.init().create_interface_tc(id(), "Current"); 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static Current read(InputStream paramInputStream) { throw new MARSHAL(); }
  
  public static void write(OutputStream paramOutputStream, Current paramCurrent) { throw new MARSHAL(); }
  
  public static Current narrow(Object paramObject) {
    if (paramObject == null)
      return null; 
    if (paramObject instanceof Current)
      return (Current)paramObject; 
    if (!paramObject._is_a(id()))
      throw new BAD_PARAM(); 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableServer\CurrentHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */