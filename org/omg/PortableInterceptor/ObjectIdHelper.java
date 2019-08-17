package org.omg.PortableInterceptor;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.OctetSeqHelper;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ObjectIdHelper {
  private static String _id = "IDL:omg.org/PortableInterceptor/ObjectId:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, byte[] paramArrayOfByte) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramArrayOfByte);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static byte[] extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null) {
      __typeCode = ORB.init().get_primitive_tc(TCKind.tk_octet);
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(OctetSeqHelper.id(), "OctetSeq", __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "ObjectId", __typeCode);
    } 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static byte[] read(InputStream paramInputStream) {
    null = null;
    return OctetSeqHelper.read(paramInputStream);
  }
  
  public static void write(OutputStream paramOutputStream, byte[] paramArrayOfByte) { OctetSeqHelper.write(paramOutputStream, paramArrayOfByte); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableInterceptor\ObjectIdHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */