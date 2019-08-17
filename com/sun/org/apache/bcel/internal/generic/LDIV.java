package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.ExceptionConstants;

public class LDIV extends ArithmeticInstruction implements ExceptionThrower {
  public LDIV() { super((short)109); }
  
  public Class[] getExceptions() { return new Class[] { ExceptionConstants.ARITHMETIC_EXCEPTION }; }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitArithmeticInstruction(this);
    paramVisitor.visitLDIV(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\LDIV.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */