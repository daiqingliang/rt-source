package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.ExceptionConstants;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.DataOutputStream;
import java.io.IOException;

public final class INVOKEINTERFACE extends InvokeInstruction {
  private int nargs;
  
  INVOKEINTERFACE() {}
  
  public INVOKEINTERFACE(int paramInt1, int paramInt2) {
    super((short)185, paramInt1);
    this.length = 5;
    if (paramInt2 < 1)
      throw new ClassGenException("Number of arguments must be > 0 " + paramInt2); 
    this.nargs = paramInt2;
  }
  
  public void dump(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeByte(this.opcode);
    paramDataOutputStream.writeShort(this.index);
    paramDataOutputStream.writeByte(this.nargs);
    paramDataOutputStream.writeByte(0);
  }
  
  public int getCount() { return this.nargs; }
  
  protected void initFromFile(ByteSequence paramByteSequence, boolean paramBoolean) throws IOException {
    super.initFromFile(paramByteSequence, paramBoolean);
    this.length = 5;
    this.nargs = paramByteSequence.readUnsignedByte();
    paramByteSequence.readByte();
  }
  
  public String toString(ConstantPool paramConstantPool) { return super.toString(paramConstantPool) + " " + this.nargs; }
  
  public int consumeStack(ConstantPoolGen paramConstantPoolGen) { return this.nargs; }
  
  public Class[] getExceptions() {
    Class[] arrayOfClass = new Class[4 + ExceptionConstants.EXCS_INTERFACE_METHOD_RESOLUTION.length];
    System.arraycopy(ExceptionConstants.EXCS_INTERFACE_METHOD_RESOLUTION, 0, arrayOfClass, 0, ExceptionConstants.EXCS_INTERFACE_METHOD_RESOLUTION.length);
    arrayOfClass[ExceptionConstants.EXCS_INTERFACE_METHOD_RESOLUTION.length + 3] = ExceptionConstants.INCOMPATIBLE_CLASS_CHANGE_ERROR;
    arrayOfClass[ExceptionConstants.EXCS_INTERFACE_METHOD_RESOLUTION.length + 2] = ExceptionConstants.ILLEGAL_ACCESS_ERROR;
    arrayOfClass[ExceptionConstants.EXCS_INTERFACE_METHOD_RESOLUTION.length + 1] = ExceptionConstants.ABSTRACT_METHOD_ERROR;
    arrayOfClass[ExceptionConstants.EXCS_INTERFACE_METHOD_RESOLUTION.length] = ExceptionConstants.UNSATISFIED_LINK_ERROR;
    return arrayOfClass;
  }
  
  public void accept(Visitor paramVisitor) {
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitLoadClass(this);
    paramVisitor.visitCPInstruction(this);
    paramVisitor.visitFieldOrMethod(this);
    paramVisitor.visitInvokeInstruction(this);
    paramVisitor.visitINVOKEINTERFACE(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\INVOKEINTERFACE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */