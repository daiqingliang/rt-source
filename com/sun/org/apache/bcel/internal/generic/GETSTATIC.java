package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.ExceptionConstants;

public class GETSTATIC extends FieldInstruction implements PushInstruction, ExceptionThrower {
  GETSTATIC() {}
  
  public GETSTATIC(int paramInt) { super((short)178, paramInt); }
  
  public int produceStack(ConstantPoolGen paramConstantPoolGen) { return getFieldSize(paramConstantPoolGen); }
  
  public Class[] getExceptions() {
    Class[] arrayOfClass = new Class[1 + ExceptionConstants.EXCS_FIELD_AND_METHOD_RESOLUTION.length];
    System.arraycopy(ExceptionConstants.EXCS_FIELD_AND_METHOD_RESOLUTION, 0, arrayOfClass, 0, ExceptionConstants.EXCS_FIELD_AND_METHOD_RESOLUTION.length);
    arrayOfClass[ExceptionConstants.EXCS_FIELD_AND_METHOD_RESOLUTION.length] = ExceptionConstants.INCOMPATIBLE_CLASS_CHANGE_ERROR;
    return arrayOfClass;
  }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitPushInstruction(this);
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitLoadClass(this);
    paramVisitor.visitCPInstruction(this);
    paramVisitor.visitFieldOrMethod(this);
    paramVisitor.visitFieldInstruction(this);
    paramVisitor.visitGETSTATIC(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\GETSTATIC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */