package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class Synthetic extends Attribute {
  private byte[] bytes;
  
  public Synthetic(Synthetic paramSynthetic) { this(paramSynthetic.getNameIndex(), paramSynthetic.getLength(), paramSynthetic.getBytes(), paramSynthetic.getConstantPool()); }
  
  public Synthetic(int paramInt1, int paramInt2, byte[] paramArrayOfByte, ConstantPool paramConstantPool) {
    super((byte)7, paramInt1, paramInt2, paramConstantPool);
    this.bytes = paramArrayOfByte;
  }
  
  Synthetic(int paramInt1, int paramInt2, DataInputStream paramDataInputStream, ConstantPool paramConstantPool) throws IOException {
    this(paramInt1, paramInt2, (byte[])null, paramConstantPool);
    if (paramInt2 > 0) {
      this.bytes = new byte[paramInt2];
      paramDataInputStream.readFully(this.bytes);
      System.err.println("Synthetic attribute with length > 0");
    } 
  }
  
  public void accept(Visitor paramVisitor) { paramVisitor.visitSynthetic(this); }
  
  public final void dump(DataOutputStream paramDataOutputStream) throws IOException {
    super.dump(paramDataOutputStream);
    if (this.length > 0)
      paramDataOutputStream.write(this.bytes, 0, this.length); 
  }
  
  public final byte[] getBytes() { return this.bytes; }
  
  public final void setBytes(byte[] paramArrayOfByte) { this.bytes = paramArrayOfByte; }
  
  public final String toString() {
    StringBuffer stringBuffer = new StringBuffer("Synthetic");
    if (this.length > 0)
      stringBuffer.append(" " + Utility.toHexString(this.bytes)); 
    return stringBuffer.toString();
  }
  
  public Attribute copy(ConstantPool paramConstantPool) {
    Synthetic synthetic = (Synthetic)clone();
    if (this.bytes != null)
      synthetic.bytes = (byte[])this.bytes.clone(); 
    synthetic.constant_pool = paramConstantPool;
    return synthetic;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\Synthetic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */