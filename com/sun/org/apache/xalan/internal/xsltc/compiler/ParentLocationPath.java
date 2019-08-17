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

final class ParentLocationPath extends RelativeLocationPath {
  private Expression _step;
  
  private final RelativeLocationPath _path;
  
  private Type stype;
  
  private boolean _orderNodes = false;
  
  private boolean _axisMismatch = false;
  
  public ParentLocationPath(RelativeLocationPath paramRelativeLocationPath, Expression paramExpression) {
    this._path = paramRelativeLocationPath;
    this._step = paramExpression;
    this._path.setParent(this);
    this._step.setParent(this);
    if (this._step instanceof Step)
      this._axisMismatch = checkAxisMismatch(); 
  }
  
  public void setAxis(int paramInt) { this._path.setAxis(paramInt); }
  
  public int getAxis() { return this._path.getAxis(); }
  
  public RelativeLocationPath getPath() { return this._path; }
  
  public Expression getStep() { return this._step; }
  
  public void setParser(Parser paramParser) {
    super.setParser(paramParser);
    this._step.setParser(paramParser);
    this._path.setParser(paramParser);
  }
  
  public String toString() { return "ParentLocationPath(" + this._path + ", " + this._step + ')'; }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    this.stype = this._step.typeCheck(paramSymbolTable);
    this._path.typeCheck(paramSymbolTable);
    if (this._axisMismatch)
      enableNodeOrdering(); 
    return this._type = Type.NodeSet;
  }
  
  public void enableNodeOrdering() {
    SyntaxTreeNode syntaxTreeNode = getParent();
    if (syntaxTreeNode instanceof ParentLocationPath) {
      ((ParentLocationPath)syntaxTreeNode).enableNodeOrdering();
    } else {
      this._orderNodes = true;
    } 
  }
  
  public boolean checkAxisMismatch() {
    int i = this._path.getAxis();
    int j = ((Step)this._step).getAxis();
    if ((i == 0 || i == 1) && (j == 3 || j == 4 || j == 5 || j == 10 || j == 11 || j == 12))
      return true; 
    if ((i == 3 && j == 0) || j == 1 || j == 10 || j == 11)
      return true; 
    if (i == 4 || i == 5)
      return true; 
    if ((i == 6 || i == 7) && (j == 6 || j == 10 || j == 11 || j == 12))
      return true; 
    if ((i == 11 || i == 12) && (j == 4 || j == 5 || j == 6 || j == 7 || j == 10 || j == 11 || j == 12))
      return true; 
    if (j == 6 && i == 3 && this._path instanceof Step) {
      int k = ((Step)this._path).getNodeType();
      if (k == 2)
        return true; 
    } 
    return false;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    this._path.translate(paramClassGenerator, paramMethodGenerator);
    translateStep(paramClassGenerator, paramMethodGenerator);
  }
  
  public void translateStep(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    LocalVariableGen localVariableGen1 = paramMethodGenerator.addLocalVariable("parent_location_path_tmp1", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
    localVariableGen1.setStart(instructionList.append(new ASTORE(localVariableGen1.getIndex())));
    this._step.translate(paramClassGenerator, paramMethodGenerator);
    LocalVariableGen localVariableGen2 = paramMethodGenerator.addLocalVariable("parent_location_path_tmp2", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
    localVariableGen2.setStart(instructionList.append(new ASTORE(localVariableGen2.getIndex())));
    int i = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.StepIterator", "<init>", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)V");
    instructionList.append(new NEW(constantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.StepIterator")));
    instructionList.append(DUP);
    localVariableGen1.setEnd(instructionList.append(new ALOAD(localVariableGen1.getIndex())));
    localVariableGen2.setEnd(instructionList.append(new ALOAD(localVariableGen2.getIndex())));
    instructionList.append(new INVOKESPECIAL(i));
    Expression expression = this._step;
    if (expression instanceof ParentLocationPath)
      expression = ((ParentLocationPath)expression).getStep(); 
    if (this._path instanceof Step && expression instanceof Step) {
      int j = ((Step)this._path).getAxis();
      int k = ((Step)expression).getAxis();
      if ((j == 5 && k == 3) || (j == 11 && k == 10)) {
        int m = constantPoolGen.addMethodref("com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase", "includeSelf", "()Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
        instructionList.append(new INVOKEVIRTUAL(m));
      } 
    } 
    if (this._orderNodes) {
      int j = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "orderNodes", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
      instructionList.append(paramMethodGenerator.loadDOM());
      instructionList.append(SWAP);
      instructionList.append(paramMethodGenerator.loadContextNode());
      instructionList.append(new INVOKEINTERFACE(j, 3));
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\ParentLocationPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */