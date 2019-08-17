package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class ConstantString extends Constant implements ConstantObject {
  private int string_index;
  
  public ConstantString(ConstantString paramConstantString) { this(paramConstantString.getStringIndex()); }
  
  ConstantString(DataInputStream paramDataInputStream) throws IOException { this(paramDataInputStream.readUnsignedShort()); }
  
  public ConstantString(int paramInt) {
    super((byte)8);
    this.string_index = paramInt;
  }
  
  public void accept(Visitor paramVisitor) { paramVisitor.visitConstantString(this); }
  
  public final void dump(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeByte(this.tag);
    paramDataOutputStream.writeShort(this.string_index);
  }
  
  public final int getStringIndex() { return this.string_index; }
  
  public final void setStringIndex(int paramInt) { this.string_index = paramInt; }
  
  public final String toString() { return super.toString() + "(string_index = " + this.string_index + ")"; }
  
  public Object getConstantValue(ConstantPool paramConstantPool) {
    Constant constant = paramConstantPool.getConstant(this.string_index, (byte)1);
    return ((ConstantUtf8)constant).getBytes();
  }
  
  public String getBytes(ConstantPool paramConstantPool) { return (String)getConstantValue(paramConstantPool); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\ConstantString.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */