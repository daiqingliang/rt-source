package com.sun.org.omg.CORBA;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class StructMemberHelper {
  private static String _id = "IDL:omg.org/CORBA/StructMember:1.0";
  
  private static TypeCode __typeCode = null;
  
  private static boolean __active = false;
  
  public static void insert(Any paramAny, StructMember paramStructMember) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramStructMember);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static StructMember extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      synchronized (TypeCode.class) {
        if (__typeCode == null) {
          if (__active)
            return ORB.init().create_recursive_tc(_id); 
          __active = true;
          StructMember[] arrayOfStructMember = new StructMember[3];
          TypeCode typeCode = null;
          typeCode = ORB.init().create_string_tc(0);
          typeCode = ORB.init().create_alias_tc(IdentifierHelper.id(), "Identifier", typeCode);
          arrayOfStructMember[0] = new StructMember("name", typeCode, null);
          typeCode = ORB.init().get_primitive_tc(TCKind.tk_TypeCode);
          arrayOfStructMember[1] = new StructMember("type", typeCode, null);
          typeCode = IDLTypeHelper.type();
          arrayOfStructMember[2] = new StructMember("type_def", typeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "StructMember", arrayOfStructMember);
          __active = false;
        } 
      }  
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static StructMember read(InputStream paramInputStream) {
    StructMember structMember = new StructMember();
    structMember.name = paramInputStream.read_string();
    structMember.type = paramInputStream.read_TypeCode();
    structMember.type_def = IDLTypeHelper.read(paramInputStream);
    return structMember;
  }
  
  public static void write(OutputStream paramOutputStream, StructMember paramStructMember) {
    paramOutputStream.write_string(paramStructMember.name);
    paramOutputStream.write_TypeCode(paramStructMember.type);
    IDLTypeHelper.write(paramOutputStream, paramStructMember.type_def);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\omg\CORBA\StructMemberHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */