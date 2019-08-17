package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.CodeException;
import java.io.Serializable;

public final class CodeExceptionGen implements InstructionTargeter, Cloneable, Serializable {
  private InstructionHandle start_pc;
  
  private InstructionHandle end_pc;
  
  private InstructionHandle handler_pc;
  
  private ObjectType catch_type;
  
  public CodeExceptionGen(InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2, InstructionHandle paramInstructionHandle3, ObjectType paramObjectType) {
    setStartPC(paramInstructionHandle1);
    setEndPC(paramInstructionHandle2);
    setHandlerPC(paramInstructionHandle3);
    this.catch_type = paramObjectType;
  }
  
  public CodeException getCodeException(ConstantPoolGen paramConstantPoolGen) { return new CodeException(this.start_pc.getPosition(), this.end_pc.getPosition() + this.end_pc.getInstruction().getLength(), this.handler_pc.getPosition(), (this.catch_type == null) ? 0 : paramConstantPoolGen.addClass(this.catch_type)); }
  
  public final void setStartPC(InstructionHandle paramInstructionHandle) {
    BranchInstruction.notifyTargetChanging(this.start_pc, this);
    this.start_pc = paramInstructionHandle;
    BranchInstruction.notifyTargetChanged(this.start_pc, this);
  }
  
  public final void setEndPC(InstructionHandle paramInstructionHandle) {
    BranchInstruction.notifyTargetChanging(this.end_pc, this);
    this.end_pc = paramInstructionHandle;
    BranchInstruction.notifyTargetChanged(this.end_pc, this);
  }
  
  public final void setHandlerPC(InstructionHandle paramInstructionHandle) {
    BranchInstruction.notifyTargetChanging(this.handler_pc, this);
    this.handler_pc = paramInstructionHandle;
    BranchInstruction.notifyTargetChanged(this.handler_pc, this);
  }
  
  public void updateTarget(InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2) {
    boolean bool = false;
    if (this.start_pc == paramInstructionHandle1) {
      bool = true;
      setStartPC(paramInstructionHandle2);
    } 
    if (this.end_pc == paramInstructionHandle1) {
      bool = true;
      setEndPC(paramInstructionHandle2);
    } 
    if (this.handler_pc == paramInstructionHandle1) {
      bool = true;
      setHandlerPC(paramInstructionHandle2);
    } 
    if (!bool)
      throw new ClassGenException("Not targeting " + paramInstructionHandle1 + ", but {" + this.start_pc + ", " + this.end_pc + ", " + this.handler_pc + "}"); 
  }
  
  public boolean containsTarget(InstructionHandle paramInstructionHandle) { return (this.start_pc == paramInstructionHandle || this.end_pc == paramInstructionHandle || this.handler_pc == paramInstructionHandle); }
  
  public void setCatchType(ObjectType paramObjectType) { this.catch_type = paramObjectType; }
  
  public ObjectType getCatchType() { return this.catch_type; }
  
  public InstructionHandle getStartPC() { return this.start_pc; }
  
  public InstructionHandle getEndPC() { return this.end_pc; }
  
  public InstructionHandle getHandlerPC() { return this.handler_pc; }
  
  public String toString() { return "CodeExceptionGen(" + this.start_pc + ", " + this.end_pc + ", " + this.handler_pc + ")"; }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      System.err.println(cloneNotSupportedException);
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\CodeExceptionGen.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */