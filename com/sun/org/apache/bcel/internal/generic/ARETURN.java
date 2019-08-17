package com.sun.org.apache.bcel.internal.generic;

public class ARETURN extends ReturnInstruction {
  public ARETURN() { super((short)176); }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitReturnInstruction(this);
    paramVisitor.visitARETURN(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\ARETURN.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */