package com.sun.org.apache.bcel.internal.generic;

public class LLOAD extends LoadInstruction {
  LLOAD() { super((short)22, (short)30); }
  
  public LLOAD(int paramInt) { super((short)22, (short)30, paramInt); }
  
  public void accept(Visitor paramVisitor) {
    super.accept(paramVisitor);
    paramVisitor.visitLLOAD(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\LLOAD.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */