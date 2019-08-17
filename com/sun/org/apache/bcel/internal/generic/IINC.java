package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.DataOutputStream;
import java.io.IOException;

public class IINC extends LocalVariableInstruction {
  private boolean wide;
  
  private int c;
  
  IINC() {}
  
  public IINC(int paramInt1, int paramInt2) {
    this.opcode = 132;
    this.length = 3;
    setIndex(paramInt1);
    setIncrement(paramInt2);
  }
  
  public void dump(DataOutputStream paramDataOutputStream) throws IOException {
    if (this.wide)
      paramDataOutputStream.writeByte(196); 
    paramDataOutputStream.writeByte(this.opcode);
    if (this.wide) {
      paramDataOutputStream.writeShort(this.n);
      paramDataOutputStream.writeShort(this.c);
    } else {
      paramDataOutputStream.writeByte(this.n);
      paramDataOutputStream.writeByte(this.c);
    } 
  }
  
  private final void setWide() {
    if (this.wide = (this.n > 65535 || Math.abs(this.c) > 127)) {
      this.length = 6;
    } else {
      this.length = 3;
    } 
  }
  
  protected void initFromFile(ByteSequence paramByteSequence, boolean paramBoolean) throws IOException {
    this.wide = paramBoolean;
    if (paramBoolean) {
      this.length = 6;
      this.n = paramByteSequence.readUnsignedShort();
      this.c = paramByteSequence.readShort();
    } else {
      this.length = 3;
      this.n = paramByteSequence.readUnsignedByte();
      this.c = paramByteSequence.readByte();
    } 
  }
  
  public String toString(boolean paramBoolean) { return super.toString(paramBoolean) + " " + this.c; }
  
  public final void setIndex(int paramInt) {
    if (paramInt < 0)
      throw new ClassGenException("Negative index value: " + paramInt); 
    this.n = paramInt;
    setWide();
  }
  
  public final int getIncrement() { return this.c; }
  
  public final void setIncrement(int paramInt) {
    this.c = paramInt;
    setWide();
  }
  
  public Type getType(ConstantPoolGen paramConstantPoolGen) { return Type.INT; }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitLocalVariableInstruction(this);
    paramVisitor.visitIINC(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\IINC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */