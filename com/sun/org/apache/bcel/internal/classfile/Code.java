package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class Code extends Attribute {
  private int max_stack;
  
  private int max_locals;
  
  private int code_length;
  
  private byte[] code;
  
  private int exception_table_length;
  
  private CodeException[] exception_table;
  
  private int attributes_count;
  
  private Attribute[] attributes;
  
  public Code(Code paramCode) { this(paramCode.getNameIndex(), paramCode.getLength(), paramCode.getMaxStack(), paramCode.getMaxLocals(), paramCode.getCode(), paramCode.getExceptionTable(), paramCode.getAttributes(), paramCode.getConstantPool()); }
  
  Code(int paramInt1, int paramInt2, DataInputStream paramDataInputStream, ConstantPool paramConstantPool) throws IOException {
    this(paramInt1, paramInt2, paramDataInputStream.readUnsignedShort(), paramDataInputStream.readUnsignedShort(), (byte[])null, (CodeException[])null, (Attribute[])null, paramConstantPool);
    this.code_length = paramDataInputStream.readInt();
    this.code = new byte[this.code_length];
    paramDataInputStream.readFully(this.code);
    this.exception_table_length = paramDataInputStream.readUnsignedShort();
    this.exception_table = new CodeException[this.exception_table_length];
    byte b;
    for (b = 0; b < this.exception_table_length; b++)
      this.exception_table[b] = new CodeException(paramDataInputStream); 
    this.attributes_count = paramDataInputStream.readUnsignedShort();
    this.attributes = new Attribute[this.attributes_count];
    for (b = 0; b < this.attributes_count; b++)
      this.attributes[b] = Attribute.readAttribute(paramDataInputStream, paramConstantPool); 
    this.length = paramInt2;
  }
  
  public Code(int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte, CodeException[] paramArrayOfCodeException, Attribute[] paramArrayOfAttribute, ConstantPool paramConstantPool) {
    super((byte)2, paramInt1, paramInt2, paramConstantPool);
    this.max_stack = paramInt3;
    this.max_locals = paramInt4;
    setCode(paramArrayOfByte);
    setExceptionTable(paramArrayOfCodeException);
    setAttributes(paramArrayOfAttribute);
  }
  
  public void accept(Visitor paramVisitor) { paramVisitor.visitCode(this); }
  
  public final void dump(DataOutputStream paramDataOutputStream) throws IOException {
    super.dump(paramDataOutputStream);
    paramDataOutputStream.writeShort(this.max_stack);
    paramDataOutputStream.writeShort(this.max_locals);
    paramDataOutputStream.writeInt(this.code_length);
    paramDataOutputStream.write(this.code, 0, this.code_length);
    paramDataOutputStream.writeShort(this.exception_table_length);
    byte b;
    for (b = 0; b < this.exception_table_length; b++)
      this.exception_table[b].dump(paramDataOutputStream); 
    paramDataOutputStream.writeShort(this.attributes_count);
    for (b = 0; b < this.attributes_count; b++)
      this.attributes[b].dump(paramDataOutputStream); 
  }
  
  public final Attribute[] getAttributes() { return this.attributes; }
  
  public LineNumberTable getLineNumberTable() {
    for (byte b = 0; b < this.attributes_count; b++) {
      if (this.attributes[b] instanceof LineNumberTable)
        return (LineNumberTable)this.attributes[b]; 
    } 
    return null;
  }
  
  public LocalVariableTable getLocalVariableTable() {
    for (byte b = 0; b < this.attributes_count; b++) {
      if (this.attributes[b] instanceof LocalVariableTable)
        return (LocalVariableTable)this.attributes[b]; 
    } 
    return null;
  }
  
  public final byte[] getCode() { return this.code; }
  
  public final CodeException[] getExceptionTable() { return this.exception_table; }
  
  public final int getMaxLocals() { return this.max_locals; }
  
  public final int getMaxStack() { return this.max_stack; }
  
  private final int getInternalLength() { return 8 + this.code_length + 2 + 8 * this.exception_table_length + 2; }
  
  private final int calculateLength() {
    int i = 0;
    for (byte b = 0; b < this.attributes_count; b++)
      i += (this.attributes[b]).length + 6; 
    return i + getInternalLength();
  }
  
  public final void setAttributes(Attribute[] paramArrayOfAttribute) {
    this.attributes = paramArrayOfAttribute;
    this.attributes_count = (paramArrayOfAttribute == null) ? 0 : paramArrayOfAttribute.length;
    this.length = calculateLength();
  }
  
  public final void setCode(byte[] paramArrayOfByte) {
    this.code = paramArrayOfByte;
    this.code_length = (paramArrayOfByte == null) ? 0 : paramArrayOfByte.length;
  }
  
  public final void setExceptionTable(CodeException[] paramArrayOfCodeException) {
    this.exception_table = paramArrayOfCodeException;
    this.exception_table_length = (paramArrayOfCodeException == null) ? 0 : paramArrayOfCodeException.length;
  }
  
  public final void setMaxLocals(int paramInt) { this.max_locals = paramInt; }
  
  public final void setMaxStack(int paramInt) { this.max_stack = paramInt; }
  
  public final String toString(boolean paramBoolean) {
    StringBuffer stringBuffer = new StringBuffer("Code(max_stack = " + this.max_stack + ", max_locals = " + this.max_locals + ", code_length = " + this.code_length + ")\n" + Utility.codeToString(this.code, this.constant_pool, 0, -1, paramBoolean));
    if (this.exception_table_length > 0) {
      stringBuffer.append("\nException handler(s) = \nFrom\tTo\tHandler\tType\n");
      for (byte b = 0; b < this.exception_table_length; b++)
        stringBuffer.append(this.exception_table[b].toString(this.constant_pool, paramBoolean) + "\n"); 
    } 
    if (this.attributes_count > 0) {
      stringBuffer.append("\nAttribute(s) = \n");
      for (byte b = 0; b < this.attributes_count; b++)
        stringBuffer.append(this.attributes[b].toString() + "\n"); 
    } 
    return stringBuffer.toString();
  }
  
  public final String toString() { return toString(true); }
  
  public Attribute copy(ConstantPool paramConstantPool) {
    Code code1 = (Code)clone();
    code1.code = (byte[])this.code.clone();
    code1.constant_pool = paramConstantPool;
    code1.exception_table = new CodeException[this.exception_table_length];
    byte b;
    for (b = 0; b < this.exception_table_length; b++)
      code1.exception_table[b] = this.exception_table[b].copy(); 
    code1.attributes = new Attribute[this.attributes_count];
    for (b = 0; b < this.attributes_count; b++)
      code1.attributes[b] = this.attributes[b].copy(paramConstantPool); 
    return code1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\Code.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */