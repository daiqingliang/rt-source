package com.sun.org.apache.bcel.internal.generic;

public class RETURN extends ReturnInstruction {
  public RETURN() { super((short)177); }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitReturnInstruction(this);
    paramVisitor.visitRETURN(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\RETURN.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */