package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.Constant;
import com.sun.org.apache.bcel.internal.classfile.ConstantDouble;
import com.sun.org.apache.bcel.internal.classfile.ConstantLong;

public class LDC2_W extends CPInstruction implements PushInstruction, TypedInstruction {
  LDC2_W() {}
  
  public LDC2_W(int paramInt) { super((short)20, paramInt); }
  
  public Type getType(ConstantPoolGen paramConstantPoolGen) {
    switch (paramConstantPoolGen.getConstantPool().getConstant(this.index).getTag()) {
      case 5:
        return Type.LONG;
      case 6:
        return Type.DOUBLE;
    } 
    throw new RuntimeException("Unknown constant type " + this.opcode);
  }
  
  public Number getValue(ConstantPoolGen paramConstantPoolGen) {
    Constant constant = paramConstantPoolGen.getConstantPool().getConstant(this.index);
    switch (constant.getTag()) {
      case 5:
        return new Long(((ConstantLong)constant).getBytes());
      case 6:
        return new Double(((ConstantDouble)constant).getBytes());
    } 
    throw new RuntimeException("Unknown or invalid constant type at " + this.index);
  }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitPushInstruction(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitCPInstruction(this);
    paramVisitor.visitLDC2_W(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\LDC2_W.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */