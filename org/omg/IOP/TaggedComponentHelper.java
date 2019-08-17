package org.omg.IOP;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class TaggedComponentHelper {
  private static String _id = "IDL:omg.org/IOP/TaggedComponent:1.0";
  
  private static TypeCode __typeCode = null;
  
  private static boolean __active = false;
  
  public static void insert(Any paramAny, TaggedComponent paramTaggedComponent) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramTaggedComponent);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static TaggedComponent extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      synchronized (TypeCode.class) {
        if (__typeCode == null) {
          if (__active)
            return ORB.init().create_recursive_tc(_id); 
          __active = true;
          StructMember[] arrayOfStructMember = new StructMember[2];
          TypeCode typeCode = null;
          typeCode = ORB.init().get_primitive_tc(TCKind.tk_ulong);
          typeCode = ORB.init().create_alias_tc(ComponentIdHelper.id(), "ComponentId", typeCode);
          arrayOfStructMember[0] = new StructMember("tag", typeCode, null);
          typeCode = ORB.init().get_primitive_tc(TCKind.tk_octet);
          typeCode = ORB.init().create_sequence_tc(0, typeCode);
          arrayOfStructMember[1] = new StructMember("component_data", typeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "TaggedComponent", arrayOfStructMember);
          __active = false;
        } 
      }  
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static TaggedComponent read(InputStream paramInputStream) {
    TaggedComponent taggedComponent = new TaggedComponent();
    taggedComponent.tag = paramInputStream.read_ulong();
    int i = paramInputStream.read_long();
    taggedComponent.component_data = new byte[i];
    paramInputStream.read_octet_array(taggedComponent.component_data, 0, i);
    return taggedComponent;
  }
  
  public static void write(OutputStream paramOutputStream, TaggedComponent paramTaggedComponent) {
    paramOutputStream.write_ulong(paramTaggedComponent.tag);
    paramOutputStream.write_long(paramTaggedComponent.component_data.length);
    paramOutputStream.write_octet_array(paramTaggedComponent.component_data, 0, paramTaggedComponent.component_data.length);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\IOP\TaggedComponentHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */