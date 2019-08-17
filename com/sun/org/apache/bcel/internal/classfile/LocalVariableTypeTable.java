package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LocalVariableTypeTable extends Attribute {
  private static final long serialVersionUID = -1082157891095177114L;
  
  private int local_variable_type_table_length;
  
  private LocalVariable[] local_variable_type_table;
  
  public LocalVariableTypeTable(LocalVariableTypeTable paramLocalVariableTypeTable) { this(paramLocalVariableTypeTable.getNameIndex(), paramLocalVariableTypeTable.getLength(), paramLocalVariableTypeTable.getLocalVariableTypeTable(), paramLocalVariableTypeTable.getConstantPool()); }
  
  public LocalVariableTypeTable(int paramInt1, int paramInt2, LocalVariable[] paramArrayOfLocalVariable, ConstantPool paramConstantPool) {
    super((byte)12, paramInt1, paramInt2, paramConstantPool);
    setLocalVariableTable(paramArrayOfLocalVariable);
  }
  
  LocalVariableTypeTable(int paramInt1, int paramInt2, DataInputStream paramDataInputStream, ConstantPool paramConstantPool) throws IOException {
    this(paramInt1, paramInt2, (LocalVariable[])null, paramConstantPool);
    this.local_variable_type_table_length = paramDataInputStream.readUnsignedShort();
    this.local_variable_type_table = new LocalVariable[this.local_variable_type_table_length];
    for (byte b = 0; b < this.local_variable_type_table_length; b++)
      this.local_variable_type_table[b] = new LocalVariable(paramDataInputStream, paramConstantPool); 
  }
  
  public void accept(Visitor paramVisitor) { paramVisitor.visitLocalVariableTypeTable(this); }
  
  public final void dump(DataOutputStream paramDataOutputStream) throws IOException {
    super.dump(paramDataOutputStream);
    paramDataOutputStream.writeShort(this.local_variable_type_table_length);
    for (byte b = 0; b < this.local_variable_type_table_length; b++)
      this.local_variable_type_table[b].dump(paramDataOutputStream); 
  }
  
  public final LocalVariable[] getLocalVariableTypeTable() { return this.local_variable_type_table; }
  
  public final LocalVariable getLocalVariable(int paramInt) {
    for (byte b = 0; b < this.local_variable_type_table_length; b++) {
      if (this.local_variable_type_table[b].getIndex() == paramInt)
        return this.local_variable_type_table[b]; 
    } 
    return null;
  }
  
  public final void setLocalVariableTable(LocalVariable[] paramArrayOfLocalVariable) {
    this.local_variable_type_table = paramArrayOfLocalVariable;
    this.local_variable_type_table_length = (paramArrayOfLocalVariable == null) ? 0 : paramArrayOfLocalVariable.length;
  }
  
  public final String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    for (byte b = 0; b < this.local_variable_type_table_length; b++) {
      stringBuilder.append(this.local_variable_type_table[b].toString());
      if (b < this.local_variable_type_table_length - 1)
        stringBuilder.append('\n'); 
    } 
    return stringBuilder.toString();
  }
  
  public Attribute copy(ConstantPool paramConstantPool) {
    LocalVariableTypeTable localVariableTypeTable = (LocalVariableTypeTable)clone();
    localVariableTypeTable.local_variable_type_table = new LocalVariable[this.local_variable_type_table_length];
    for (byte b = 0; b < this.local_variable_type_table_length; b++)
      localVariableTypeTable.local_variable_type_table[b] = this.local_variable_type_table[b].copy(); 
    localVariableTypeTable.constant_pool = paramConstantPool;
    return localVariableTypeTable;
  }
  
  public final int getTableLength() { return this.local_variable_type_table_length; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\LocalVariableTypeTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */