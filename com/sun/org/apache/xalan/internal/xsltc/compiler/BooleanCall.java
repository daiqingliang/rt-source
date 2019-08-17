package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class BooleanCall extends FunctionCall {
  private Expression _arg = null;
  
  public BooleanCall(QName paramQName, Vector paramVector) {
    super(paramQName, paramVector);
    this._arg = argument(0);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    this._arg.typeCheck(paramSymbolTable);
    return this._type = Type.Boolean;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    this._arg.translate(paramClassGenerator, paramMethodGenerator);
    Type type = this._arg.getType();
    if (!type.identicalTo(Type.Boolean)) {
      this._arg.startIterator(paramClassGenerator, paramMethodGenerator);
      type.translateTo(paramClassGenerator, paramMethodGenerator, Type.Boolean);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\BooleanCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */