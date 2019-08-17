package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.DataOutputStream;
import java.io.IOException;

public class LOOKUPSWITCH extends Select {
  LOOKUPSWITCH() {}
  
  public LOOKUPSWITCH(int[] paramArrayOfInt, InstructionHandle[] paramArrayOfInstructionHandle, InstructionHandle paramInstructionHandle) {
    super((short)171, paramArrayOfInt, paramArrayOfInstructionHandle, paramInstructionHandle);
    this.length = (short)(9 + this.match_length * 8);
    this.fixed_length = this.length;
  }
  
  public void dump(DataOutputStream paramDataOutputStream) throws IOException {
    super.dump(paramDataOutputStream);
    paramDataOutputStream.writeInt(this.match_length);
    for (byte b = 0; b < this.match_length; b++) {
      paramDataOutputStream.writeInt(this.match[b]);
      paramDataOutputStream.writeInt(this.indices[b] = getTargetOffset(this.targets[b]));
    } 
  }
  
  protected void initFromFile(ByteSequence paramByteSequence, boolean paramBoolean) throws IOException {
    super.initFromFile(paramByteSequence, paramBoolean);
    this.match_length = paramByteSequence.readInt();
    this.fixed_length = (short)(9 + this.match_length * 8);
    this.length = (short)(this.fixed_length + this.padding);
    this.match = new int[this.match_length];
    this.indices = new int[this.match_length];
    this.targets = new InstructionHandle[this.match_length];
    for (byte b = 0; b < this.match_length; b++) {
      this.match[b] = paramByteSequence.readInt();
      this.indices[b] = paramByteSequence.readInt();
    } 
  }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitVariableLengthInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitSelect(this);
    paramVisitor.visitLOOKUPSWITCH(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\LOOKUPSWITCH.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */