package com.sun.org.apache.bcel.internal.generic;

public class IF_ICMPGT extends IfInstruction {
  IF_ICMPGT() {}
  
  public IF_ICMPGT(InstructionHandle paramInstructionHandle) { super((short)163, paramInstructionHandle); }
  
  public IfInstruction negate() { return new IF_ICMPLE(this.target); }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitIfInstruction(this);
    paramVisitor.visitIF_ICMPGT(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\IF_ICMPGT.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */