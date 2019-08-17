package com.sun.org.apache.bcel.internal.classfile;

import com.sun.org.apache.bcel.internal.Constants;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class StackMapType implements Cloneable {
  private byte type;
  
  private int index = -1;
  
  private ConstantPool constant_pool;
  
  StackMapType(DataInputStream paramDataInputStream, ConstantPool paramConstantPool) throws IOException {
    this(paramDataInputStream.readByte(), -1, paramConstantPool);
    if (hasIndex())
      setIndex(paramDataInputStream.readShort()); 
    setConstantPool(paramConstantPool);
  }
  
  public StackMapType(byte paramByte, int paramInt, ConstantPool paramConstantPool) {
    setType(paramByte);
    setIndex(paramInt);
    setConstantPool(paramConstantPool);
  }
  
  public void setType(byte paramByte) {
    if (paramByte < 0 || paramByte > 8)
      throw new RuntimeException("Illegal type for StackMapType: " + paramByte); 
    this.type = paramByte;
  }
  
  public byte getType() { return this.type; }
  
  public void setIndex(int paramInt) { this.index = paramInt; }
  
  public int getIndex() { return this.index; }
  
  public final void dump(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeByte(this.type);
    if (hasIndex())
      paramDataOutputStream.writeShort(getIndex()); 
  }
  
  public final boolean hasIndex() { return (this.type == 7 || this.type == 8); }
  
  private String printIndex() { return (this.type == 7) ? (", class=" + this.constant_pool.constantToString(this.index, (byte)7)) : ((this.type == 8) ? (", offset=" + this.index) : ""); }
  
  public final String toString() { return "(type=" + Constants.ITEM_NAMES[this.type] + printIndex() + ")"; }
  
  public StackMapType copy() {
    try {
      return (StackMapType)clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      return null;
    } 
  }
  
  public final ConstantPool getConstantPool() { return this.constant_pool; }
  
  public final void setConstantPool(ConstantPool paramConstantPool) { this.constant_pool = paramConstantPool; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\StackMapType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */