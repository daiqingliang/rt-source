package com.sun.org.omg.CORBA;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class InitializerHelper {
  private static String _id = "IDL:omg.org/CORBA/Initializer:1.0";
  
  private static TypeCode __typeCode = null;
  
  private static boolean __active = false;
  
  public static void insert(Any paramAny, Initializer paramInitializer) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramInitializer);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static Initializer extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      synchronized (TypeCode.class) {
        if (__typeCode == null) {
          if (__active)
            return ORB.init().create_recursive_tc(_id); 
          __active = true;
          StructMember[] arrayOfStructMember = new StructMember[2];
          TypeCode typeCode = null;
          typeCode = StructMemberHelper.type();
          typeCode = ORB.init().create_sequence_tc(0, typeCode);
          typeCode = ORB.init().create_alias_tc(StructMemberSeqHelper.id(), "StructMemberSeq", typeCode);
          arrayOfStructMember[0] = new StructMember("members", typeCode, null);
          typeCode = ORB.init().create_string_tc(0);
          typeCode = ORB.init().create_alias_tc(IdentifierHelper.id(), "Identifier", typeCode);
          arrayOfStructMember[1] = new StructMember("name", typeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "Initializer", arrayOfStructMember);
          __active = false;
        } 
      }  
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static Initializer read(InputStream paramInputStream) {
    Initializer initializer = new Initializer();
    initializer.members = StructMemberSeqHelper.read(paramInputStream);
    initializer.name = paramInputStream.read_string();
    return initializer;
  }
  
  public static void write(OutputStream paramOutputStream, Initializer paramInitializer) {
    StructMemberSeqHelper.write(paramOutputStream, paramInitializer.members);
    paramOutputStream.write_string(paramInitializer.name);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\omg\CORBA\InitializerHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */