package org.omg.DynamicAny;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class NameDynAnyPairHelper {
  private static String _id = "IDL:omg.org/DynamicAny/NameDynAnyPair:1.0";
  
  private static TypeCode __typeCode = null;
  
  private static boolean __active = false;
  
  public static void insert(Any paramAny, NameDynAnyPair paramNameDynAnyPair) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramNameDynAnyPair);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static NameDynAnyPair extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
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
          typeCode = ORB.init().create_alias_tc(FieldNameHelper.id(), "FieldName", typeCode);
          arrayOfStructMember[0] = new StructMember("id", typeCode, null);
          typeCode = DynAnyHelper.type();
          arrayOfStructMember[1] = new StructMember("value", typeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "NameDynAnyPair", arrayOfStructMember);
          __active = false;
        } 
      }  
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static NameDynAnyPair read(InputStream paramInputStream) {
    NameDynAnyPair nameDynAnyPair = new NameDynAnyPair();
    nameDynAnyPair.id = paramInputStream.read_string();
    nameDynAnyPair.value = DynAnyHelper.read(paramInputStream);
    return nameDynAnyPair;
  }
  
  public static void write(OutputStream paramOutputStream, NameDynAnyPair paramNameDynAnyPair) {
    paramOutputStream.write_string(paramNameDynAnyPair.id);
    DynAnyHelper.write(paramOutputStream, paramNameDynAnyPair.value);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\DynamicAny\NameDynAnyPairHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */