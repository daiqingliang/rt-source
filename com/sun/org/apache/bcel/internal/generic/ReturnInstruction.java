package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.ExceptionConstants;

public abstract class ReturnInstruction extends Instruction implements ExceptionThrower, TypedInstruction, StackConsumer {
  ReturnInstruction() {}
  
  protected ReturnInstruction(short paramShort) { super(paramShort, (short)1); }
  
  public Type getType() {
    switch (this.opcode) {
      case 172:
        return Type.INT;
      case 173:
        return Type.LONG;
      case 174:
        return Type.FLOAT;
      case 175:
        return Type.DOUBLE;
      case 176:
        return Type.OBJECT;
      case 177:
        return Type.VOID;
    } 
    throw new ClassGenException("Unknown type " + this.opcode);
  }
  
  public Class[] getExceptions() { return new Class[] { ExceptionConstants.ILLEGAL_MONITOR_STATE }; }
  
  public Type getType(ConstantPoolGen paramConstantPoolGen) { return getType(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\ReturnInstruction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */