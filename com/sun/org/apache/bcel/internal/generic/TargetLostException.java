package com.sun.org.apache.bcel.internal.generic;

public final class TargetLostException extends Exception {
  private InstructionHandle[] targets;
  
  TargetLostException(InstructionHandle[] paramArrayOfInstructionHandle, String paramString) {
    super(paramString);
    this.targets = paramArrayOfInstructionHandle;
  }
  
  public InstructionHandle[] getTargets() { return this.targets; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\TargetLostException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */