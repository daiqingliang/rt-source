package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class NotFoundReasonHelper {
  private static String _id = "IDL:omg.org/CosNaming/NamingContext/NotFoundReason:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, NotFoundReason paramNotFoundReason) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramNotFoundReason);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static NotFoundReason extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      __typeCode = ORB.init().create_enum_tc(id(), "NotFoundReason", new String[] { "missing_node", "not_context", "not_object" }); 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static NotFoundReason read(InputStream paramInputStream) { return NotFoundReason.from_int(paramInputStream.read_long()); }
  
  public static void write(OutputStream paramOutputStream, NotFoundReason paramNotFoundReason) { paramOutputStream.write_long(paramNotFoundReason.value()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CosNaming\NamingContextPackage\NotFoundReasonHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */