package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class PMGClass extends Attribute {
  private int pmg_class_index;
  
  private int pmg_index;
  
  public PMGClass(PMGClass paramPMGClass) { this(paramPMGClass.getNameIndex(), paramPMGClass.getLength(), paramPMGClass.getPMGIndex(), paramPMGClass.getPMGClassIndex(), paramPMGClass.getConstantPool()); }
  
  PMGClass(int paramInt1, int paramInt2, DataInputStream paramDataInputStream, ConstantPool paramConstantPool) throws IOException { this(paramInt1, paramInt2, paramDataInputStream.readUnsignedShort(), paramDataInputStream.readUnsignedShort(), paramConstantPool); }
  
  public PMGClass(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ConstantPool paramConstantPool) {
    super((byte)9, paramInt1, paramInt2, paramConstantPool);
    this.pmg_index = paramInt3;
    this.pmg_class_index = paramInt4;
  }
  
  public void accept(Visitor paramVisitor) { System.err.println("Visiting non-standard PMGClass object"); }
  
  public final void dump(DataOutputStream paramDataOutputStream) throws IOException {
    super.dump(paramDataOutputStream);
    paramDataOutputStream.writeShort(this.pmg_index);
    paramDataOutputStream.writeShort(this.pmg_class_index);
  }
  
  public final int getPMGClassIndex() { return this.pmg_class_index; }
  
  public final void setPMGClassIndex(int paramInt) { this.pmg_class_index = paramInt; }
  
  public final int getPMGIndex() { return this.pmg_index; }
  
  public final void setPMGIndex(int paramInt) { this.pmg_index = paramInt; }
  
  public final String getPMGName() {
    ConstantUtf8 constantUtf8 = (ConstantUtf8)this.constant_pool.getConstant(this.pmg_index, (byte)1);
    return constantUtf8.getBytes();
  }
  
  public final String getPMGClassName() {
    ConstantUtf8 constantUtf8 = (ConstantUtf8)this.constant_pool.getConstant(this.pmg_class_index, (byte)1);
    return constantUtf8.getBytes();
  }
  
  public final String toString() { return "PMGClass(" + getPMGName() + ", " + getPMGClassName() + ")"; }
  
  public Attribute copy(ConstantPool paramConstantPool) { return (PMGClass)clone(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\PMGClass.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */