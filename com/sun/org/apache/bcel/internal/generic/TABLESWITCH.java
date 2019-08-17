package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.DataOutputStream;
import java.io.IOException;

public class TABLESWITCH extends Select {
  TABLESWITCH() {}
  
  public TABLESWITCH(int[] paramArrayOfInt, InstructionHandle[] paramArrayOfInstructionHandle, InstructionHandle paramInstructionHandle) {
    super((short)170, paramArrayOfInt, paramArrayOfInstructionHandle, paramInstructionHandle);
    this.length = (short)(13 + this.match_length * 4);
    this.fixed_length = this.length;
  }
  
  public void dump(DataOutputStream paramDataOutputStream) throws IOException {
    super.dump(paramDataOutputStream);
    int i = (this.match_length > 0) ? this.match[0] : 0;
    paramDataOutputStream.writeInt(i);
    int j = (this.match_length > 0) ? this.match[this.match_length - 1] : 0;
    paramDataOutputStream.writeInt(j);
    for (byte b = 0; b < this.match_length; b++)
      paramDataOutputStream.writeInt(this.indices[b] = getTargetOffset(this.targets[b])); 
  }
  
  protected void initFromFile(ByteSequence paramByteSequence, boolean paramBoolean) throws IOException {
    super.initFromFile(paramByteSequence, paramBoolean);
    int i = paramByteSequence.readInt();
    int j = paramByteSequence.readInt();
    this.match_length = j - i + 1;
    this.fixed_length = (short)(13 + this.match_length * 4);
    this.length = (short)(this.fixed_length + this.padding);
    this.match = new int[this.match_length];
    this.indices = new int[this.match_length];
    this.targets = new InstructionHandle[this.match_length];
    int k;
    for (k = i; k <= j; k++)
      this.match[k - i] = k; 
    for (k = 0; k < this.match_length; k++)
      this.indices[k] = paramByteSequence.readInt(); 
  }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitVariableLengthInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitSelect(this);
    paramVisitor.visitTABLESWITCH(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\TABLESWITCH.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */