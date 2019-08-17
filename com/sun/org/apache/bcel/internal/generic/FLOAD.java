package com.sun.org.apache.bcel.internal.generic;

public class FLOAD extends LoadInstruction {
  FLOAD() { super((short)23, (short)34); }
  
  public FLOAD(int paramInt) { super((short)23, (short)34, paramInt); }
  
  public void accept(Visitor paramVisitor) {
    super.accept(paramVisitor);
    paramVisitor.visitFLOAD(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\FLOAD.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */