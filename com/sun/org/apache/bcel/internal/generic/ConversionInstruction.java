package com.sun.org.apache.bcel.internal.generic;

public abstract class ConversionInstruction extends Instruction implements TypedInstruction, StackProducer, StackConsumer {
  ConversionInstruction() {}
  
  protected ConversionInstruction(short paramShort) { super(paramShort, (short)1); }
  
  public Type getType(ConstantPoolGen paramConstantPoolGen) {
    switch (this.opcode) {
      case 136:
      case 139:
      case 142:
        return Type.INT;
      case 134:
      case 137:
      case 144:
        return Type.FLOAT;
      case 133:
      case 140:
      case 143:
        return Type.LONG;
      case 135:
      case 138:
      case 141:
        return Type.DOUBLE;
      case 145:
        return Type.BYTE;
      case 146:
        return Type.CHAR;
      case 147:
        return Type.SHORT;
    } 
    throw new ClassGenException("Unknown type " + this.opcode);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\ConversionInstruction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */