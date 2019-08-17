package com.sun.org.omg.CORBA;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.ValueMember;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class ValueMemberSeqHelper {
  private static String _id = "IDL:omg.org/CORBA/ValueMemberSeq:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, ValueMember[] paramArrayOfValueMember) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramArrayOfValueMember);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static ValueMember[] extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null) {
      __typeCode = ValueMemberHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "ValueMemberSeq", __typeCode);
    } 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static ValueMember[] read(InputStream paramInputStream) {
    ValueMember[] arrayOfValueMember = null;
    int i = paramInputStream.read_long();
    arrayOfValueMember = new ValueMember[i];
    for (byte b = 0; b < arrayOfValueMember.length; b++)
      arrayOfValueMember[b] = ValueMemberHelper.read(paramInputStream); 
    return arrayOfValueMember;
  }
  
  public static void write(OutputStream paramOutputStream, ValueMember[] paramArrayOfValueMember) {
    paramOutputStream.write_long(paramArrayOfValueMember.length);
    for (byte b = 0; b < paramArrayOfValueMember.length; b++)
      ValueMemberHelper.write(paramOutputStream, paramArrayOfValueMember[b]); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\omg\CORBA\ValueMemberSeqHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */