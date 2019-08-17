package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import java.util.Vector;

final class StringLengthCall extends FunctionCall {
  public StringLengthCall(QName paramQName, Vector paramVector) { super(paramQName, paramVector); }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    if (argumentCount() > 0) {
      argument().translate(paramClassGenerator, paramMethodGenerator);
    } else {
      instructionList.append(paramMethodGenerator.loadContextNode());
      Type.Node.translateTo(paramClassGenerator, paramMethodGenerator, Type.String);
    } 
    instructionList.append(new INVOKESTATIC(constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "getStringLength", "(Ljava/lang/String;)I")));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\StringLengthCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */