package com.sun.org.omg.CORBA;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class OperationDescriptionHelper {
  private static String _id = "IDL:omg.org/CORBA/OperationDescription:1.0";
  
  private static TypeCode __typeCode = null;
  
  private static boolean __active = false;
  
  public static void insert(Any paramAny, OperationDescription paramOperationDescription) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramOperationDescription);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static OperationDescription extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      synchronized (TypeCode.class) {
        if (__typeCode == null) {
          if (__active)
            return ORB.init().create_recursive_tc(_id); 
          __active = true;
          StructMember[] arrayOfStructMember = new StructMember[9];
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
          arrayOfStructMember[4] = new StructMember("result", typeCode, null);
          typeCode = OperationModeHelper.type();
          arrayOfStructMember[5] = new StructMember("mode", typeCode, null);
          typeCode = ORB.init().create_string_tc(0);
          typeCode = ORB.init().create_alias_tc(IdentifierHelper.id(), "Identifier", typeCode);
          typeCode = ORB.init().create_alias_tc(ContextIdentifierHelper.id(), "ContextIdentifier", typeCode);
          typeCode = ORB.init().create_sequence_tc(0, typeCode);
          typeCode = ORB.init().create_alias_tc(ContextIdSeqHelper.id(), "ContextIdSeq", typeCode);
          arrayOfStructMember[6] = new StructMember("contexts", typeCode, null);
          typeCode = ParameterDescriptionHelper.type();
          typeCode = ORB.init().create_sequence_tc(0, typeCode);
          typeCode = ORB.init().create_alias_tc(ParDescriptionSeqHelper.id(), "ParDescriptionSeq", typeCode);
          arrayOfStructMember[7] = new StructMember("parameters", typeCode, null);
          typeCode = ExceptionDescriptionHelper.type();
          typeCode = ORB.init().create_sequence_tc(0, typeCode);
          typeCode = ORB.init().create_alias_tc(ExcDescriptionSeqHelper.id(), "ExcDescriptionSeq", typeCode);
          arrayOfStructMember[8] = new StructMember("exceptions", typeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "OperationDescription", arrayOfStructMember);
          __active = false;
        } 
      }  
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static OperationDescription read(InputStream paramInputStream) {
    OperationDescription operationDescription = new OperationDescription();
    operationDescription.name = paramInputStream.read_string();
    operationDescription.id = paramInputStream.read_string();
    operationDescription.defined_in = paramInputStream.read_string();
    operationDescription.version = paramInputStream.read_string();
    operationDescription.result = paramInputStream.read_TypeCode();
    operationDescription.mode = OperationModeHelper.read(paramInputStream);
    operationDescription.contexts = ContextIdSeqHelper.read(paramInputStream);
    operationDescription.parameters = ParDescriptionSeqHelper.read(paramInputStream);
    operationDescription.exceptions = ExcDescriptionSeqHelper.read(paramInputStream);
    return operationDescription;
  }
  
  public static void write(OutputStream paramOutputStream, OperationDescription paramOperationDescription) {
    paramOutputStream.write_string(paramOperationDescription.name);
    paramOutputStream.write_string(paramOperationDescription.id);
    paramOutputStream.write_string(paramOperationDescription.defined_in);
    paramOutputStream.write_string(paramOperationDescription.version);
    paramOutputStream.write_TypeCode(paramOperationDescription.result);
    OperationModeHelper.write(paramOutputStream, paramOperationDescription.mode);
    ContextIdSeqHelper.write(paramOutputStream, paramOperationDescription.contexts);
    ParDescriptionSeqHelper.write(paramOutputStream, paramOperationDescription.parameters);
    ExcDescriptionSeqHelper.write(paramOutputStream, paramOperationDescription.exceptions);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\omg\CORBA\OperationDescriptionHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */