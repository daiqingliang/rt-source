package com.sun.org.apache.bcel.internal.generic;

public class IFNULL extends IfInstruction {
  IFNULL() {}
  
  public IFNULL(InstructionHandle paramInstructionHandle) { super((short)198, paramInstructionHandle); }
  
  public IfInstruction negate() { return new IFNONNULL(this.target); }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitIfInstruction(this);
    paramVisitor.visitIFNULL(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\IFNULL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */