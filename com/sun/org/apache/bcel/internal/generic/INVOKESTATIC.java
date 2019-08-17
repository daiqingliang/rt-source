package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.ExceptionConstants;

public class INVOKESTATIC extends InvokeInstruction {
  INVOKESTATIC() {}
  
  public INVOKESTATIC(int paramInt) { super((short)184, paramInt); }
  
  public Class[] getExceptions() {
    Class[] arrayOfClass = new Class[2 + ExceptionConstants.EXCS_FIELD_AND_METHOD_RESOLUTION.length];
    System.arraycopy(ExceptionConstants.EXCS_FIELD_AND_METHOD_RESOLUTION, 0, arrayOfClass, 0, ExceptionConstants.EXCS_FIELD_AND_METHOD_RESOLUTION.length);
    arrayOfClass[ExceptionConstants.EXCS_FIELD_AND_METHOD_RESOLUTION.length] = ExceptionConstants.UNSATISFIED_LINK_ERROR;
    arrayOfClass[ExceptionConstants.EXCS_FIELD_AND_METHOD_RESOLUTION.length + 1] = ExceptionConstants.INCOMPATIBLE_CLASS_CHANGE_ERROR;
    return arrayOfClass;
  }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitLoadClass(this);
    paramVisitor.visitCPInstruction(this);
    paramVisitor.visitFieldOrMethod(this);
    paramVisitor.visitInvokeInstruction(this);
    paramVisitor.visitINVOKESTATIC(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\INVOKESTATIC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */