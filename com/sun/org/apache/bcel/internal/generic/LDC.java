package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.ExceptionConstants;
import com.sun.org.apache.bcel.internal.classfile.Constant;
import com.sun.org.apache.bcel.internal.classfile.ConstantFloat;
import com.sun.org.apache.bcel.internal.classfile.ConstantInteger;
import com.sun.org.apache.bcel.internal.classfile.ConstantString;
import com.sun.org.apache.bcel.internal.classfile.ConstantUtf8;
import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.DataOutputStream;
import java.io.IOException;

public class LDC extends CPInstruction implements PushInstruction, ExceptionThrower, TypedInstruction {
  LDC() {}
  
  public LDC(int paramInt) {
    super((short)19, paramInt);
    setSize();
  }
  
  protected final void setSize() {
    if (this.index <= 255) {
      this.opcode = 18;
      this.length = 2;
    } else {
      this.opcode = 19;
      this.length = 3;
    } 
  }
  
  public void dump(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeByte(this.opcode);
    if (this.length == 2) {
      paramDataOutputStream.writeByte(this.index);
    } else {
      paramDataOutputStream.writeShort(this.index);
    } 
  }
  
  public final void setIndex(int paramInt) {
    super.setIndex(paramInt);
    setSize();
  }
  
  protected void initFromFile(ByteSequence paramByteSequence, boolean paramBoolean) throws IOException {
    this.length = 2;
    this.index = paramByteSequence.readUnsignedByte();
  }
  
  public Object getValue(ConstantPoolGen paramConstantPoolGen) {
    int i;
    Constant constant = paramConstantPoolGen.getConstantPool().getConstant(this.index);
    switch (constant.getTag()) {
      case 8:
        i = ((ConstantString)constant).getStringIndex();
        constant = paramConstantPoolGen.getConstantPool().getConstant(i);
        return ((ConstantUtf8)constant).getBytes();
      case 4:
        return new Float(((ConstantFloat)constant).getBytes());
      case 3:
        return new Integer(((ConstantInteger)constant).getBytes());
    } 
    throw new RuntimeException("Unknown or invalid constant type at " + this.index);
  }
  
  public Type getType(ConstantPoolGen paramConstantPoolGen) {
    switch (paramConstantPoolGen.getConstantPool().getConstant(this.index).getTag()) {
      case 8:
        return Type.STRING;
      case 4:
        return Type.FLOAT;
      case 3:
        return Type.INT;
    } 
    throw new RuntimeException("Unknown or invalid constant type at " + this.index);
  }
  
  public Class[] getExceptions() { return ExceptionConstants.EXCS_STRING_RESOLUTION; }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitPushInstruction(this);
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitCPInstruction(this);
    paramVisitor.visitLDC(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\LDC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */