package org.omg.DynamicAny;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class DynAnySeqHelper {
  private static String _id = "IDL:omg.org/DynamicAny/DynAnySeq:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, DynAny[] paramArrayOfDynAny) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramArrayOfDynAny);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static DynAny[] extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null) {
      __typeCode = DynAnyHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "DynAnySeq", __typeCode);
    } 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static DynAny[] read(InputStream paramInputStream) {
    DynAny[] arrayOfDynAny = null;
    int i = paramInputStream.read_long();
    arrayOfDynAny = new DynAny[i];
    for (byte b = 0; b < arrayOfDynAny.length; b++)
      arrayOfDynAny[b] = DynAnyHelper.read(paramInputStream); 
    return arrayOfDynAny;
  }
  
  public static void write(OutputStream paramOutputStream, DynAny[] paramArrayOfDynAny) {
    paramOutputStream.write_long(paramArrayOfDynAny.length);
    for (byte b = 0; b < paramArrayOfDynAny.length; b++)
      DynAnyHelper.write(paramOutputStream, paramArrayOfDynAny[b]); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\DynamicAny\DynAnySeqHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */