package com.sun.org.apache.bcel.internal.classfile;

import com.sun.org.apache.bcel.internal.Constants;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class Deprecated extends Attribute {
  private byte[] bytes;
  
  public Deprecated(Deprecated paramDeprecated) { this(paramDeprecated.getNameIndex(), paramDeprecated.getLength(), paramDeprecated.getBytes(), paramDeprecated.getConstantPool()); }
  
  public Deprecated(int paramInt1, int paramInt2, byte[] paramArrayOfByte, ConstantPool paramConstantPool) {
    super((byte)8, paramInt1, paramInt2, paramConstantPool);
    this.bytes = paramArrayOfByte;
  }
  
  Deprecated(int paramInt1, int paramInt2, DataInputStream paramDataInputStream, ConstantPool paramConstantPool) throws IOException {
    this(paramInt1, paramInt2, (byte[])null, paramConstantPool);
    if (paramInt2 > 0) {
      this.bytes = new byte[paramInt2];
      paramDataInputStream.readFully(this.bytes);
      System.err.println("Deprecated attribute with length > 0");
    } 
  }
  
  public void accept(Visitor paramVisitor) { paramVisitor.visitDeprecated(this); }
  
  public final void dump(DataOutputStream paramDataOutputStream) throws IOException {
    super.dump(paramDataOutputStream);
    if (this.length > 0)
      paramDataOutputStream.write(this.bytes, 0, this.length); 
  }
  
  public final byte[] getBytes() { return this.bytes; }
  
  public final void setBytes(byte[] paramArrayOfByte) { this.bytes = paramArrayOfByte; }
  
  public final String toString() { return Constants.ATTRIBUTE_NAMES[8]; }
  
  public Attribute copy(ConstantPool paramConstantPool) {
    Deprecated deprecated = (Deprecated)clone();
    if (this.bytes != null)
      deprecated.bytes = (byte[])this.bytes.clone(); 
    deprecated.constant_pool = paramConstantPool;
    return deprecated;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\Deprecated.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */