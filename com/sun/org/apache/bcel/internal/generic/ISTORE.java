package com.sun.org.apache.bcel.internal.generic;

public class ISTORE extends StoreInstruction {
  ISTORE() { super((short)54, (short)59); }
  
  public ISTORE(int paramInt) { super((short)54, (short)59, paramInt); }
  
  public void accept(Visitor paramVisitor) {
    super.accept(paramVisitor);
    paramVisitor.visitISTORE(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\ISTORE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */