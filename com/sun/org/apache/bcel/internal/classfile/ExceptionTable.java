package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class ExceptionTable extends Attribute {
  private int number_of_exceptions;
  
  private int[] exception_index_table;
  
  public ExceptionTable(ExceptionTable paramExceptionTable) { this(paramExceptionTable.getNameIndex(), paramExceptionTable.getLength(), paramExceptionTable.getExceptionIndexTable(), paramExceptionTable.getConstantPool()); }
  
  public ExceptionTable(int paramInt1, int paramInt2, int[] paramArrayOfInt, ConstantPool paramConstantPool) {
    super((byte)3, paramInt1, paramInt2, paramConstantPool);
    setExceptionIndexTable(paramArrayOfInt);
  }
  
  ExceptionTable(int paramInt1, int paramInt2, DataInputStream paramDataInputStream, ConstantPool paramConstantPool) throws IOException {
    this(paramInt1, paramInt2, (int[])null, paramConstantPool);
    this.number_of_exceptions = paramDataInputStream.readUnsignedShort();
    this.exception_index_table = new int[this.number_of_exceptions];
    for (byte b = 0; b < this.number_of_exceptions; b++)
      this.exception_index_table[b] = paramDataInputStream.readUnsignedShort(); 
  }
  
  public void accept(Visitor paramVisitor) { paramVisitor.visitExceptionTable(this); }
  
  public final void dump(DataOutputStream paramDataOutputStream) throws IOException {
    super.dump(paramDataOutputStream);
    paramDataOutputStream.writeShort(this.number_of_exceptions);
    for (byte b = 0; b < this.number_of_exceptions; b++)
      paramDataOutputStream.writeShort(this.exception_index_table[b]); 
  }
  
  public final int[] getExceptionIndexTable() { return this.exception_index_table; }
  
  public final int getNumberOfExceptions() { return this.number_of_exceptions; }
  
  public final String[] getExceptionNames() {
    String[] arrayOfString = new String[this.number_of_exceptions];
    for (byte b = 0; b < this.number_of_exceptions; b++)
      arrayOfString[b] = this.constant_pool.getConstantString(this.exception_index_table[b], (byte)7).replace('/', '.'); 
    return arrayOfString;
  }
  
  public final void setExceptionIndexTable(int[] paramArrayOfInt) {
    this.exception_index_table = paramArrayOfInt;
    this.number_of_exceptions = (paramArrayOfInt == null) ? 0 : paramArrayOfInt.length;
  }
  
  public final String toString() {
    StringBuffer stringBuffer = new StringBuffer("");
    for (byte b = 0; b < this.number_of_exceptions; b++) {
      String str = this.constant_pool.getConstantString(this.exception_index_table[b], (byte)7);
      stringBuffer.append(Utility.compactClassName(str, false));
      if (b < this.number_of_exceptions - 1)
        stringBuffer.append(", "); 
    } 
    return stringBuffer.toString();
  }
  
  public Attribute copy(ConstantPool paramConstantPool) {
    ExceptionTable exceptionTable = (ExceptionTable)clone();
    exceptionTable.exception_index_table = (int[])this.exception_index_table.clone();
    exceptionTable.constant_pool = paramConstantPool;
    return exceptionTable;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\ExceptionTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */