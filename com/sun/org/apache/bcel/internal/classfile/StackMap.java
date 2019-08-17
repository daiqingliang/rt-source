package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class StackMap extends Attribute implements Node {
  private int map_length;
  
  private StackMapEntry[] map;
  
  public StackMap(int paramInt1, int paramInt2, StackMapEntry[] paramArrayOfStackMapEntry, ConstantPool paramConstantPool) {
    super((byte)11, paramInt1, paramInt2, paramConstantPool);
    setStackMap(paramArrayOfStackMapEntry);
  }
  
  StackMap(int paramInt1, int paramInt2, DataInputStream paramDataInputStream, ConstantPool paramConstantPool) throws IOException {
    this(paramInt1, paramInt2, (StackMapEntry[])null, paramConstantPool);
    this.map_length = paramDataInputStream.readUnsignedShort();
    this.map = new StackMapEntry[this.map_length];
    for (byte b = 0; b < this.map_length; b++)
      this.map[b] = new StackMapEntry(paramDataInputStream, paramConstantPool); 
  }
  
  public final void dump(DataOutputStream paramDataOutputStream) throws IOException {
    super.dump(paramDataOutputStream);
    paramDataOutputStream.writeShort(this.map_length);
    for (byte b = 0; b < this.map_length; b++)
      this.map[b].dump(paramDataOutputStream); 
  }
  
  public final StackMapEntry[] getStackMap() { return this.map; }
  
  public final void setStackMap(StackMapEntry[] paramArrayOfStackMapEntry) {
    this.map = paramArrayOfStackMapEntry;
    this.map_length = (paramArrayOfStackMapEntry == null) ? 0 : paramArrayOfStackMapEntry.length;
  }
  
  public final String toString() {
    StringBuffer stringBuffer = new StringBuffer("StackMap(");
    for (byte b = 0; b < this.map_length; b++) {
      stringBuffer.append(this.map[b].toString());
      if (b < this.map_length - 1)
        stringBuffer.append(", "); 
    } 
    stringBuffer.append(')');
    return stringBuffer.toString();
  }
  
  public Attribute copy(ConstantPool paramConstantPool) {
    StackMap stackMap = (StackMap)clone();
    stackMap.map = new StackMapEntry[this.map_length];
    for (byte b = 0; b < this.map_length; b++)
      stackMap.map[b] = this.map[b].copy(); 
    stackMap.constant_pool = paramConstantPool;
    return stackMap;
  }
  
  public void accept(Visitor paramVisitor) { paramVisitor.visitStackMap(this); }
  
  public final int getMapLength() { return this.map_length; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\StackMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */