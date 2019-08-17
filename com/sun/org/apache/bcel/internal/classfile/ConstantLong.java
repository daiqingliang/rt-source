package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class ConstantLong extends Constant implements ConstantObject {
  private long bytes;
  
  public ConstantLong(long paramLong) {
    super((byte)5);
    this.bytes = paramLong;
  }
  
  public ConstantLong(ConstantLong paramConstantLong) { this(paramConstantLong.getBytes()); }
  
  ConstantLong(DataInputStream paramDataInputStream) throws IOException { this(paramDataInputStream.readLong()); }
  
  public void accept(Visitor paramVisitor) { paramVisitor.visitConstantLong(this); }
  
  public final void dump(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeByte(this.tag);
    paramDataOutputStream.writeLong(this.bytes);
  }
  
  public final long getBytes() { return this.bytes; }
  
  public final void setBytes(long paramLong) { this.bytes = paramLong; }
  
  public final String toString() { return super.toString() + "(bytes = " + this.bytes + ")"; }
  
  public Object getConstantValue(ConstantPool paramConstantPool) { return new Long(this.bytes); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\ConstantLong.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */