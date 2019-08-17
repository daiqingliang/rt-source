package com.sun.org.omg.CORBA;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class StructMemberSeqHelper {
  private static String _id = "IDL:omg.org/CORBA/StructMemberSeq:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, StructMember[] paramArrayOfStructMember) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramArrayOfStructMember);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static StructMember[] extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null) {
      __typeCode = StructMemberHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "StructMemberSeq", __typeCode);
    } 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static StructMember[] read(InputStream paramInputStream) {
    StructMember[] arrayOfStructMember = null;
    int i = paramInputStream.read_long();
    arrayOfStructMember = new StructMember[i];
    for (byte b = 0; b < arrayOfStructMember.length; b++)
      arrayOfStructMember[b] = StructMemberHelper.read(paramInputStream); 
    return arrayOfStructMember;
  }
  
  public static void write(OutputStream paramOutputStream, StructMember[] paramArrayOfStructMember) {
    paramOutputStream.write_long(paramArrayOfStructMember.length);
    for (byte b = 0; b < paramArrayOfStructMember.length; b++)
      StructMemberHelper.write(paramOutputStream, paramArrayOfStructMember[b]); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\omg\CORBA\StructMemberSeqHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */