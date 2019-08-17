package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class ConstantInteger extends Constant implements ConstantObject {
  private int bytes;
  
  public ConstantInteger(int paramInt) {
    super((byte)3);
    this.bytes = paramInt;
  }
  
  public ConstantInteger(ConstantInteger paramConstantInteger) { this(paramConstantInteger.getBytes()); }
  
  ConstantInteger(DataInputStream paramDataInputStream) throws IOException { this(paramDataInputStream.readInt()); }
  
  public void accept(Visitor paramVisitor) { paramVisitor.visitConstantInteger(this); }
  
  public final void dump(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeByte(this.tag);
    paramDataOutputStream.writeInt(this.bytes);
  }
  
  public final int getBytes() { return this.bytes; }
  
  public final void setBytes(int paramInt) { this.bytes = paramInt; }
  
  public final String toString() { return super.toString() + "(bytes = " + this.bytes + ")"; }
  
  public Object getConstantValue(ConstantPool paramConstantPool) { return new Integer(this.bytes); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\ConstantInteger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */