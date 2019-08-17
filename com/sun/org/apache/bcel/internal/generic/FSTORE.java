package com.sun.org.apache.bcel.internal.generic;

public class FSTORE extends StoreInstruction {
  FSTORE() { super((short)56, (short)67); }
  
  public FSTORE(int paramInt) { super((short)56, (short)67, paramInt); }
  
  public void accept(Visitor paramVisitor) {
    super.accept(paramVisitor);
    paramVisitor.visitFSTORE(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\FSTORE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */