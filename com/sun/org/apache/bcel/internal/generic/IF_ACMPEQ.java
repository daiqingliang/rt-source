package com.sun.org.apache.bcel.internal.generic;

public class IF_ACMPEQ extends IfInstruction {
  IF_ACMPEQ() {}
  
  public IF_ACMPEQ(InstructionHandle paramInstructionHandle) { super((short)165, paramInstructionHandle); }
  
  public IfInstruction negate() { return new IF_ACMPNE(this.target); }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitIfInstruction(this);
    paramVisitor.visitIF_ACMPEQ(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\IF_ACMPEQ.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */