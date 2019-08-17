package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class CompletionStatusHelper {
  private static String _id = "IDL:omg.org/CORBA/CompletionStatus:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, CompletionStatus paramCompletionStatus) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramCompletionStatus);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static CompletionStatus extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      __typeCode = ORB.init().create_enum_tc(id(), "CompletionStatus", new String[] { "COMPLETED_YES", "COMPLETED_NO", "COMPLETED_MAYBE" }); 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static CompletionStatus read(InputStream paramInputStream) { return CompletionStatus.from_int(paramInputStream.read_long()); }
  
  public static void write(OutputStream paramOutputStream, CompletionStatus paramCompletionStatus) { paramOutputStream.write_long(paramCompletionStatus.value()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\CompletionStatusHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */