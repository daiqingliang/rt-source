package com.sun.org.apache.bcel.internal.generic;

public class DCONST extends Instruction implements ConstantPushInstruction, TypedInstruction {
  private double value;
  
  DCONST() {}
  
  public DCONST(double paramDouble) {
    super((short)14, (short)1);
    if (paramDouble == 0.0D) {
      this.opcode = 14;
    } else if (paramDouble == 1.0D) {
      this.opcode = 15;
    } else {
      throw new ClassGenException("DCONST can be used only for 0.0 and 1.0: " + paramDouble);
    } 
    this.value = paramDouble;
  }
  
  public Number getValue() { return new Double(this.value); }
  
  public Type getType(ConstantPoolGen paramConstantPoolGen) { return Type.DOUBLE; }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitPushInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitConstantPushInstruction(this);
    paramVisitor.visitDCONST(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\DCONST.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */