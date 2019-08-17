package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

final class UnaryOpExpr extends Expression {
  private Expression _left;
  
  public UnaryOpExpr(Expression paramExpression) { (this._left = paramExpression).setParent(this); }
  
  public boolean hasPositionCall() { return this._left.hasPositionCall(); }
  
  public boolean hasLastCall() { return this._left.hasLastCall(); }
  
  public void setParser(Parser paramParser) {
    super.setParser(paramParser);
    this._left.setParser(paramParser);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    Type type = this._left.typeCheck(paramSymbolTable);
    MethodType methodType = lookupPrimop(paramSymbolTable, "u-", new MethodType(Type.Void, type));
    if (methodType != null) {
      Type type1 = (Type)methodType.argsType().elementAt(0);
      if (!type1.identicalTo(type))
        this._left = new CastExpr(this._left, type1); 
      return this._type = methodType.resultType();
    } 
    throw new TypeCheckError(this);
  }
  
  public String toString() { return "u-(" + this._left + ')'; }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    this._left.translate(paramClassGenerator, paramMethodGenerator);
    instructionList.append(this._type.NEG());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\UnaryOpExpr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */