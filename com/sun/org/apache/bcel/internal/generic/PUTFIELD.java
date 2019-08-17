package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.ExceptionConstants;

public class PUTFIELD extends FieldInstruction implements PopInstruction, ExceptionThrower {
  PUTFIELD() {}
  
  public PUTFIELD(int paramInt) { super((short)181, paramInt); }
  
  public int consumeStack(ConstantPoolGen paramConstantPoolGen) { return getFieldSize(paramConstantPoolGen) + 1; }
  
  public Class[] getExceptions() {
    Class[] arrayOfClass = new Class[2 + ExceptionConstants.EXCS_FIELD_AND_METHOD_RESOLUTION.length];
    System.arraycopy(ExceptionConstants.EXCS_FIELD_AND_METHOD_RESOLUTION, 0, arrayOfClass, 0, ExceptionConstants.EXCS_FIELD_AND_METHOD_RESOLUTION.length);
    arrayOfClass[ExceptionConstants.EXCS_FIELD_AND_METHOD_RESOLUTION.length + 1] = ExceptionConstants.INCOMPATIBLE_CLASS_CHANGE_ERROR;
    arrayOfClass[ExceptionConstants.EXCS_FIELD_AND_METHOD_RESOLUTION.length] = ExceptionConstants.NULL_POINTER_EXCEPTION;
    return arrayOfClass;
  }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitPopInstruction(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitLoadClass(this);
    paramVisitor.visitCPInstruction(this);
    paramVisitor.visitFieldOrMethod(this);
    paramVisitor.visitFieldInstruction(this);
    paramVisitor.visitPUTFIELD(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\PUTFIELD.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */