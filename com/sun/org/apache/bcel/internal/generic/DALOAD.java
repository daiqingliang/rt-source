package com.sun.org.apache.bcel.internal.generic;

public class DALOAD extends ArrayInstruction implements StackProducer {
  public DALOAD() { super((short)49); }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitArrayInstruction(this);
    paramVisitor.visitDALOAD(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\DALOAD.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */