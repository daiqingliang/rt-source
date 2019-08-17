package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.IFGE;
import com.sun.org.apache.bcel.internal.generic.IFGT;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
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

final class Key extends TopLevelElement {
  private QName _name;
  
  private Pattern _match;
  
  private Expression _use;
  
  private Type _useType;
  
  public void parseContents(Parser paramParser) {
    String str = getAttribute("name");
    if (!XML11Char.isXML11ValidQName(str)) {
      ErrorMsg errorMsg = new ErrorMsg("INVALID_QNAME_ERR", str, this);
      paramParser.reportError(3, errorMsg);
    } 
    this._name = paramParser.getQNameIgnoreDefaultNs(str);
    getSymbolTable().addKey(this._name, this);
    this._match = paramParser.parsePattern(this, "match", null);
    this._use = paramParser.parseExpression(this, "use", null);
    if (this._name == null) {
      reportError(this, paramParser, "REQUIRED_ATTR_ERR", "name");
      return;
    } 
    if (this._match.isDummy()) {
      reportError(this, paramParser, "REQUIRED_ATTR_ERR", "match");
      return;
    } 
    if (this._use.isDummy()) {
      reportError(this, paramParser, "REQUIRED_ATTR_ERR", "use");
      return;
    } 
  }
  
  public String getName() { return this._name.toString(); }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    this._match.typeCheck(paramSymbolTable);
    this._useType = this._use.typeCheck(paramSymbolTable);
    if (!(this._useType instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType) && !(this._useType instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType))
      this._use = new CastExpr(this._use, Type.String); 
    return Type.Void;
  }
  
  public void traverseNodeSet(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, int paramInt) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    int i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getStringValueX", "(I)Ljava/lang/String;");
    int j = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getNodeIdent", "(I)I");
    int k = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "setKeyIndexDom", "(Ljava/lang/String;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)V");
    LocalVariableGen localVariableGen = paramMethodGenerator.addLocalVariable("parentNode", Util.getJCRefType("I"), null, null);
    localVariableGen.setStart(instructionList.append(new ISTORE(localVariableGen.getIndex())));
    instructionList.append(paramMethodGenerator.loadCurrentNode());
    instructionList.append(paramMethodGenerator.loadIterator());
    this._use.translate(paramClassGenerator, paramMethodGenerator);
    this._use.startIterator(paramClassGenerator, paramMethodGenerator);
    instructionList.append(paramMethodGenerator.storeIterator());
    BranchHandle branchHandle = instructionList.append(new GOTO(null));
    InstructionHandle instructionHandle = instructionList.append(NOP);
    instructionList.append(paramClassGenerator.loadTranslet());
    instructionList.append(new PUSH(constantPoolGen, this._name.toString()));
    localVariableGen.setEnd(instructionList.append(new ILOAD(localVariableGen.getIndex())));
    instructionList.append(paramMethodGenerator.loadDOM());
    instructionList.append(paramMethodGenerator.loadCurrentNode());
    instructionList.append(new INVOKEINTERFACE(i, 2));
    instructionList.append(new INVOKEVIRTUAL(paramInt));
    instructionList.append(paramClassGenerator.loadTranslet());
    instructionList.append(new PUSH(constantPoolGen, getName()));
    instructionList.append(paramMethodGenerator.loadDOM());
    instructionList.append(new INVOKEVIRTUAL(k));
    branchHandle.setTarget(instructionList.append(paramMethodGenerator.loadIterator()));
    instructionList.append(paramMethodGenerator.nextNode());
    instructionList.append(DUP);
    instructionList.append(paramMethodGenerator.storeCurrentNode());
    instructionList.append(new IFGE(instructionHandle));
    instructionList.append(paramMethodGenerator.storeIterator());
    instructionList.append(paramMethodGenerator.storeCurrentNode());
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    int i = paramMethodGenerator.getLocalIndex("current");
    int j = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "buildKeyIndex", "(Ljava/lang/String;ILjava/lang/String;)V");
    int k = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "setKeyIndexDom", "(Ljava/lang/String;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)V");
    int m = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getNodeIdent", "(I)I");
    int n = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getAxisIterator", "(I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
    instructionList.append(paramMethodGenerator.loadCurrentNode());
    instructionList.append(paramMethodGenerator.loadIterator());
    instructionList.append(paramMethodGenerator.loadDOM());
    instructionList.append(new PUSH(constantPoolGen, 4));
    instructionList.append(new INVOKEINTERFACE(n, 2));
    instructionList.append(paramMethodGenerator.loadCurrentNode());
    instructionList.append(paramMethodGenerator.setStartNode());
    instructionList.append(paramMethodGenerator.storeIterator());
    BranchHandle branchHandle1 = instructionList.append(new GOTO(null));
    InstructionHandle instructionHandle1 = instructionList.append(NOP);
    instructionList.append(paramMethodGenerator.loadCurrentNode());
    this._match.translate(paramClassGenerator, paramMethodGenerator);
    this._match.synthesize(paramClassGenerator, paramMethodGenerator);
    BranchHandle branchHandle2 = instructionList.append(new IFEQ(null));
    if (this._useType instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType) {
      instructionList.append(paramMethodGenerator.loadCurrentNode());
      traverseNodeSet(paramClassGenerator, paramMethodGenerator, j);
    } else {
      instructionList.append(paramClassGenerator.loadTranslet());
      instructionList.append(DUP);
      instructionList.append(new PUSH(constantPoolGen, this._name.toString()));
      instructionList.append(DUP_X1);
      instructionList.append(paramMethodGenerator.loadCurrentNode());
      this._use.translate(paramClassGenerator, paramMethodGenerator);
      instructionList.append(new INVOKEVIRTUAL(j));
      instructionList.append(paramMethodGenerator.loadDOM());
      instructionList.append(new INVOKEVIRTUAL(k));
    } 
    InstructionHandle instructionHandle2 = instructionList.append(NOP);
    instructionList.append(paramMethodGenerator.loadIterator());
    instructionList.append(paramMethodGenerator.nextNode());
    instructionList.append(DUP);
    instructionList.append(paramMethodGenerator.storeCurrentNode());
    instructionList.append(new IFGT(instructionHandle1));
    instructionList.append(paramMethodGenerator.storeIterator());
    instructionList.append(paramMethodGenerator.storeCurrentNode());
    branchHandle1.setTarget(instructionHandle2);
    branchHandle2.setTarget(instructionHandle2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Key.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */