package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.DataOutputStream;
import java.io.IOException;

public class SIPUSH extends Instruction implements ConstantPushInstruction {
  private short b;
  
  SIPUSH() {}
  
  public SIPUSH(short paramShort) {
    super((short)17, (short)3);
    this.b = paramShort;
  }
  
  public void dump(DataOutputStream paramDataOutputStream) throws IOException {
    super.dump(paramDataOutputStream);
    paramDataOutputStream.writeShort(this.b);
  }
  
  public String toString(boolean paramBoolean) { return super.toString(paramBoolean) + " " + this.b; }
  
  protected void initFromFile(ByteSequence paramByteSequence, boolean paramBoolean) throws IOException {
    this.length = 3;
    this.b = paramByteSequence.readShort();
  }
  
  public Number getValue() { return new Integer(this.b); }
  
  public Type getType(ConstantPoolGen paramConstantPoolGen) { return Type.SHORT; }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitPushInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitConstantPushInstruction(this);
    paramVisitor.visitSIPUSH(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\SIPUSH.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */