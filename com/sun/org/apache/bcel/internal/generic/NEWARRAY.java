package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.ExceptionConstants;
import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.DataOutputStream;
import java.io.IOException;

public class NEWARRAY extends Instruction implements AllocationInstruction, ExceptionThrower, StackProducer {
  private byte type;
  
  NEWARRAY() {}
  
  public NEWARRAY(byte paramByte) {
    super((short)188, (short)2);
    this.type = paramByte;
  }
  
  public NEWARRAY(BasicType paramBasicType) { this(paramBasicType.getType()); }
  
  public void dump(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeByte(this.opcode);
    paramDataOutputStream.writeByte(this.type);
  }
  
  public final byte getTypecode() { return this.type; }
  
  public final Type getType() { return new ArrayType(BasicType.getType(this.type), 1); }
  
  public String toString(boolean paramBoolean) { return super.toString(paramBoolean) + " " + Constants.TYPE_NAMES[this.type]; }
  
  protected void initFromFile(ByteSequence paramByteSequence, boolean paramBoolean) throws IOException {
    this.type = paramByteSequence.readByte();
    this.length = 2;
  }
  
  public Class[] getExceptions() { return new Class[] { ExceptionConstants.NEGATIVE_ARRAY_SIZE_EXCEPTION }; }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitAllocationInstruction(this);
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitNEWARRAY(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\NEWARRAY.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */