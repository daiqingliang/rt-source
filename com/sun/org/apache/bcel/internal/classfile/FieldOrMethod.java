package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class FieldOrMethod extends AccessFlags implements Cloneable, Node {
  protected int name_index;
  
  protected int signature_index;
  
  protected int attributes_count;
  
  protected Attribute[] attributes;
  
  protected ConstantPool constant_pool;
  
  FieldOrMethod() {}
  
  protected FieldOrMethod(FieldOrMethod paramFieldOrMethod) { this(paramFieldOrMethod.getAccessFlags(), paramFieldOrMethod.getNameIndex(), paramFieldOrMethod.getSignatureIndex(), paramFieldOrMethod.getAttributes(), paramFieldOrMethod.getConstantPool()); }
  
  protected FieldOrMethod(DataInputStream paramDataInputStream, ConstantPool paramConstantPool) throws IOException, ClassFormatException {
    this(paramDataInputStream.readUnsignedShort(), paramDataInputStream.readUnsignedShort(), paramDataInputStream.readUnsignedShort(), null, paramConstantPool);
    this.attributes_count = paramDataInputStream.readUnsignedShort();
    this.attributes = new Attribute[this.attributes_count];
    for (byte b = 0; b < this.attributes_count; b++)
      this.attributes[b] = Attribute.readAttribute(paramDataInputStream, paramConstantPool); 
  }
  
  protected FieldOrMethod(int paramInt1, int paramInt2, int paramInt3, Attribute[] paramArrayOfAttribute, ConstantPool paramConstantPool) {
    this.access_flags = paramInt1;
    this.name_index = paramInt2;
    this.signature_index = paramInt3;
    this.constant_pool = paramConstantPool;
    setAttributes(paramArrayOfAttribute);
  }
  
  public final void dump(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeShort(this.access_flags);
    paramDataOutputStream.writeShort(this.name_index);
    paramDataOutputStream.writeShort(this.signature_index);
    paramDataOutputStream.writeShort(this.attributes_count);
    for (byte b = 0; b < this.attributes_count; b++)
      this.attributes[b].dump(paramDataOutputStream); 
  }
  
  public final Attribute[] getAttributes() { return this.attributes; }
  
  public final void setAttributes(Attribute[] paramArrayOfAttribute) {
    this.attributes = paramArrayOfAttribute;
    this.attributes_count = (paramArrayOfAttribute == null) ? 0 : paramArrayOfAttribute.length;
  }
  
  public final ConstantPool getConstantPool() { return this.constant_pool; }
  
  public final void setConstantPool(ConstantPool paramConstantPool) { this.constant_pool = paramConstantPool; }
  
  public final int getNameIndex() { return this.name_index; }
  
  public final void setNameIndex(int paramInt) { this.name_index = paramInt; }
  
  public final int getSignatureIndex() { return this.signature_index; }
  
  public final void setSignatureIndex(int paramInt) { this.signature_index = paramInt; }
  
  public final String getName() {
    ConstantUtf8 constantUtf8 = (ConstantUtf8)this.constant_pool.getConstant(this.name_index, (byte)1);
    return constantUtf8.getBytes();
  }
  
  public final String getSignature() {
    ConstantUtf8 constantUtf8 = (ConstantUtf8)this.constant_pool.getConstant(this.signature_index, (byte)1);
    return constantUtf8.getBytes();
  }
  
  protected FieldOrMethod copy_(ConstantPool paramConstantPool) {
    FieldOrMethod fieldOrMethod = null;
    try {
      fieldOrMethod = (FieldOrMethod)clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {}
    fieldOrMethod.constant_pool = paramConstantPool;
    fieldOrMethod.attributes = new Attribute[this.attributes_count];
    for (byte b = 0; b < this.attributes_count; b++)
      fieldOrMethod.attributes[b] = this.attributes[b].copy(paramConstantPool); 
    return fieldOrMethod;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\FieldOrMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */