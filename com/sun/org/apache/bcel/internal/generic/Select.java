package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class Select extends BranchInstruction implements VariableLengthInstruction, StackProducer {
  protected int[] match;
  
  protected int[] indices;
  
  protected InstructionHandle[] targets;
  
  protected int fixed_length;
  
  protected int match_length;
  
  protected int padding = 0;
  
  Select() {}
  
  Select(short paramShort, int[] paramArrayOfInt, InstructionHandle[] paramArrayOfInstructionHandle, InstructionHandle paramInstructionHandle) {
    super(paramShort, paramInstructionHandle);
    this.targets = paramArrayOfInstructionHandle;
    for (byte b = 0; b < paramArrayOfInstructionHandle.length; b++)
      BranchInstruction.notifyTargetChanged(paramArrayOfInstructionHandle[b], this); 
    this.match = paramArrayOfInt;
    if ((this.match_length = paramArrayOfInt.length) != paramArrayOfInstructionHandle.length)
      throw new ClassGenException("Match and target array have not the same length"); 
    this.indices = new int[this.match_length];
  }
  
  protected int updatePosition(int paramInt1, int paramInt2) {
    this.position += paramInt1;
    short s = this.length;
    this.padding = (4 - (this.position + 1) % 4) % 4;
    this.length = (short)(this.fixed_length + this.padding);
    return this.length - s;
  }
  
  public void dump(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeByte(this.opcode);
    for (byte b = 0; b < this.padding; b++)
      paramDataOutputStream.writeByte(0); 
    this.index = getTargetOffset();
    paramDataOutputStream.writeInt(this.index);
  }
  
  protected void initFromFile(ByteSequence paramByteSequence, boolean paramBoolean) throws IOException {
    this.padding = (4 - paramByteSequence.getIndex() % 4) % 4;
    for (byte b = 0; b < this.padding; b++)
      paramByteSequence.readByte(); 
    this.index = paramByteSequence.readInt();
  }
  
  public String toString(boolean paramBoolean) {
    StringBuilder stringBuilder = new StringBuilder(super.toString(paramBoolean));
    if (paramBoolean) {
      for (byte b = 0; b < this.match_length; b++) {
        String str = "null";
        if (this.targets[b] != null)
          str = this.targets[b].getInstruction().toString(); 
        stringBuilder.append("(").append(this.match[b]).append(", ").append(str).append(" = {").append(this.indices[b]).append("})");
      } 
    } else {
      stringBuilder.append(" ...");
    } 
    return stringBuilder.toString();
  }
  
  public final void setTarget(int paramInt, InstructionHandle paramInstructionHandle) {
    notifyTargetChanging(this.targets[paramInt], this);
    this.targets[paramInt] = paramInstructionHandle;
    notifyTargetChanged(this.targets[paramInt], this);
  }
  
  public void updateTarget(InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2) {
    boolean bool = false;
    if (this.target == paramInstructionHandle1) {
      bool = true;
      setTarget(paramInstructionHandle2);
    } 
    for (byte b = 0; b < this.targets.length; b++) {
      if (this.targets[b] == paramInstructionHandle1) {
        bool = true;
        setTarget(b, paramInstructionHandle2);
      } 
    } 
    if (!bool)
      throw new ClassGenException("Not targeting " + paramInstructionHandle1); 
  }
  
  public boolean containsTarget(InstructionHandle paramInstructionHandle) {
    if (this.target == paramInstructionHandle)
      return true; 
    for (byte b = 0; b < this.targets.length; b++) {
      if (this.targets[b] == paramInstructionHandle)
        return true; 
    } 
    return false;
  }
  
  void dispose() {
    super.dispose();
    for (byte b = 0; b < this.targets.length; b++)
      this.targets[b].removeTargeter(this); 
  }
  
  public int[] getMatchs() { return this.match; }
  
  public int[] getIndices() { return this.indices; }
  
  public InstructionHandle[] getTargets() { return this.targets; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\Select.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */