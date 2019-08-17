package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.ExceptionConstants;

public class INSTANCEOF extends CPInstruction implements LoadClass, ExceptionThrower, StackProducer, StackConsumer {
  INSTANCEOF() {}
  
  public INSTANCEOF(int paramInt) { super((short)193, paramInt); }
  
  public Class[] getExceptions() { return ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION; }
  
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
    paramVisitor.visitINSTANCEOF(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\INSTANCEOF.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */