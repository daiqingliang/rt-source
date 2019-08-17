package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class AnySeqHelper {
  private static String _id = "IDL:omg.org/CORBA/AnySeq:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, Any[] paramArrayOfAny) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramArrayOfAny);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static Any[] extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null) {
      __typeCode = ORB.init().get_primitive_tc(TCKind.tk_any);
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "AnySeq", __typeCode);
    } 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static Any[] read(InputStream paramInputStream) {
    Any[] arrayOfAny = null;
    int i = paramInputStream.read_long();
    arrayOfAny = new Any[i];
    for (byte b = 0; b < arrayOfAny.length; b++)
      arrayOfAny[b] = paramInputStream.read_any(); 
    return arrayOfAny;
  }
  
  public static void write(OutputStream paramOutputStream, Any[] paramArrayOfAny) {
    paramOutputStream.write_long(paramArrayOfAny.length);
    for (byte b = 0; b < paramArrayOfAny.length; b++)
      paramOutputStream.write_any(paramArrayOfAny[b]); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\AnySeqHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */