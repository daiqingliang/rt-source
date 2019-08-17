package com.sun.corba.se.spi.activation;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ORBidListHelper {
  private static String _id = "IDL:activation/ORBidList:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, String[] paramArrayOfString) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramArrayOfString);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static String[] extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null) {
      __typeCode = ORB.init().create_string_tc(0);
      __typeCode = ORB.init().create_alias_tc(ORBidHelper.id(), "ORBid", __typeCode);
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "ORBidList", __typeCode);
    } 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static String[] read(InputStream paramInputStream) {
    String[] arrayOfString = null;
    int i = paramInputStream.read_long();
    arrayOfString = new String[i];
    for (byte b = 0; b < arrayOfString.length; b++)
      arrayOfString[b] = ORBidHelper.read(paramInputStream); 
    return arrayOfString;
  }
  
  public static void write(OutputStream paramOutputStream, String[] paramArrayOfString) {
    paramOutputStream.write_long(paramArrayOfString.length);
    for (byte b = 0; b < paramArrayOfString.length; b++)
      ORBidHelper.write(paramOutputStream, paramArrayOfString[b]); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\ORBidListHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */