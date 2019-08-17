package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.runtime.Operators;

final class RelationalExpr extends Expression {
  private int _op;
  
  private Expression _left;
  
  private Expression _right;
  
  public RelationalExpr(int paramInt, Expression paramExpression1, Expression paramExpression2) {
    this._op = paramInt;
    (this._left = paramExpression1).setParent(this);
    (this._right = paramExpression2).setParent(this);
  }
  
  public void setParser(Parser paramParser) {
    super.setParser(paramParser);
    this._left.setParser(paramParser);
    this._right.setParser(paramParser);
  }
  
  public boolean hasPositionCall() { return this._left.hasPositionCall() ? true : (this._right.hasPositionCall()); }
  
  public boolean hasLastCall() { return (this._left.hasLastCall() || this._right.hasLastCall()); }
  
  public boolean hasReferenceArgs() { return (this._left.getType() instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType || this._right.getType() instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType); }
  
  public boolean hasNodeArgs() { return (this._left.getType() instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType || this._right.getType() instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType); }
  
  public boolean hasNodeSetArgs() { return (this._left.getType() instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType || this._right.getType() instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType); }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    Type type1 = this._left.typeCheck(paramSymbolTable);
    Type type2 = this._right.typeCheck(paramSymbolTable);
    if (type1 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType && type2 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType) {
      this._right = new CastExpr(this._right, Type.Real);
      this._left = new CastExpr(this._left, Type.Real);
      return this._type = Type.Boolean;
    } 
    if (hasReferenceArgs()) {
      Type type3 = null;
      Type type4 = null;
      Type type5 = null;
      if (type1 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType && this._left instanceof VariableRefBase) {
        VariableRefBase variableRefBase = (VariableRefBase)this._left;
        VariableBase variableBase = variableRefBase.getVariable();
        type4 = variableBase.getType();
      } 
      if (type2 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType && this._right instanceof VariableRefBase) {
        VariableRefBase variableRefBase = (VariableRefBase)this._right;
        VariableBase variableBase = variableRefBase.getVariable();
        type5 = variableBase.getType();
      } 
      if (type4 == null) {
        type3 = type5;
      } else if (type5 == null) {
        type3 = type4;
      } else {
        type3 = Type.Real;
      } 
      if (type3 == null)
        type3 = Type.Real; 
      this._right = new CastExpr(this._right, type3);
      this._left = new CastExpr(this._left, type3);
      return this._type = Type.Boolean;
    } 
    if (hasNodeSetArgs()) {
      if (type2 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType) {
        Expression expression = this._right;
        this._right = this._left;
        this._left = expression;
        this._op = (this._op == 2) ? 3 : ((this._op == 3) ? 2 : ((this._op == 4) ? 5 : 4));
        type2 = this._right.getType();
      } 
      if (type2 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType)
        this._right = new CastExpr(this._right, Type.NodeSet); 
      if (type2 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.IntType)
        this._right = new CastExpr(this._right, Type.Real); 
      if (type2 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType)
        this._right = new CastExpr(this._right, Type.String); 
      return this._type = Type.Boolean;
    } 
    if (hasNodeArgs()) {
      if (type1 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType) {
        this._right = new CastExpr(this._right, Type.Boolean);
        type2 = Type.Boolean;
      } 
      if (type2 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType) {
        this._left = new CastExpr(this._left, Type.Boolean);
        type1 = Type.Boolean;
      } 
    } 
    MethodType methodType = lookupPrimop(paramSymbolTable, Operators.getOpNames(this._op), new MethodType(Type.Void, type1, type2));
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
    if (hasNodeSetArgs() || hasReferenceArgs()) {
      ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
      InstructionList instructionList = paramMethodGenerator.getInstructionList();
      this._left.translate(paramClassGenerator, paramMethodGenerator);
      this._left.startIterator(paramClassGenerator, paramMethodGenerator);
      this._right.translate(paramClassGenerator, paramMethodGenerator);
      this._right.startIterator(paramClassGenerator, paramMethodGenerator);
      instructionList.append(new PUSH(constantPoolGen, this._op));
      instructionList.append(paramMethodGenerator.loadDOM());
      int i = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "compare", "(" + this._left.getType().toSignature() + this._right.getType().toSignature() + "I" + "Lcom/sun/org/apache/xalan/internal/xsltc/DOM;" + ")Z");
      instructionList.append(new INVOKESTATIC(i));
    } else {
      translateDesynthesized(paramClassGenerator, paramMethodGenerator);
      synthesize(paramClassGenerator, paramMethodGenerator);
    } 
  }
  
  public void translateDesynthesized(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    if (hasNodeSetArgs() || hasReferenceArgs()) {
      translate(paramClassGenerator, paramMethodGenerator);
      desynthesize(paramClassGenerator, paramMethodGenerator);
    } else {
      ErrorMsg errorMsg;
      BranchInstruction branchInstruction = null;
      InstructionList instructionList = paramMethodGenerator.getInstructionList();
      this._left.translate(paramClassGenerator, paramMethodGenerator);
      this._right.translate(paramClassGenerator, paramMethodGenerator);
      boolean bool = false;
      Type type = this._left.getType();
      if (type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.RealType) {
        instructionList.append(type.CMP((this._op == 3 || this._op == 5)));
        type = Type.Int;
        bool = true;
      } 
      switch (this._op) {
        case 3:
          branchInstruction = type.GE(bool);
          break;
        case 2:
          branchInstruction = type.LE(bool);
          break;
        case 5:
          branchInstruction = type.GT(bool);
          break;
        case 4:
          branchInstruction = type.LT(bool);
          break;
        default:
          errorMsg = new ErrorMsg("ILLEGAL_RELAT_OP_ERR", this);
          getParser().reportError(2, errorMsg);
          break;
      } 
      this._falseList.add(instructionList.append(branchInstruction));
    } 
  }
  
  public String toString() { return Operators.getOpNames(this._op) + '(' + this._left + ", " + this._right + ')'; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\RelationalExpr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */