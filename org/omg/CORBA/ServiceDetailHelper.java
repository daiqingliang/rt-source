package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ServiceDetailHelper {
  private static TypeCode _tc;
  
  public static void write(OutputStream paramOutputStream, ServiceDetail paramServiceDetail) {
    paramOutputStream.write_ulong(paramServiceDetail.service_detail_type);
    paramOutputStream.write_long(paramServiceDetail.service_detail.length);
    paramOutputStream.write_octet_array(paramServiceDetail.service_detail, 0, paramServiceDetail.service_detail.length);
  }
  
  public static ServiceDetail read(InputStream paramInputStream) {
    ServiceDetail serviceDetail = new ServiceDetail();
    serviceDetail.service_detail_type = paramInputStream.read_ulong();
    int i = paramInputStream.read_long();
    serviceDetail.service_detail = new byte[i];
    paramInputStream.read_octet_array(serviceDetail.service_detail, 0, serviceDetail.service_detail.length);
    return serviceDetail;
  }
  
  public static ServiceDetail extract(Any paramAny) {
    InputStream inputStream = paramAny.create_input_stream();
    return read(inputStream);
  }
  
  public static void insert(Any paramAny, ServiceDetail paramServiceDetail) {
    OutputStream outputStream = paramAny.create_output_stream();
    write(outputStream, paramServiceDetail);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static TypeCode type() {
    byte b = 2;
    StructMember[] arrayOfStructMember = null;
    if (_tc == null) {
      arrayOfStructMember = new StructMember[2];
      arrayOfStructMember[0] = new StructMember("service_detail_type", ORB.init().get_primitive_tc(TCKind.tk_ulong), null);
      arrayOfStructMember[1] = new StructMember("service_detail", ORB.init().create_sequence_tc(0, ORB.init().get_primitive_tc(TCKind.tk_octet)), null);
      _tc = ORB.init().create_struct_tc(id(), "ServiceDetail", arrayOfStructMember);
    } 
    return _tc;
  }
  
  public static String id() { return "IDL:omg.org/CORBA/ServiceDetail:1.0"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\ServiceDetailHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */