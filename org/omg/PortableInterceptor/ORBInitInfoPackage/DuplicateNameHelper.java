package org.omg.PortableInterceptor.ORBInitInfoPackage;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class DuplicateNameHelper {
  private static String _id = "IDL:omg.org/PortableInterceptor/ORBInitInfo/DuplicateName:1.0";
  
  private static TypeCode __typeCode = null;
  
  private static boolean __active = false;
  
  public static void insert(Any paramAny, DuplicateName paramDuplicateName) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramDuplicateName);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static DuplicateName extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
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
          arrayOfStructMember[0] = new StructMember("name", typeCode, null);
          __typeCode = ORB.init().create_exception_tc(id(), "DuplicateName", arrayOfStructMember);
          __active = false;
        } 
      }  
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static DuplicateName read(InputStream paramInputStream) {
    DuplicateName duplicateName = new DuplicateName();
    paramInputStream.read_string();
    duplicateName.name = paramInputStream.read_string();
    return duplicateName;
  }
  
  public static void write(OutputStream paramOutputStream, DuplicateName paramDuplicateName) {
    paramOutputStream.write_string(id());
    paramOutputStream.write_string(paramDuplicateName.name);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableInterceptor\ORBInitInfoPackage\DuplicateNameHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */