package com.sun.org.apache.bcel.internal.generic;

public class D2I extends ConversionInstruction {
  public D2I() { super((short)142); }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitConversionInstruction(this);
    paramVisitor.visitD2I(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\D2I.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */