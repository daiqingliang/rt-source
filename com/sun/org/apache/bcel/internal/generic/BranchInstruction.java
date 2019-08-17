package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class BranchInstruction extends Instruction implements InstructionTargeter {
  protected int index;
  
  protected InstructionHandle target;
  
  protected int position;
  
  BranchInstruction() {}
  
  protected BranchInstruction(short paramShort, InstructionHandle paramInstructionHandle) {
    super(paramShort, (short)3);
    setTarget(paramInstructionHandle);
  }
  
  public void dump(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeByte(this.opcode);
    this.index = getTargetOffset();
    if (Math.abs(this.index) >= 32767)
      throw new ClassGenException("Branch target offset too large for short"); 
    paramDataOutputStream.writeShort(this.index);
  }
  
  protected int getTargetOffset(InstructionHandle paramInstructionHandle) {
    if (paramInstructionHandle == null)
      throw new ClassGenException("Target of " + super.toString(true) + " is invalid null handle"); 
    int i = paramInstructionHandle.getPosition();
    if (i < 0)
      throw new ClassGenException("Invalid branch target position offset for " + super.toString(true) + ":" + i + ":" + paramInstructionHandle); 
    return i - this.position;
  }
  
  protected int getTargetOffset() { return getTargetOffset(this.target); }
  
  protected int updatePosition(int paramInt1, int paramInt2) {
    this.position += paramInt1;
    return 0;
  }
  
  public String toString(boolean paramBoolean) {
    String str1 = super.toString(paramBoolean);
    String str2 = "null";
    if (paramBoolean) {
      if (this.target != null)
        if (this.target.getInstruction() == this) {
          str2 = "<points to itself>";
        } else if (this.target.getInstruction() == null) {
          str2 = "<null instruction!!!?>";
        } else {
          str2 = this.target.getInstruction().toString(false);
        }  
    } else if (this.target != null) {
      this.index = getTargetOffset();
      str2 = "" + (this.index + this.position);
    } 
    return str1 + " -> " + str2;
  }
  
  protected void initFromFile(ByteSequence paramByteSequence, boolean paramBoolean) throws IOException {
    this.length = 3;
    this.index = paramByteSequence.readShort();
  }
  
  public final int getIndex() { return this.index; }
  
  public InstructionHandle getTarget() { return this.target; }
  
  public final void setTarget(InstructionHandle paramInstructionHandle) {
    notifyTargetChanging(this.target, this);
    this.target = paramInstructionHandle;
    notifyTargetChanged(this.target, this);
  }
  
  static void notifyTargetChanging(InstructionHandle paramInstructionHandle, InstructionTargeter paramInstructionTargeter) {
    if (paramInstructionHandle != null)
      paramInstructionHandle.removeTargeter(paramInstructionTargeter); 
  }
  
  static void notifyTargetChanged(InstructionHandle paramInstructionHandle, InstructionTargeter paramInstructionTargeter) {
    if (paramInstructionHandle != null)
      paramInstructionHandle.addTargeter(paramInstructionTargeter); 
  }
  
  public void updateTarget(InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2) {
    if (this.target == paramInstructionHandle1) {
      setTarget(paramInstructionHandle2);
    } else {
      throw new ClassGenException("Not targeting " + paramInstructionHandle1 + ", but " + this.target);
    } 
  }
  
  public boolean containsTarget(InstructionHandle paramInstructionHandle) { return (this.target == paramInstructionHandle); }
  
  void dispose() {
    setTarget(null);
    this.index = -1;
    this.position = -1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\BranchInstruction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */