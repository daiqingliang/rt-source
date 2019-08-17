package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xml.internal.dtm.Axis;
import java.util.Vector;

final class UnionPathExpr extends Expression {
  private final Expression _pathExpr;
  
  private final Expression _rest;
  
  private boolean _reverse = false;
  
  private Expression[] _components;
  
  public UnionPathExpr(Expression paramExpression1, Expression paramExpression2) {
    this._pathExpr = paramExpression1;
    this._rest = paramExpression2;
  }
  
  public void setParser(Parser paramParser) {
    super.setParser(paramParser);
    Vector vector = new Vector();
    flatten(vector);
    int i = vector.size();
    this._components = (Expression[])vector.toArray(new Expression[i]);
    for (byte b = 0; b < i; b++) {
      this._components[b].setParser(paramParser);
      this._components[b].setParent(this);
      if (this._components[b] instanceof Step) {
        Step step = (Step)this._components[b];
        int j = step.getAxis();
        int k = step.getNodeType();
        if (j == 2 || k == 2) {
          this._components[b] = this._components[0];
          this._components[0] = step;
        } 
        if (Axis.isReverse(j))
          this._reverse = true; 
      } 
    } 
    if (getParent() instanceof Expression)
      this._reverse = false; 
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    int i = this._components.length;
    for (byte b = 0; b < i; b++) {
      if (this._components[b].typeCheck(paramSymbolTable) != Type.NodeSet)
        this._components[b] = new CastExpr(this._components[b], Type.NodeSet); 
    } 
    return this._type = Type.NodeSet;
  }
  
  public String toString() { return "union(" + this._pathExpr + ", " + this._rest + ')'; }
  
  private void flatten(Vector paramVector) {
    paramVector.addElement(this._pathExpr);
    if (this._rest != null)
      if (this._rest instanceof UnionPathExpr) {
        ((UnionPathExpr)this._rest).flatten(paramVector);
      } else {
        paramVector.addElement(this._rest);
      }  
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    int i = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.UnionIterator", "<init>", "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)V");
    int j = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.UnionIterator", "addIterator", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)Lcom/sun/org/apache/xalan/internal/xsltc/dom/UnionIterator;");
    instructionList.append(new NEW(constantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.UnionIterator")));
    instructionList.append(DUP);
    instructionList.append(paramMethodGenerator.loadDOM());
    instructionList.append(new INVOKESPECIAL(i));
    int k = this._components.length;
    int m;
    for (m = 0; m < k; m++) {
      this._components[m].translate(paramClassGenerator, paramMethodGenerator);
      instructionList.append(new INVOKEVIRTUAL(j));
    } 
    if (this._reverse) {
      m = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "orderNodes", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
      instructionList.append(paramMethodGenerator.loadDOM());
      instructionList.append(SWAP);
      instructionList.append(paramMethodGenerator.loadContextNode());
      instructionList.append(new INVOKEINTERFACE(m, 3));
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\UnionPathExpr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */