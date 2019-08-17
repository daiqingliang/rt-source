package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class Otherwise extends Instruction {
  public void display(int paramInt) {
    indent(paramInt);
    Util.println("Otherwise");
    indent(paramInt + 4);
    displayContents(paramInt + 4);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    typeCheckContents(paramSymbolTable);
    return Type.Void;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    Parser parser = getParser();
    ErrorMsg errorMsg = new ErrorMsg("STRAY_OTHERWISE_ERR", this);
    parser.reportError(3, errorMsg);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Otherwise.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */