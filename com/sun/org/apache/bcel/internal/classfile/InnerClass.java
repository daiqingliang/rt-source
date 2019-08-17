package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class InnerClass implements Cloneable, Node {
  private int inner_class_index;
  
  private int outer_class_index;
  
  private int inner_name_index;
  
  private int inner_access_flags;
  
  public InnerClass(InnerClass paramInnerClass) { this(paramInnerClass.getInnerClassIndex(), paramInnerClass.getOuterClassIndex(), paramInnerClass.getInnerNameIndex(), paramInnerClass.getInnerAccessFlags()); }
  
  InnerClass(DataInputStream paramDataInputStream) throws IOException { this(paramDataInputStream.readUnsignedShort(), paramDataInputStream.readUnsignedShort(), paramDataInputStream.readUnsignedShort(), paramDataInputStream.readUnsignedShort()); }
  
  public InnerClass(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.inner_class_index = paramInt1;
    this.outer_class_index = paramInt2;
    this.inner_name_index = paramInt3;
    this.inner_access_flags = paramInt4;
  }
  
  public void accept(Visitor paramVisitor) { paramVisitor.visitInnerClass(this); }
  
  public final void dump(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeShort(this.inner_class_index);
    paramDataOutputStream.writeShort(this.outer_class_index);
    paramDataOutputStream.writeShort(this.inner_name_index);
    paramDataOutputStream.writeShort(this.inner_access_flags);
  }
  
  public final int getInnerAccessFlags() { return this.inner_access_flags; }
  
  public final int getInnerClassIndex() { return this.inner_class_index; }
  
  public final int getInnerNameIndex() { return this.inner_name_index; }
  
  public final int getOuterClassIndex() { return this.outer_class_index; }
  
  public final void setInnerAccessFlags(int paramInt) { this.inner_access_flags = paramInt; }
  
  public final void setInnerClassIndex(int paramInt) { this.inner_class_index = paramInt; }
  
  public final void setInnerNameIndex(int paramInt) { this.inner_name_index = paramInt; }
  
  public final void setOuterClassIndex(int paramInt) { this.outer_class_index = paramInt; }
  
  public final String toString() { return "InnerClass(" + this.inner_class_index + ", " + this.outer_class_index + ", " + this.inner_name_index + ", " + this.inner_access_flags + ")"; }
  
  public final String toString(ConstantPool paramConstantPool) {
    String str3;
    String str2;
    String str1 = paramConstantPool.getConstantString(this.inner_class_index, (byte)7);
    str1 = Utility.compactClassName(str1);
    if (this.outer_class_index != 0) {
      str2 = paramConstantPool.getConstantString(this.outer_class_index, (byte)7);
      str2 = Utility.compactClassName(str2);
    } else {
      str2 = "<not a member>";
    } 
    if (this.inner_name_index != 0) {
      str3 = ((ConstantUtf8)paramConstantPool.getConstant(this.inner_name_index, (byte)1)).getBytes();
    } else {
      str3 = "<anonymous>";
    } 
    String str4 = Utility.accessToString(this.inner_access_flags, true);
    str4 = str4.equals("") ? "" : (str4 + " ");
    return "InnerClass:" + str4 + str1 + "(\"" + str2 + "\", \"" + str3 + "\")";
  }
  
  public InnerClass copy() {
    try {
      return (InnerClass)clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\InnerClass.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */