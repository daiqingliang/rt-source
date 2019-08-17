package com.sun.org.apache.bcel.internal.generic;

public class IOR extends ArithmeticInstruction {
  public IOR() { super((short)128); }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitArithmeticInstruction(this);
    paramVisitor.visitIOR(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\IOR.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */