package com.sun.org.apache.bcel.internal.generic;

public class IF_ICMPEQ extends IfInstruction {
  IF_ICMPEQ() {}
  
  public IF_ICMPEQ(InstructionHandle paramInstructionHandle) { super((short)159, paramInstructionHandle); }
  
  public IfInstruction negate() { return new IF_ICMPNE(this.target); }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitIfInstruction(this);
    paramVisitor.visitIF_ICMPEQ(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\IF_ICMPEQ.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */