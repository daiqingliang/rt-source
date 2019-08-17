package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class InnerClasses extends Attribute {
  private InnerClass[] inner_classes;
  
  private int number_of_classes;
  
  public InnerClasses(InnerClasses paramInnerClasses) { this(paramInnerClasses.getNameIndex(), paramInnerClasses.getLength(), paramInnerClasses.getInnerClasses(), paramInnerClasses.getConstantPool()); }
  
  public InnerClasses(int paramInt1, int paramInt2, InnerClass[] paramArrayOfInnerClass, ConstantPool paramConstantPool) {
    super((byte)6, paramInt1, paramInt2, paramConstantPool);
    setInnerClasses(paramArrayOfInnerClass);
  }
  
  InnerClasses(int paramInt1, int paramInt2, DataInputStream paramDataInputStream, ConstantPool paramConstantPool) throws IOException {
    this(paramInt1, paramInt2, (InnerClass[])null, paramConstantPool);
    this.number_of_classes = paramDataInputStream.readUnsignedShort();
    this.inner_classes = new InnerClass[this.number_of_classes];
    for (byte b = 0; b < this.number_of_classes; b++)
      this.inner_classes[b] = new InnerClass(paramDataInputStream); 
  }
  
  public void accept(Visitor paramVisitor) { paramVisitor.visitInnerClasses(this); }
  
  public final void dump(DataOutputStream paramDataOutputStream) throws IOException {
    super.dump(paramDataOutputStream);
    paramDataOutputStream.writeShort(this.number_of_classes);
    for (byte b = 0; b < this.number_of_classes; b++)
      this.inner_classes[b].dump(paramDataOutputStream); 
  }
  
  public final InnerClass[] getInnerClasses() { return this.inner_classes; }
  
  public final void setInnerClasses(InnerClass[] paramArrayOfInnerClass) {
    this.inner_classes = paramArrayOfInnerClass;
    this.number_of_classes = (paramArrayOfInnerClass == null) ? 0 : paramArrayOfInnerClass.length;
  }
  
  public final String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < this.number_of_classes; b++)
      stringBuffer.append(this.inner_classes[b].toString(this.constant_pool) + "\n"); 
    return stringBuffer.toString();
  }
  
  public Attribute copy(ConstantPool paramConstantPool) {
    InnerClasses innerClasses = (InnerClasses)clone();
    innerClasses.inner_classes = new InnerClass[this.number_of_classes];
    for (byte b = 0; b < this.number_of_classes; b++)
      innerClasses.inner_classes[b] = this.inner_classes[b].copy(); 
    innerClasses.constant_pool = paramConstantPool;
    return innerClasses;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\InnerClasses.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */