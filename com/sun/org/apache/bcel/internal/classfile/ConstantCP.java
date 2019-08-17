package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class ConstantCP extends Constant {
  protected int class_index;
  
  protected int name_and_type_index;
  
  public ConstantCP(ConstantCP paramConstantCP) { this(paramConstantCP.getTag(), paramConstantCP.getClassIndex(), paramConstantCP.getNameAndTypeIndex()); }
  
  ConstantCP(byte paramByte, DataInputStream paramDataInputStream) throws IOException { this(paramByte, paramDataInputStream.readUnsignedShort(), paramDataInputStream.readUnsignedShort()); }
  
  protected ConstantCP(byte paramByte, int paramInt1, int paramInt2) {
    super(paramByte);
    this.class_index = paramInt1;
    this.name_and_type_index = paramInt2;
  }
  
  public final void dump(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeByte(this.tag);
    paramDataOutputStream.writeShort(this.class_index);
    paramDataOutputStream.writeShort(this.name_and_type_index);
  }
  
  public final int getClassIndex() { return this.class_index; }
  
  public final int getNameAndTypeIndex() { return this.name_and_type_index; }
  
  public final void setClassIndex(int paramInt) { this.class_index = paramInt; }
  
  public String getClass(ConstantPool paramConstantPool) { return paramConstantPool.constantToString(this.class_index, (byte)7); }
  
  public final void setNameAndTypeIndex(int paramInt) { this.name_and_type_index = paramInt; }
  
  public final String toString() { return super.toString() + "(class_index = " + this.class_index + ", name_and_type_index = " + this.name_and_type_index + ")"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\ConstantCP.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */