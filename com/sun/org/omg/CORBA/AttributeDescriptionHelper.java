package com.sun.org.omg.CORBA;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class AttributeDescriptionHelper {
  private static String _id = "IDL:omg.org/CORBA/AttributeDescription:1.0";
  
  private static TypeCode __typeCode = null;
  
  private static boolean __active = false;
  
  public static void insert(Any paramAny, AttributeDescription paramAttributeDescription) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramAttributeDescription);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static AttributeDescription extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      synchronized (TypeCode.class) {
        if (__typeCode == null) {
          if (__active)
            return ORB.init().create_recursive_tc(_id); 
          __active = true;
          StructMember[] arrayOfStructMember = new StructMember[6];
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
          typeCode = AttributeModeHelper.type();
          arrayOfStructMember[5] = new StructMember("mode", typeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "AttributeDescription", arrayOfStructMember);
          __active = false;
        } 
      }  
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static AttributeDescription read(InputStream paramInputStream) {
    AttributeDescription attributeDescription = new AttributeDescription();
    attributeDescription.name = paramInputStream.read_string();
    attributeDescription.id = paramInputStream.read_string();
    attributeDescription.defined_in = paramInputStream.read_string();
    attributeDescription.version = paramInputStream.read_string();
    attributeDescription.type = paramInputStream.read_TypeCode();
    attributeDescription.mode = AttributeModeHelper.read(paramInputStream);
    return attributeDescription;
  }
  
  public static void write(OutputStream paramOutputStream, AttributeDescription paramAttributeDescription) {
    paramOutputStream.write_string(paramAttributeDescription.name);
    paramOutputStream.write_string(paramAttributeDescription.id);
    paramOutputStream.write_string(paramAttributeDescription.defined_in);
    paramOutputStream.write_string(paramAttributeDescription.version);
    paramOutputStream.write_TypeCode(paramAttributeDescription.type);
    AttributeModeHelper.write(paramOutputStream, paramAttributeDescription.mode);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\omg\CORBA\AttributeDescriptionHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */