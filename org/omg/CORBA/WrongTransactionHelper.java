package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class WrongTransactionHelper {
  private static String _id = "IDL:omg.org/CORBA/WrongTransaction:1.0";
  
  private static TypeCode __typeCode = null;
  
  private static boolean __active = false;
  
  public static void insert(Any paramAny, WrongTransaction paramWrongTransaction) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramWrongTransaction);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static WrongTransaction extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      synchronized (TypeCode.class) {
        if (__typeCode == null) {
          if (__active)
            return ORB.init().create_recursive_tc(_id); 
          __active = true;
          StructMember[] arrayOfStructMember = new StructMember[0];
          java.lang.Object object = null;
          __typeCode = ORB.init().create_exception_tc(id(), "WrongTransaction", arrayOfStructMember);
          __active = false;
        } 
      }  
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static WrongTransaction read(InputStream paramInputStream) {
    WrongTransaction wrongTransaction = new WrongTransaction();
    paramInputStream.read_string();
    return wrongTransaction;
  }
  
  public static void write(OutputStream paramOutputStream, WrongTransaction paramWrongTransaction) { paramOutputStream.write_string(id()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\WrongTransactionHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */