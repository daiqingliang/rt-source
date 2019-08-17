package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ServiceInformationHelper {
  private static TypeCode _tc;
  
  public static void write(OutputStream paramOutputStream, ServiceInformation paramServiceInformation) {
    paramOutputStream.write_long(paramServiceInformation.service_options.length);
    paramOutputStream.write_ulong_array(paramServiceInformation.service_options, 0, paramServiceInformation.service_options.length);
    paramOutputStream.write_long(paramServiceInformation.service_details.length);
    for (byte b = 0; b < paramServiceInformation.service_details.length; b++)
      ServiceDetailHelper.write(paramOutputStream, paramServiceInformation.service_details[b]); 
  }
  
  public static ServiceInformation read(InputStream paramInputStream) {
    ServiceInformation serviceInformation = new ServiceInformation();
    int i = paramInputStream.read_long();
    serviceInformation.service_options = new int[i];
    paramInputStream.read_ulong_array(serviceInformation.service_options, 0, serviceInformation.service_options.length);
    i = paramInputStream.read_long();
    serviceInformation.service_details = new ServiceDetail[i];
    for (byte b = 0; b < serviceInformation.service_details.length; b++)
      serviceInformation.service_details[b] = ServiceDetailHelper.read(paramInputStream); 
    return serviceInformation;
  }
  
  public static ServiceInformation extract(Any paramAny) {
    InputStream inputStream = paramAny.create_input_stream();
    return read(inputStream);
  }
  
  public static void insert(Any paramAny, ServiceInformation paramServiceInformation) {
    OutputStream outputStream = paramAny.create_output_stream();
    write(outputStream, paramServiceInformation);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static TypeCode type() {
    byte b = 2;
    StructMember[] arrayOfStructMember = null;
    if (_tc == null) {
      arrayOfStructMember = new StructMember[2];
      arrayOfStructMember[0] = new StructMember("service_options", ORB.init().create_sequence_tc(0, ORB.init().get_primitive_tc(TCKind.tk_ulong)), null);
      arrayOfStructMember[1] = new StructMember("service_details", ORB.init().create_sequence_tc(0, ServiceDetailHelper.type()), null);
      _tc = ORB.init().create_struct_tc(id(), "ServiceInformation", arrayOfStructMember);
    } 
    return _tc;
  }
  
  public static String id() { return "IDL:omg.org/CORBA/ServiceInformation:1.0"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\ServiceInformationHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */