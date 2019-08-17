package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class ElementAvailableCall extends FunctionCall {
  public ElementAvailableCall(QName paramQName, Vector paramVector) { super(paramQName, paramVector); }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    if (argument() instanceof LiteralExpr)
      return this._type = Type.Boolean; 
    ErrorMsg errorMsg = new ErrorMsg("NEED_LITERAL_ERR", "element-available", this);
    throw new TypeCheckError(errorMsg);
  }
  
  public Object evaluateAtCompileTime() { return getResult() ? Boolean.TRUE : Boolean.FALSE; }
  
  public boolean getResult() {
    try {
      LiteralExpr literalExpr = (LiteralExpr)argument();
      String str1 = literalExpr.getValue();
      int i = str1.indexOf(':');
      String str2 = (i > 0) ? str1.substring(i + 1) : str1;
      return getParser().elementSupported(literalExpr.getNamespace(), str2);
    } catch (ClassCastException classCastException) {
      return false;
    } 
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    boolean bool = getResult();
    paramMethodGenerator.getInstructionList().append(new PUSH(constantPoolGen, bool));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\ElementAvailableCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */