package com.sun.org.apache.bcel.internal.generic;

public class IF_ICMPLT extends IfInstruction {
  IF_ICMPLT() {}
  
  public IF_ICMPLT(InstructionHandle paramInstructionHandle) { super((short)161, paramInstructionHandle); }
  
  public IfInstruction negate() { return new IF_ICMPGE(this.target); }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitIfInstruction(this);
    paramVisitor.visitIF_ICMPLT(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\IF_ICMPLT.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */