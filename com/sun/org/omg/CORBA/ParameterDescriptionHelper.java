package com.sun.org.omg.CORBA;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class ParameterDescriptionHelper {
  private static String _id = "IDL:omg.org/CORBA/ParameterDescription:1.0";
  
  private static TypeCode __typeCode = null;
  
  private static boolean __active = false;
  
  public static void insert(Any paramAny, ParameterDescription paramParameterDescription) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramParameterDescription);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static ParameterDescription extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
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
          typeCode = ORB.init().get_primitive_tc(TCKind.tk_TypeCode);
          arrayOfStructMember[1] = new StructMember("type", typeCode, null);
          typeCode = IDLTypeHelper.type();
          arrayOfStructMember[2] = new StructMember("type_def", typeCode, null);
          typeCode = ParameterModeHelper.type();
          arrayOfStructMember[3] = new StructMember("mode", typeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "ParameterDescription", arrayOfStructMember);
          __active = false;
        } 
      }  
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static ParameterDescription read(InputStream paramInputStream) {
    ParameterDescription parameterDescription = new ParameterDescription();
    parameterDescription.name = paramInputStream.read_string();
    parameterDescription.type = paramInputStream.read_TypeCode();
    parameterDescription.type_def = IDLTypeHelper.read(paramInputStream);
    parameterDescription.mode = ParameterModeHelper.read(paramInputStream);
    return parameterDescription;
  }
  
  public static void write(OutputStream paramOutputStream, ParameterDescription paramParameterDescription) {
    paramOutputStream.write_string(paramParameterDescription.name);
    paramOutputStream.write_TypeCode(paramParameterDescription.type);
    IDLTypeHelper.write(paramOutputStream, paramParameterDescription.type_def);
    ParameterModeHelper.write(paramOutputStream, paramParameterDescription.mode);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\omg\CORBA\ParameterDescriptionHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */