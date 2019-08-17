package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class OctetSeqHelper {
  private static String _id = "IDL:omg.org/CORBA/OctetSeq:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, byte[] paramArrayOfByte) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramArrayOfByte);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static byte[] extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null) {
      __typeCode = ORB.init().get_primitive_tc(TCKind.tk_octet);
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "OctetSeq", __typeCode);
    } 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static byte[] read(InputStream paramInputStream) {
    byte[] arrayOfByte = null;
    int i = paramInputStream.read_long();
    arrayOfByte = new byte[i];
    paramInputStream.read_octet_array(arrayOfByte, 0, i);
    return arrayOfByte;
  }
  
  public static void write(OutputStream paramOutputStream, byte[] paramArrayOfByte) {
    paramOutputStream.write_long(paramArrayOfByte.length);
    paramOutputStream.write_octet_array(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\OctetSeqHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */