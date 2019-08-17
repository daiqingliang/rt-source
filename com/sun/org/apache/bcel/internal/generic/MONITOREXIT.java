package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.ExceptionConstants;

public class MONITOREXIT extends Instruction implements ExceptionThrower, StackConsumer {
  public MONITOREXIT() { super((short)195, (short)1); }
  
  public Class[] getExceptions() { return new Class[] { ExceptionConstants.NULL_POINTER_EXCEPTION }; }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitMONITOREXIT(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\MONITOREXIT.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */