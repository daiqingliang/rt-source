package com.sun.corba.se.impl.protocol.giopmsgheaders;

import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.UnionMember;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.IOP.TaggedProfile;
import org.omg.IOP.TaggedProfileHelper;

public abstract class TargetAddressHelper {
  private static String _id = "IDL:messages/TargetAddress:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, TargetAddress paramTargetAddress) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramTargetAddress);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static TargetAddress extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null) {
      TypeCode typeCode1 = ORB.init().get_primitive_tc(TCKind.tk_short);
      typeCode1 = ORB.init().create_alias_tc(AddressingDispositionHelper.id(), "AddressingDisposition", typeCode1);
      UnionMember[] arrayOfUnionMember = new UnionMember[3];
      Any any = ORB.init().create_any();
      any.insert_short((short)0);
      TypeCode typeCode2 = ORB.init().get_primitive_tc(TCKind.tk_octet);
      typeCode2 = ORB.init().create_sequence_tc(0, typeCode2);
      arrayOfUnionMember[0] = new UnionMember("object_key", any, typeCode2, null);
      any = ORB.init().create_any();
      any.insert_short((short)1);
      typeCode2 = TaggedProfileHelper.type();
      arrayOfUnionMember[1] = new UnionMember("profile", any, typeCode2, null);
      any = ORB.init().create_any();
      any.insert_short((short)2);
      typeCode2 = IORAddressingInfoHelper.type();
      arrayOfUnionMember[2] = new UnionMember("ior", any, typeCode2, null);
      __typeCode = ORB.init().create_union_tc(id(), "TargetAddress", typeCode1, arrayOfUnionMember);
    } 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static TargetAddress read(InputStream paramInputStream) {
    IORAddressingInfo iORAddressingInfo;
    TaggedProfile taggedProfile;
    int i;
    byte[] arrayOfByte;
    TargetAddress targetAddress = new TargetAddress();
    short s = 0;
    s = paramInputStream.read_short();
    switch (s) {
      case 0:
        arrayOfByte = null;
        i = paramInputStream.read_long();
        arrayOfByte = new byte[i];
        paramInputStream.read_octet_array(arrayOfByte, 0, i);
        targetAddress.object_key(arrayOfByte);
        return targetAddress;
      case 1:
        taggedProfile = null;
        taggedProfile = TaggedProfileHelper.read(paramInputStream);
        targetAddress.profile(taggedProfile);
        return targetAddress;
      case 2:
        iORAddressingInfo = null;
        iORAddressingInfo = IORAddressingInfoHelper.read(paramInputStream);
        targetAddress.ior(iORAddressingInfo);
        return targetAddress;
    } 
    throw new BAD_OPERATION();
  }
  
  public static void write(OutputStream paramOutputStream, TargetAddress paramTargetAddress) {
    paramOutputStream.write_short(paramTargetAddress.discriminator());
    switch (paramTargetAddress.discriminator()) {
      case 0:
        paramOutputStream.write_long(paramTargetAddress.object_key().length);
        paramOutputStream.write_octet_array(paramTargetAddress.object_key(), 0, paramTargetAddress.object_key().length);
        return;
      case 1:
        TaggedProfileHelper.write(paramOutputStream, paramTargetAddress.profile());
        return;
      case 2:
        IORAddressingInfoHelper.write(paramOutputStream, paramTargetAddress.ior());
        return;
    } 
    throw new BAD_OPERATION();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\TargetAddressHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */