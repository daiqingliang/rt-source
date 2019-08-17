package com.sun.org.apache.bcel.internal.generic;

public abstract class StackInstruction extends Instruction {
  StackInstruction() {}
  
  protected StackInstruction(short paramShort) { super(paramShort, (short)1); }
  
  public Type getType(ConstantPoolGen paramConstantPoolGen) { return Type.UNKNOWN; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\StackInstruction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */