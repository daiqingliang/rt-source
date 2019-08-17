package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class ConstantValue extends Attribute {
  private int constantvalue_index;
  
  public ConstantValue(ConstantValue paramConstantValue) { this(paramConstantValue.getNameIndex(), paramConstantValue.getLength(), paramConstantValue.getConstantValueIndex(), paramConstantValue.getConstantPool()); }
  
  ConstantValue(int paramInt1, int paramInt2, DataInputStream paramDataInputStream, ConstantPool paramConstantPool) throws IOException { this(paramInt1, paramInt2, paramDataInputStream.readUnsignedShort(), paramConstantPool); }
  
  public ConstantValue(int paramInt1, int paramInt2, int paramInt3, ConstantPool paramConstantPool) {
    super((byte)1, paramInt1, paramInt2, paramConstantPool);
    this.constantvalue_index = paramInt3;
  }
  
  public void accept(Visitor paramVisitor) { paramVisitor.visitConstantValue(this); }
  
  public final void dump(DataOutputStream paramDataOutputStream) throws IOException {
    super.dump(paramDataOutputStream);
    paramDataOutputStream.writeShort(this.constantvalue_index);
  }
  
  public final int getConstantValueIndex() { return this.constantvalue_index; }
  
  public final void setConstantValueIndex(int paramInt) { this.constantvalue_index = paramInt; }
  
  public final String toString() {
    int i;
    Constant constant = this.constant_pool.getConstant(this.constantvalue_index);
    switch (constant.getTag()) {
      case 5:
        return "" + ((ConstantLong)constant).getBytes();
      case 4:
        return "" + ((ConstantFloat)constant).getBytes();
      case 6:
        return "" + ((ConstantDouble)constant).getBytes();
      case 3:
        return "" + ((ConstantInteger)constant).getBytes();
      case 8:
        i = ((ConstantString)constant).getStringIndex();
        constant = this.constant_pool.getConstant(i, (byte)1);
        return "\"" + Utility.convertString(((ConstantUtf8)constant).getBytes()) + "\"";
    } 
    throw new IllegalStateException("Type of ConstValue invalid: " + constant);
  }
  
  public Attribute copy(ConstantPool paramConstantPool) {
    ConstantValue constantValue = (ConstantValue)clone();
    constantValue.constant_pool = paramConstantPool;
    return constantValue;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\ConstantValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */