package org.omg.IOP;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class IORHelper {
  private static String _id = "IDL:omg.org/IOP/IOR:1.0";
  
  private static TypeCode __typeCode = null;
  
  private static boolean __active = false;
  
  public static void insert(Any paramAny, IOR paramIOR) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramIOR);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static IOR extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      synchronized (TypeCode.class) {
        if (__typeCode == null) {
          if (__active)
            return ORB.init().create_recursive_tc(_id); 
          __active = true;
          StructMember[] arrayOfStructMember = new StructMember[2];
          TypeCode typeCode = null;
          typeCode = ORB.init().create_string_tc(0);
          arrayOfStructMember[0] = new StructMember("type_id", typeCode, null);
          typeCode = TaggedProfileHelper.type();
          typeCode = ORB.init().create_sequence_tc(0, typeCode);
          arrayOfStructMember[1] = new StructMember("profiles", typeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "IOR", arrayOfStructMember);
          __active = false;
        } 
      }  
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static IOR read(InputStream paramInputStream) {
    IOR iOR = new IOR();
    iOR.type_id = paramInputStream.read_string();
    int i = paramInputStream.read_long();
    iOR.profiles = new TaggedProfile[i];
    for (byte b = 0; b < iOR.profiles.length; b++)
      iOR.profiles[b] = TaggedProfileHelper.read(paramInputStream); 
    return iOR;
  }
  
  public static void write(OutputStream paramOutputStream, IOR paramIOR) {
    paramOutputStream.write_string(paramIOR.type_id);
    paramOutputStream.write_long(paramIOR.profiles.length);
    for (byte b = 0; b < paramIOR.profiles.length; b++)
      TaggedProfileHelper.write(paramOutputStream, paramIOR.profiles[b]); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\IOP\IORHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */