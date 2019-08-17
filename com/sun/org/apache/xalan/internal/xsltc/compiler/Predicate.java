package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.PUTFIELD;
import com.sun.org.apache.bcel.internal.generic.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.FilterGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TestGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.util.ArrayList;

final class Predicate extends Expression implements Closure {
  private Expression _exp = null;
  
  private boolean _canOptimize = true;
  
  private boolean _nthPositionFilter = false;
  
  private boolean _nthDescendant = false;
  
  int _ptype = -1;
  
  private String _className = null;
  
  private ArrayList _closureVars = null;
  
  private Closure _parentClosure = null;
  
  private Expression _value = null;
  
  private Step _step = null;
  
  public Predicate(Expression paramExpression) {
    this._exp = paramExpression;
    this._exp.setParent(this);
  }
  
  public void setParser(Parser paramParser) {
    super.setParser(paramParser);
    this._exp.setParser(paramParser);
  }
  
  public boolean isNthPositionFilter() { return this._nthPositionFilter; }
  
  public boolean isNthDescendant() { return this._nthDescendant; }
  
  public void dontOptimize() { this._canOptimize = false; }
  
  public boolean hasPositionCall() { return this._exp.hasPositionCall(); }
  
  public boolean hasLastCall() { return this._exp.hasLastCall(); }
  
  public boolean inInnerClass() { return (this._className != null); }
  
  public Closure getParentClosure() {
    if (this._parentClosure == null) {
      SyntaxTreeNode syntaxTreeNode = getParent();
      do {
        if (syntaxTreeNode instanceof Closure) {
          this._parentClosure = (Closure)syntaxTreeNode;
          break;
        } 
        if (syntaxTreeNode instanceof TopLevelElement)
          break; 
        syntaxTreeNode = syntaxTreeNode.getParent();
      } while (syntaxTreeNode != null);
    } 
    return this._parentClosure;
  }
  
  public String getInnerClassName() { return this._className; }
  
  public void addVariable(VariableRefBase paramVariableRefBase) {
    if (this._closureVars == null)
      this._closureVars = new ArrayList(); 
    if (!this._closureVars.contains(paramVariableRefBase)) {
      this._closureVars.add(paramVariableRefBase);
      Closure closure = getParentClosure();
      if (closure != null)
        closure.addVariable(paramVariableRefBase); 
    } 
  }
  
  public int getPosType() {
    if (this._ptype == -1) {
      SyntaxTreeNode syntaxTreeNode = getParent();
      if (syntaxTreeNode instanceof StepPattern) {
        this._ptype = ((StepPattern)syntaxTreeNode).getNodeType();
      } else if (syntaxTreeNode instanceof AbsoluteLocationPath) {
        AbsoluteLocationPath absoluteLocationPath = (AbsoluteLocationPath)syntaxTreeNode;
        Expression expression = absoluteLocationPath.getPath();
        if (expression instanceof Step)
          this._ptype = ((Step)expression).getNodeType(); 
      } else if (syntaxTreeNode instanceof VariableRefBase) {
        VariableRefBase variableRefBase = (VariableRefBase)syntaxTreeNode;
        VariableBase variableBase = variableRefBase.getVariable();
        Expression expression = variableBase.getExpression();
        if (expression instanceof Step)
          this._ptype = ((Step)expression).getNodeType(); 
      } else if (syntaxTreeNode instanceof Step) {
        this._ptype = ((Step)syntaxTreeNode).getNodeType();
      } 
    } 
    return this._ptype;
  }
  
  public boolean parentIsPattern() { return getParent() instanceof Pattern; }
  
  public Expression getExpr() { return this._exp; }
  
  public String toString() { return "pred(" + this._exp + ')'; }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    Type type = this._exp.typeCheck(paramSymbolTable);
    if (type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType)
      this._exp = new CastExpr(this._exp, type = Type.Real); 
    if (type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType) {
      this._exp = new CastExpr(this._exp, Type.Boolean);
      this._exp = new CastExpr(this._exp, Type.Real);
      type = this._exp.typeCheck(paramSymbolTable);
    } 
    if (type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NumberType) {
      if (!(type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.IntType))
        this._exp = new CastExpr(this._exp, Type.Int); 
      if (this._canOptimize) {
        this._nthPositionFilter = (!this._exp.hasLastCall() && !this._exp.hasPositionCall());
        if (this._nthPositionFilter) {
          SyntaxTreeNode syntaxTreeNode = getParent();
          this._nthDescendant = (syntaxTreeNode instanceof Step && syntaxTreeNode.getParent() instanceof AbsoluteLocationPath);
          return this._type = Type.NodeSet;
        } 
      } 
      this._nthPositionFilter = this._nthDescendant = false;
      QName qName = getParser().getQNameIgnoreDefaultNs("position");
      PositionCall positionCall = new PositionCall(qName);
      positionCall.setParser(getParser());
      positionCall.setParent(this);
      this._exp = new EqualityExpr(0, positionCall, this._exp);
      if (this._exp.typeCheck(paramSymbolTable) != Type.Boolean)
        this._exp = new CastExpr(this._exp, Type.Boolean); 
      return this._type = Type.Boolean;
    } 
    if (!(type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType))
      this._exp = new CastExpr(this._exp, Type.Boolean); 
    return this._type = Type.Boolean;
  }
  
  private void compileFilter(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    this._className = getXSLTC().getHelperClassName();
    FilterGenerator filterGenerator = new FilterGenerator(this._className, "java.lang.Object", toString(), 33, new String[] { "com.sun.org.apache.xalan.internal.xsltc.dom.CurrentNodeListFilter" }, paramClassGenerator.getStylesheet());
    ConstantPoolGen constantPoolGen = filterGenerator.getConstantPool();
    boolean bool = (this._closureVars == null) ? 0 : this._closureVars.size();
    for (byte b = 0; b < bool; b++) {
      VariableBase variableBase = ((VariableRefBase)this._closureVars.get(b)).getVariable();
      filterGenerator.addField(new Field(1, constantPoolGen.addUtf8(variableBase.getEscapedName()), constantPoolGen.addUtf8(variableBase.getType().toSignature()), null, constantPoolGen.getConstantPool()));
    } 
    InstructionList instructionList = new InstructionList();
    TestGenerator testGenerator = new TestGenerator(17, Type.BOOLEAN, new Type[] { Type.INT, Type.INT, Type.INT, Type.INT, Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/runtime/AbstractTranslet;"), Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;") }, new String[] { "node", "position", "last", "current", "translet", "iterator" }, "test", this._className, instructionList, constantPoolGen);
    LocalVariableGen localVariableGen = testGenerator.addLocalVariable("document", Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;"), null, null);
    String str = paramClassGenerator.getClassName();
    instructionList.append(filterGenerator.loadTranslet());
    instructionList.append(new CHECKCAST(constantPoolGen.addClass(str)));
    instructionList.append(new GETFIELD(constantPoolGen.addFieldref(str, "_dom", "Lcom/sun/org/apache/xalan/internal/xsltc/DOM;")));
    localVariableGen.setStart(instructionList.append(new ASTORE(localVariableGen.getIndex())));
    testGenerator.setDomIndex(localVariableGen.getIndex());
    this._exp.translate(filterGenerator, testGenerator);
    instructionList.append(IRETURN);
    filterGenerator.addEmptyConstructor(1);
    filterGenerator.addMethod(testGenerator);
    getXSLTC().dumpClass(filterGenerator.getJavaClass());
  }
  
  public boolean isBooleanTest() { return this._exp instanceof BooleanExpr; }
  
  public boolean isNodeValueTest() { return !this._canOptimize ? false : ((getStep() != null && getCompareValue() != null)); }
  
  public Step getStep() {
    if (this._step != null)
      return this._step; 
    if (this._exp == null)
      return null; 
    if (this._exp instanceof EqualityExpr) {
      EqualityExpr equalityExpr = (EqualityExpr)this._exp;
      Expression expression1 = equalityExpr.getLeft();
      Expression expression2 = equalityExpr.getRight();
      if (expression1 instanceof CastExpr)
        expression1 = ((CastExpr)expression1).getExpr(); 
      if (expression1 instanceof Step)
        this._step = (Step)expression1; 
      if (expression2 instanceof CastExpr)
        expression2 = ((CastExpr)expression2).getExpr(); 
      if (expression2 instanceof Step)
        this._step = (Step)expression2; 
    } 
    return this._step;
  }
  
  public Expression getCompareValue() {
    if (this._value != null)
      return this._value; 
    if (this._exp == null)
      return null; 
    if (this._exp instanceof EqualityExpr) {
      EqualityExpr equalityExpr = (EqualityExpr)this._exp;
      Expression expression1 = equalityExpr.getLeft();
      Expression expression2 = equalityExpr.getRight();
      if (expression1 instanceof LiteralExpr) {
        this._value = expression1;
        return this._value;
      } 
      if (expression1 instanceof VariableRefBase && expression1.getType() == Type.String) {
        this._value = expression1;
        return this._value;
      } 
      if (expression2 instanceof LiteralExpr) {
        this._value = expression2;
        return this._value;
      } 
      if (expression2 instanceof VariableRefBase && expression2.getType() == Type.String) {
        this._value = expression2;
        return this._value;
      } 
    } 
    return null;
  }
  
  public void translateFilter(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    compileFilter(paramClassGenerator, paramMethodGenerator);
    instructionList.append(new NEW(constantPoolGen.addClass(this._className)));
    instructionList.append(DUP);
    instructionList.append(new INVOKESPECIAL(constantPoolGen.addMethodref(this._className, "<init>", "()V")));
    boolean bool = (this._closureVars == null) ? 0 : this._closureVars.size();
    for (byte b = 0; b < bool; b++) {
      VariableRefBase variableRefBase = (VariableRefBase)this._closureVars.get(b);
      VariableBase variableBase = variableRefBase.getVariable();
      Type type = variableBase.getType();
      instructionList.append(DUP);
      Closure closure;
      for (closure = this._parentClosure; closure != null && !closure.inInnerClass(); closure = closure.getParentClosure());
      if (closure != null) {
        instructionList.append(ALOAD_0);
        instructionList.append(new GETFIELD(constantPoolGen.addFieldref(closure.getInnerClassName(), variableBase.getEscapedName(), type.toSignature())));
      } else {
        instructionList.append(variableBase.loadInstruction());
      } 
      instructionList.append(new PUTFIELD(constantPoolGen.addFieldref(this._className, variableBase.getEscapedName(), type.toSignature())));
    } 
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    if (this._nthPositionFilter || this._nthDescendant) {
      this._exp.translate(paramClassGenerator, paramMethodGenerator);
    } else if (isNodeValueTest() && getParent() instanceof Step) {
      this._value.translate(paramClassGenerator, paramMethodGenerator);
      instructionList.append(new CHECKCAST(constantPoolGen.addClass("java.lang.String")));
      instructionList.append(new PUSH(constantPoolGen, ((EqualityExpr)this._exp).getOp()));
    } else {
      translateFilter(paramClassGenerator, paramMethodGenerator);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Predicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */