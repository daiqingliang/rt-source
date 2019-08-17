package com.sun.org.apache.bcel.internal.generic;

public class AASTORE extends ArrayInstruction implements StackConsumer {
  public AASTORE() { super((short)83); }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitArrayInstruction(this);
    paramVisitor.visitAASTORE(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\AASTORE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */