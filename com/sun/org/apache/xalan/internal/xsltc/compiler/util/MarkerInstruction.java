package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.Visitor;
import java.io.DataOutputStream;
import java.io.IOException;

abstract class MarkerInstruction extends Instruction {
  public MarkerInstruction() { super((short)-1, (short)0); }
  
  public void accept(Visitor paramVisitor) {}
  
  public final int consumeStack(ConstantPoolGen paramConstantPoolGen) { return 0; }
  
  public final int produceStack(ConstantPoolGen paramConstantPoolGen) { return 0; }
  
  public Instruction copy() { return this; }
  
  public final void dump(DataOutputStream paramDataOutputStream) throws IOException {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compile\\util\MarkerInstruction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */