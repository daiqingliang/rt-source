package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class StringCall extends FunctionCall {
  public StringCall(QName paramQName, Vector paramVector) { super(paramQName, paramVector); }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    int i = argumentCount();
    if (i > 1) {
      ErrorMsg errorMsg = new ErrorMsg("ILLEGAL_ARG_ERR", this);
      throw new TypeCheckError(errorMsg);
    } 
    if (i > 0)
      argument().typeCheck(paramSymbolTable); 
    return this._type = Type.String;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    Type type;
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    if (argumentCount() == 0) {
      instructionList.append(paramMethodGenerator.loadContextNode());
      type = Type.Node;
    } else {
      Expression expression = argument();
      expression.translate(paramClassGenerator, paramMethodGenerator);
      expression.startIterator(paramClassGenerator, paramMethodGenerator);
      type = expression.getType();
    } 
    if (!type.identicalTo(Type.String))
      type.translateTo(paramClassGenerator, paramMethodGenerator, Type.String); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\StringCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */