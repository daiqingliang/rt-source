package org.omg.CosNaming;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class NameComponentHelper {
  private static String _id = "IDL:omg.org/CosNaming/NameComponent:1.0";
  
  private static TypeCode __typeCode = null;
  
  private static boolean __active = false;
  
  public static void insert(Any paramAny, NameComponent paramNameComponent) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramNameComponent);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static NameComponent extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
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
          typeCode = ORB.init().create_alias_tc(IstringHelper.id(), "Istring", typeCode);
          arrayOfStructMember[0] = new StructMember("id", typeCode, null);
          typeCode = ORB.init().create_string_tc(0);
          typeCode = ORB.init().create_alias_tc(IstringHelper.id(), "Istring", typeCode);
          arrayOfStructMember[1] = new StructMember("kind", typeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "NameComponent", arrayOfStructMember);
          __active = false;
        } 
      }  
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static NameComponent read(InputStream paramInputStream) {
    NameComponent nameComponent = new NameComponent();
    nameComponent.id = paramInputStream.read_string();
    nameComponent.kind = paramInputStream.read_string();
    return nameComponent;
  }
  
  public static void write(OutputStream paramOutputStream, NameComponent paramNameComponent) {
    paramOutputStream.write_string(paramNameComponent.id);
    paramOutputStream.write_string(paramNameComponent.kind);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CosNaming\NameComponentHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */