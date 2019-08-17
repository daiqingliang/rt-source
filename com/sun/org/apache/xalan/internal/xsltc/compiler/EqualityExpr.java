package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.IFNE;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPEQ;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPNE;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.runtime.Operators;

final class EqualityExpr extends Expression {
  private final int _op;
  
  private Expression _left;
  
  private Expression _right;
  
  public EqualityExpr(int paramInt, Expression paramExpression1, Expression paramExpression2) {
    this._op = paramInt;
    (this._left = paramExpression1).setParent(this);
    (this._right = paramExpression2).setParent(this);
  }
  
  public void setParser(Parser paramParser) {
    super.setParser(paramParser);
    this._left.setParser(paramParser);
    this._right.setParser(paramParser);
  }
  
  public String toString() { return Operators.getOpNames(this._op) + '(' + this._left + ", " + this._right + ')'; }
  
  public Expression getLeft() { return this._left; }
  
  public Expression getRight() { return this._right; }
  
  public boolean getOp() { return (this._op != 1); }
  
  public boolean hasPositionCall() { return this._left.hasPositionCall() ? true : (this._right.hasPositionCall()); }
  
  public boolean hasLastCall() { return this._left.hasLastCall() ? true : (this._right.hasLastCall()); }
  
  private void swapArguments() {
    Expression expression = this._left;
    this._left = this._right;
    this._right = expression;
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    Type type1 = this._left.typeCheck(paramSymbolTable);
    Type type2 = this._right.typeCheck(paramSymbolTable);
    if (type1.isSimple() && type2.isSimple()) {
      if (type1 != type2)
        if (type1 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType) {
          this._right = new CastExpr(this._right, Type.Boolean);
        } else if (type2 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType) {
          this._left = new CastExpr(this._left, Type.Boolean);
        } else if (type1 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NumberType || type2 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NumberType) {
          this._left = new CastExpr(this._left, Type.Real);
          this._right = new CastExpr(this._right, Type.Real);
        } else {
          this._left = new CastExpr(this._left, Type.String);
          this._right = new CastExpr(this._right, Type.String);
        }  
    } else if (type1 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType) {
      this._right = new CastExpr(this._right, Type.Reference);
    } else if (type2 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType) {
      this._left = new CastExpr(this._left, Type.Reference);
    } else if (type1 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType && type2 == Type.String) {
      this._left = new CastExpr(this._left, Type.String);
    } else if (type1 == Type.String && type2 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType) {
      this._right = new CastExpr(this._right, Type.String);
    } else if (type1 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType && type2 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType) {
      this._left = new CastExpr(this._left, Type.String);
      this._right = new CastExpr(this._right, Type.String);
    } else if (!(type1 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType) || !(type2 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType)) {
      if (type1 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType && type2 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType) {
        swapArguments();
      } else {
        if (type1 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType)
          this._left = new CastExpr(this._left, Type.NodeSet); 
        if (type2 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType)
          this._right = new CastExpr(this._right, Type.NodeSet); 
        if (type1.isSimple() || (type1 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType && type2 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType))
          swapArguments(); 
        if (this._right.getType() instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.IntType)
          this._right = new CastExpr(this._right, Type.Real); 
      } 
    } 
    return this._type = Type.Boolean;
  }
  
  public void translateDesynthesized(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    Type type = this._left.getType();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    if (type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType) {
      this._left.translate(paramClassGenerator, paramMethodGenerator);
      this._right.translate(paramClassGenerator, paramMethodGenerator);
      this._falseList.add(instructionList.append((this._op == 0) ? new IF_ICMPNE(null) : new IF_ICMPEQ(null)));
    } else if (type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NumberType) {
      this._left.translate(paramClassGenerator, paramMethodGenerator);
      this._right.translate(paramClassGenerator, paramMethodGenerator);
      if (type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.RealType) {
        instructionList.append(DCMPG);
        this._falseList.add(instructionList.append((this._op == 0) ? new IFNE(null) : new IFEQ(null)));
      } else {
        this._falseList.add(instructionList.append((this._op == 0) ? new IF_ICMPNE(null) : new IF_ICMPEQ(null)));
      } 
    } else {
      translate(paramClassGenerator, paramMethodGenerator);
      desynthesize(paramClassGenerator, paramMethodGenerator);
    } 
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    Type type1 = this._left.getType();
    Type type2 = this._right.getType();
    if (type1 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType || type1 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NumberType) {
      translateDesynthesized(paramClassGenerator, paramMethodGenerator);
      synthesize(paramClassGenerator, paramMethodGenerator);
      return;
    } 
    if (type1 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType) {
      int j = constantPoolGen.addMethodref("java.lang.String", "equals", "(Ljava/lang/Object;)Z");
      this._left.translate(paramClassGenerator, paramMethodGenerator);
      this._right.translate(paramClassGenerator, paramMethodGenerator);
      instructionList.append(new INVOKEVIRTUAL(j));
      if (this._op == 1) {
        instructionList.append(ICONST_1);
        instructionList.append(IXOR);
      } 
      return;
    } 
    if (type1 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType) {
      if (type2 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType) {
        this._right.translate(paramClassGenerator, paramMethodGenerator);
        if (this._op == 1) {
          instructionList.append(ICONST_1);
          instructionList.append(IXOR);
        } 
        return;
      } 
      if (type2 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.RealType) {
        this._left.translate(paramClassGenerator, paramMethodGenerator);
        type1.translateTo(paramClassGenerator, paramMethodGenerator, Type.Real);
        this._right.translate(paramClassGenerator, paramMethodGenerator);
        instructionList.append(DCMPG);
        BranchHandle branchHandle2 = instructionList.append((this._op == 0) ? new IFNE(null) : new IFEQ(null));
        instructionList.append(ICONST_1);
        BranchHandle branchHandle1 = instructionList.append(new GOTO(null));
        branchHandle2.setTarget(instructionList.append(ICONST_0));
        branchHandle1.setTarget(instructionList.append(NOP));
        return;
      } 
      this._left.translate(paramClassGenerator, paramMethodGenerator);
      type1.translateTo(paramClassGenerator, paramMethodGenerator, Type.String);
      this._right.translate(paramClassGenerator, paramMethodGenerator);
      if (type2 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType)
        type2.translateTo(paramClassGenerator, paramMethodGenerator, Type.String); 
      int j = constantPoolGen.addMethodref("java.lang.String", "equals", "(Ljava/lang/Object;)Z");
      instructionList.append(new INVOKEVIRTUAL(j));
      if (this._op == 1) {
        instructionList.append(ICONST_1);
        instructionList.append(IXOR);
      } 
      return;
    } 
    if (type1 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType && type2 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType) {
      this._left.translate(paramClassGenerator, paramMethodGenerator);
      this._left.startIterator(paramClassGenerator, paramMethodGenerator);
      Type.NodeSet.translateTo(paramClassGenerator, paramMethodGenerator, Type.Boolean);
      this._right.translate(paramClassGenerator, paramMethodGenerator);
      instructionList.append(IXOR);
      if (this._op == 0) {
        instructionList.append(ICONST_1);
        instructionList.append(IXOR);
      } 
      return;
    } 
    if (type1 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType && type2 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType) {
      this._left.translate(paramClassGenerator, paramMethodGenerator);
      this._left.startIterator(paramClassGenerator, paramMethodGenerator);
      this._right.translate(paramClassGenerator, paramMethodGenerator);
      instructionList.append(new PUSH(constantPoolGen, this._op));
      instructionList.append(paramMethodGenerator.loadDOM());
      int j = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "compare", "(" + type1.toSignature() + type2.toSignature() + "I" + "Lcom/sun/org/apache/xalan/internal/xsltc/DOM;" + ")Z");
      instructionList.append(new INVOKESTATIC(j));
      return;
    } 
    this._left.translate(paramClassGenerator, paramMethodGenerator);
    this._left.startIterator(paramClassGenerator, paramMethodGenerator);
    this._right.translate(paramClassGenerator, paramMethodGenerator);
    this._right.startIterator(paramClassGenerator, paramMethodGenerator);
    if (type2 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType) {
      type2.translateTo(paramClassGenerator, paramMethodGenerator, Type.String);
      type2 = Type.String;
    } 
    instructionList.append(new PUSH(constantPoolGen, this._op));
    instructionList.append(paramMethodGenerator.loadDOM());
    int i = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "compare", "(" + type1.toSignature() + type2.toSignature() + "I" + "Lcom/sun/org/apache/xalan/internal/xsltc/DOM;" + ")Z");
    instructionList.append(new INVOKESTATIC(i));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\EqualityExpr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */