package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;

final class CurrentCall extends FunctionCall {
  public CurrentCall(QName paramQName) { super(paramQName); }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) { paramMethodGenerator.getInstructionList().append(paramMethodGenerator.loadCurrentNode()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\CurrentCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */