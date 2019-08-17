package com.sun.corba.se.impl.protocol.giopmsgheaders;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.IOP.IORHelper;

public abstract class IORAddressingInfoHelper {
  private static String _id = "IDL:messages/IORAddressingInfo:1.0";
  
  private static TypeCode __typeCode = null;
  
  private static boolean __active = false;
  
  public static void insert(Any paramAny, IORAddressingInfo paramIORAddressingInfo) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramIORAddressingInfo);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static IORAddressingInfo extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      synchronized (TypeCode.class) {
        if (__typeCode == null) {
          if (__active)
            return ORB.init().create_recursive_tc(_id); 
          __active = true;
          StructMember[] arrayOfStructMember = new StructMember[2];
          TypeCode typeCode = null;
          typeCode = ORB.init().get_primitive_tc(TCKind.tk_ulong);
          arrayOfStructMember[0] = new StructMember("selected_profile_index", typeCode, null);
          typeCode = IORHelper.type();
          arrayOfStructMember[1] = new StructMember("ior", typeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "IORAddressingInfo", arrayOfStructMember);
          __active = false;
        } 
      }  
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static IORAddressingInfo read(InputStream paramInputStream) {
    IORAddressingInfo iORAddressingInfo = new IORAddressingInfo();
    iORAddressingInfo.selected_profile_index = paramInputStream.read_ulong();
    iORAddressingInfo.ior = IORHelper.read(paramInputStream);
    return iORAddressingInfo;
  }
  
  public static void write(OutputStream paramOutputStream, IORAddressingInfo paramIORAddressingInfo) {
    paramOutputStream.write_ulong(paramIORAddressingInfo.selected_profile_index);
    IORHelper.write(paramOutputStream, paramIORAddressingInfo.ior);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\IORAddressingInfoHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */