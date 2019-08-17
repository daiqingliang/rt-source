package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

final class BinOpExpr extends Expression {
  public static final int PLUS = 0;
  
  public static final int MINUS = 1;
  
  public static final int TIMES = 2;
  
  public static final int DIV = 3;
  
  public static final int MOD = 4;
  
  private static final String[] Ops = { "+", "-", "*", "/", "%" };
  
  private int _op;
  
  private Expression _left;
  
  private Expression _right;
  
  public BinOpExpr(int paramInt, Expression paramExpression1, Expression paramExpression2) {
    this._op = paramInt;
    (this._left = paramExpression1).setParent(this);
    (this._right = paramExpression2).setParent(this);
  }
  
  public boolean hasPositionCall() { return this._left.hasPositionCall() ? true : (this._right.hasPositionCall()); }
  
  public boolean hasLastCall() { return (this._left.hasLastCall() || this._right.hasLastCall()); }
  
  public void setParser(Parser paramParser) {
    super.setParser(paramParser);
    this._left.setParser(paramParser);
    this._right.setParser(paramParser);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    Type type1 = this._left.typeCheck(paramSymbolTable);
    Type type2 = this._right.typeCheck(paramSymbolTable);
    MethodType methodType = lookupPrimop(paramSymbolTable, Ops[this._op], new MethodType(Type.Void, type1, type2));
    if (methodType != null) {
      Type type3 = (Type)methodType.argsType().elementAt(0);
      if (!type3.identicalTo(type1))
        this._left = new CastExpr(this._left, type3); 
      Type type4 = (Type)methodType.argsType().elementAt(1);
      if (!type4.identicalTo(type2))
        this._right = new CastExpr(this._right, type3); 
      return this._type = methodType.resultType();
    } 
    throw new TypeCheckError(this);
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    this._left.translate(paramClassGenerator, paramMethodGenerator);
    this._right.translate(paramClassGenerator, paramMethodGenerator);
    switch (this._op) {
      case 0:
        instructionList.append(this._type.ADD());
        return;
      case 1:
        instructionList.append(this._type.SUB());
        return;
      case 2:
        instructionList.append(this._type.MUL());
        return;
      case 3:
        instructionList.append(this._type.DIV());
        return;
      case 4:
        instructionList.append(this._type.REM());
        return;
    } 
    ErrorMsg errorMsg = new ErrorMsg("ILLEGAL_BINARY_OP_ERR", this);
    getParser().reportError(3, errorMsg);
  }
  
  public String toString() { return Ops[this._op] + '(' + this._left + ", " + this._right + ')'; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\BinOpExpr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */