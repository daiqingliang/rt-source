package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ULongLongSeqHelper {
  private static String _id = "IDL:omg.org/CORBA/ULongLongSeq:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, long[] paramArrayOfLong) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramArrayOfLong);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static long[] extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null) {
      __typeCode = ORB.init().get_primitive_tc(TCKind.tk_ulonglong);
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "ULongLongSeq", __typeCode);
    } 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static long[] read(InputStream paramInputStream) {
    long[] arrayOfLong = null;
    int i = paramInputStream.read_long();
    arrayOfLong = new long[i];
    paramInputStream.read_ulonglong_array(arrayOfLong, 0, i);
    return arrayOfLong;
  }
  
  public static void write(OutputStream paramOutputStream, long[] paramArrayOfLong) {
    paramOutputStream.write_long(paramArrayOfLong.length);
    paramOutputStream.write_ulonglong_array(paramArrayOfLong, 0, paramArrayOfLong.length);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\ULongLongSeqHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */