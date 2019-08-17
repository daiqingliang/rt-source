package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class UnionMemberHelper {
  private static String _id = "IDL:omg.org/CORBA/UnionMember:1.0";
  
  private static TypeCode __typeCode = null;
  
  private static boolean __active = false;
  
  public static void insert(Any paramAny, UnionMember paramUnionMember) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramUnionMember);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static UnionMember extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      synchronized (TypeCode.class) {
        if (__typeCode == null) {
          if (__active)
            return ORB.init().create_recursive_tc(_id); 
          __active = true;
          StructMember[] arrayOfStructMember = new StructMember[4];
          TypeCode typeCode = null;
          typeCode = ORB.init().create_string_tc(0);
          typeCode = ORB.init().create_alias_tc(IdentifierHelper.id(), "Identifier", typeCode);
          arrayOfStructMember[0] = new StructMember("name", typeCode, null);
          typeCode = ORB.init().get_primitive_tc(TCKind.tk_any);
          arrayOfStructMember[1] = new StructMember("label", typeCode, null);
          typeCode = ORB.init().get_primitive_tc(TCKind.tk_TypeCode);
          arrayOfStructMember[2] = new StructMember("type", typeCode, null);
          typeCode = IDLTypeHelper.type();
          arrayOfStructMember[3] = new StructMember("type_def", typeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "UnionMember", arrayOfStructMember);
          __active = false;
        } 
      }  
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static UnionMember read(InputStream paramInputStream) {
    UnionMember unionMember = new UnionMember();
    unionMember.name = paramInputStream.read_string();
    unionMember.label = paramInputStream.read_any();
    unionMember.type = paramInputStream.read_TypeCode();
    unionMember.type_def = IDLTypeHelper.read(paramInputStream);
    return unionMember;
  }
  
  public static void write(OutputStream paramOutputStream, UnionMember paramUnionMember) {
    paramOutputStream.write_string(paramUnionMember.name);
    paramOutputStream.write_any(paramUnionMember.label);
    paramOutputStream.write_TypeCode(paramUnionMember.type);
    IDLTypeHelper.write(paramOutputStream, paramUnionMember.type_def);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\UnionMemberHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */