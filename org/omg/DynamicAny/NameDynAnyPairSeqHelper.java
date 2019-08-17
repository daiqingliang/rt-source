package org.omg.DynamicAny;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class NameDynAnyPairSeqHelper {
  private static String _id = "IDL:omg.org/DynamicAny/NameDynAnyPairSeq:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, NameDynAnyPair[] paramArrayOfNameDynAnyPair) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramArrayOfNameDynAnyPair);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static NameDynAnyPair[] extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null) {
      __typeCode = NameDynAnyPairHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "NameDynAnyPairSeq", __typeCode);
    } 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static NameDynAnyPair[] read(InputStream paramInputStream) {
    NameDynAnyPair[] arrayOfNameDynAnyPair = null;
    int i = paramInputStream.read_long();
    arrayOfNameDynAnyPair = new NameDynAnyPair[i];
    for (byte b = 0; b < arrayOfNameDynAnyPair.length; b++)
      arrayOfNameDynAnyPair[b] = NameDynAnyPairHelper.read(paramInputStream); 
    return arrayOfNameDynAnyPair;
  }
  
  public static void write(OutputStream paramOutputStream, NameDynAnyPair[] paramArrayOfNameDynAnyPair) {
    paramOutputStream.write_long(paramArrayOfNameDynAnyPair.length);
    for (byte b = 0; b < paramArrayOfNameDynAnyPair.length; b++)
      NameDynAnyPairHelper.write(paramOutputStream, paramArrayOfNameDynAnyPair[b]); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\DynamicAny\NameDynAnyPairSeqHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */