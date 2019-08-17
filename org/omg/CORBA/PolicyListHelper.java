package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class PolicyListHelper {
  private static String _id = "IDL:omg.org/CORBA/PolicyList:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, Policy[] paramArrayOfPolicy) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramArrayOfPolicy);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static Policy[] extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null) {
      __typeCode = PolicyHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "PolicyList", __typeCode);
    } 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static Policy[] read(InputStream paramInputStream) {
    Policy[] arrayOfPolicy = null;
    int i = paramInputStream.read_long();
    arrayOfPolicy = new Policy[i];
    for (byte b = 0; b < arrayOfPolicy.length; b++)
      arrayOfPolicy[b] = PolicyHelper.read(paramInputStream); 
    return arrayOfPolicy;
  }
  
  public static void write(OutputStream paramOutputStream, Policy[] paramArrayOfPolicy) {
    paramOutputStream.write_long(paramArrayOfPolicy.length);
    for (byte b = 0; b < paramArrayOfPolicy.length; b++)
      PolicyHelper.write(paramOutputStream, paramArrayOfPolicy[b]); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\PolicyListHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */