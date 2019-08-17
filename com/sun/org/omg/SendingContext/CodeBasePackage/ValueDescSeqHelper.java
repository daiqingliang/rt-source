package com.sun.org.omg.SendingContext.CodeBasePackage;

import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescriptionHelper;
import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class ValueDescSeqHelper {
  private static String _id = "IDL:omg.org/SendingContext/CodeBase/ValueDescSeq:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, FullValueDescription[] paramArrayOfFullValueDescription) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramArrayOfFullValueDescription);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static FullValueDescription[] extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null) {
      __typeCode = FullValueDescriptionHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "ValueDescSeq", __typeCode);
    } 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static FullValueDescription[] read(InputStream paramInputStream) {
    FullValueDescription[] arrayOfFullValueDescription = null;
    int i = paramInputStream.read_long();
    arrayOfFullValueDescription = new FullValueDescription[i];
    for (byte b = 0; b < arrayOfFullValueDescription.length; b++)
      arrayOfFullValueDescription[b] = FullValueDescriptionHelper.read(paramInputStream); 
    return arrayOfFullValueDescription;
  }
  
  public static void write(OutputStream paramOutputStream, FullValueDescription[] paramArrayOfFullValueDescription) {
    paramOutputStream.write_long(paramArrayOfFullValueDescription.length);
    for (byte b = 0; b < paramArrayOfFullValueDescription.length; b++)
      FullValueDescriptionHelper.write(paramOutputStream, paramArrayOfFullValueDescription[b]); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\omg\SendingContext\CodeBasePackage\ValueDescSeqHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */