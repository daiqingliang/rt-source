package com.sun.org.apache.bcel.internal.classfile;

import com.sun.org.apache.bcel.internal.Constants;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

public final class LocalVariable implements Constants, Cloneable, Node, Serializable {
  private int start_pc;
  
  private int length;
  
  private int name_index;
  
  private int signature_index;
  
  private int index;
  
  private ConstantPool constant_pool;
  
  public LocalVariable(LocalVariable paramLocalVariable) { this(paramLocalVariable.getStartPC(), paramLocalVariable.getLength(), paramLocalVariable.getNameIndex(), paramLocalVariable.getSignatureIndex(), paramLocalVariable.getIndex(), paramLocalVariable.getConstantPool()); }
  
  LocalVariable(DataInputStream paramDataInputStream, ConstantPool paramConstantPool) throws IOException { this(paramDataInputStream.readUnsignedShort(), paramDataInputStream.readUnsignedShort(), paramDataInputStream.readUnsignedShort(), paramDataInputStream.readUnsignedShort(), paramDataInputStream.readUnsignedShort(), paramConstantPool); }
  
  public LocalVariable(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, ConstantPool paramConstantPool) {
    this.start_pc = paramInt1;
    this.length = paramInt2;
    this.name_index = paramInt3;
    this.signature_index = paramInt4;
    this.index = paramInt5;
    this.constant_pool = paramConstantPool;
  }
  
  public void accept(Visitor paramVisitor) { paramVisitor.visitLocalVariable(this); }
  
  public final void dump(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeShort(this.start_pc);
    paramDataOutputStream.writeShort(this.length);
    paramDataOutputStream.writeShort(this.name_index);
    paramDataOutputStream.writeShort(this.signature_index);
    paramDataOutputStream.writeShort(this.index);
  }
  
  public final ConstantPool getConstantPool() { return this.constant_pool; }
  
  public final int getLength() { return this.length; }
  
  public final String getName() {
    ConstantUtf8 constantUtf8 = (ConstantUtf8)this.constant_pool.getConstant(this.name_index, (byte)1);
    return constantUtf8.getBytes();
  }
  
  public final int getNameIndex() { return this.name_index; }
  
  public final String getSignature() {
    ConstantUtf8 constantUtf8 = (ConstantUtf8)this.constant_pool.getConstant(this.signature_index, (byte)1);
    return constantUtf8.getBytes();
  }
  
  public final int getSignatureIndex() { return this.signature_index; }
  
  public final int getIndex() { return this.index; }
  
  public final int getStartPC() { return this.start_pc; }
  
  public final void setConstantPool(ConstantPool paramConstantPool) { this.constant_pool = paramConstantPool; }
  
  public final void setLength(int paramInt) { this.length = paramInt; }
  
  public final void setNameIndex(int paramInt) { this.name_index = paramInt; }
  
  public final void setSignatureIndex(int paramInt) { this.signature_index = paramInt; }
  
  public final void setIndex(int paramInt) { this.index = paramInt; }
  
  public final void setStartPC(int paramInt) { this.start_pc = paramInt; }
  
  public final String toString() {
    String str1 = getName();
    String str2 = Utility.signatureToString(getSignature());
    return "LocalVariable(start_pc = " + this.start_pc + ", length = " + this.length + ", index = " + this.index + ":" + str2 + " " + str1 + ")";
  }
  
  public LocalVariable copy() {
    try {
      return (LocalVariable)clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\LocalVariable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */