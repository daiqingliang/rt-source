package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.ExceptionConstants;

public class ARRAYLENGTH extends Instruction implements ExceptionThrower, StackProducer {
  public ARRAYLENGTH() { super((short)190, (short)1); }
  
  public Class[] getExceptions() { return new Class[] { ExceptionConstants.NULL_POINTER_EXCEPTION }; }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitARRAYLENGTH(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\ARRAYLENGTH.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */