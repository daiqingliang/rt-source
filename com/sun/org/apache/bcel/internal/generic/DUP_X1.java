package com.sun.org.apache.bcel.internal.generic;

public class DUP_X1 extends StackInstruction {
  public DUP_X1() { super((short)90); }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitStackInstruction(this);
    paramVisitor.visitDUP_X1(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\DUP_X1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */