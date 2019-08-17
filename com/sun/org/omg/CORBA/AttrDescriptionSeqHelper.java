package com.sun.org.omg.CORBA;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class AttrDescriptionSeqHelper {
  private static String _id = "IDL:omg.org/CORBA/AttrDescriptionSeq:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, AttributeDescription[] paramArrayOfAttributeDescription) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramArrayOfAttributeDescription);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static AttributeDescription[] extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null) {
      __typeCode = AttributeDescriptionHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "AttrDescriptionSeq", __typeCode);
    } 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static AttributeDescription[] read(InputStream paramInputStream) {
    AttributeDescription[] arrayOfAttributeDescription = null;
    int i = paramInputStream.read_long();
    arrayOfAttributeDescription = new AttributeDescription[i];
    for (byte b = 0; b < arrayOfAttributeDescription.length; b++)
      arrayOfAttributeDescription[b] = AttributeDescriptionHelper.read(paramInputStream); 
    return arrayOfAttributeDescription;
  }
  
  public static void write(OutputStream paramOutputStream, AttributeDescription[] paramArrayOfAttributeDescription) {
    paramOutputStream.write_long(paramArrayOfAttributeDescription.length);
    for (byte b = 0; b < paramArrayOfAttributeDescription.length; b++)
      AttributeDescriptionHelper.write(paramOutputStream, paramArrayOfAttributeDescription[b]); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\omg\CORBA\AttrDescriptionSeqHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */