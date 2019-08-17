package com.sun.corba.se.spi.activation;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ServerIdsHelper {
  private static String _id = "IDL:activation/ServerIds:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, int[] paramArrayOfInt) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramArrayOfInt);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static int[] extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null) {
      __typeCode = ORB.init().get_primitive_tc(TCKind.tk_long);
      __typeCode = ORB.init().create_alias_tc(ServerIdHelper.id(), "ServerId", __typeCode);
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "ServerIds", __typeCode);
    } 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static int[] read(InputStream paramInputStream) {
    int[] arrayOfInt = null;
    int i = paramInputStream.read_long();
    arrayOfInt = new int[i];
    for (byte b = 0; b < arrayOfInt.length; b++)
      arrayOfInt[b] = ServerIdHelper.read(paramInputStream); 
    return arrayOfInt;
  }
  
  public static void write(OutputStream paramOutputStream, int[] paramArrayOfInt) {
    paramOutputStream.write_long(paramArrayOfInt.length);
    for (byte b = 0; b < paramArrayOfInt.length; b++)
      ServerIdHelper.write(paramOutputStream, paramArrayOfInt[b]); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\ServerIdsHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */