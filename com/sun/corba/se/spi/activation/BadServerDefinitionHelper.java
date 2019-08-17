package com.sun.corba.se.spi.activation;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class BadServerDefinitionHelper {
  private static String _id = "IDL:activation/BadServerDefinition:1.0";
  
  private static TypeCode __typeCode = null;
  
  private static boolean __active = false;
  
  public static void insert(Any paramAny, BadServerDefinition paramBadServerDefinition) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramBadServerDefinition);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static BadServerDefinition extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      synchronized (TypeCode.class) {
        if (__typeCode == null) {
          if (__active)
            return ORB.init().create_recursive_tc(_id); 
          __active = true;
          StructMember[] arrayOfStructMember = new StructMember[1];
          TypeCode typeCode = null;
          typeCode = ORB.init().create_string_tc(0);
          arrayOfStructMember[0] = new StructMember("reason", typeCode, null);
          __typeCode = ORB.init().create_exception_tc(id(), "BadServerDefinition", arrayOfStructMember);
          __active = false;
        } 
      }  
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static BadServerDefinition read(InputStream paramInputStream) {
    BadServerDefinition badServerDefinition = new BadServerDefinition();
    paramInputStream.read_string();
    badServerDefinition.reason = paramInputStream.read_string();
    return badServerDefinition;
  }
  
  public static void write(OutputStream paramOutputStream, BadServerDefinition paramBadServerDefinition) {
    paramOutputStream.write_string(id());
    paramOutputStream.write_string(paramBadServerDefinition.reason);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\BadServerDefinitionHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */