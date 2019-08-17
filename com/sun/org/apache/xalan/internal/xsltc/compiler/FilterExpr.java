package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.util.Vector;

class FilterExpr extends Expression {
  private Expression _primary;
  
  private final Vector _predicates;
  
  public FilterExpr(Expression paramExpression, Vector paramVector) {
    this._primary = paramExpression;
    this._predicates = paramVector;
    paramExpression.setParent(this);
  }
  
  protected Expression getExpr() { return (this._primary instanceof CastExpr) ? ((CastExpr)this._primary).getExpr() : this._primary; }
  
  public void setParser(Parser paramParser) {
    super.setParser(paramParser);
    this._primary.setParser(paramParser);
    if (this._predicates != null) {
      int i = this._predicates.size();
      for (byte b = 0; b < i; b++) {
        Expression expression = (Expression)this._predicates.elementAt(b);
        expression.setParser(paramParser);
        expression.setParent(this);
      } 
    } 
  }
  
  public String toString() { return "filter-expr(" + this._primary + ", " + this._predicates + ")"; }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    Type type = this._primary.typeCheck(paramSymbolTable);
    boolean bool = this._primary instanceof KeyCall;
    if (!(type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType))
      if (type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType) {
        this._primary = new CastExpr(this._primary, Type.NodeSet);
      } else {
        throw new TypeCheckError(this);
      }  
    int i = this._predicates.size();
    for (byte b = 0; b < i; b++) {
      Predicate predicate = (Predicate)this._predicates.elementAt(b);
      if (!bool)
        predicate.dontOptimize(); 
      predicate.typeCheck(paramSymbolTable);
    } 
    return this._type = Type.NodeSet;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) { translateFilterExpr(paramClassGenerator, paramMethodGenerator, (this._predicates == null) ? -1 : (this._predicates.size() - 1)); }
  
  private void translateFilterExpr(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, int paramInt) {
    if (paramInt >= 0) {
      translatePredicates(paramClassGenerator, paramMethodGenerator, paramInt);
    } else {
      this._primary.translate(paramClassGenerator, paramMethodGenerator);
    } 
  }
  
  public void translatePredicates(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, int paramInt) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    if (paramInt < 0) {
      translateFilterExpr(paramClassGenerator, paramMethodGenerator, paramInt);
    } else {
      Predicate predicate = (Predicate)this._predicates.get(paramInt--);
      translatePredicates(paramClassGenerator, paramMethodGenerator, paramInt);
      if (predicate.isNthPositionFilter()) {
        int i = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.NthIterator", "<init>", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;I)V");
        LocalVariableGen localVariableGen1 = paramMethodGenerator.addLocalVariable("filter_expr_tmp1", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
        localVariableGen1.setStart(instructionList.append(new ASTORE(localVariableGen1.getIndex())));
        predicate.translate(paramClassGenerator, paramMethodGenerator);
        LocalVariableGen localVariableGen2 = paramMethodGenerator.addLocalVariable("filter_expr_tmp2", Util.getJCRefType("I"), null, null);
        localVariableGen2.setStart(instructionList.append(new ISTORE(localVariableGen2.getIndex())));
        instructionList.append(new NEW(constantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.NthIterator")));
        instructionList.append(DUP);
        localVariableGen1.setEnd(instructionList.append(new ALOAD(localVariableGen1.getIndex())));
        localVariableGen2.setEnd(instructionList.append(new ILOAD(localVariableGen2.getIndex())));
        instructionList.append(new INVOKESPECIAL(i));
      } else {
        int i = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.CurrentNodeListIterator", "<init>", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;ZLcom/sun/org/apache/xalan/internal/xsltc/dom/CurrentNodeListFilter;ILcom/sun/org/apache/xalan/internal/xsltc/runtime/AbstractTranslet;)V");
        LocalVariableGen localVariableGen1 = paramMethodGenerator.addLocalVariable("filter_expr_tmp1", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
        localVariableGen1.setStart(instructionList.append(new ASTORE(localVariableGen1.getIndex())));
        predicate.translate(paramClassGenerator, paramMethodGenerator);
        LocalVariableGen localVariableGen2 = paramMethodGenerator.addLocalVariable("filter_expr_tmp2", Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/dom/CurrentNodeListFilter;"), null, null);
        localVariableGen2.setStart(instructionList.append(new ASTORE(localVariableGen2.getIndex())));
        instructionList.append(new NEW(constantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.CurrentNodeListIterator")));
        instructionList.append(DUP);
        localVariableGen1.setEnd(instructionList.append(new ALOAD(localVariableGen1.getIndex())));
        instructionList.append(ICONST_1);
        localVariableGen2.setEnd(instructionList.append(new ALOAD(localVariableGen2.getIndex())));
        instructionList.append(paramMethodGenerator.loadCurrentNode());
        instructionList.append(paramClassGenerator.loadTranslet());
        instructionList.append(new INVOKESPECIAL(i));
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\FilterExpr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */