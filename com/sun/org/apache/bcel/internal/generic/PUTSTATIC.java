package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.ExceptionConstants;

public class PUTSTATIC extends FieldInstruction implements ExceptionThrower, PopInstruction {
  PUTSTATIC() {}
  
  public PUTSTATIC(int paramInt) { super((short)179, paramInt); }
  
  public int consumeStack(ConstantPoolGen paramConstantPoolGen) { return getFieldSize(paramConstantPoolGen); }
  
  public Class[] getExceptions() {
    Class[] arrayOfClass = new Class[1 + ExceptionConstants.EXCS_FIELD_AND_METHOD_RESOLUTION.length];
    System.arraycopy(ExceptionConstants.EXCS_FIELD_AND_METHOD_RESOLUTION, 0, arrayOfClass, 0, ExceptionConstants.EXCS_FIELD_AND_METHOD_RESOLUTION.length);
    arrayOfClass[ExceptionConstants.EXCS_FIELD_AND_METHOD_RESOLUTION.length] = ExceptionConstants.INCOMPATIBLE_CLASS_CHANGE_ERROR;
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
    paramVisitor.visitPUTSTATIC(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\PUTSTATIC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */