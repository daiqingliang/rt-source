package com.sun.org.apache.bcel.internal.generic;

public class I2L extends ConversionInstruction {
  public I2L() { super((short)133); }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitConversionInstruction(this);
    paramVisitor.visitI2L(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\I2L.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */