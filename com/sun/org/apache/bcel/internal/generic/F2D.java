package com.sun.org.apache.bcel.internal.generic;

public class F2D extends ConversionInstruction {
  public F2D() { super((short)141); }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitConversionInstruction(this);
    paramVisitor.visitF2D(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\F2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */