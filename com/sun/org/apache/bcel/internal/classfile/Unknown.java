package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public final class Unknown extends Attribute {
  private byte[] bytes;
  
  private String name;
  
  private static HashMap unknown_attributes = new HashMap();
  
  static Unknown[] getUnknownAttributes() {
    Unknown[] arrayOfUnknown = new Unknown[unknown_attributes.size()];
    Iterator iterator = unknown_attributes.values().iterator();
    for (byte b = 0; iterator.hasNext(); b++)
      arrayOfUnknown[b] = (Unknown)iterator.next(); 
    unknown_attributes.clear();
    return arrayOfUnknown;
  }
  
  public Unknown(Unknown paramUnknown) { this(paramUnknown.getNameIndex(), paramUnknown.getLength(), paramUnknown.getBytes(), paramUnknown.getConstantPool()); }
  
  public Unknown(int paramInt1, int paramInt2, byte[] paramArrayOfByte, ConstantPool paramConstantPool) {
    super((byte)-1, paramInt1, paramInt2, paramConstantPool);
    this.bytes = paramArrayOfByte;
    this.name = ((ConstantUtf8)paramConstantPool.getConstant(paramInt1, (byte)1)).getBytes();
    unknown_attributes.put(this.name, this);
  }
  
  Unknown(int paramInt1, int paramInt2, DataInputStream paramDataInputStream, ConstantPool paramConstantPool) throws IOException {
    this(paramInt1, paramInt2, (byte[])null, paramConstantPool);
    if (paramInt2 > 0) {
      this.bytes = new byte[paramInt2];
      paramDataInputStream.readFully(this.bytes);
    } 
  }
  
  public void accept(Visitor paramVisitor) { paramVisitor.visitUnknown(this); }
  
  public final void dump(DataOutputStream paramDataOutputStream) throws IOException {
    super.dump(paramDataOutputStream);
    if (this.length > 0)
      paramDataOutputStream.write(this.bytes, 0, this.length); 
  }
  
  public final byte[] getBytes() { return this.bytes; }
  
  public final String getName() { return this.name; }
  
  public final void setBytes(byte[] paramArrayOfByte) { this.bytes = paramArrayOfByte; }
  
  public final String toString() {
    String str;
    if (this.length == 0 || this.bytes == null)
      return "(Unknown attribute " + this.name + ")"; 
    if (this.length > 10) {
      byte[] arrayOfByte = new byte[10];
      System.arraycopy(this.bytes, 0, arrayOfByte, 0, 10);
      str = Utility.toHexString(arrayOfByte) + "... (truncated)";
    } else {
      str = Utility.toHexString(this.bytes);
    } 
    return "(Unknown attribute " + this.name + ": " + str + ")";
  }
  
  public Attribute copy(ConstantPool paramConstantPool) {
    Unknown unknown = (Unknown)clone();
    if (this.bytes != null)
      unknown.bytes = (byte[])this.bytes.clone(); 
    unknown.constant_pool = paramConstantPool;
    return unknown;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\Unknown.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */