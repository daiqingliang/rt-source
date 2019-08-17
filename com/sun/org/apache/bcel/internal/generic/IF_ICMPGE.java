package com.sun.org.apache.bcel.internal.generic;

public class IF_ICMPGE extends IfInstruction {
  IF_ICMPGE() {}
  
  public IF_ICMPGE(InstructionHandle paramInstructionHandle) { super((short)162, paramInstructionHandle); }
  
  public IfInstruction negate() { return new IF_ICMPLT(this.target); }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitIfInstruction(this);
    paramVisitor.visitIF_ICMPGE(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\IF_ICMPGE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */