package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.CompareGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;

final class LastCall extends FunctionCall {
  public LastCall(QName paramQName) { super(paramQName); }
  
  public boolean hasPositionCall() { return true; }
  
  public boolean hasLastCall() { return true; }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    if (paramMethodGenerator instanceof CompareGenerator) {
      instructionList.append(((CompareGenerator)paramMethodGenerator).loadLastNode());
    } else if (paramMethodGenerator instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.TestGenerator) {
      instructionList.append(new ILOAD(3));
    } else {
      ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
      int i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator", "getLast", "()I");
      instructionList.append(paramMethodGenerator.loadIterator());
      instructionList.append(new INVOKEINTERFACE(i, 1));
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\LastCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */