package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

final class LogicalExpr extends Expression {
  public static final int OR = 0;
  
  public static final int AND = 1;
  
  private final int _op;
  
  private Expression _left;
  
  private Expression _right;
  
  private static final String[] Ops = { "or", "and" };
  
  public LogicalExpr(int paramInt, Expression paramExpression1, Expression paramExpression2) {
    this._op = paramInt;
    (this._left = paramExpression1).setParent(this);
    (this._right = paramExpression2).setParent(this);
  }
  
  public boolean hasPositionCall() { return (this._left.hasPositionCall() || this._right.hasPositionCall()); }
  
  public boolean hasLastCall() { return (this._left.hasLastCall() || this._right.hasLastCall()); }
  
  public Object evaluateAtCompileTime() {
    Object object1 = this._left.evaluateAtCompileTime();
    Object object2 = this._right.evaluateAtCompileTime();
    return (object1 == null || object2 == null) ? null : ((this._op == 1) ? ((object1 == Boolean.TRUE && object2 == Boolean.TRUE) ? Boolean.TRUE : Boolean.FALSE) : ((object1 == Boolean.TRUE || object2 == Boolean.TRUE) ? Boolean.TRUE : Boolean.FALSE));
  }
  
  public int getOp() { return this._op; }
  
  public void setParser(Parser paramParser) {
    super.setParser(paramParser);
    this._left.setParser(paramParser);
    this._right.setParser(paramParser);
  }
  
  public String toString() { return Ops[this._op] + '(' + this._left + ", " + this._right + ')'; }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    Type type1 = this._left.typeCheck(paramSymbolTable);
    Type type2 = this._right.typeCheck(paramSymbolTable);
    MethodType methodType1 = new MethodType(Type.Void, type1, type2);
    MethodType methodType2 = lookupPrimop(paramSymbolTable, Ops[this._op], methodType1);
    if (methodType2 != null) {
      Type type3 = (Type)methodType2.argsType().elementAt(0);
      if (!type3.identicalTo(type1))
        this._left = new CastExpr(this._left, type3); 
      Type type4 = (Type)methodType2.argsType().elementAt(1);
      if (!type4.identicalTo(type2))
        this._right = new CastExpr(this._right, type3); 
      return this._type = methodType2.resultType();
    } 
    throw new TypeCheckError(this);
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    translateDesynthesized(paramClassGenerator, paramMethodGenerator);
    synthesize(paramClassGenerator, paramMethodGenerator);
  }
  
  public void translateDesynthesized(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    SyntaxTreeNode syntaxTreeNode = getParent();
    if (this._op == 1) {
      this._left.translateDesynthesized(paramClassGenerator, paramMethodGenerator);
      InstructionHandle instructionHandle1 = instructionList.append(NOP);
      this._right.translateDesynthesized(paramClassGenerator, paramMethodGenerator);
      InstructionHandle instructionHandle2 = instructionList.append(NOP);
      this._falseList.append(this._right._falseList.append(this._left._falseList));
      if (this._left instanceof LogicalExpr && ((LogicalExpr)this._left).getOp() == 0) {
        this._left.backPatchTrueList(instructionHandle1);
      } else if (this._left instanceof NotCall) {
        this._left.backPatchTrueList(instructionHandle1);
      } else {
        this._trueList.append(this._left._trueList);
      } 
      if (this._right instanceof LogicalExpr && ((LogicalExpr)this._right).getOp() == 0) {
        this._right.backPatchTrueList(instructionHandle2);
      } else if (this._right instanceof NotCall) {
        this._right.backPatchTrueList(instructionHandle2);
      } else {
        this._trueList.append(this._right._trueList);
      } 
    } else {
      this._left.translateDesynthesized(paramClassGenerator, paramMethodGenerator);
      BranchHandle branchHandle = instructionList.append(new GOTO(null));
      this._right.translateDesynthesized(paramClassGenerator, paramMethodGenerator);
      this._left._trueList.backPatch(branchHandle);
      this._left._falseList.backPatch(branchHandle.getNext());
      this._falseList.append(this._right._falseList);
      this._trueList.add(branchHandle).append(this._right._trueList);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\LogicalExpr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */