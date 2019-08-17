package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ValueMemberHelper {
  private static String _id = "IDL:omg.org/CORBA/ValueMember:1.0";
  
  private static TypeCode __typeCode = null;
  
  private static boolean __active = false;
  
  public static void insert(Any paramAny, ValueMember paramValueMember) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramValueMember);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static ValueMember extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      synchronized (TypeCode.class) {
        if (__typeCode == null) {
          if (__active)
            return ORB.init().create_recursive_tc(_id); 
          __active = true;
          StructMember[] arrayOfStructMember = new StructMember[7];
          TypeCode typeCode = null;
          typeCode = ORB.init().create_string_tc(0);
          typeCode = ORB.init().create_alias_tc(IdentifierHelper.id(), "Identifier", typeCode);
          arrayOfStructMember[0] = new StructMember("name", typeCode, null);
          typeCode = ORB.init().create_string_tc(0);
          typeCode = ORB.init().create_alias_tc(RepositoryIdHelper.id(), "RepositoryId", typeCode);
          arrayOfStructMember[1] = new StructMember("id", typeCode, null);
          typeCode = ORB.init().create_string_tc(0);
          typeCode = ORB.init().create_alias_tc(RepositoryIdHelper.id(), "RepositoryId", typeCode);
          arrayOfStructMember[2] = new StructMember("defined_in", typeCode, null);
          typeCode = ORB.init().create_string_tc(0);
          typeCode = ORB.init().create_alias_tc(VersionSpecHelper.id(), "VersionSpec", typeCode);
          arrayOfStructMember[3] = new StructMember("version", typeCode, null);
          typeCode = ORB.init().get_primitive_tc(TCKind.tk_TypeCode);
          arrayOfStructMember[4] = new StructMember("type", typeCode, null);
          typeCode = IDLTypeHelper.type();
          arrayOfStructMember[5] = new StructMember("type_def", typeCode, null);
          typeCode = ORB.init().get_primitive_tc(TCKind.tk_short);
          typeCode = ORB.init().create_alias_tc(VisibilityHelper.id(), "Visibility", typeCode);
          arrayOfStructMember[6] = new StructMember("access", typeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "ValueMember", arrayOfStructMember);
          __active = false;
        } 
      }  
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static ValueMember read(InputStream paramInputStream) {
    ValueMember valueMember = new ValueMember();
    valueMember.name = paramInputStream.read_string();
    valueMember.id = paramInputStream.read_string();
    valueMember.defined_in = paramInputStream.read_string();
    valueMember.version = paramInputStream.read_string();
    valueMember.type = paramInputStream.read_TypeCode();
    valueMember.type_def = IDLTypeHelper.read(paramInputStream);
    valueMember.access = paramInputStream.read_short();
    return valueMember;
  }
  
  public static void write(OutputStream paramOutputStream, ValueMember paramValueMember) {
    paramOutputStream.write_string(paramValueMember.name);
    paramOutputStream.write_string(paramValueMember.id);
    paramOutputStream.write_string(paramValueMember.defined_in);
    paramOutputStream.write_string(paramValueMember.version);
    paramOutputStream.write_TypeCode(paramValueMember.type);
    IDLTypeHelper.write(paramOutputStream, paramValueMember.type_def);
    paramOutputStream.write_short(paramValueMember.access);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\ValueMemberHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */