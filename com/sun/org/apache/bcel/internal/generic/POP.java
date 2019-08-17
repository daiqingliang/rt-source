package com.sun.org.apache.bcel.internal.generic;

public class POP extends StackInstruction implements PopInstruction {
  public POP() { super((short)87); }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitPopInstruction(this);
    paramVisitor.visitStackInstruction(this);
    paramVisitor.visitPOP(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\POP.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */