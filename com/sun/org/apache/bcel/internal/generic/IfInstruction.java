package com.sun.org.apache.bcel.internal.generic;

public abstract class IfInstruction extends BranchInstruction implements StackConsumer {
  IfInstruction() {}
  
  protected IfInstruction(short paramShort, InstructionHandle paramInstructionHandle) { super(paramShort, paramInstructionHandle); }
  
  public abstract IfInstruction negate();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\IfInstruction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */