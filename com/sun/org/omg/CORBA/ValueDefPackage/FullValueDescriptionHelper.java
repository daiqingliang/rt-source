package com.sun.org.omg.CORBA.ValueDefPackage;

import com.sun.org.omg.CORBA.AttrDescriptionSeqHelper;
import com.sun.org.omg.CORBA.AttributeDescriptionHelper;
import com.sun.org.omg.CORBA.IdentifierHelper;
import com.sun.org.omg.CORBA.InitializerHelper;
import com.sun.org.omg.CORBA.InitializerSeqHelper;
import com.sun.org.omg.CORBA.OpDescriptionSeqHelper;
import com.sun.org.omg.CORBA.OperationDescriptionHelper;
import com.sun.org.omg.CORBA.RepositoryIdHelper;
import com.sun.org.omg.CORBA.RepositoryIdSeqHelper;
import com.sun.org.omg.CORBA.ValueMemberHelper;
import com.sun.org.omg.CORBA.ValueMemberSeqHelper;
import com.sun.org.omg.CORBA.VersionSpecHelper;
import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class FullValueDescriptionHelper {
  private static String _id = "IDL:omg.org/CORBA/ValueDef/FullValueDescription:1.0";
  
  private static TypeCode __typeCode = null;
  
  private static boolean __active = false;
  
  public static void insert(Any paramAny, FullValueDescription paramFullValueDescription) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramFullValueDescription);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static FullValueDescription extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      synchronized (TypeCode.class) {
        if (__typeCode == null) {
          if (__active)
            return ORB.init().create_recursive_tc(_id); 
          __active = true;
          StructMember[] arrayOfStructMember = new StructMember[15];
          TypeCode typeCode = null;
          typeCode = ORB.init().create_string_tc(0);
          typeCode = ORB.init().create_alias_tc(IdentifierHelper.id(), "Identifier", typeCode);
          arrayOfStructMember[0] = new StructMember("name", typeCode, null);
          typeCode = ORB.init().create_string_tc(0);
          typeCode = ORB.init().create_alias_tc(RepositoryIdHelper.id(), "RepositoryId", typeCode);
          arrayOfStructMember[1] = new StructMember("id", typeCode, null);
          typeCode = ORB.init().get_primitive_tc(TCKind.tk_boolean);
          arrayOfStructMember[2] = new StructMember("is_abstract", typeCode, null);
          typeCode = ORB.init().get_primitive_tc(TCKind.tk_boolean);
          arrayOfStructMember[3] = new StructMember("is_custom", typeCode, null);
          typeCode = ORB.init().create_string_tc(0);
          typeCode = ORB.init().create_alias_tc(RepositoryIdHelper.id(), "RepositoryId", typeCode);
          arrayOfStructMember[4] = new StructMember("defined_in", typeCode, null);
          typeCode = ORB.init().create_string_tc(0);
          typeCode = ORB.init().create_alias_tc(VersionSpecHelper.id(), "VersionSpec", typeCode);
          arrayOfStructMember[5] = new StructMember("version", typeCode, null);
          typeCode = OperationDescriptionHelper.type();
          typeCode = ORB.init().create_sequence_tc(0, typeCode);
          typeCode = ORB.init().create_alias_tc(OpDescriptionSeqHelper.id(), "OpDescriptionSeq", typeCode);
          arrayOfStructMember[6] = new StructMember("operations", typeCode, null);
          typeCode = AttributeDescriptionHelper.type();
          typeCode = ORB.init().create_sequence_tc(0, typeCode);
          typeCode = ORB.init().create_alias_tc(AttrDescriptionSeqHelper.id(), "AttrDescriptionSeq", typeCode);
          arrayOfStructMember[7] = new StructMember("attributes", typeCode, null);
          typeCode = ValueMemberHelper.type();
          typeCode = ORB.init().create_sequence_tc(0, typeCode);
          typeCode = ORB.init().create_alias_tc(ValueMemberSeqHelper.id(), "ValueMemberSeq", typeCode);
          arrayOfStructMember[8] = new StructMember("members", typeCode, null);
          typeCode = InitializerHelper.type();
          typeCode = ORB.init().create_sequence_tc(0, typeCode);
          typeCode = ORB.init().create_alias_tc(InitializerSeqHelper.id(), "InitializerSeq", typeCode);
          arrayOfStructMember[9] = new StructMember("initializers", typeCode, null);
          typeCode = ORB.init().create_string_tc(0);
          typeCode = ORB.init().create_alias_tc(RepositoryIdHelper.id(), "RepositoryId", typeCode);
          typeCode = ORB.init().create_sequence_tc(0, typeCode);
          typeCode = ORB.init().create_alias_tc(RepositoryIdSeqHelper.id(), "RepositoryIdSeq", typeCode);
          arrayOfStructMember[10] = new StructMember("supported_interfaces", typeCode, null);
          typeCode = ORB.init().create_string_tc(0);
          typeCode = ORB.init().create_alias_tc(RepositoryIdHelper.id(), "RepositoryId", typeCode);
          typeCode = ORB.init().create_sequence_tc(0, typeCode);
          typeCode = ORB.init().create_alias_tc(RepositoryIdSeqHelper.id(), "RepositoryIdSeq", typeCode);
          arrayOfStructMember[11] = new StructMember("abstract_base_values", typeCode, null);
          typeCode = ORB.init().get_primitive_tc(TCKind.tk_boolean);
          arrayOfStructMember[12] = new StructMember("is_truncatable", typeCode, null);
          typeCode = ORB.init().create_string_tc(0);
          typeCode = ORB.init().create_alias_tc(RepositoryIdHelper.id(), "RepositoryId", typeCode);
          arrayOfStructMember[13] = new StructMember("base_value", typeCode, null);
          typeCode = ORB.init().get_primitive_tc(TCKind.tk_TypeCode);
          arrayOfStructMember[14] = new StructMember("type", typeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "FullValueDescription", arrayOfStructMember);
          __active = false;
        } 
      }  
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static FullValueDescription read(InputStream paramInputStream) {
    FullValueDescription fullValueDescription = new FullValueDescription();
    fullValueDescription.name = paramInputStream.read_string();
    fullValueDescription.id = paramInputStream.read_string();
    fullValueDescription.is_abstract = paramInputStream.read_boolean();
    fullValueDescription.is_custom = paramInputStream.read_boolean();
    fullValueDescription.defined_in = paramInputStream.read_string();
    fullValueDescription.version = paramInputStream.read_string();
    fullValueDescription.operations = OpDescriptionSeqHelper.read(paramInputStream);
    fullValueDescription.attributes = AttrDescriptionSeqHelper.read(paramInputStream);
    fullValueDescription.members = ValueMemberSeqHelper.read(paramInputStream);
    fullValueDescription.initializers = InitializerSeqHelper.read(paramInputStream);
    fullValueDescription.supported_interfaces = RepositoryIdSeqHelper.read(paramInputStream);
    fullValueDescription.abstract_base_values = RepositoryIdSeqHelper.read(paramInputStream);
    fullValueDescription.is_truncatable = paramInputStream.read_boolean();
    fullValueDescription.base_value = paramInputStream.read_string();
    fullValueDescription.type = paramInputStream.read_TypeCode();
    return fullValueDescription;
  }
  
  public static void write(OutputStream paramOutputStream, FullValueDescription paramFullValueDescription) {
    paramOutputStream.write_string(paramFullValueDescription.name);
    paramOutputStream.write_string(paramFullValueDescription.id);
    paramOutputStream.write_boolean(paramFullValueDescription.is_abstract);
    paramOutputStream.write_boolean(paramFullValueDescription.is_custom);
    paramOutputStream.write_string(paramFullValueDescription.defined_in);
    paramOutputStream.write_string(paramFullValueDescription.version);
    OpDescriptionSeqHelper.write(paramOutputStream, paramFullValueDescription.operations);
    AttrDescriptionSeqHelper.write(paramOutputStream, paramFullValueDescription.attributes);
    ValueMemberSeqHelper.write(paramOutputStream, paramFullValueDescription.members);
    InitializerSeqHelper.write(paramOutputStream, paramFullValueDescription.initializers);
    RepositoryIdSeqHelper.write(paramOutputStream, paramFullValueDescription.supported_interfaces);
    RepositoryIdSeqHelper.write(paramOutputStream, paramFullValueDescription.abstract_base_values);
    paramOutputStream.write_boolean(paramFullValueDescription.is_truncatable);
    paramOutputStream.write_string(paramFullValueDescription.base_value);
    paramOutputStream.write_TypeCode(paramFullValueDescription.type);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\omg\CORBA\ValueDefPackage\FullValueDescriptionHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */