package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class ConstantUtf8 extends Constant {
  private String bytes;
  
  public ConstantUtf8(ConstantUtf8 paramConstantUtf8) { this(paramConstantUtf8.getBytes()); }
  
  ConstantUtf8(DataInputStream paramDataInputStream) throws IOException {
    super((byte)1);
    this.bytes = paramDataInputStream.readUTF();
  }
  
  public ConstantUtf8(String paramString) {
    super((byte)1);
    if (paramString == null)
      throw new IllegalArgumentException("bytes must not be null!"); 
    this.bytes = paramString;
  }
  
  public void accept(Visitor paramVisitor) { paramVisitor.visitConstantUtf8(this); }
  
  public final void dump(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeByte(this.tag);
    paramDataOutputStream.writeUTF(this.bytes);
  }
  
  public final String getBytes() { return this.bytes; }
  
  public final void setBytes(String paramString) { this.bytes = paramString; }
  
  public final String toString() { return super.toString() + "(\"" + Utility.replace(this.bytes, "\n", "\\n") + "\")"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\ConstantUtf8.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */