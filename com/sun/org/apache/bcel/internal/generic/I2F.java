package com.sun.org.apache.bcel.internal.generic;

public class I2F extends ConversionInstruction {
  public I2F() { super((short)134); }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitConversionInstruction(this);
    paramVisitor.visitI2F(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\I2F.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */