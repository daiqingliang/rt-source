package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class KeyCall extends FunctionCall {
  private Expression _name;
  
  private Expression _value;
  
  private Type _valueType;
  
  private QName _resolvedQName = null;
  
  public KeyCall(QName paramQName, Vector paramVector) {
    super(paramQName, paramVector);
    switch (argumentCount()) {
      case 1:
        this._name = null;
        this._value = argument(0);
        return;
      case 2:
        this._name = argument(0);
        this._value = argument(1);
        return;
    } 
    this._name = this._value = null;
  }
  
  public void addParentDependency() {
    if (this._resolvedQName == null)
      return; 
    SyntaxTreeNode syntaxTreeNode = this;
    while (syntaxTreeNode != null && !(syntaxTreeNode instanceof TopLevelElement))
      syntaxTreeNode = syntaxTreeNode.getParent(); 
    TopLevelElement topLevelElement = (TopLevelElement)syntaxTreeNode;
    if (topLevelElement != null)
      topLevelElement.addDependency(getSymbolTable().getKey(this._resolvedQName)); 
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    Type type = super.typeCheck(paramSymbolTable);
    if (this._name != null) {
      Type type1 = this._name.typeCheck(paramSymbolTable);
      if (this._name instanceof LiteralExpr) {
        LiteralExpr literalExpr = (LiteralExpr)this._name;
        this._resolvedQName = getParser().getQNameIgnoreDefaultNs(literalExpr.getValue());
      } else if (!(type1 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType)) {
        this._name = new CastExpr(this._name, Type.String);
      } 
    } 
    this._valueType = this._value.typeCheck(paramSymbolTable);
    if (this._valueType != Type.NodeSet && this._valueType != Type.Reference && this._valueType != Type.String) {
      this._value = new CastExpr(this._value, Type.String);
      this._valueType = this._value.typeCheck(paramSymbolTable);
    } 
    addParentDependency();
    return type;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    int i = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "getKeyIndex", "(Ljava/lang/String;)Lcom/sun/org/apache/xalan/internal/xsltc/dom/KeyIndex;");
    int j = constantPoolGen.addMethodref("com/sun/org/apache/xalan/internal/xsltc/dom/KeyIndex", "setDom", "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;I)V");
    int k = constantPoolGen.addMethodref("com/sun/org/apache/xalan/internal/xsltc/dom/KeyIndex", "getKeyIndexIterator", "(" + this._valueType.toSignature() + "Z)" + "Lcom/sun/org/apache/xalan/internal/xsltc/dom/KeyIndex$KeyIndexIterator;");
    instructionList.append(paramClassGenerator.loadTranslet());
    if (this._name == null) {
      instructionList.append(new PUSH(constantPoolGen, "##id"));
    } else if (this._resolvedQName != null) {
      instructionList.append(new PUSH(constantPoolGen, this._resolvedQName.toString()));
    } else {
      this._name.translate(paramClassGenerator, paramMethodGenerator);
    } 
    instructionList.append(new INVOKEVIRTUAL(i));
    instructionList.append(DUP);
    instructionList.append(paramMethodGenerator.loadDOM());
    instructionList.append(paramMethodGenerator.loadCurrentNode());
    instructionList.append(new INVOKEVIRTUAL(j));
    this._value.translate(paramClassGenerator, paramMethodGenerator);
    instructionList.append((this._name != null) ? ICONST_1 : ICONST_0);
    instructionList.append(new INVOKEVIRTUAL(k));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\KeyCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */