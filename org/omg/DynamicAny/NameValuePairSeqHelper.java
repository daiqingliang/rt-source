package org.omg.DynamicAny;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class NameValuePairSeqHelper {
  private static String _id = "IDL:omg.org/DynamicAny/NameValuePairSeq:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, NameValuePair[] paramArrayOfNameValuePair) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramArrayOfNameValuePair);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static NameValuePair[] extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null) {
      __typeCode = NameValuePairHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "NameValuePairSeq", __typeCode);
    } 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static NameValuePair[] read(InputStream paramInputStream) {
    NameValuePair[] arrayOfNameValuePair = null;
    int i = paramInputStream.read_long();
    arrayOfNameValuePair = new NameValuePair[i];
    for (byte b = 0; b < arrayOfNameValuePair.length; b++)
      arrayOfNameValuePair[b] = NameValuePairHelper.read(paramInputStream); 
    return arrayOfNameValuePair;
  }
  
  public static void write(OutputStream paramOutputStream, NameValuePair[] paramArrayOfNameValuePair) {
    paramOutputStream.write_long(paramArrayOfNameValuePair.length);
    for (byte b = 0; b < paramArrayOfNameValuePair.length; b++)
      NameValuePairHelper.write(paramOutputStream, paramArrayOfNameValuePair[b]); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\DynamicAny\NameValuePairSeqHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */