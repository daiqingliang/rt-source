package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.LineNumber;
import java.io.Serializable;

public class LineNumberGen implements InstructionTargeter, Cloneable, Serializable {
  private InstructionHandle ih;
  
  private int src_line;
  
  public LineNumberGen(InstructionHandle paramInstructionHandle, int paramInt) {
    setInstruction(paramInstructionHandle);
    setSourceLine(paramInt);
  }
  
  public boolean containsTarget(InstructionHandle paramInstructionHandle) { return (this.ih == paramInstructionHandle); }
  
  public void updateTarget(InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2) {
    if (paramInstructionHandle1 != this.ih)
      throw new ClassGenException("Not targeting " + paramInstructionHandle1 + ", but " + this.ih + "}"); 
    setInstruction(paramInstructionHandle2);
  }
  
  public LineNumber getLineNumber() { return new LineNumber(this.ih.getPosition(), this.src_line); }
  
  public final void setInstruction(InstructionHandle paramInstructionHandle) {
    BranchInstruction.notifyTargetChanging(this.ih, this);
    this.ih = paramInstructionHandle;
    BranchInstruction.notifyTargetChanged(this.ih, this);
  }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      System.err.println(cloneNotSupportedException);
      return null;
    } 
  }
  
  public InstructionHandle getInstruction() { return this.ih; }
  
  public void setSourceLine(int paramInt) { this.src_line = paramInt; }
  
  public int getSourceLine() { return this.src_line; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\LineNumberGen.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */