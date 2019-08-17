package com.sun.org.apache.bcel.internal.generic;

public class IMPDEP2 extends Instruction {
  public IMPDEP2() { super((short)255, (short)1); }
  
  public void accept(Visitor paramVisitor) { paramVisitor.visitIMPDEP2(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\IMPDEP2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */