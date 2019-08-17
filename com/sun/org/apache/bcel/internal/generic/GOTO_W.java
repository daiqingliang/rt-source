package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.DataOutputStream;
import java.io.IOException;

public class GOTO_W extends GotoInstruction {
  GOTO_W() {}
  
  public GOTO_W(InstructionHandle paramInstructionHandle) {
    super((short)200, paramInstructionHandle);
    this.length = 5;
  }
  
  public void dump(DataOutputStream paramDataOutputStream) throws IOException {
    this.index = getTargetOffset();
    paramDataOutputStream.writeByte(this.opcode);
    paramDataOutputStream.writeInt(this.index);
  }
  
  protected void initFromFile(ByteSequence paramByteSequence, boolean paramBoolean) throws IOException {
    this.index = paramByteSequence.readInt();
    this.length = 5;
  }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitUnconditionalBranch(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitGotoInstruction(this);
    paramVisitor.visitGOTO_W(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\GOTO_W.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */