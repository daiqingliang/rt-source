package com.sun.org.omg.CORBA;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class OpDescriptionSeqHelper {
  private static String _id = "IDL:omg.org/CORBA/OpDescriptionSeq:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, OperationDescription[] paramArrayOfOperationDescription) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramArrayOfOperationDescription);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static OperationDescription[] extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null) {
      __typeCode = OperationDescriptionHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "OpDescriptionSeq", __typeCode);
    } 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static OperationDescription[] read(InputStream paramInputStream) {
    OperationDescription[] arrayOfOperationDescription = null;
    int i = paramInputStream.read_long();
    arrayOfOperationDescription = new OperationDescription[i];
    for (byte b = 0; b < arrayOfOperationDescription.length; b++)
      arrayOfOperationDescription[b] = OperationDescriptionHelper.read(paramInputStream); 
    return arrayOfOperationDescription;
  }
  
  public static void write(OutputStream paramOutputStream, OperationDescription[] paramArrayOfOperationDescription) {
    paramOutputStream.write_long(paramArrayOfOperationDescription.length);
    for (byte b = 0; b < paramArrayOfOperationDescription.length; b++)
      OperationDescriptionHelper.write(paramOutputStream, paramArrayOfOperationDescription[b]); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\omg\CORBA\OpDescriptionSeqHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */