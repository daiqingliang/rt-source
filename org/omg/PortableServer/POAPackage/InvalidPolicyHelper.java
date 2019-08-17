package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class InvalidPolicyHelper {
  private static String _id = "IDL:omg.org/PortableServer/POA/InvalidPolicy:1.0";
  
  private static TypeCode __typeCode = null;
  
  private static boolean __active = false;
  
  public static void insert(Any paramAny, InvalidPolicy paramInvalidPolicy) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramInvalidPolicy);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static InvalidPolicy extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      synchronized (TypeCode.class) {
        if (__typeCode == null) {
          if (__active)
            return ORB.init().create_recursive_tc(_id); 
          __active = true;
          StructMember[] arrayOfStructMember = new StructMember[1];
          TypeCode typeCode = null;
          typeCode = ORB.init().get_primitive_tc(TCKind.tk_ushort);
          arrayOfStructMember[0] = new StructMember("index", typeCode, null);
          __typeCode = ORB.init().create_exception_tc(id(), "InvalidPolicy", arrayOfStructMember);
          __active = false;
        } 
      }  
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static InvalidPolicy read(InputStream paramInputStream) {
    InvalidPolicy invalidPolicy = new InvalidPolicy();
    paramInputStream.read_string();
    invalidPolicy.index = paramInputStream.read_ushort();
    return invalidPolicy;
  }
  
  public static void write(OutputStream paramOutputStream, InvalidPolicy paramInvalidPolicy) {
    paramOutputStream.write_string(id());
    paramOutputStream.write_ushort(paramInvalidPolicy.index);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableServer\POAPackage\InvalidPolicyHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */