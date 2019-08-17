package com.sun.org.apache.bcel.internal.generic;

import java.io.DataOutputStream;
import java.io.IOException;

public class JSR extends JsrInstruction implements VariableLengthInstruction {
  JSR() {}
  
  public JSR(InstructionHandle paramInstructionHandle) { super((short)168, paramInstructionHandle); }
  
  public void dump(DataOutputStream paramDataOutputStream) throws IOException {
    this.index = getTargetOffset();
    if (this.opcode == 168) {
      super.dump(paramDataOutputStream);
    } else {
      this.index = getTargetOffset();
      paramDataOutputStream.writeByte(this.opcode);
      paramDataOutputStream.writeInt(this.index);
    } 
  }
  
  protected int updatePosition(int paramInt1, int paramInt2) {
    int i = getTargetOffset();
    this.position += paramInt1;
    if (Math.abs(i) >= 32767 - paramInt2) {
      this.opcode = 201;
      this.length = 5;
      return 2;
    } 
    return 0;
  }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitVariableLengthInstruction(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitJsrInstruction(this);
    paramVisitor.visitJSR(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\JSR.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */