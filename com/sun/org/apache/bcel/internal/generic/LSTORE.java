package com.sun.org.apache.bcel.internal.generic;

public class LSTORE extends StoreInstruction {
  LSTORE() { super((short)55, (short)63); }
  
  public LSTORE(int paramInt) { super((short)55, (short)63, paramInt); }
  
  public void accept(Visitor paramVisitor) {
    super.accept(paramVisitor);
    paramVisitor.visitLSTORE(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\LSTORE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */