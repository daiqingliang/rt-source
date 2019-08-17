package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class LocalVariableInstruction extends Instruction implements TypedInstruction, IndexedInstruction {
  protected int n = -1;
  
  private short c_tag = -1;
  
  private short canon_tag = -1;
  
  private final boolean wide() { return (this.n > 255); }
  
  LocalVariableInstruction(short paramShort1, short paramShort2) {
    this.canon_tag = paramShort1;
    this.c_tag = paramShort2;
  }
  
  LocalVariableInstruction() {}
  
  protected LocalVariableInstruction(short paramShort1, short paramShort2, int paramInt) {
    super(paramShort1, (short)2);
    this.c_tag = paramShort2;
    this.canon_tag = paramShort1;
    setIndex(paramInt);
  }
  
  public void dump(DataOutputStream paramDataOutputStream) throws IOException {
    if (wide())
      paramDataOutputStream.writeByte(196); 
    paramDataOutputStream.writeByte(this.opcode);
    if (this.length > 1)
      if (wide()) {
        paramDataOutputStream.writeShort(this.n);
      } else {
        paramDataOutputStream.writeByte(this.n);
      }  
  }
  
  public String toString(boolean paramBoolean) { return ((this.opcode >= 26 && this.opcode <= 45) || (this.opcode >= 59 && this.opcode <= 78)) ? super.toString(paramBoolean) : (super.toString(paramBoolean) + " " + this.n); }
  
  protected void initFromFile(ByteSequence paramByteSequence, boolean paramBoolean) throws IOException {
    if (paramBoolean) {
      this.n = paramByteSequence.readUnsignedShort();
      this.length = 4;
    } else if ((this.opcode >= 21 && this.opcode <= 25) || (this.opcode >= 54 && this.opcode <= 58)) {
      this.n = paramByteSequence.readUnsignedByte();
      this.length = 2;
    } else if (this.opcode <= 45) {
      this.n = (this.opcode - 26) % 4;
      this.length = 1;
    } else {
      this.n = (this.opcode - 59) % 4;
      this.length = 1;
    } 
  }
  
  public final int getIndex() { return this.n; }
  
  public void setIndex(int paramInt) {
    if (paramInt < 0 || paramInt > 65535)
      throw new ClassGenException("Illegal value: " + paramInt); 
    this.n = paramInt;
    if (paramInt >= 0 && paramInt <= 3) {
      this.opcode = (short)(this.c_tag + paramInt);
      this.length = 1;
    } else {
      this.opcode = this.canon_tag;
      if (wide()) {
        this.length = 4;
      } else {
        this.length = 2;
      } 
    } 
  }
  
  public short getCanonicalTag() { return this.canon_tag; }
  
  public Type getType(ConstantPoolGen paramConstantPoolGen) {
    switch (this.canon_tag) {
      case 21:
      case 54:
        return Type.INT;
      case 22:
      case 55:
        return Type.LONG;
      case 24:
      case 57:
        return Type.DOUBLE;
      case 23:
      case 56:
        return Type.FLOAT;
      case 25:
      case 58:
        return Type.OBJECT;
    } 
    throw new ClassGenException("Oops: unknown case in switch" + this.canon_tag);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\LocalVariableInstruction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */