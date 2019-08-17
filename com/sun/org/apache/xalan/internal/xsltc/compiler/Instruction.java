package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

abstract class Instruction extends SyntaxTreeNode {
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError { return typeCheckContents(paramSymbolTable); }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ErrorMsg errorMsg = new ErrorMsg("NOT_IMPLEMENTED_ERR", getClass(), this);
    getParser().reportError(2, errorMsg);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Instruction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */