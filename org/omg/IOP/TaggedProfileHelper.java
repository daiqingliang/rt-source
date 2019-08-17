package org.omg.IOP;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class TaggedProfileHelper {
  private static String _id = "IDL:omg.org/IOP/TaggedProfile:1.0";
  
  private static TypeCode __typeCode = null;
  
  private static boolean __active = false;
  
  public static void insert(Any paramAny, TaggedProfile paramTaggedProfile) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramTaggedProfile);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static TaggedProfile extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
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
          typeCode = ORB.init().create_alias_tc(ProfileIdHelper.id(), "ProfileId", typeCode);
          arrayOfStructMember[0] = new StructMember("tag", typeCode, null);
          typeCode = ORB.init().get_primitive_tc(TCKind.tk_octet);
          typeCode = ORB.init().create_sequence_tc(0, typeCode);
          arrayOfStructMember[1] = new StructMember("profile_data", typeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "TaggedProfile", arrayOfStructMember);
          __active = false;
        } 
      }  
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static TaggedProfile read(InputStream paramInputStream) {
    TaggedProfile taggedProfile = new TaggedProfile();
    taggedProfile.tag = paramInputStream.read_ulong();
    int i = paramInputStream.read_long();
    taggedProfile.profile_data = new byte[i];
    paramInputStream.read_octet_array(taggedProfile.profile_data, 0, i);
    return taggedProfile;
  }
  
  public static void write(OutputStream paramOutputStream, TaggedProfile paramTaggedProfile) {
    paramOutputStream.write_ulong(paramTaggedProfile.tag);
    paramOutputStream.write_long(paramTaggedProfile.profile_data.length);
    paramOutputStream.write_octet_array(paramTaggedProfile.profile_data, 0, paramTaggedProfile.profile_data.length);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\IOP\TaggedProfileHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */