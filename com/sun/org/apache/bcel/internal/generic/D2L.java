package com.sun.org.apache.bcel.internal.generic;

public class D2L extends ConversionInstruction {
  public D2L() { super((short)143); }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitConversionInstruction(this);
    paramVisitor.visitD2L(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\D2L.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */