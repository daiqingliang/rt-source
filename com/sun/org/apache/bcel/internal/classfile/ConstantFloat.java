package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class ConstantFloat extends Constant implements ConstantObject {
  private float bytes;
  
  public ConstantFloat(float paramFloat) {
    super((byte)4);
    this.bytes = paramFloat;
  }
  
  public ConstantFloat(ConstantFloat paramConstantFloat) { this(paramConstantFloat.getBytes()); }
  
  ConstantFloat(DataInputStream paramDataInputStream) throws IOException { this(paramDataInputStream.readFloat()); }
  
  public void accept(Visitor paramVisitor) { paramVisitor.visitConstantFloat(this); }
  
  public final void dump(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeByte(this.tag);
    paramDataOutputStream.writeFloat(this.bytes);
  }
  
  public final float getBytes() { return this.bytes; }
  
  public final void setBytes(float paramFloat) { this.bytes = paramFloat; }
  
  public final String toString() { return super.toString() + "(bytes = " + this.bytes + ")"; }
  
  public Object getConstantValue(ConstantPool paramConstantPool) { return new Float(this.bytes); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\ConstantFloat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */