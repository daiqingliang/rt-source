package com.sun.org.apache.bcel.internal.generic;

public class SWAP extends StackInstruction implements StackConsumer, StackProducer {
  public SWAP() { super((short)95); }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackInstruction(this);
    paramVisitor.visitSWAP(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\SWAP.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */