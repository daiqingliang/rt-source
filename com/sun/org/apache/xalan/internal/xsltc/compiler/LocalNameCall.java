package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import java.util.Vector;

final class LocalNameCall extends NameBase {
  public LocalNameCall(QName paramQName) { super(paramQName); }
  
  public LocalNameCall(QName paramQName, Vector paramVector) { super(paramQName, paramVector); }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    int i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getNodeName", "(I)Ljava/lang/String;");
    int j = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "getLocalName", "(Ljava/lang/String;)Ljava/lang/String;");
    super.translate(paramClassGenerator, paramMethodGenerator);
    instructionList.append(new INVOKEINTERFACE(i, 2));
    instructionList.append(new INVOKESTATIC(j));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\LocalNameCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */