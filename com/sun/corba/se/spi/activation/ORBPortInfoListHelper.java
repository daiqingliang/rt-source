package com.sun.corba.se.spi.activation;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ORBPortInfoListHelper {
  private static String _id = "IDL:activation/ORBPortInfoList:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, ORBPortInfo[] paramArrayOfORBPortInfo) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramArrayOfORBPortInfo);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static ORBPortInfo[] extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null) {
      __typeCode = ORBPortInfoHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "ORBPortInfoList", __typeCode);
    } 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static ORBPortInfo[] read(InputStream paramInputStream) {
    ORBPortInfo[] arrayOfORBPortInfo = null;
    int i = paramInputStream.read_long();
    arrayOfORBPortInfo = new ORBPortInfo[i];
    for (byte b = 0; b < arrayOfORBPortInfo.length; b++)
      arrayOfORBPortInfo[b] = ORBPortInfoHelper.read(paramInputStream); 
    return arrayOfORBPortInfo;
  }
  
  public static void write(OutputStream paramOutputStream, ORBPortInfo[] paramArrayOfORBPortInfo) {
    paramOutputStream.write_long(paramArrayOfORBPortInfo.length);
    for (byte b = 0; b < paramArrayOfORBPortInfo.length; b++)
      ORBPortInfoHelper.write(paramOutputStream, paramArrayOfORBPortInfo[b]); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\ORBPortInfoListHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */