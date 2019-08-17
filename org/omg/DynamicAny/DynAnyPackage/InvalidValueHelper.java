package org.omg.DynamicAny.DynAnyPackage;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class InvalidValueHelper {
  private static String _id = "IDL:omg.org/DynamicAny/DynAny/InvalidValue:1.0";
  
  private static TypeCode __typeCode = null;
  
  private static boolean __active = false;
  
  public static void insert(Any paramAny, InvalidValue paramInvalidValue) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramInvalidValue);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static InvalidValue extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      synchronized (TypeCode.class) {
        if (__typeCode == null) {
          if (__active)
            return ORB.init().create_recursive_tc(_id); 
          __active = true;
          StructMember[] arrayOfStructMember = new StructMember[0];
          Object object = null;
          __typeCode = ORB.init().create_exception_tc(id(), "InvalidValue", arrayOfStructMember);
          __active = false;
        } 
      }  
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static InvalidValue read(InputStream paramInputStream) {
    InvalidValue invalidValue = new InvalidValue();
    paramInputStream.read_string();
    return invalidValue;
  }
  
  public static void write(OutputStream paramOutputStream, InvalidValue paramInvalidValue) { paramOutputStream.write_string(id()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\DynamicAny\DynAnyPackage\InvalidValueHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */