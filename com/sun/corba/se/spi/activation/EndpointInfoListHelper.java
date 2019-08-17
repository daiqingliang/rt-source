package com.sun.corba.se.spi.activation;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class EndpointInfoListHelper {
  private static String _id = "IDL:activation/EndpointInfoList:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, EndPointInfo[] paramArrayOfEndPointInfo) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramArrayOfEndPointInfo);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static EndPointInfo[] extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null) {
      __typeCode = EndPointInfoHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "EndpointInfoList", __typeCode);
    } 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static EndPointInfo[] read(InputStream paramInputStream) {
    EndPointInfo[] arrayOfEndPointInfo = null;
    int i = paramInputStream.read_long();
    arrayOfEndPointInfo = new EndPointInfo[i];
    for (byte b = 0; b < arrayOfEndPointInfo.length; b++)
      arrayOfEndPointInfo[b] = EndPointInfoHelper.read(paramInputStream); 
    return arrayOfEndPointInfo;
  }
  
  public static void write(OutputStream paramOutputStream, EndPointInfo[] paramArrayOfEndPointInfo) {
    paramOutputStream.write_long(paramArrayOfEndPointInfo.length);
    for (byte b = 0; b < paramArrayOfEndPointInfo.length; b++)
      EndPointInfoHelper.write(paramOutputStream, paramArrayOfEndPointInfo[b]); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\EndpointInfoListHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */