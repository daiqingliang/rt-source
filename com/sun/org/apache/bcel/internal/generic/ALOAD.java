package com.sun.org.apache.bcel.internal.generic;

public class ALOAD extends LoadInstruction {
  ALOAD() { super((short)25, (short)42); }
  
  public ALOAD(int paramInt) { super((short)25, (short)42, paramInt); }
  
  public void accept(Visitor paramVisitor) {
    super.accept(paramVisitor);
    paramVisitor.visitALOAD(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\ALOAD.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */