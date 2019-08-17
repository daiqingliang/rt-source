package com.sun.org.apache.bcel.internal.generic;

public class FRETURN extends ReturnInstruction {
  public FRETURN() { super((short)174); }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitReturnInstruction(this);
    paramVisitor.visitFRETURN(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\FRETURN.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */