package org.omg.CosNaming;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class NameHelper {
  private static String _id = "IDL:omg.org/CosNaming/Name:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, NameComponent[] paramArrayOfNameComponent) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramArrayOfNameComponent);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static NameComponent[] extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null) {
      __typeCode = NameComponentHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "Name", __typeCode);
    } 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static NameComponent[] read(InputStream paramInputStream) {
    NameComponent[] arrayOfNameComponent = null;
    int i = paramInputStream.read_long();
    arrayOfNameComponent = new NameComponent[i];
    for (byte b = 0; b < arrayOfNameComponent.length; b++)
      arrayOfNameComponent[b] = NameComponentHelper.read(paramInputStream); 
    return arrayOfNameComponent;
  }
  
  public static void write(OutputStream paramOutputStream, NameComponent[] paramArrayOfNameComponent) {
    paramOutputStream.write_long(paramArrayOfNameComponent.length);
    for (byte b = 0; b < paramArrayOfNameComponent.length; b++)
      NameComponentHelper.write(paramOutputStream, paramArrayOfNameComponent[b]); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CosNaming\NameHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */