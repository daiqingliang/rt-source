package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class WStringSeqHelper {
  private static String _id = "IDL:omg.org/CORBA/WStringSeq:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, String[] paramArrayOfString) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramArrayOfString);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static String[] extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null) {
      __typeCode = ORB.init().create_wstring_tc(0);
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "WStringSeq", __typeCode);
    } 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static String[] read(InputStream paramInputStream) {
    String[] arrayOfString = null;
    int i = paramInputStream.read_long();
    arrayOfString = new String[i];
    for (byte b = 0; b < arrayOfString.length; b++)
      arrayOfString[b] = paramInputStream.read_wstring(); 
    return arrayOfString;
  }
  
  public static void write(OutputStream paramOutputStream, String[] paramArrayOfString) {
    paramOutputStream.write_long(paramArrayOfString.length);
    for (byte b = 0; b < paramArrayOfString.length; b++)
      paramOutputStream.write_wstring(paramArrayOfString[b]); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\WStringSeqHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */