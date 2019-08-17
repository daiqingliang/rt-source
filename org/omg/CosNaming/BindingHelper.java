package org.omg.CosNaming;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class BindingHelper {
  private static String _id = "IDL:omg.org/CosNaming/Binding:1.0";
  
  private static TypeCode __typeCode = null;
  
  private static boolean __active = false;
  
  public static void insert(Any paramAny, Binding paramBinding) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramBinding);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static Binding extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      synchronized (TypeCode.class) {
        if (__typeCode == null) {
          if (__active)
            return ORB.init().create_recursive_tc(_id); 
          __active = true;
          StructMember[] arrayOfStructMember = new StructMember[2];
          TypeCode typeCode = null;
          typeCode = NameComponentHelper.type();
          typeCode = ORB.init().create_sequence_tc(0, typeCode);
          typeCode = ORB.init().create_alias_tc(NameHelper.id(), "Name", typeCode);
          arrayOfStructMember[0] = new StructMember("binding_name", typeCode, null);
          typeCode = BindingTypeHelper.type();
          arrayOfStructMember[1] = new StructMember("binding_type", typeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "Binding", arrayOfStructMember);
          __active = false;
        } 
      }  
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static Binding read(InputStream paramInputStream) {
    Binding binding = new Binding();
    binding.binding_name = NameHelper.read(paramInputStream);
    binding.binding_type = BindingTypeHelper.read(paramInputStream);
    return binding;
  }
  
  public static void write(OutputStream paramOutputStream, Binding paramBinding) {
    NameHelper.write(paramOutputStream, paramBinding.binding_name);
    BindingTypeHelper.write(paramOutputStream, paramBinding.binding_type);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CosNaming\BindingHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */