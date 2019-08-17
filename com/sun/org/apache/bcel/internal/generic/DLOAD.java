package com.sun.org.apache.bcel.internal.generic;

public class DLOAD extends LoadInstruction {
  DLOAD() { super((short)24, (short)38); }
  
  public DLOAD(int paramInt) { super((short)24, (short)38, paramInt); }
  
  public void accept(Visitor paramVisitor) {
    super.accept(paramVisitor);
    paramVisitor.visitDLOAD(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\DLOAD.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */