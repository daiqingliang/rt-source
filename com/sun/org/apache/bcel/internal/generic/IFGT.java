package com.sun.org.apache.bcel.internal.generic;

public class IFGT extends IfInstruction {
  IFGT() {}
  
  public IFGT(InstructionHandle paramInstructionHandle) { super((short)157, paramInstructionHandle); }
  
  public IfInstruction negate() { return new IFLE(this.target); }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitIfInstruction(this);
    paramVisitor.visitIFGT(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\IFGT.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */