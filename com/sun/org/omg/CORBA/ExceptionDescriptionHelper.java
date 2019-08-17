package com.sun.org.omg.CORBA;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class ExceptionDescriptionHelper {
  private static String _id = "IDL:omg.org/CORBA/ExceptionDescription:1.0";
  
  private static TypeCode __typeCode = null;
  
  private static boolean __active = false;
  
  public static void insert(Any paramAny, ExceptionDescription paramExceptionDescription) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramExceptionDescription);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static ExceptionDescription extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      synchronized (TypeCode.class) {
        if (__typeCode == null) {
          if (__active)
            return ORB.init().create_recursive_tc(_id); 
          __active = true;
          StructMember[] arrayOfStructMember = new StructMember[5];
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
          __typeCode = ORB.init().create_struct_tc(id(), "ExceptionDescription", arrayOfStructMember);
          __active = false;
        } 
      }  
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static ExceptionDescription read(InputStream paramInputStream) {
    ExceptionDescription exceptionDescription = new ExceptionDescription();
    exceptionDescription.name = paramInputStream.read_string();
    exceptionDescription.id = paramInputStream.read_string();
    exceptionDescription.defined_in = paramInputStream.read_string();
    exceptionDescription.version = paramInputStream.read_string();
    exceptionDescription.type = paramInputStream.read_TypeCode();
    return exceptionDescription;
  }
  
  public static void write(OutputStream paramOutputStream, ExceptionDescription paramExceptionDescription) {
    paramOutputStream.write_string(paramExceptionDescription.name);
    paramOutputStream.write_string(paramExceptionDescription.id);
    paramOutputStream.write_string(paramExceptionDescription.defined_in);
    paramOutputStream.write_string(paramExceptionDescription.version);
    paramOutputStream.write_TypeCode(paramExceptionDescription.type);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\omg\CORBA\ExceptionDescriptionHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */