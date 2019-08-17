package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.ConstantCP;
import com.sun.org.apache.bcel.internal.classfile.ConstantNameAndType;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.classfile.ConstantUtf8;

public abstract class FieldOrMethod extends CPInstruction implements LoadClass {
  FieldOrMethod() {}
  
  protected FieldOrMethod(short paramShort, int paramInt) { super(paramShort, paramInt); }
  
  public String getSignature(ConstantPoolGen paramConstantPoolGen) {
    ConstantPool constantPool = paramConstantPoolGen.getConstantPool();
    ConstantCP constantCP = (ConstantCP)constantPool.getConstant(this.index);
    ConstantNameAndType constantNameAndType = (ConstantNameAndType)constantPool.getConstant(constantCP.getNameAndTypeIndex());
    return ((ConstantUtf8)constantPool.getConstant(constantNameAndType.getSignatureIndex())).getBytes();
  }
  
  public String getName(ConstantPoolGen paramConstantPoolGen) {
    ConstantPool constantPool = paramConstantPoolGen.getConstantPool();
    ConstantCP constantCP = (ConstantCP)constantPool.getConstant(this.index);
    ConstantNameAndType constantNameAndType = (ConstantNameAndType)constantPool.getConstant(constantCP.getNameAndTypeIndex());
    return ((ConstantUtf8)constantPool.getConstant(constantNameAndType.getNameIndex())).getBytes();
  }
  
  public String getClassName(ConstantPoolGen paramConstantPoolGen) {
    ConstantPool constantPool = paramConstantPoolGen.getConstantPool();
    ConstantCP constantCP = (ConstantCP)constantPool.getConstant(this.index);
    return constantPool.getConstantString(constantCP.getClassIndex(), (byte)7).replace('/', '.');
  }
  
  public ObjectType getClassType(ConstantPoolGen paramConstantPoolGen) { return new ObjectType(getClassName(paramConstantPoolGen)); }
  
  public ObjectType getLoadClassType(ConstantPoolGen paramConstantPoolGen) { return getClassType(paramConstantPoolGen); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\FieldOrMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */