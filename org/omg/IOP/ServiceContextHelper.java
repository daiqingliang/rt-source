package org.omg.IOP;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ServiceContextHelper {
  private static String _id = "IDL:omg.org/IOP/ServiceContext:1.0";
  
  private static TypeCode __typeCode = null;
  
  private static boolean __active = false;
  
  public static void insert(Any paramAny, ServiceContext paramServiceContext) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramServiceContext);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static ServiceContext extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      synchronized (TypeCode.class) {
        if (__typeCode == null) {
          if (__active)
            return ORB.init().create_recursive_tc(_id); 
          __active = true;
          StructMember[] arrayOfStructMember = new StructMember[2];
          TypeCode typeCode = null;
          typeCode = ORB.init().get_primitive_tc(TCKind.tk_ulong);
          typeCode = ORB.init().create_alias_tc(ServiceIdHelper.id(), "ServiceId", typeCode);
          arrayOfStructMember[0] = new StructMember("context_id", typeCode, null);
          typeCode = ORB.init().get_primitive_tc(TCKind.tk_octet);
          typeCode = ORB.init().create_sequence_tc(0, typeCode);
          arrayOfStructMember[1] = new StructMember("context_data", typeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "ServiceContext", arrayOfStructMember);
          __active = false;
        } 
      }  
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static ServiceContext read(InputStream paramInputStream) {
    ServiceContext serviceContext = new ServiceContext();
    serviceContext.context_id = paramInputStream.read_ulong();
    int i = paramInputStream.read_long();
    serviceContext.context_data = new byte[i];
    paramInputStream.read_octet_array(serviceContext.context_data, 0, i);
    return serviceContext;
  }
  
  public static void write(OutputStream paramOutputStream, ServiceContext paramServiceContext) {
    paramOutputStream.write_ulong(paramServiceContext.context_id);
    paramOutputStream.write_long(paramServiceContext.context_data.length);
    paramOutputStream.write_octet_array(paramServiceContext.context_data, 0, paramServiceContext.context_data.length);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\IOP\ServiceContextHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */