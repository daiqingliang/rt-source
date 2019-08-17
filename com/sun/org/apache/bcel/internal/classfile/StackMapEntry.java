package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class StackMapEntry implements Cloneable {
  private int byte_code_offset;
  
  private int number_of_locals;
  
  private StackMapType[] types_of_locals;
  
  private int number_of_stack_items;
  
  private StackMapType[] types_of_stack_items;
  
  private ConstantPool constant_pool;
  
  StackMapEntry(DataInputStream paramDataInputStream, ConstantPool paramConstantPool) throws IOException {
    this(paramDataInputStream.readShort(), paramDataInputStream.readShort(), null, -1, null, paramConstantPool);
    this.types_of_locals = new StackMapType[this.number_of_locals];
    byte b;
    for (b = 0; b < this.number_of_locals; b++)
      this.types_of_locals[b] = new StackMapType(paramDataInputStream, paramConstantPool); 
    this.number_of_stack_items = paramDataInputStream.readShort();
    this.types_of_stack_items = new StackMapType[this.number_of_stack_items];
    for (b = 0; b < this.number_of_stack_items; b++)
      this.types_of_stack_items[b] = new StackMapType(paramDataInputStream, paramConstantPool); 
  }
  
  public StackMapEntry(int paramInt1, int paramInt2, StackMapType[] paramArrayOfStackMapType1, int paramInt3, StackMapType[] paramArrayOfStackMapType2, ConstantPool paramConstantPool) {
    this.byte_code_offset = paramInt1;
    this.number_of_locals = paramInt2;
    this.types_of_locals = paramArrayOfStackMapType1;
    this.number_of_stack_items = paramInt3;
    this.types_of_stack_items = paramArrayOfStackMapType2;
    this.constant_pool = paramConstantPool;
  }
  
  public final void dump(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeShort(this.byte_code_offset);
    paramDataOutputStream.writeShort(this.number_of_locals);
    byte b;
    for (b = 0; b < this.number_of_locals; b++)
      this.types_of_locals[b].dump(paramDataOutputStream); 
    paramDataOutputStream.writeShort(this.number_of_stack_items);
    for (b = 0; b < this.number_of_stack_items; b++)
      this.types_of_stack_items[b].dump(paramDataOutputStream); 
  }
  
  public final String toString() {
    StringBuffer stringBuffer = new StringBuffer("(offset=" + this.byte_code_offset);
    if (this.number_of_locals > 0) {
      stringBuffer.append(", locals={");
      for (byte b = 0; b < this.number_of_locals; b++) {
        stringBuffer.append(this.types_of_locals[b]);
        if (b < this.number_of_locals - 1)
          stringBuffer.append(", "); 
      } 
      stringBuffer.append("}");
    } 
    if (this.number_of_stack_items > 0) {
      stringBuffer.append(", stack items={");
      for (byte b = 0; b < this.number_of_stack_items; b++) {
        stringBuffer.append(this.types_of_stack_items[b]);
        if (b < this.number_of_stack_items - 1)
          stringBuffer.append(", "); 
      } 
      stringBuffer.append("}");
    } 
    stringBuffer.append(")");
    return stringBuffer.toString();
  }
  
  public void setByteCodeOffset(int paramInt) { this.byte_code_offset = paramInt; }
  
  public int getByteCodeOffset() { return this.byte_code_offset; }
  
  public void setNumberOfLocals(int paramInt) { this.number_of_locals = paramInt; }
  
  public int getNumberOfLocals() { return this.number_of_locals; }
  
  public void setTypesOfLocals(StackMapType[] paramArrayOfStackMapType) { this.types_of_locals = paramArrayOfStackMapType; }
  
  public StackMapType[] getTypesOfLocals() { return this.types_of_locals; }
  
  public void setNumberOfStackItems(int paramInt) { this.number_of_stack_items = paramInt; }
  
  public int getNumberOfStackItems() { return this.number_of_stack_items; }
  
  public void setTypesOfStackItems(StackMapType[] paramArrayOfStackMapType) { this.types_of_stack_items = paramArrayOfStackMapType; }
  
  public StackMapType[] getTypesOfStackItems() { return this.types_of_stack_items; }
  
  public StackMapEntry copy() {
    try {
      return (StackMapEntry)clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      return null;
    } 
  }
  
  public void accept(Visitor paramVisitor) { paramVisitor.visitStackMapEntry(this); }
  
  public final ConstantPool getConstantPool() { return this.constant_pool; }
  
  public final void setConstantPool(ConstantPool paramConstantPool) { this.constant_pool = paramConstantPool; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\StackMapEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */