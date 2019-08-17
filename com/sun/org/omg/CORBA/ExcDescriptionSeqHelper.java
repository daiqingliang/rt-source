package com.sun.org.omg.CORBA;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class ExcDescriptionSeqHelper {
  private static String _id = "IDL:omg.org/CORBA/ExcDescriptionSeq:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, ExceptionDescription[] paramArrayOfExceptionDescription) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramArrayOfExceptionDescription);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static ExceptionDescription[] extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null) {
      __typeCode = ExceptionDescriptionHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "ExcDescriptionSeq", __typeCode);
    } 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static ExceptionDescription[] read(InputStream paramInputStream) {
    ExceptionDescription[] arrayOfExceptionDescription = null;
    int i = paramInputStream.read_long();
    arrayOfExceptionDescription = new ExceptionDescription[i];
    for (byte b = 0; b < arrayOfExceptionDescription.length; b++)
      arrayOfExceptionDescription[b] = ExceptionDescriptionHelper.read(paramInputStream); 
    return arrayOfExceptionDescription;
  }
  
  public static void write(OutputStream paramOutputStream, ExceptionDescription[] paramArrayOfExceptionDescription) {
    paramOutputStream.write_long(paramArrayOfExceptionDescription.length);
    for (byte b = 0; b < paramArrayOfExceptionDescription.length; b++)
      ExceptionDescriptionHelper.write(paramOutputStream, paramArrayOfExceptionDescription[b]); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\omg\CORBA\ExcDescriptionSeqHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */