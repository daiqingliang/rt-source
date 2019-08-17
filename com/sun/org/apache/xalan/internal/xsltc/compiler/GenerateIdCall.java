package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import java.util.Vector;

final class GenerateIdCall extends FunctionCall {
  public GenerateIdCall(QName paramQName, Vector paramVector) { super(paramQName, paramVector); }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    if (argumentCount() == 0) {
      instructionList.append(paramMethodGenerator.loadContextNode());
    } else {
      argument().translate(paramClassGenerator, paramMethodGenerator);
    } 
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    instructionList.append(new INVOKESTATIC(constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "generate_idF", "(I)Ljava/lang/String;")));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\GenerateIdCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */