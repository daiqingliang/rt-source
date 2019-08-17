package org.omg.PortableInterceptor;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ObjectReferenceTemplateSeqHelper {
  private static String _id = "IDL:omg.org/PortableInterceptor/ObjectReferenceTemplateSeq:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, ObjectReferenceTemplate[] paramArrayOfObjectReferenceTemplate) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramArrayOfObjectReferenceTemplate);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static ObjectReferenceTemplate[] extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null) {
      __typeCode = ObjectReferenceTemplateHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "ObjectReferenceTemplateSeq", __typeCode);
    } 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static ObjectReferenceTemplate[] read(InputStream paramInputStream) {
    ObjectReferenceTemplate[] arrayOfObjectReferenceTemplate = null;
    int i = paramInputStream.read_long();
    arrayOfObjectReferenceTemplate = new ObjectReferenceTemplate[i];
    for (byte b = 0; b < arrayOfObjectReferenceTemplate.length; b++)
      arrayOfObjectReferenceTemplate[b] = ObjectReferenceTemplateHelper.read(paramInputStream); 
    return arrayOfObjectReferenceTemplate;
  }
  
  public static void write(OutputStream paramOutputStream, ObjectReferenceTemplate[] paramArrayOfObjectReferenceTemplate) {
    paramOutputStream.write_long(paramArrayOfObjectReferenceTemplate.length);
    for (byte b = 0; b < paramArrayOfObjectReferenceTemplate.length; b++)
      ObjectReferenceTemplateHelper.write(paramOutputStream, paramArrayOfObjectReferenceTemplate[b]); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableInterceptor\ObjectReferenceTemplateSeqHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */