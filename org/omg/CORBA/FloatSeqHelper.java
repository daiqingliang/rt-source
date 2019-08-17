package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class FloatSeqHelper {
  private static String _id = "IDL:omg.org/CORBA/FloatSeq:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, float[] paramArrayOfFloat) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramArrayOfFloat);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static float[] extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null) {
      __typeCode = ORB.init().get_primitive_tc(TCKind.tk_float);
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "FloatSeq", __typeCode);
    } 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static float[] read(InputStream paramInputStream) {
    float[] arrayOfFloat = null;
    int i = paramInputStream.read_long();
    arrayOfFloat = new float[i];
    paramInputStream.read_float_array(arrayOfFloat, 0, i);
    return arrayOfFloat;
  }
  
  public static void write(OutputStream paramOutputStream, float[] paramArrayOfFloat) {
    paramOutputStream.write_long(paramArrayOfFloat.length);
    paramOutputStream.write_float_array(paramArrayOfFloat, 0, paramArrayOfFloat.length);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\FloatSeqHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */