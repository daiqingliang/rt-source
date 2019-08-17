package org.omg.CORBA;

import java.io.Serializable;
import org.omg.CORBA.portable.BoxedValueHelper;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class StringValueHelper implements BoxedValueHelper {
  private static String _id = "IDL:omg.org/CORBA/StringValue:1.0";
  
  private static StringValueHelper _instance = new StringValueHelper();
  
  private static TypeCode __typeCode = null;
  
  private static boolean __active = false;
  
  public static void insert(Any paramAny, String paramString) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramString);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static String extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      synchronized (TypeCode.class) {
        if (__typeCode == null) {
          if (__active)
            return ORB.init().create_recursive_tc(_id); 
          __active = true;
          __typeCode = ORB.init().create_string_tc(0);
          __typeCode = ORB.init().create_value_box_tc(_id, "StringValue", __typeCode);
          __active = false;
        } 
      }  
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static String read(InputStream paramInputStream) {
    if (!(paramInputStream instanceof InputStream))
      throw new BAD_PARAM(); 
    return (String)((InputStream)paramInputStream).read_value(_instance);
  }
  
  public Serializable read_value(InputStream paramInputStream) { return paramInputStream.read_string(); }
  
  public static void write(OutputStream paramOutputStream, String paramString) {
    if (!(paramOutputStream instanceof OutputStream))
      throw new BAD_PARAM(); 
    ((OutputStream)paramOutputStream).write_value(paramString, _instance);
  }
  
  public void write_value(OutputStream paramOutputStream, Serializable paramSerializable) {
    if (!(paramSerializable instanceof String))
      throw new MARSHAL(); 
    String str = (String)paramSerializable;
    paramOutputStream.write_string(str);
  }
  
  public String get_id() { return _id; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\StringValueHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */