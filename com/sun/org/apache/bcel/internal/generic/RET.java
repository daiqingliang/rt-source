package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.DataOutputStream;
import java.io.IOException;

public class RET extends Instruction implements IndexedInstruction, TypedInstruction {
  private boolean wide;
  
  private int index;
  
  RET() {}
  
  public RET(int paramInt) {
    super((short)169, (short)2);
    setIndex(paramInt);
  }
  
  public void dump(DataOutputStream paramDataOutputStream) throws IOException {
    if (this.wide)
      paramDataOutputStream.writeByte(196); 
    paramDataOutputStream.writeByte(this.opcode);
    if (this.wide) {
      paramDataOutputStream.writeShort(this.index);
    } else {
      paramDataOutputStream.writeByte(this.index);
    } 
  }
  
  private final void setWide() {
    if (this.wide = (this.index > 255)) {
      this.length = 4;
    } else {
      this.length = 2;
    } 
  }
  
  protected void initFromFile(ByteSequence paramByteSequence, boolean paramBoolean) throws IOException {
    this.wide = paramBoolean;
    if (paramBoolean) {
      this.index = paramByteSequence.readUnsignedShort();
      this.length = 4;
    } else {
      this.index = paramByteSequence.readUnsignedByte();
      this.length = 2;
    } 
  }
  
  public final int getIndex() { return this.index; }
  
  public final void setIndex(int paramInt) {
    if (paramInt < 0)
      throw new ClassGenException("Negative index value: " + paramInt); 
    this.index = paramInt;
    setWide();
  }
  
  public String toString(boolean paramBoolean) { return super.toString(paramBoolean) + " " + this.index; }
  
  public Type getType(ConstantPoolGen paramConstantPoolGen) { return ReturnaddressType.NO_TARGET; }
  
  public void accept(Visitor paramVisitor) { paramVisitor.visitRET(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\RET.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */