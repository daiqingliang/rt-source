package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class LineNumberTable extends Attribute {
  private int line_number_table_length;
  
  private LineNumber[] line_number_table;
  
  public LineNumberTable(LineNumberTable paramLineNumberTable) { this(paramLineNumberTable.getNameIndex(), paramLineNumberTable.getLength(), paramLineNumberTable.getLineNumberTable(), paramLineNumberTable.getConstantPool()); }
  
  public LineNumberTable(int paramInt1, int paramInt2, LineNumber[] paramArrayOfLineNumber, ConstantPool paramConstantPool) {
    super((byte)4, paramInt1, paramInt2, paramConstantPool);
    setLineNumberTable(paramArrayOfLineNumber);
  }
  
  LineNumberTable(int paramInt1, int paramInt2, DataInputStream paramDataInputStream, ConstantPool paramConstantPool) throws IOException {
    this(paramInt1, paramInt2, (LineNumber[])null, paramConstantPool);
    this.line_number_table_length = paramDataInputStream.readUnsignedShort();
    this.line_number_table = new LineNumber[this.line_number_table_length];
    for (byte b = 0; b < this.line_number_table_length; b++)
      this.line_number_table[b] = new LineNumber(paramDataInputStream); 
  }
  
  public void accept(Visitor paramVisitor) { paramVisitor.visitLineNumberTable(this); }
  
  public final void dump(DataOutputStream paramDataOutputStream) throws IOException {
    super.dump(paramDataOutputStream);
    paramDataOutputStream.writeShort(this.line_number_table_length);
    for (byte b = 0; b < this.line_number_table_length; b++)
      this.line_number_table[b].dump(paramDataOutputStream); 
  }
  
  public final LineNumber[] getLineNumberTable() { return this.line_number_table; }
  
  public final void setLineNumberTable(LineNumber[] paramArrayOfLineNumber) {
    this.line_number_table = paramArrayOfLineNumber;
    this.line_number_table_length = (paramArrayOfLineNumber == null) ? 0 : paramArrayOfLineNumber.length;
  }
  
  public final String toString() {
    StringBuffer stringBuffer1 = new StringBuffer();
    StringBuffer stringBuffer2 = new StringBuffer();
    for (byte b = 0; b < this.line_number_table_length; b++) {
      stringBuffer2.append(this.line_number_table[b].toString());
      if (b < this.line_number_table_length - 1)
        stringBuffer2.append(", "); 
      if (stringBuffer2.length() > 72) {
        stringBuffer2.append('\n');
        stringBuffer1.append(stringBuffer2);
        stringBuffer2.setLength(0);
      } 
    } 
    stringBuffer1.append(stringBuffer2);
    return stringBuffer1.toString();
  }
  
  public int getSourceLine(int paramInt) {
    int i = 0;
    int j = this.line_number_table_length - 1;
    if (j < 0)
      return -1; 
    int k = -1;
    int m = -1;
    do {
      int n = (i + j) / 2;
      int i1 = this.line_number_table[n].getStartPC();
      if (i1 == paramInt)
        return this.line_number_table[n].getLineNumber(); 
      if (paramInt < i1) {
        j = n - 1;
      } else {
        i = n + 1;
      } 
      if (i1 >= paramInt || i1 <= m)
        continue; 
      m = i1;
      k = n;
    } while (i <= j);
    return (k < 0) ? -1 : this.line_number_table[k].getLineNumber();
  }
  
  public Attribute copy(ConstantPool paramConstantPool) {
    LineNumberTable lineNumberTable = (LineNumberTable)clone();
    lineNumberTable.line_number_table = new LineNumber[this.line_number_table_length];
    for (byte b = 0; b < this.line_number_table_length; b++)
      lineNumberTable.line_number_table[b] = this.line_number_table[b].copy(); 
    lineNumberTable.constant_pool = paramConstantPool;
    return lineNumberTable;
  }
  
  public final int getTableLength() { return this.line_number_table_length; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\LineNumberTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */