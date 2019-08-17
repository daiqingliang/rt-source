package org.omg.PortableServer;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ObjectHelper;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ForwardRequestHelper {
  private static String _id = "IDL:omg.org/PortableServer/ForwardRequest:1.0";
  
  private static TypeCode __typeCode = null;
  
  private static boolean __active = false;
  
  public static void insert(Any paramAny, ForwardRequest paramForwardRequest) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramForwardRequest);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static ForwardRequest extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      synchronized (TypeCode.class) {
        if (__typeCode == null) {
          if (__active)
            return ORB.init().create_recursive_tc(_id); 
          __active = true;
          StructMember[] arrayOfStructMember = new StructMember[1];
          TypeCode typeCode = null;
          typeCode = ObjectHelper.type();
          arrayOfStructMember[0] = new StructMember("forward_reference", typeCode, null);
          __typeCode = ORB.init().create_exception_tc(id(), "ForwardRequest", arrayOfStructMember);
          __active = false;
        } 
      }  
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static ForwardRequest read(InputStream paramInputStream) {
    ForwardRequest forwardRequest = new ForwardRequest();
    paramInputStream.read_string();
    forwardRequest.forward_reference = ObjectHelper.read(paramInputStream);
    return forwardRequest;
  }
  
  public static void write(OutputStream paramOutputStream, ForwardRequest paramForwardRequest) {
    paramOutputStream.write_string(id());
    ObjectHelper.write(paramOutputStream, paramForwardRequest.forward_reference);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableServer\ForwardRequestHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */