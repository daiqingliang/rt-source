package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LocalVariableTable extends Attribute {
  private int local_variable_table_length;
  
  private LocalVariable[] local_variable_table;
  
  public LocalVariableTable(LocalVariableTable paramLocalVariableTable) { this(paramLocalVariableTable.getNameIndex(), paramLocalVariableTable.getLength(), paramLocalVariableTable.getLocalVariableTable(), paramLocalVariableTable.getConstantPool()); }
  
  public LocalVariableTable(int paramInt1, int paramInt2, LocalVariable[] paramArrayOfLocalVariable, ConstantPool paramConstantPool) {
    super((byte)5, paramInt1, paramInt2, paramConstantPool);
    setLocalVariableTable(paramArrayOfLocalVariable);
  }
  
  LocalVariableTable(int paramInt1, int paramInt2, DataInputStream paramDataInputStream, ConstantPool paramConstantPool) throws IOException {
    this(paramInt1, paramInt2, (LocalVariable[])null, paramConstantPool);
    this.local_variable_table_length = paramDataInputStream.readUnsignedShort();
    this.local_variable_table = new LocalVariable[this.local_variable_table_length];
    for (byte b = 0; b < this.local_variable_table_length; b++)
      this.local_variable_table[b] = new LocalVariable(paramDataInputStream, paramConstantPool); 
  }
  
  public void accept(Visitor paramVisitor) { paramVisitor.visitLocalVariableTable(this); }
  
  public final void dump(DataOutputStream paramDataOutputStream) throws IOException {
    super.dump(paramDataOutputStream);
    paramDataOutputStream.writeShort(this.local_variable_table_length);
    for (byte b = 0; b < this.local_variable_table_length; b++)
      this.local_variable_table[b].dump(paramDataOutputStream); 
  }
  
  public final LocalVariable[] getLocalVariableTable() { return this.local_variable_table; }
  
  public final LocalVariable getLocalVariable(int paramInt) {
    for (byte b = 0; b < this.local_variable_table_length; b++) {
      if (this.local_variable_table[b].getIndex() == paramInt)
        return this.local_variable_table[b]; 
    } 
    return null;
  }
  
  public final void setLocalVariableTable(LocalVariable[] paramArrayOfLocalVariable) {
    this.local_variable_table = paramArrayOfLocalVariable;
    this.local_variable_table_length = (paramArrayOfLocalVariable == null) ? 0 : paramArrayOfLocalVariable.length;
  }
  
  public final String toString() {
    StringBuffer stringBuffer = new StringBuffer("");
    for (byte b = 0; b < this.local_variable_table_length; b++) {
      stringBuffer.append(this.local_variable_table[b].toString());
      if (b < this.local_variable_table_length - 1)
        stringBuffer.append('\n'); 
    } 
    return stringBuffer.toString();
  }
  
  public Attribute copy(ConstantPool paramConstantPool) {
    LocalVariableTable localVariableTable = (LocalVariableTable)clone();
    localVariableTable.local_variable_table = new LocalVariable[this.local_variable_table_length];
    for (byte b = 0; b < this.local_variable_table_length; b++)
      localVariableTable.local_variable_table[b] = this.local_variable_table[b].copy(); 
    localVariableTable.constant_pool = paramConstantPool;
    return localVariableTable;
  }
  
  public final int getTableLength() { return this.local_variable_table_length; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\LocalVariableTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */