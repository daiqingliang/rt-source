package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.classfile.Constant;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import java.util.StringTokenizer;

public abstract class InvokeInstruction extends FieldOrMethod implements ExceptionThrower, TypedInstruction, StackConsumer, StackProducer {
  InvokeInstruction() {}
  
  protected InvokeInstruction(short paramShort, int paramInt) { super(paramShort, paramInt); }
  
  public String toString(ConstantPool paramConstantPool) {
    Constant constant = paramConstantPool.getConstant(this.index);
    StringTokenizer stringTokenizer = new StringTokenizer(paramConstantPool.constantToString(constant));
    return Constants.OPCODE_NAMES[this.opcode] + " " + stringTokenizer.nextToken().replace('.', '/') + stringTokenizer.nextToken();
  }
  
  public int consumeStack(ConstantPoolGen paramConstantPoolGen) {
    int i;
    String str = getSignature(paramConstantPoolGen);
    Type[] arrayOfType = Type.getArgumentTypes(str);
    if (this.opcode == 184) {
      i = 0;
    } else {
      i = 1;
    } 
    int j = arrayOfType.length;
    for (byte b = 0; b < j; b++)
      i += arrayOfType[b].getSize(); 
    return i;
  }
  
  public int produceStack(ConstantPoolGen paramConstantPoolGen) { return getReturnType(paramConstantPoolGen).getSize(); }
  
  public Type getType(ConstantPoolGen paramConstantPoolGen) { return getReturnType(paramConstantPoolGen); }
  
  public String getMethodName(ConstantPoolGen paramConstantPoolGen) { return getName(paramConstantPoolGen); }
  
  public Type getReturnType(ConstantPoolGen paramConstantPoolGen) { return Type.getReturnType(getSignature(paramConstantPoolGen)); }
  
  public Type[] getArgumentTypes(ConstantPoolGen paramConstantPoolGen) { return Type.getArgumentTypes(getSignature(paramConstantPoolGen)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\InvokeInstruction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */