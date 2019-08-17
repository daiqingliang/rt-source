package com.sun.org.omg.CORBA;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class ParDescriptionSeqHelper {
  private static String _id = "IDL:omg.org/CORBA/ParDescriptionSeq:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, ParameterDescription[] paramArrayOfParameterDescription) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramArrayOfParameterDescription);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static ParameterDescription[] extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null) {
      __typeCode = ParameterDescriptionHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "ParDescriptionSeq", __typeCode);
    } 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static ParameterDescription[] read(InputStream paramInputStream) {
    ParameterDescription[] arrayOfParameterDescription = null;
    int i = paramInputStream.read_long();
    arrayOfParameterDescription = new ParameterDescription[i];
    for (byte b = 0; b < arrayOfParameterDescription.length; b++)
      arrayOfParameterDescription[b] = ParameterDescriptionHelper.read(paramInputStream); 
    return arrayOfParameterDescription;
  }
  
  public static void write(OutputStream paramOutputStream, ParameterDescription[] paramArrayOfParameterDescription) {
    paramOutputStream.write_long(paramArrayOfParameterDescription.length);
    for (byte b = 0; b < paramArrayOfParameterDescription.length; b++)
      ParameterDescriptionHelper.write(paramOutputStream, paramArrayOfParameterDescription[b]); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\omg\CORBA\ParDescriptionSeqHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */