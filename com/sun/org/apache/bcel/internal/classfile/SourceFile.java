package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class SourceFile extends Attribute {
  private int sourcefile_index;
  
  public SourceFile(SourceFile paramSourceFile) { this(paramSourceFile.getNameIndex(), paramSourceFile.getLength(), paramSourceFile.getSourceFileIndex(), paramSourceFile.getConstantPool()); }
  
  SourceFile(int paramInt1, int paramInt2, DataInputStream paramDataInputStream, ConstantPool paramConstantPool) throws IOException { this(paramInt1, paramInt2, paramDataInputStream.readUnsignedShort(), paramConstantPool); }
  
  public SourceFile(int paramInt1, int paramInt2, int paramInt3, ConstantPool paramConstantPool) {
    super((byte)0, paramInt1, paramInt2, paramConstantPool);
    this.sourcefile_index = paramInt3;
  }
  
  public void accept(Visitor paramVisitor) { paramVisitor.visitSourceFile(this); }
  
  public final void dump(DataOutputStream paramDataOutputStream) throws IOException {
    super.dump(paramDataOutputStream);
    paramDataOutputStream.writeShort(this.sourcefile_index);
  }
  
  public final int getSourceFileIndex() { return this.sourcefile_index; }
  
  public final void setSourceFileIndex(int paramInt) { this.sourcefile_index = paramInt; }
  
  public final String getSourceFileName() {
    ConstantUtf8 constantUtf8 = (ConstantUtf8)this.constant_pool.getConstant(this.sourcefile_index, (byte)1);
    return constantUtf8.getBytes();
  }
  
  public final String toString() { return "SourceFile(" + getSourceFileName() + ")"; }
  
  public Attribute copy(ConstantPool paramConstantPool) { return (SourceFile)clone(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\SourceFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */