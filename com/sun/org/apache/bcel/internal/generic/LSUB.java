package com.sun.org.apache.bcel.internal.generic;

public class LSUB extends ArithmeticInstruction {
  public LSUB() { super((short)101); }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitArithmeticInstruction(this);
    paramVisitor.visitLSUB(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\LSUB.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */