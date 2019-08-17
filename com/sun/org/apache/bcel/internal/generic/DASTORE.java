package com.sun.org.apache.bcel.internal.generic;

public class DASTORE extends ArrayInstruction implements StackConsumer {
  public DASTORE() { super((short)82); }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitArrayInstruction(this);
    paramVisitor.visitDASTORE(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\DASTORE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */