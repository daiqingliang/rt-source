package com.sun.org.apache.bcel.internal.generic;

public class IF_ACMPNE extends IfInstruction {
  IF_ACMPNE() {}
  
  public IF_ACMPNE(InstructionHandle paramInstructionHandle) { super((short)166, paramInstructionHandle); }
  
  public IfInstruction negate() { return new IF_ACMPEQ(this.target); }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitIfInstruction(this);
    paramVisitor.visitIF_ACMPNE(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\IF_ACMPNE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */