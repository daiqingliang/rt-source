package com.sun.org.apache.bcel.internal.generic;

public class SALOAD extends ArrayInstruction implements StackProducer {
  public SALOAD() { super((short)53); }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitArrayInstruction(this);
    paramVisitor.visitSALOAD(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\SALOAD.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */