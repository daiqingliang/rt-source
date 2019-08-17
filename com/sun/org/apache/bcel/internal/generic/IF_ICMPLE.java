package com.sun.org.apache.bcel.internal.generic;

public class IF_ICMPLE extends IfInstruction {
  IF_ICMPLE() {}
  
  public IF_ICMPLE(InstructionHandle paramInstructionHandle) { super((short)164, paramInstructionHandle); }
  
  public IfInstruction negate() { return new IF_ICMPGT(this.target); }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitIfInstruction(this);
    paramVisitor.visitIF_ICMPLE(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\IF_ICMPLE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */