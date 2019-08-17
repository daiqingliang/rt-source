package com.sun.org.apache.bcel.internal.generic;

public class FCMPL extends Instruction implements TypedInstruction, StackProducer, StackConsumer {
  public FCMPL() { super((short)149, (short)1); }
  
  public Type getType(ConstantPoolGen paramConstantPoolGen) { return Type.FLOAT; }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitFCMPL(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\FCMPL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */