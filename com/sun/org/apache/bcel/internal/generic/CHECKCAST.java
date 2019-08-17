package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.ExceptionConstants;

public class CHECKCAST extends CPInstruction implements LoadClass, ExceptionThrower, StackProducer, StackConsumer {
  CHECKCAST() {}
  
  public CHECKCAST(int paramInt) { super((short)192, paramInt); }
  
  public Class[] getExceptions() {
    Class[] arrayOfClass = new Class[1 + ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION.length];
    System.arraycopy(ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION, 0, arrayOfClass, 0, ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION.length);
    arrayOfClass[ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION.length] = ExceptionConstants.CLASS_CAST_EXCEPTION;
    return arrayOfClass;
  }
  
  public ObjectType getLoadClassType(ConstantPoolGen paramConstantPoolGen) {
    Type type = getType(paramConstantPoolGen);
    if (type instanceof ArrayType)
      type = ((ArrayType)type).getBasicType(); 
    return (type instanceof ObjectType) ? (ObjectType)type : null;
  }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitLoadClass(this);
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitCPInstruction(this);
    paramVisitor.visitCHECKCAST(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\CHECKCAST.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */