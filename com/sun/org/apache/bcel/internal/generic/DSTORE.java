package com.sun.org.apache.bcel.internal.generic;

public class DSTORE extends StoreInstruction {
  DSTORE() { super((short)57, (short)71); }
  
  public DSTORE(int paramInt) { super((short)57, (short)71, paramInt); }
  
  public void accept(Visitor paramVisitor) {
    super.accept(paramVisitor);
    paramVisitor.visitDSTORE(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\DSTORE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */