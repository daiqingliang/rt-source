package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class NumberCall extends FunctionCall {
  public NumberCall(QName paramQName, Vector paramVector) { super(paramQName, paramVector); }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    if (argumentCount() > 0)
      argument().typeCheck(paramSymbolTable); 
    return this._type = Type.Real;
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
    if (!type.identicalTo(Type.Real))
      type.translateTo(paramClassGenerator, paramMethodGenerator, Type.Real); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\NumberCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */