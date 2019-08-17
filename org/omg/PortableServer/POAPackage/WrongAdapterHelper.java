package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class WrongAdapterHelper {
  private static String _id = "IDL:omg.org/PortableServer/POA/WrongAdapter:1.0";
  
  private static TypeCode __typeCode = null;
  
  private static boolean __active = false;
  
  public static void insert(Any paramAny, WrongAdapter paramWrongAdapter) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramWrongAdapter);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static WrongAdapter extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      synchronized (TypeCode.class) {
        if (__typeCode == null) {
          if (__active)
            return ORB.init().create_recursive_tc(_id); 
          __active = true;
          StructMember[] arrayOfStructMember = new StructMember[0];
          Object object = null;
          __typeCode = ORB.init().create_exception_tc(id(), "WrongAdapter", arrayOfStructMember);
          __active = false;
        } 
      }  
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static WrongAdapter read(InputStream paramInputStream) {
    WrongAdapter wrongAdapter = new WrongAdapter();
    paramInputStream.read_string();
    return wrongAdapter;
  }
  
  public static void write(OutputStream paramOutputStream, WrongAdapter paramWrongAdapter) { paramOutputStream.write_string(id()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableServer\POAPackage\WrongAdapterHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */