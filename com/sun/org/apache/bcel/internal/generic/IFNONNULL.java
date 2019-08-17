package com.sun.org.apache.bcel.internal.generic;

public class IFNONNULL extends IfInstruction {
  IFNONNULL() {}
  
  public IFNONNULL(InstructionHandle paramInstructionHandle) { super((short)199, paramInstructionHandle); }
  
  public IfInstruction negate() { return new IFNULL(this.target); }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitIfInstruction(this);
    paramVisitor.visitIFNONNULL(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\IFNONNULL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */