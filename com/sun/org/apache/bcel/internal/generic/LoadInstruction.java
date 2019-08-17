package com.sun.org.apache.bcel.internal.generic;

public abstract class LoadInstruction extends LocalVariableInstruction implements PushInstruction {
  LoadInstruction(short paramShort1, short paramShort2) { super(paramShort1, paramShort2); }
  
  protected LoadInstruction(short paramShort1, short paramShort2, int paramInt) { super(paramShort1, paramShort2, paramInt); }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitPushInstruction(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitLocalVariableInstruction(this);
    paramVisitor.visitLoadInstruction(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\LoadInstruction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */