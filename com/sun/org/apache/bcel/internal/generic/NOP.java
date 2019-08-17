package com.sun.org.apache.bcel.internal.generic;

public class NOP extends Instruction {
  public NOP() { super((short)0, (short)1); }
  
  public void accept(Visitor paramVisitor) { paramVisitor.visitNOP(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\NOP.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */