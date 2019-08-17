package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFGT;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.util.Iterator;
import java.util.Vector;

final class ForEach extends Instruction {
  private Expression _select;
  
  private Type _type;
  
  public void display(int paramInt) {
    indent(paramInt);
    Util.println("ForEach");
    indent(paramInt + 4);
    Util.println("select " + this._select.toString());
    displayContents(paramInt + 4);
  }
  
  public void parseContents(Parser paramParser) {
    this._select = paramParser.parseExpression(this, "select", null);
    parseChildren(paramParser);
    if (this._select.isDummy())
      reportError(this, paramParser, "REQUIRED_ATTR_ERR", "select"); 
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    this._type = this._select.typeCheck(paramSymbolTable);
    if (this._type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType || this._type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType) {
      this._select = new CastExpr(this._select, Type.NodeSet);
      typeCheckContents(paramSymbolTable);
      return Type.Void;
    } 
    if (this._type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType || this._type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType) {
      typeCheckContents(paramSymbolTable);
      return Type.Void;
    } 
    throw new TypeCheckError(this);
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    instructionList.append(paramMethodGenerator.loadCurrentNode());
    instructionList.append(paramMethodGenerator.loadIterator());
    Vector vector = new Vector();
    Iterator iterator = elements();
    while (iterator.hasNext()) {
      SyntaxTreeNode syntaxTreeNode = (SyntaxTreeNode)iterator.next();
      if (syntaxTreeNode instanceof Sort)
        vector.addElement(syntaxTreeNode); 
    } 
    if (this._type != null && this._type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType) {
      instructionList.append(paramMethodGenerator.loadDOM());
      if (vector.size() > 0) {
        ErrorMsg errorMsg = new ErrorMsg("RESULT_TREE_SORT_ERR", this);
        getParser().reportError(4, errorMsg);
      } 
      this._select.translate(paramClassGenerator, paramMethodGenerator);
      this._type.translateTo(paramClassGenerator, paramMethodGenerator, Type.NodeSet);
      instructionList.append(SWAP);
      instructionList.append(paramMethodGenerator.storeDOM());
    } else {
      if (vector.size() > 0) {
        Sort.translateSortIterator(paramClassGenerator, paramMethodGenerator, this._select, vector);
      } else {
        this._select.translate(paramClassGenerator, paramMethodGenerator);
      } 
      if (!(this._type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType)) {
        instructionList.append(paramMethodGenerator.loadContextNode());
        instructionList.append(paramMethodGenerator.setStartNode());
      } 
    } 
    instructionList.append(paramMethodGenerator.storeIterator());
    initializeVariables(paramClassGenerator, paramMethodGenerator);
    BranchHandle branchHandle = instructionList.append(new GOTO(null));
    InstructionHandle instructionHandle = instructionList.append(NOP);
    translateContents(paramClassGenerator, paramMethodGenerator);
    branchHandle.setTarget(instructionList.append(paramMethodGenerator.loadIterator()));
    instructionList.append(paramMethodGenerator.nextNode());
    instructionList.append(DUP);
    instructionList.append(paramMethodGenerator.storeCurrentNode());
    instructionList.append(new IFGT(instructionHandle));
    if (this._type != null && this._type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType)
      instructionList.append(paramMethodGenerator.storeDOM()); 
    instructionList.append(paramMethodGenerator.storeIterator());
    instructionList.append(paramMethodGenerator.storeCurrentNode());
  }
  
  public void initializeVariables(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    int i = elementCount();
    for (byte b = 0; b < i; b++) {
      SyntaxTreeNode syntaxTreeNode = (SyntaxTreeNode)getContents().get(b);
      if (syntaxTreeNode instanceof Variable) {
        Variable variable = (Variable)syntaxTreeNode;
        variable.initialize(paramClassGenerator, paramMethodGenerator);
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\ForEach.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */