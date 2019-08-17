package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.ExceptionConstants;

public class NEW extends CPInstruction implements LoadClass, AllocationInstruction, ExceptionThrower, StackProducer {
  NEW() {}
  
  public NEW(int paramInt) { super((short)187, paramInt); }
  
  public Class[] getExceptions() {
    Class[] arrayOfClass = new Class[2 + ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION.length];
    System.arraycopy(ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION, 0, arrayOfClass, 0, ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION.length);
    arrayOfClass[ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION.length + 1] = ExceptionConstants.INSTANTIATION_ERROR;
    arrayOfClass[ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION.length] = ExceptionConstants.ILLEGAL_ACCESS_ERROR;
    return arrayOfClass;
  }
  
  public ObjectType getLoadClassType(ConstantPoolGen paramConstantPoolGen) { return (ObjectType)getType(paramConstantPoolGen); }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitLoadClass(this);
    paramVisitor.visitAllocationInstruction(this);
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitCPInstruction(this);
    paramVisitor.visitNEW(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\NEW.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */