package com.sun.org.apache.bcel.internal.generic;

public interface InstructionTargeter {
  boolean containsTarget(InstructionHandle paramInstructionHandle);
  
  void updateTarget(InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\InstructionTargeter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */