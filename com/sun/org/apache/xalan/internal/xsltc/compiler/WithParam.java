package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.utils.XML11Char;

final class WithParam extends Instruction {
  private QName _name;
  
  protected String _escapedName;
  
  private Expression _select;
  
  private LocalVariableGen _domAdapter;
  
  private boolean _doParameterOptimization = false;
  
  public void display(int paramInt) {
    indent(paramInt);
    Util.println("with-param " + this._name);
    if (this._select != null) {
      indent(paramInt + 4);
      Util.println("select " + this._select.toString());
    } 
    displayContents(paramInt + 4);
  }
  
  public String getEscapedName() { return this._escapedName; }
  
  public QName getName() { return this._name; }
  
  public void setName(QName paramQName) {
    this._name = paramQName;
    this._escapedName = Util.escape(paramQName.getStringRep());
  }
  
  public void setDoParameterOptimization(boolean paramBoolean) { this._doParameterOptimization = paramBoolean; }
  
  public void parseContents(Parser paramParser) {
    String str1 = getAttribute("name");
    if (str1.length() > 0) {
      if (!XML11Char.isXML11ValidQName(str1)) {
        ErrorMsg errorMsg = new ErrorMsg("INVALID_QNAME_ERR", str1, this);
        paramParser.reportError(3, errorMsg);
      } 
      setName(paramParser.getQNameIgnoreDefaultNs(str1));
    } else {
      reportError(this, paramParser, "REQUIRED_ATTR_ERR", "name");
    } 
    String str2 = getAttribute("select");
    if (str2.length() > 0)
      this._select = paramParser.parseExpression(this, "select", null); 
    parseChildren(paramParser);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    if (this._select != null) {
      Type type = this._select.typeCheck(paramSymbolTable);
      if (!(type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType))
        this._select = new CastExpr(this._select, Type.Reference); 
    } else {
      typeCheckContents(paramSymbolTable);
    } 
    return Type.Void;
  }
  
  public void translateValue(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    if (this._select != null) {
      this._select.translate(paramClassGenerator, paramMethodGenerator);
      this._select.startIterator(paramClassGenerator, paramMethodGenerator);
    } else if (hasContents()) {
      InstructionList instructionList = paramMethodGenerator.getInstructionList();
      compileResultTree(paramClassGenerator, paramMethodGenerator);
      this._domAdapter = paramMethodGenerator.addLocalVariable2("@" + this._escapedName, Type.ResultTree.toJCType(), instructionList.getEnd());
      instructionList.append(DUP);
      instructionList.append(new ASTORE(this._domAdapter.getIndex()));
    } else {
      ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
      InstructionList instructionList = paramMethodGenerator.getInstructionList();
      instructionList.append(new PUSH(constantPoolGen, ""));
    } 
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    if (this._doParameterOptimization) {
      translateValue(paramClassGenerator, paramMethodGenerator);
      return;
    } 
    String str = Util.escape(getEscapedName());
    instructionList.append(paramClassGenerator.loadTranslet());
    instructionList.append(new PUSH(constantPoolGen, str));
    translateValue(paramClassGenerator, paramMethodGenerator);
    instructionList.append(new PUSH(constantPoolGen, false));
    instructionList.append(new INVOKEVIRTUAL(constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "addParameter", "(Ljava/lang/String;Ljava/lang/Object;Z)Ljava/lang/Object;")));
    instructionList.append(POP);
  }
  
  public void releaseResultTree(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    if (this._domAdapter != null) {
      ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
      InstructionList instructionList = paramMethodGenerator.getInstructionList();
      if (paramClassGenerator.getStylesheet().callsNodeset() && paramClassGenerator.getDOMClass().equals("com.sun.org.apache.xalan.internal.xsltc.dom.MultiDOM")) {
        int j = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.MultiDOM", "removeDOMAdapter", "(Lcom/sun/org/apache/xalan/internal/xsltc/dom/DOMAdapter;)V");
        instructionList.append(paramMethodGenerator.loadDOM());
        instructionList.append(new CHECKCAST(constantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.MultiDOM")));
        instructionList.append(new ALOAD(this._domAdapter.getIndex()));
        instructionList.append(new CHECKCAST(constantPoolGen.addClass("com/sun/org/apache/xalan/internal/xsltc/dom/DOMAdapter")));
        instructionList.append(new INVOKEVIRTUAL(j));
      } 
      int i = constantPoolGen.addInterfaceMethodref("com/sun/org/apache/xalan/internal/xsltc/DOM", "release", "()V");
      instructionList.append(new ALOAD(this._domAdapter.getIndex()));
      instructionList.append(new INVOKEINTERFACE(i, 1));
      this._domAdapter.setEnd(instructionList.getEnd());
      paramMethodGenerator.removeLocalVariable(this._domAdapter);
      this._domAdapter = null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\WithParam.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */