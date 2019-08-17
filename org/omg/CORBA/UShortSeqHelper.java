package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class UShortSeqHelper {
  private static String _id = "IDL:omg.org/CORBA/UShortSeq:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, short[] paramArrayOfShort) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramArrayOfShort);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static short[] extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null) {
      __typeCode = ORB.init().get_primitive_tc(TCKind.tk_ushort);
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "UShortSeq", __typeCode);
    } 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static short[] read(InputStream paramInputStream) {
    short[] arrayOfShort = null;
    int i = paramInputStream.read_long();
    arrayOfShort = new short[i];
    paramInputStream.read_ushort_array(arrayOfShort, 0, i);
    return arrayOfShort;
  }
  
  public static void write(OutputStream paramOutputStream, short[] paramArrayOfShort) {
    paramOutputStream.write_long(paramArrayOfShort.length);
    paramOutputStream.write_ushort_array(paramArrayOfShort, 0, paramArrayOfShort.length);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\UShortSeqHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */