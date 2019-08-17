package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CosNaming.NameComponentHelper;
import org.omg.CosNaming.NameHelper;
import org.omg.CosNaming.NamingContextHelper;

public abstract class CannotProceedHelper {
  private static String _id = "IDL:omg.org/CosNaming/NamingContext/CannotProceed:1.0";
  
  private static TypeCode __typeCode = null;
  
  private static boolean __active = false;
  
  public static void insert(Any paramAny, CannotProceed paramCannotProceed) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramCannotProceed);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static CannotProceed extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      synchronized (TypeCode.class) {
        if (__typeCode == null) {
          if (__active)
            return ORB.init().create_recursive_tc(_id); 
          __active = true;
          StructMember[] arrayOfStructMember = new StructMember[2];
          TypeCode typeCode = null;
          typeCode = NamingContextHelper.type();
          arrayOfStructMember[0] = new StructMember("cxt", typeCode, null);
          typeCode = NameComponentHelper.type();
          typeCode = ORB.init().create_sequence_tc(0, typeCode);
          typeCode = ORB.init().create_alias_tc(NameHelper.id(), "Name", typeCode);
          arrayOfStructMember[1] = new StructMember("rest_of_name", typeCode, null);
          __typeCode = ORB.init().create_exception_tc(id(), "CannotProceed", arrayOfStructMember);
          __active = false;
        } 
      }  
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static CannotProceed read(InputStream paramInputStream) {
    CannotProceed cannotProceed = new CannotProceed();
    paramInputStream.read_string();
    cannotProceed.cxt = NamingContextHelper.read(paramInputStream);
    cannotProceed.rest_of_name = NameHelper.read(paramInputStream);
    return cannotProceed;
  }
  
  public static void write(OutputStream paramOutputStream, CannotProceed paramCannotProceed) {
    paramOutputStream.write_string(id());
    NamingContextHelper.write(paramOutputStream, paramCannotProceed.cxt);
    NameHelper.write(paramOutputStream, paramCannotProceed.rest_of_name);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CosNaming\NamingContextPackage\CannotProceedHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */