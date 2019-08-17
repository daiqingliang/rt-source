package com.sun.org.apache.bcel.internal.generic;

public class IFLT extends IfInstruction {
  IFLT() {}
  
  public IFLT(InstructionHandle paramInstructionHandle) { super((short)155, paramInstructionHandle); }
  
  public IfInstruction negate() { return new IFGE(this.target); }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitIfInstruction(this);
    paramVisitor.visitIFLT(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\IFLT.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */