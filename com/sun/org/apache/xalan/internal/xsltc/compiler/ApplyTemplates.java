package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.utils.XML11Char;
import java.util.Vector;

final class ApplyTemplates extends Instruction {
  private Expression _select;
  
  private Type _type = null;
  
  private QName _modeName;
  
  private String _functionName;
  
  public void display(int paramInt) {
    indent(paramInt);
    Util.println("ApplyTemplates");
    indent(paramInt + 4);
    Util.println("select " + this._select.toString());
    if (this._modeName != null) {
      indent(paramInt + 4);
      Util.println("mode " + this._modeName);
    } 
  }
  
  public boolean hasWithParams() { return hasContents(); }
  
  public void parseContents(Parser paramParser) {
    String str1 = getAttribute("select");
    String str2 = getAttribute("mode");
    if (str1.length() > 0)
      this._select = paramParser.parseExpression(this, "select", null); 
    if (str2.length() > 0) {
      if (!XML11Char.isXML11ValidQName(str2)) {
        ErrorMsg errorMsg = new ErrorMsg("INVALID_QNAME_ERR", str2, this);
        paramParser.reportError(3, errorMsg);
      } 
      this._modeName = paramParser.getQNameIgnoreDefaultNs(str2);
    } 
    this._functionName = paramParser.getTopLevelStylesheet().getMode(this._modeName).functionName();
    parseChildren(paramParser);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    if (this._select != null) {
      this._type = this._select.typeCheck(paramSymbolTable);
      if (this._type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType || this._type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType) {
        this._select = new CastExpr(this._select, Type.NodeSet);
        this._type = Type.NodeSet;
      } 
      if (this._type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType || this._type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType) {
        typeCheckContents(paramSymbolTable);
        return Type.Void;
      } 
      throw new TypeCheckError(this);
    } 
    typeCheckContents(paramSymbolTable);
    return Type.Void;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    boolean bool = false;
    Stylesheet stylesheet = paramClassGenerator.getStylesheet();
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    int i = paramMethodGenerator.getLocalIndex("current");
    Vector vector = new Vector();
    for (SyntaxTreeNode syntaxTreeNode : getContents()) {
      if (syntaxTreeNode instanceof Sort)
        vector.addElement((Sort)syntaxTreeNode); 
    } 
    if (stylesheet.hasLocalParams() || hasContents()) {
      instructionList.append(paramClassGenerator.loadTranslet());
      int k = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "pushParamFrame", "()V");
      instructionList.append(new INVOKEVIRTUAL(k));
      translateContents(paramClassGenerator, paramMethodGenerator);
    } 
    instructionList.append(paramClassGenerator.loadTranslet());
    if (this._type != null && this._type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType) {
      if (vector.size() > 0) {
        ErrorMsg errorMsg = new ErrorMsg("RESULT_TREE_SORT_ERR", this);
        getParser().reportError(4, errorMsg);
      } 
      this._select.translate(paramClassGenerator, paramMethodGenerator);
      this._type.translateTo(paramClassGenerator, paramMethodGenerator, Type.NodeSet);
    } else {
      instructionList.append(paramMethodGenerator.loadDOM());
      if (vector.size() > 0) {
        Sort.translateSortIterator(paramClassGenerator, paramMethodGenerator, this._select, vector);
        int k = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator", "setStartNode", "(I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
        instructionList.append(paramMethodGenerator.loadCurrentNode());
        instructionList.append(new INVOKEINTERFACE(k, 2));
        bool = true;
      } else if (this._select == null) {
        Mode.compileGetChildren(paramClassGenerator, paramMethodGenerator, i);
      } else {
        this._select.translate(paramClassGenerator, paramMethodGenerator);
      } 
    } 
    if (this._select != null && !bool)
      this._select.startIterator(paramClassGenerator, paramMethodGenerator); 
    String str1 = paramClassGenerator.getStylesheet().getClassName();
    instructionList.append(paramMethodGenerator.loadHandler());
    String str2 = paramClassGenerator.getApplyTemplatesSig();
    int j = constantPoolGen.addMethodref(str1, this._functionName, str2);
    instructionList.append(new INVOKEVIRTUAL(j));
    for (SyntaxTreeNode syntaxTreeNode : getContents()) {
      if (syntaxTreeNode instanceof WithParam)
        ((WithParam)syntaxTreeNode).releaseResultTree(paramClassGenerator, paramMethodGenerator); 
    } 
    if (stylesheet.hasLocalParams() || hasContents()) {
      instructionList.append(paramClassGenerator.loadTranslet());
      int k = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "popParamFrame", "()V");
      instructionList.append(new INVOKEVIRTUAL(k));
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\ApplyTemplates.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */