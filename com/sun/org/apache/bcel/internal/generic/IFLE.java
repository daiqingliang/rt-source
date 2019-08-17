package com.sun.org.apache.bcel.internal.generic;

public class IFLE extends IfInstruction {
  IFLE() {}
  
  public IFLE(InstructionHandle paramInstructionHandle) { super((short)158, paramInstructionHandle); }
  
  public IfInstruction negate() { return new IFGT(this.target); }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitIfInstruction(this);
    paramVisitor.visitIFLE(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\IFLE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */