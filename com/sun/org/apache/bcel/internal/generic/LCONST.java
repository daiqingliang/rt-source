package com.sun.org.apache.bcel.internal.generic;

public class LCONST extends Instruction implements ConstantPushInstruction, TypedInstruction {
  private long value;
  
  LCONST() {}
  
  public LCONST(long paramLong) {
    super((short)9, (short)1);
    if (paramLong == 0L) {
      this.opcode = 9;
    } else if (paramLong == 1L) {
      this.opcode = 10;
    } else {
      throw new ClassGenException("LCONST can be used only for 0 and 1: " + paramLong);
    } 
    this.value = paramLong;
  }
  
  public Number getValue() { return new Long(this.value); }
  
  public Type getType(ConstantPoolGen paramConstantPoolGen) { return Type.LONG; }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitPushInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitConstantPushInstruction(this);
    paramVisitor.visitLCONST(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\LCONST.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */