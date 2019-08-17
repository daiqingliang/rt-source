package org.omg.IOP;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class MultipleComponentProfileHelper {
  private static String _id = "IDL:omg.org/IOP/MultipleComponentProfile:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, TaggedComponent[] paramArrayOfTaggedComponent) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramArrayOfTaggedComponent);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static TaggedComponent[] extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null) {
      __typeCode = TaggedComponentHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "MultipleComponentProfile", __typeCode);
    } 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static TaggedComponent[] read(InputStream paramInputStream) {
    TaggedComponent[] arrayOfTaggedComponent = null;
    int i = paramInputStream.read_long();
    arrayOfTaggedComponent = new TaggedComponent[i];
    for (byte b = 0; b < arrayOfTaggedComponent.length; b++)
      arrayOfTaggedComponent[b] = TaggedComponentHelper.read(paramInputStream); 
    return arrayOfTaggedComponent;
  }
  
  public static void write(OutputStream paramOutputStream, TaggedComponent[] paramArrayOfTaggedComponent) {
    paramOutputStream.write_long(paramArrayOfTaggedComponent.length);
    for (byte b = 0; b < paramArrayOfTaggedComponent.length; b++)
      TaggedComponentHelper.write(paramOutputStream, paramArrayOfTaggedComponent[b]); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\IOP\MultipleComponentProfileHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */