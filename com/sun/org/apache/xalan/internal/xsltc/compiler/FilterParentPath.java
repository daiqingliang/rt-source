package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class FilterParentPath extends Expression {
  private Expression _filterExpr;
  
  private Expression _path;
  
  private boolean _hasDescendantAxis = false;
  
  public FilterParentPath(Expression paramExpression1, Expression paramExpression2) {
    (this._path = paramExpression2).setParent(this);
    (this._filterExpr = paramExpression1).setParent(this);
  }
  
  public void setParser(Parser paramParser) {
    super.setParser(paramParser);
    this._filterExpr.setParser(paramParser);
    this._path.setParser(paramParser);
  }
  
  public String toString() { return "FilterParentPath(" + this._filterExpr + ", " + this._path + ')'; }
  
  public void setDescendantAxis() { this._hasDescendantAxis = true; }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    Type type1 = this._filterExpr.typeCheck(paramSymbolTable);
    if (!(type1 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType))
      if (type1 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType) {
        this._filterExpr = new CastExpr(this._filterExpr, Type.NodeSet);
      } else if (type1 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType) {
        this._filterExpr = new CastExpr(this._filterExpr, Type.NodeSet);
      } else {
        throw new TypeCheckError(this);
      }  
    Type type2 = this._path.typeCheck(paramSymbolTable);
    if (!(type2 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType))
      this._path = new CastExpr(this._path, Type.NodeSet); 
    return this._type = Type.NodeSet;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    int i = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.StepIterator", "<init>", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)V");
    this._filterExpr.translate(paramClassGenerator, paramMethodGenerator);
    LocalVariableGen localVariableGen1 = paramMethodGenerator.addLocalVariable("filter_parent_path_tmp1", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
    localVariableGen1.setStart(instructionList.append(new ASTORE(localVariableGen1.getIndex())));
    this._path.translate(paramClassGenerator, paramMethodGenerator);
    LocalVariableGen localVariableGen2 = paramMethodGenerator.addLocalVariable("filter_parent_path_tmp2", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
    localVariableGen2.setStart(instructionList.append(new ASTORE(localVariableGen2.getIndex())));
    instructionList.append(new NEW(constantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.StepIterator")));
    instructionList.append(DUP);
    localVariableGen1.setEnd(instructionList.append(new ALOAD(localVariableGen1.getIndex())));
    localVariableGen2.setEnd(instructionList.append(new ALOAD(localVariableGen2.getIndex())));
    instructionList.append(new INVOKESPECIAL(i));
    if (this._hasDescendantAxis) {
      int j = constantPoolGen.addMethodref("com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase", "includeSelf", "()Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
      instructionList.append(new INVOKEVIRTUAL(j));
    } 
    SyntaxTreeNode syntaxTreeNode = getParent();
    boolean bool = (syntaxTreeNode instanceof RelativeLocationPath || syntaxTreeNode instanceof FilterParentPath || syntaxTreeNode instanceof KeyCall || syntaxTreeNode instanceof CurrentCall || syntaxTreeNode instanceof DocumentCall) ? 1 : 0;
    if (!bool) {
      int j = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "orderNodes", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
      instructionList.append(paramMethodGenerator.loadDOM());
      instructionList.append(SWAP);
      instructionList.append(paramMethodGenerator.loadContextNode());
      instructionList.append(new INVOKEINTERFACE(j, 3));
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\FilterParentPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */