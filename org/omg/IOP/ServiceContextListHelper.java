package org.omg.IOP;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ServiceContextListHelper {
  private static String _id = "IDL:omg.org/IOP/ServiceContextList:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, ServiceContext[] paramArrayOfServiceContext) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramArrayOfServiceContext);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static ServiceContext[] extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null) {
      __typeCode = ServiceContextHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "ServiceContextList", __typeCode);
    } 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static ServiceContext[] read(InputStream paramInputStream) {
    ServiceContext[] arrayOfServiceContext = null;
    int i = paramInputStream.read_long();
    arrayOfServiceContext = new ServiceContext[i];
    for (byte b = 0; b < arrayOfServiceContext.length; b++)
      arrayOfServiceContext[b] = ServiceContextHelper.read(paramInputStream); 
    return arrayOfServiceContext;
  }
  
  public static void write(OutputStream paramOutputStream, ServiceContext[] paramArrayOfServiceContext) {
    paramOutputStream.write_long(paramArrayOfServiceContext.length);
    for (byte b = 0; b < paramArrayOfServiceContext.length; b++)
      ServiceContextHelper.write(paramOutputStream, paramArrayOfServiceContext[b]); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\IOP\ServiceContextListHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */