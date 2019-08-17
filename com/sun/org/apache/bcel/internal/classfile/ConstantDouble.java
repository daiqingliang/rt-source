package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class ConstantDouble extends Constant implements ConstantObject {
  private double bytes;
  
  public ConstantDouble(double paramDouble) {
    super((byte)6);
    this.bytes = paramDouble;
  }
  
  public ConstantDouble(ConstantDouble paramConstantDouble) { this(paramConstantDouble.getBytes()); }
  
  ConstantDouble(DataInputStream paramDataInputStream) throws IOException { this(paramDataInputStream.readDouble()); }
  
  public void accept(Visitor paramVisitor) { paramVisitor.visitConstantDouble(this); }
  
  public final void dump(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeByte(this.tag);
    paramDataOutputStream.writeDouble(this.bytes);
  }
  
  public final double getBytes() { return this.bytes; }
  
  public final void setBytes(double paramDouble) { this.bytes = paramDouble; }
  
  public final String toString() { return super.toString() + "(bytes = " + this.bytes + ")"; }
  
  public Object getConstantValue(ConstantPool paramConstantPool) { return new Double(this.bytes); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\ConstantDouble.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */