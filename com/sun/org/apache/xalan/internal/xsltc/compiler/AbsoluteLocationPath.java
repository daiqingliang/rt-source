package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class AbsoluteLocationPath extends Expression {
  private Expression _path = null;
  
  public AbsoluteLocationPath() {}
  
  public AbsoluteLocationPath(Expression paramExpression) {
    if (paramExpression != null)
      this._path.setParent(this); 
  }
  
  public void setParser(Parser paramParser) {
    super.setParser(paramParser);
    if (this._path != null)
      this._path.setParser(paramParser); 
  }
  
  public Expression getPath() { return this._path; }
  
  public String toString() { return "AbsoluteLocationPath(" + ((this._path != null) ? this._path.toString() : "null") + ')'; }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    if (this._path != null) {
      Type type = this._path.typeCheck(paramSymbolTable);
      if (type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType)
        this._path = new CastExpr(this._path, Type.NodeSet); 
    } 
    return this._type = Type.NodeSet;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    if (this._path != null) {
      int i = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.AbsoluteIterator", "<init>", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)V");
      this._path.translate(paramClassGenerator, paramMethodGenerator);
      LocalVariableGen localVariableGen = paramMethodGenerator.addLocalVariable("abs_location_path_tmp", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
      localVariableGen.setStart(instructionList.append(new ASTORE(localVariableGen.getIndex())));
      instructionList.append(new NEW(constantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.AbsoluteIterator")));
      instructionList.append(DUP);
      localVariableGen.setEnd(instructionList.append(new ALOAD(localVariableGen.getIndex())));
      instructionList.append(new INVOKESPECIAL(i));
    } else {
      int i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getIterator", "()Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
      instructionList.append(paramMethodGenerator.loadDOM());
      instructionList.append(new INVOKEINTERFACE(i, 1));
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\AbsoluteLocationPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */